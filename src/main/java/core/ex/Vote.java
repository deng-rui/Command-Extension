package extension.core.ex;

import arc.Events;
import arc.math.Mathf;
import arc.util.Time;
import extension.data.global.Config;
import extension.data.global.Data;
import extension.data.global.Lists;
import extension.data.global.Maps;
import extension.util.LocaleUtil;
import mindustry.Vars;
import mindustry.entities.type.Player;
import mindustry.game.EventType.GameOverEvent;
import mindustry.game.Gamemode;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.maps.Map;
import mindustry.net.Packets.KickReason;
import mindustry.world.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static extension.core.ex.Extend.Authority_control;
import static extension.data.global.Lists.getMaps_List;
import static extension.util.IsUtil.Blank;
import static mindustry.Vars.*;

/**
 * @author Dr
 * @Date ?
 */
public class Vote {
	private Player player;
	private Player target;
	private String type;
	private String name;
	private int require;
	private int reciprocal;
	private ScheduledFuture voteTime;
	private ScheduledFuture countDown;
	private int temp = 0;
	private Runnable endYesMsg;
	private Runnable endNomsg;
	public static Team team;
	// 投票状态
	public static boolean sted = true;
	// 队伍锁定 /FF
	public static boolean isteam = false;
	// 已投票玩家
	public static List<String> playerList = new ArrayList<String>();
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
		Vote.team = team;
		isteam = true;
		Preprocessing();
	}

	public Vote(Player player, String type){
		this.player = player;
		this.type = type.toLowerCase();
		Preprocessing();
	}


	public void ToVote(Player playerplayer, String playerpick) {
		LocaleUtil localeUtil = Maps.getPlayer_Data(playerplayer.uuid).Info;
		switch(playerpick){
			// 我也不像写一堆一样的啊:(
			case "y" :
			case "n" :
				playerList.add(playerplayer.uuid);
				if ("y".equals(playerpick)) {
					playervote++;
					playerplayer.sendMessage(localeUtil.getinput("vote.y"));
				}else {
                    playerplayer.sendMessage(localeUtil.getinput("vote.n"));
                }
				Inspect_End();
				break;
			// Vote Compulsory passage
			case "cy" :
				if (Authority_control(playerplayer,"votec")) {
					if (isteam) {
                        endYesMsg = () -> extension.core.ex.Extend.addMesg_Team(team,"vote.cy.end",playerplayer.name,type+" "+(Blank(name)?"":name));
                    } else {
                        endYesMsg = () -> extension.core.ex.Extend.addMesg_All("vote.cy.end",playerplayer.name,type+" "+(Blank(name)?"":name));
                    }
					require = 0;
					Force_End();
				} else {
                    playerplayer.sendMessage(localeUtil.getinput("authority.no"));
                }
				break;
			// Vote Compulsory refusal
			case "cn" :
				if (Authority_control(playerplayer,"votec")) {
					if (isteam) {
                        endNomsg = () -> extension.core.ex.Extend.addMesg_Team(team,"vote.cn.end",playerplayer.name,type+" "+(Blank(name)?"":name));
                    } else {
                        endNomsg = () -> extension.core.ex.Extend.addMesg_All("vote.cn.end",playerplayer.name,type+" "+(Blank(name)?"":name));
                    }
					playervote = 0;
					Force_End();
				} else {
                    playerplayer.sendMessage(localeUtil.getinput("authority.no"));
                }
				break;
			default:
				break;
		}
	}


	private void Preprocessing() {
		LocaleUtil localeUtil = Maps.getPlayer_Data(player.uuid).Info;
		// 预处理
		switch(type){
			case "gameover" :
				Normal_distribution();
				break;
			case "ff" :
				Team_only();
				break;
			case "host" :
				if (!(Lists.getMaps_List().size() >= Integer.parseInt(name))) {
                    player.sendMessage(localeUtil.getinput("vote.host.maps.err",name));
                } else {
                    Normal_distribution();
                }
				break;
			case "kick" :
				target = playerGroup.find(p -> p.name.equals(name));
				if(target == null) {
                    player.sendMessage(localeUtil.getinput("vote.kick.err",name));
                } else {
					if (target.isAdmin) {
                        player.sendMessage(localeUtil.getinput("vote.err.admin",name));
                    } else {
                        Normal_distribution();
                    }
				}	
				break;
			case "skipwave" :
				if (state.rules.pvp) {
                    player.sendMessage(localeUtil.getinput("vote.wave.fail"));
                } else {
                    Normal_distribution();
                }
				break;		
			case "admin" :
				if(Config.Vote_Admin) {
                    if(player.isAdmin) {
                        Management_only();
                    } else {
                        player.sendMessage(localeUtil.getinput("admin.no"));
                    }
                } else {
                    player.sendMessage(localeUtil.getinput("vote.admin.no"));
                }
				break;
			default :
				player.sendMessage(localeUtil.getinput("vote.end.err",type+" "+(Blank(name)?"":name)));
				break;
		}
	}

	// 正常投票

	private void Normal_distribution() {
		if(Config.Login_Radical) {
			for (Player it : Vars.playerGroup.all()) {
                if (Maps.getPlayer_Data(it.uuid).Authority > 0) {
                    temp++;
                }
            }
		} else {
            temp = playerGroup.size();
        }
		endNomsg = () -> extension.core.ex.Extend.addMesg_All("vote.done.no",type+" "+(Blank(name)?"":name),playervote,temp);
		endYesMsg = () -> extension.core.ex.Extend.addMesg_All("vote.ok");
		Start(() -> extension.core.ex.Extend.addMesg_All("vote.start",player.name,type+" "+(Blank(name)?"":name)));
	}

	// 团队投票

	private void Team_only() {
		for (Player it : Vars.playerGroup.all()) {
            if (it.getTeam().equals(team)) {
                temp++;
            }
        }
		require = temp;
		endNomsg = () -> extension.core.ex.Extend.addMesg_Team(team,"vote.done.no",type+" "+(Blank(name)?"":name),playervote,temp);
		endYesMsg = () -> extension.core.ex.Extend.addMesg_Team(team,"vote.ok");
		Start(() -> extension.core.ex.Extend.addMesg_Team(team,"vote.start",player.name,type+" "+(Blank(name)?"":name)));
	}

	// 管理投票

	private void Management_only() {
		for (Player it : Vars.playerGroup.all()) {
            if(it.isAdmin) {
                temp++;
            }
        }
		if (temp > 5) {
			require = temp;
			//Start();
		} else {
            player.sendMessage(Maps.getPlayer_Data(player.uuid).Info.getinput("vote.admin.no"));
        }
	}


	private void Start(Runnable run){
		if(temp == 1){
			player.sendMessage(Maps.getPlayer_Data(player.uuid).Info.getinput("vote.no1"));
			require = 1;
		} else if(temp <= 3) {
            require = 2;
        } else {
            require = (int) Math.ceil((double) temp / 2);
        }

		countDown=Data.service.scheduleAtFixedRate(
			new Runnable() {
			@Override
			public void run() {
				reciprocal = reciprocal-10;
				extension.core.ex.Extend.addMesg_All("vote.ing",reciprocal);
			}
		},10,10,TimeUnit.SECONDS);

		voteTime=Data.service.schedule(
			new Runnable() {
			@Override
			public void run() {
				countDown.cancel(true);
				End();
			}
		},58,TimeUnit.SECONDS);
		// 剩余时间
		reciprocal=60;
		// 正在投票
		sted = false;
		playerList.add(player.uuid);
		playervote++;
		if (playervote >= require) {
            Force_End();
        } else {
            run.run();
        }
	}


	private void End() {
		if (playervote >= require) {
			endYesMsg.run();	
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
            endNomsg.run();
        }
		playerList.clear();
		isteam = false;
		sted = true;
		// 归零.jpg
		temp = 0;
		playervote = 0;
		// 释放内存
		name = null;
		endNomsg = null;
		endYesMsg = null;
		countDown=null;
		voteTime=null;
		Data.vote = null;
	}

	private void kick() {
		extension.core.ex.Extend.addMesg_All("kick.done",name);
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
			player.sendMessage(Maps.getPlayer_Data(player.uuid).Info.getinput("host.mode",data[2]));
			return;
		}
		final Gamemode gamemode = mode;
		Call.sendMessage(Maps.getPlayer_Data(player.uuid).Info.getinput("host.re"));
		extension.core.ex.Extend.loadmaps(true, () -> world.loadMap(result, result.applyRules(gamemode)),gamemode);
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
		for (Player it : Vars.playerGroup.all()) {
            if (it.getTeam().equals(team)) {
                killTiles(it.getTeam());
                it.kill();
                it.setTeam(Team.derelict);
            }
        }

	}


	private void Inspect_End() {
		if (playervote >= require) {
			countDown.cancel(true);
			voteTime.cancel(true);
			End();
		}
	}


	private void Force_End() {
		countDown.cancel(true);
		voteTime.cancel(true);
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