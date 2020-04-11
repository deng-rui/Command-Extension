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
import extension.data.global.Maps;
import extension.data.global.Lists;
import extension.util.Log;
//GA-Exted


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
	private Runnable endmsg;
	public static Team team;
	// 投票状态
	public static boolean sted = true;
	// 队伍锁定 /FF
	public static boolean isteam = false;
	// 已投票玩家
	public static List<String> playerlist = new ArrayList<String>();
	// 投票数
	public static int playervote = 0;


	public Vote(Player player, String type, String name){
		this.player = player;
		this.type = type;
		this.name = name;
		Preprocessing();
	}

	public Vote(Player player, String type, Team team){
		this.player = player;
		this.type = type;
		this.team = team;
		isteam = true;
		Preprocessing();
	}

	public Vote(Player player, String type){
		this.player = player;
		this.type = type;
		Preprocessing();
	}

	public Vote(){
		if (playerlist.size() >= require) {
			Count_down.cancel(true);
			Vote_time.cancel(true);
			End();
		}
	}

	private void Preprocessing() {
		// 预处理
		switch(type){
			case "kick" :
				target = playerGroup.find(p -> p.name.equals(name));
				if(target == null)
					player.sendMessage(getinput("vote.kick.err",name));
				else
					Normal_distribution();
				break;
			case "host" :
				if (!(Lists.getMaps_List().size() >= Integer.parseInt(name)))
					player.sendMessage(getinput("vote.host.maps.err",name));
				else
					Normal_distribution();
				break;
			case "skipwave" :
				if (state.rules.pvp)
					player.sendMessage(getinput("vote.wave.fail"));
				else
					Normal_distribution();
				break;
			case "gameover" :
				Normal_distribution();
				break;
			case "ff" :
				Team_only();
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
		Start(() -> Call.sendMessage(getinput("vote.start",player.name,type+" "+(Blank(name)?"":name))));
		endmsg = () -> Call.sendMessage(getinput("vote.done.no",type+" "+(Blank(name)?"":name),playerlist.size(),temp));;
	}

	// 团队投票
	private void Team_only() {
		for (Player it : Vars.playerGroup.all())
			if (it.getTeam().equals(team))
				temp++;
		require = temp;
		Start(() -> Extend.addMesg_Team(getinput("vote.start",player.name,type+" "+(Blank(name)?"":name)),team));
		endmsg = () -> Extend.addMesg_Team(getinput("vote.done.no",type+" "+(Blank(name)?"":name),playerlist.size(),temp),team);
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
		Count_down=Config.service.scheduleAtFixedRate(Countdown,10,10,TimeUnit.SECONDS);
		Runnable Votetime=new Runnable() {
			@Override
			public void run() {
				Count_down.cancel(true);
				End();
			}
		};
		Vote_time=Config.service.schedule(Votetime,58,TimeUnit.SECONDS);
		// 剩余时间
		reciprocal=60;
		// 正在投票
		sted = false;
		playerlist.add(player.uuid);
		if (playerlist.size() >= require) {
			Count_down.cancel(true);
			Vote_time.cancel(true);
			End();
		} else
			run.run();
	}

	private void End() {
		//释放内存-
		Count_down=null;
		Vote_time=null;
		//-
		if (playerlist.size() >= require) {
			Call.sendMessage(getinput("vote.ok"));
			playerlist.clear();
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
		} else {
			endmsg.run();
		}
		isteam = false;
		sted = true;
		// 归零.jpg
		name = null;
		temp = 0;
		endmsg = null;
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