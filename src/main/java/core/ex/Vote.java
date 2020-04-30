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
import extension.core.ex.Extend;

import static extension.core.ex.Extend.authorityControl;
import static extension.util.IsUtil.isBlank;
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
	private Runnable endNoMsg;
	public static Team team;
	// 投票状态
	public static boolean sted = true;
	// 队伍锁定 /ff
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
		preprocessing();
	}

	public Vote(Player player, String type, Team team){
		this.player = player;
		this.type = type.toLowerCase();
		Vote.team = team;
		isteam = true;
		preprocessing();
	}

	public Vote(Player player, String type){
		this.player = player;
		this.type = type.toLowerCase();
		preprocessing();
	}


	public void toVote(Player playerplayer, String playerpick) {
		LocaleUtil localeUtil = Maps.getPlayerData(playerplayer.uuid).info;
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
				inspectEnd();
				break;
			// Vote Compulsory passage
			case "cy" :
				if (authorityControl(playerplayer,"votec")) {
					if (isteam) {
						endYesMsg = () -> Extend.addMesgTeam(team,"vote.cy.end",playerplayer.name,type+" "+(isBlank(name)?"":name));
                    } else {
                        endYesMsg = () -> Extend.addMesgAll("vote.cy.end",playerplayer.name,type+" "+(isBlank(name)?"":name));
                    }
					require = 0;
					forceEnd();
				} else {
                    playerplayer.sendMessage(localeUtil.getinput("authority.no"));
                }
				break;
			// Vote Compulsory refusal
			case "cn" :
				if (authorityControl(playerplayer,"votec")) {
					if (isteam) {
                        endNoMsg = () -> Extend.addMesgTeam(team,"vote.cn.end",playerplayer.name,type+" "+(isBlank(name)?"":name));
                    } else {
                        endNoMsg = () -> Extend.addMesgAll("vote.cn.end",playerplayer.name,type+" "+(isBlank(name)?"":name));
                    }
					playervote = 0;
					forceEnd();
				} else {
                    playerplayer.sendMessage(localeUtil.getinput("authority.no"));
                }
				break;
			default:
				break;
		}
	}


	private void preprocessing() {
		LocaleUtil localeUtil = Maps.getPlayerData(player.uuid).info;
		// 预处理
		switch(type){
			case "gameover" :
				normalDistribution();
				break;
			case "ff" :
				teamOnly();
				break;
			case "host" :
				if (Lists.getMapsList().size() >= Integer.parseInt(name)) {
					normalDistribution();
                } else {
					player.sendMessage(localeUtil.getinput("vote.host.maps.err",name));
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
						normalDistribution();
					}
				}	
				break;
			case "skipwave" :
				if (state.rules.pvp) {
                    player.sendMessage(localeUtil.getinput("vote.wave.fail"));
                } else {
                    normalDistribution();
                }
				break;		
			case "admin" :
				if(Config.VOTE_ADMIN) {
                    if(player.isAdmin) {
                        managementOnly();
                    } else {
                        player.sendMessage(localeUtil.getinput("admin.no"));
                    }
                } else {
                    player.sendMessage(localeUtil.getinput("vote.admin.no"));
                }
				break;
			default :
				player.sendMessage(localeUtil.getinput("vote.end.err",type+" "+(isBlank(name)?"":name)));
				break;
		}
	}

	// 正常投票

	private void normalDistribution() {
		if(Config.LOGIN_RADICAL) {
			for (Player it : Vars.playerGroup.all()) {
                if (Maps.getPlayerData(it.uuid).authority > 0) {
                    temp++;
                }
            }
		} else {
            temp = playerGroup.size();
        }
		endNoMsg = () -> Extend.addMesgAll("vote.done.no",type+" "+(isBlank(name)?"":name),playervote,temp);
		endYesMsg = () -> Extend.addMesgAll("vote.ok");
		start(() -> Extend.addMesgAll("vote.start",player.name,type+" "+(isBlank(name)?"":name)));
	}

	// 团队投票

	private void teamOnly() {
		for (Player it : Vars.playerGroup.all()) {
            if (it.getTeam().equals(team)) {
                temp++;
            }
        }
		require = temp;
		endNoMsg = () -> Extend.addMesgTeam(team,"vote.done.no",type+" "+(isBlank(name)?"":name),playervote,temp);
		endYesMsg = () -> Extend.addMesgTeam(team,"vote.ok");
		start(() -> Extend.addMesgTeam(team,"vote.start",player.name,type+" "+(isBlank(name)?"":name)));
	}

	// 管理投票

	private void managementOnly() {
		for (Player it : Vars.playerGroup.all()) {
            if(it.isAdmin) {
                temp++;
            }
        }
		if (temp > 5) {
			require = temp;
			//Start();
		} else {
            player.sendMessage(Maps.getPlayerData(player.uuid).info.getinput("vote.admin.no"));
        }
	}


	private void start(Runnable run){
		if(temp == 1){
			player.sendMessage(Maps.getPlayerData(player.uuid).info.getinput("vote.no1"));
			require = 1;
		} else if(temp <= 3) {
            require = 2;
        } else {
            require = (int) Math.ceil((double) temp / 2);
        }

		countDown=Data.SERVICE.scheduleAtFixedRate(
			new Runnable() {
			@Override
			public void run() {
				reciprocal = reciprocal-10;
				Extend.addMesgAll("vote.ing",reciprocal);
			}
		},10,10,TimeUnit.SECONDS);

		voteTime=Data.SERVICE.schedule(
			new Runnable() {
			@Override
			public void run() {
				countDown.cancel(true);
				end();
			}
		},58,TimeUnit.SECONDS);
		// 剩余时间
		reciprocal=60;
		// 正在投票
		sted = false;
		playerList.add(player.uuid);
		playervote++;
		if (playervote >= require) {
            forceEnd();
        } else {
            run.run();
        }
	}


	private void end() {
		if (playervote >= require) {
			this.endYesMsg.run();
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
				default:
					break;;
			}
		} else {
            endNoMsg.run();
        }
		playerList.clear();
		isteam = false;
		sted = true;
		// 归零.jpg
		temp = 0;
		playervote = 0;
		// 释放内存
		name = null;
		endNoMsg = null;
		endYesMsg = null;
		countDown=null;
		voteTime=null;
		Data.VOTE = null;
	}

	private void kick() {
		Extend.addMesgAll("kick.done",name);
		target.con.kick(KickReason.kick);
	}


	private void host() {
		List<String> mapsList = (List<String>)Lists.getMapsList();
		String [] data = mapsList.get(Integer.parseInt(name)).split("\\s+");
		Map result = maps.all().find(map -> map.name().equalsIgnoreCase(data[0].replace('_', ' ')) || map.name().equalsIgnoreCase(data[0]));
		Gamemode mode = Gamemode.survival;
		try{
			mode = Gamemode.valueOf(data[2]);
		}catch(IllegalArgumentException ex){
			player.sendMessage(Maps.getPlayerData(player.uuid).info.getinput("host.mode",data[2]));
			return;
		}
		final Gamemode gamemode = mode;
		Call.sendMessage(Maps.getPlayerData(player.uuid).info.getinput("host.re"));
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


	private void inspectEnd() {
		if (playervote >= require) {
			countDown.cancel(true);
			voteTime.cancel(true);
			end();
		}
	}


	private void forceEnd() {
		countDown.cancel(true);
		voteTime.cancel(true);
		end();
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