package extension.core.ex;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
//Java

import arc.Events;
import arc.math.Mathf;
import arc.util.Time;
//Arc

import mindustry.net.Packets.KickReason;
import mindustry.gen.Call;
import mindustry.game.Team;
import mindustry.entities.type.Player;
import mindustry.game.Gamemode;
import mindustry.game.EventType.GameOverEvent;
import mindustry.maps.Map;
//import mindustry.maps.Maps;
import mindustry.world.Tile;
import mindustry.Vars;
//Mindustry

import static mindustry.Vars.logic;
import static mindustry.Vars.maps;
import static mindustry.Vars.netServer;
import static mindustry.Vars.playerGroup;
import static mindustry.Vars.world;
import static mindustry.Vars.state;
//Mindustry-Static

import extension.core.ex.Extend;
import extension.data.global.Config;
import extension.data.global.Data;
import extension.data.global.Maps;
import extension.data.global.Lists;
import extension.util.Log;
//GA-Exted

import static extension.core.ex.Extend.Authority_control;
import static extension.data.global.Lists.getMaps_List;
import static extension.util.DateUtil.getLocalTimeFromUTC;
import static extension.util.LocaleUtil.getinput;
import static extension.util.IsUtil.Blank;
//Static

public class Vote {
	private Player player;
	private Player target;
	private String type;
	private String name;
	private int require;
	private int reciprocal;
	private ScheduledFuture Vote_time;
	private ScheduledFuture Count_down;
	private int temp = 0;
	private Runnable endyesmsg;
	private Runnable endnomsg;
	public static Team team;
	// 投票状态
	public static boolean sted = true;
	// 队伍锁定 /FF
	public static boolean isteam = false;
	// 已投票玩家
	public static List<String> playerlist = new ArrayList<String>();
	// 投票数 A/B ?
	private int playervote = 0;

	// 防止输入法开头大写
	public Vote(Player player, String type, String name){
		this.player = player;
		this.type = type.toLowerCase();
		this.name = name;
		Preprocessing();
	}

	public Vote(Player player, String type, Team team){
		this.player = player;
		this.type = type.toLowerCase();
		this.team = team;
		isteam = true;
		Preprocessing();
	}

	public Vote(Player player, String type){
		this.player = player;
		this.type = type.toLowerCase();
		Preprocessing();
	}

	public void ToVote(Player playerplayer,String playerpick) {
		switch(playerpick){
			// 我也不像写一堆一样的啊:(
			case "y" :
			case "n" :
				playerlist.add(playerplayer.uuid);
				if ("y".equals(playerpick)) {
					playervote++;
					playerplayer.sendMessage(getinput("vote.y"));
				}else
					playerplayer.sendMessage(getinput("vote.n"));
				Inspect_End();
				break;
			// Vote Compulsory passage
			case "cy" :
				if (Authority_control(playerplayer,"votec")) {
					if (isteam)
						endyesmsg = () -> Extend.addMesg_Team(getinput("vote.cy.end",playerplayer.name,type+" "+(Blank(name)?"":name)),team);
					else
						endyesmsg = () -> Call.sendMessage(getinput("vote.cy.end",playerplayer.name,type+" "+(Blank(name)?"":name)));
					require = 0;
					Force_End();
				} else
					playerplayer.sendMessage(getinput("authority.no"));
				break;
			// Vote Compulsory refusal
			case "cn" :
				if (Authority_control(playerplayer,"votec")) {
					if (isteam)
						endnomsg = () -> Extend.addMesg_Team(getinput("vote.cn.end",playerplayer.name,type+" "+(Blank(name)?"":name)),team);
					else
						endnomsg = () -> Call.sendMessage(getinput("vote.cn.end",playerplayer.name,type+" "+(Blank(name)?"":name)));
					playervote = 0;
					Force_End();
				} else
					playerplayer.sendMessage(getinput("authority.no"));
				break;
		}
	}

	private void Preprocessing() {
		// 预处理
		switch(type){
			case "gameover" :
				Normal_distribution();
				break;
			case "ff" :
				Team_only();
				break;
			case "host" :
				if (!(Lists.getMaps_List().size() >= Integer.parseInt(name)))
					player.sendMessage(getinput("vote.host.maps.err",name));
				else
					Normal_distribution();
				break;
			case "kick" :
				target = playerGroup.find(p -> p.name.equals(name));
				if(target == null)
					player.sendMessage(getinput("vote.kick.err",name));
				else {
					if (target.isAdmin)
						player.sendMessage(getinput("vote.err.admin",name));
					else
						Normal_distribution();
				}	
				break;
			case "skipwave" :
				if (state.rules.pvp)
					player.sendMessage(getinput("vote.wave.fail"));
				else
					Normal_distribution();
				break;		
			case "admin" :
				if(Config.Vote_Admin)
					if(player.isAdmin)
						Management_only();
					else
						player.sendMessage(getinput("admin.no"));
				else
					player.sendMessage(getinput("vote.admin.no"));
				break;
			default :
				Call.sendMessage(getinput("vote.end.err",type+" "+(Blank(name)?"":name)));
				break;
		}
	}

	// 正常投票
	private void Normal_distribution() {
		if(Config.Login_Radical) {
			for (Player it : Vars.playerGroup.all())
				if (Maps.getPlayer_Data(it.uuid).Authority > 0)
					temp++;
		} else
			temp = playerGroup.size();	
		endnomsg = () -> Call.sendMessage(getinput("vote.done.no",type+" "+(Blank(name)?"":name),playervote,temp));
		endyesmsg = () -> Call.sendMessage(getinput("vote.ok"));
		Start(() -> Call.sendMessage(getinput("vote.start",player.name,type+" "+(Blank(name)?"":name))));
	}

	// 团队投票
	private void Team_only() {
		for (Player it : Vars.playerGroup.all())
			if (it.getTeam().equals(team))
				temp++;
		require = temp;
		endnomsg = () -> Extend.addMesg_Team(getinput("vote.done.no",type+" "+(Blank(name)?"":name),playervote,temp),team);
		endyesmsg = () -> Extend.addMesg_Team(getinput("vote.ok"),team);
		Start(() -> Extend.addMesg_Team(getinput("vote.start",player.name,type+" "+(Blank(name)?"":name)),team));
	}

	// 管理投票
	private void Management_only() {
		for (Player it : Vars.playerGroup.all())
			if(it.isAdmin)
				temp++;
		if (temp > 5) {
			require = temp;
			//Start();
		} else
			player.sendMessage(getinput("vote.admin.no"));
	}

	private void Start(Runnable run){
		if(temp == 1){
			player.sendMessage(getinput("vote.no1"));
			require = 1;
		} else if(temp <= 3)
			require = 2;
		else
			require = (int) Math.ceil((double) temp / 2);

		Runnable Countdown=new Runnable() {
			@Override
			public void run() {
				reciprocal = reciprocal-10;
				Call.sendMessage(getinput("vote.ing",reciprocal));
			}
		};
		Count_down=Data.service.scheduleAtFixedRate(Countdown,10,10,TimeUnit.SECONDS);
		Runnable Votetime=new Runnable() {
			@Override
			public void run() {
				Count_down.cancel(true);
				End();
			}
		};
		Vote_time=Data.service.schedule(Votetime,58,TimeUnit.SECONDS);
		// 剩余时间
		reciprocal=60;
		// 正在投票
		sted = false;
		playerlist.add(player.uuid);
		playervote++;
		if (playervote >= require)
			Force_End();
		else
			run.run();
	}

	private void End() {
		if (playervote >= require) {
			endyesmsg.run();	
			switch(type){
				case "kick" :
					kick();
					break;
				case "host" :
					host();
					break;
				case "skipwave" :
					skipwave();
					break;
				case "gameover" :
					gameover();
					break;
				case "ff" :
					ff();
					break;
			}
		} else
			endnomsg.run();
		playerlist.clear();
		isteam = false;
		sted = true;
		// 归零.jpg
		temp = 0;
		playervote = 0;
		// 释放内存
		name = null;
		endnomsg = null;
		endyesmsg = null;
		Count_down=null;
		Vote_time=null;
		Data.vote = null;
	}

	private void kick() {
		Call.sendMessage(getinput("kick.done",name));
		target.con.kick(KickReason.kick);
	}

	private void host() {
		List<String> MapsList = (List<String>)getMaps_List();
		String [] data = MapsList.get(Integer.parseInt(name)).split("\\s+");
		Map result = maps.all().find(map -> map.name().equalsIgnoreCase(data[0].replace('_', ' ')) || map.name().equalsIgnoreCase(data[0]));
		Gamemode mode = Gamemode.survival;
		try{
			mode = Gamemode.valueOf(data[2]);
		}catch(IllegalArgumentException ex){
			player.sendMessage(getinput("host.mode",data[2]));
			return;
		}
		final Gamemode gamemode = mode;
		Call.sendMessage(getinput("host.re"));
		Extend.loadmaps(true, () -> world.loadMap(result, result.applyRules(gamemode)),gamemode);
	}

	private void skipwave() {
		for (int i = 0; i < 10; i++) {
			logic.runWave();
		}
	}

	private void gameover() {
		Events.fire(new GameOverEvent(Team.crux));
	}

	private void ff() {
		for (Player it : Vars.playerGroup.all())
			if (it.getTeam().equals(team)) {
				killTiles(it.getTeam());
				it.kill();
				it.setTeam(Team.derelict);
			}

	}

	private void Inspect_End() {
		if (playervote >= require) {
			Count_down.cancel(true);
			Vote_time.cancel(true);
			End();
		}
	}

	private void Force_End() {
		Count_down.cancel(true);
		Vote_time.cancel(true);
		End();
	}

	private void killTiles(Team team){
		for(int x = 0; x < world.width(); x++){
			for(int y = 0; y < world.height(); y++){
				Tile tile = world.tile(x, y);
				if(tile.entity != null && tile.getTeam() == team){
					Time.run(Mathf.random(60f * 6), tile.entity::kill);
				}
			}
		}
	}
}