package com.github.dr.extension.core;

import arc.Events;
import arc.math.Mathf;
import arc.util.Time;
import com.github.dr.extension.core.ex.Extend;
import com.github.dr.extension.core.ex.Vote;
import com.github.dr.extension.data.db.PlayerData;
import com.github.dr.extension.data.global.Config;
import com.github.dr.extension.data.global.Data;
import com.github.dr.extension.data.global.Lists;
import com.github.dr.extension.data.global.Maps;
import com.github.dr.extension.util.LocaleUtil;
import com.github.dr.extension.util.translation.Google;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.type.Player;
import mindustry.game.EventType.*;
import mindustry.game.Gamemode;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.gen.Call;
import mindustry.maps.Map;
import mindustry.net.ValidateException;
import mindustry.world.Block;
import mindustry.world.Tile;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static arc.util.Log.info;
import static com.github.dr.extension.core.ex.Threads.newThredDb;
import static com.github.dr.extension.core.ex.Threads.newThredSe;
import static com.github.dr.extension.data.db.Player.savePlayer;
import static com.github.dr.extension.util.DateUtil.getLocalTimeFromU;
import static com.github.dr.extension.util.StringFilteringUtil.removeAllCn;
import static com.github.dr.extension.util.StringFilteringUtil.removeAllEn;
import static com.github.dr.extension.util.alone.BadWord.badWord;
import static com.github.dr.extension.util.alone.BadWord.replaceBadWord;
import static mindustry.Vars.*;

//Arc
//Mindustry
//Mindustry-Static
//GA-Exted
//Static

public class Event {
	// 

	private final java.util.Map<Integer, Integer> BUILDING_NUMBER = new ConcurrentHashMap<Integer, Integer>();
	private static final java.util.Map<String, Team> SAVA_TEAM = new ConcurrentHashMap<String, Team>();

	public void register() {

		// 服务器加载完成时
		Events.on(ServerLoadEvent.class, e-> {
			netServer.admins.addChatFilter((player, message) -> {
				String result = replaceBadWord(message,2,"*");
				// 自动翻译
				// 检查是否启用翻译
				if (Maps.getPlayerData(player.uuid).translate) {
					boolean valid = message.matches("\\w+");
					if (!valid) {
                        result = new Google().translate(message,"en")+"   -From Google Translator";
                    }
				}
				return result;
			});

			netServer.assigner = ((player, players) -> {
				if (state.rules.pvp) {
					// 若存在 直接读取队伍
					if (SAVA_TEAM.containsKey(player.uuid)) {
                        return SAVA_TEAM.get(player.uuid);
                    }
					Teams.TeamData re = (Teams.TeamData) state.teams.getActive().min(data -> {
						int count = 0;
						for (final Player other : players) {
                            if (other.getTeam() == data.team && other != player) {
                                count++;
                            }
                        }
						if (!data.hasCore()) {
                            count = 256;
                        }
						return (float)count;
					});
					// 查询核心死否存在 防止无核心队伍锁定
					if (!state.teams.get(Team.all()[re.team.id]).cores.isEmpty()) {
                        if (playerGroup.size() >= 1) {
                            SAVA_TEAM.put(player.uuid, re.team);
                        }
                    }
							//*/
					return (null == re) ? null : re.team;
				}
				return state.rules.defaultTeam;
			});
			// linglan
		});

		// 连接时
		Events.on(PlayerConnect.class, e -> {
			// 中英分检测  过滤屏蔽词
			Set<String> set = (Set<String>)badWord(removeAllEn(e.player.name));
			if (0 < set.size()) {
                Call.onKick(e.player.con,new LocaleUtil(null).getinput("Sensitive.Thesaurus.join.kick",set.iterator().next()));
            }
			Set<String> set1 = (Set<String>)badWord(removeAllCn(e.player.name));
			if (0 < set1.size()) {
                Call.onKick(e.player.con,new LocaleUtil(null).getinput("Sensitive.Thesaurus.join.kick",set1.iterator().next()));
            }
		});

		// 加入服务器时
		Events.on(PlayerJoin.class, e -> {
			if(Config.LOGIN_RADICAL) {
				if(!Maps.isPlayerData(e.player.uuid)) {
					PlayerData playerdata = new PlayerData(e.player.uuid,e.player.name,0);
					// 如果不需要时区 我可能会考虑db-ip.com
					newThredSe(() -> PlayerData.playerip(playerdata,e.player, netServer.admins.getInfo(e.player.uuid).lastIP));
					Call.onInfoToast(e.player.con,playerdata.info.getinput("join.tourist",getLocalTimeFromU(0,1)),30f);
					Maps.setPlayerData(e.player.uuid,playerdata);
				}
				if(Maps.getPlayerData(e.player.uuid).authority == 0) {
					// 设置队伍 登陆
					e.player.kill();
					e.player.setTeam(Team.derelict);
					return;
				}
				PlayerData playerdata = Maps.getPlayerData(e.player.uuid);
				if (e.player.isAdmin) {
                    playerdata.authority = 2;
                }
				playerdata.online = true;
				playerdata.joinCount++;
				playerdata.joinTime = getLocalTimeFromU();
				newThredDb(() -> savePlayer(playerdata,playerdata.user));
				Call.onInfoToast(e.player.con,playerdata.info.getinput("join.start",getLocalTimeFromU(playerdata.gmt,playerdata.timeFormat)),40f);
			} else {
				if(!Maps.isPlayerData(e.player.uuid)) {
					PlayerData playerdata = new PlayerData(e.player.uuid,e.player.name,1);
					newThredSe(() -> PlayerData.playerip(playerdata,e.player, netServer.admins.getInfo(e.player.uuid).lastIP));
					Call.onInfoMessage(e.player.con,playerdata.info.getinput("gc"));
					Maps.setPlayerData(e.player.uuid,playerdata);
					return;
				}
				PlayerData playerdata = Maps.getPlayerData(e.player.uuid);
				if (e.player.isAdmin) {
                    playerdata.authority = 2;
                }
				playerdata.online = true;
				playerdata.joinCount++;
				playerdata.joinTime = getLocalTimeFromU();
				Call.onInfoToast(e.player.con,playerdata.info.getinput("join.start",getLocalTimeFromU(playerdata.gmt,playerdata.timeFormat)),40f);
			}
			state.rules.playerDamageMultiplier = 0f;
			Call.onSetRules(state.rules);
		});

		// 发送消息时
		Events.on(PlayerChatEvent.class, e -> {
			if((int)Maps.getPlayerData(e.player.uuid).authority > 0) {
				String msg = String.valueOf(e.message).toLowerCase();
				if("y".equals(msg) || "n".equals(msg) || "cy".equals(msg) || "cn".equals(msg)) {
					if(!Vote.sted) {
						if (Vote.playerList.contains(e.player.uuid)) {
                            e.player.sendMessage(Maps.getPlayerData(e.player.uuid).info.getinput("vote.rey"));
                        } else {
							if (Vote.isteam) {
								if (Vote.team.equals(e.player.getTeam())) {
                                    Data.VOTE.toVote(e.player,msg);
                                } else {
                                    e.player.sendMessage(Maps.getPlayerData(e.player.uuid).info.getinput("vote.team"));
                                }
							} else {
                                Data.VOTE.toVote(e.player,msg);
                            }
						}	
					} else {
                        e.player.sendMessage(Maps.getPlayerData(e.player.uuid).info.getinput("vote.noy"));
                    }
				}
			}

			PlayerData playerdata = Maps.getPlayerData(e.player.uuid);
			playerdata.lastChat = getLocalTimeFromU(playerdata.gmt);
		});

		// :(
		Events.on(Trigger.update, () -> {
			for(Player player : playerGroup.all()) {
                if (player.getTeam() != Team.derelict && player.getTeam().cores().isEmpty()) {
                    SAVA_TEAM.remove(player.uuid);
                    player.kill();
                    killTiles(player.getTeam());
                    player.setTeam(Team.derelict);
                    player.sendMessage(Maps.getPlayerData(player.uuid).info.getinput("Gameover.Team"));
                }
            }
		});

		// 建造时
		Events.on(BlockBuildEndEvent.class, e -> {

			if (!e.breaking && e.player != null && e.player.buildRequest() != null && !state.teams.get(e.player.getTeam()).cores.isEmpty() && e.tile != null && e.player.buildRequest() != null) {
				// 桥
				Block block = e.player.buildRequest().block;
				if (block == Blocks.itemBridge || block == Blocks.phaseConveyor || block == Blocks.bridgeConduit || block == Blocks.phaseConduit) {
                    return;
                }
				PlayerData playerdata = Maps.getPlayerData(e.player.uuid);
				// 传送带
				if (block == Blocks.conveyor || block == Blocks.titaniumConveyor || block == Blocks.armoredConveyor) {
					playerdata.cumulativeBuild++;
				}
				// 管道
				else if (block == Blocks.conduit || block == Blocks.pulseConduit || block == Blocks.platedConduit) {
					playerdata.pipeBuild++;
				}
				// 刷? 连接 路由 分配 分类 反向分类 溢流 反向溢流
				else if (block == Blocks.junction || block == Blocks.router || block == Blocks.distributor || block == Blocks.sorter || block == Blocks.invertedSorter || block == Blocks.overflowGate || block == Blocks.underflowGate) {
					//
				}
				// 过滤墙
				else if (block == Blocks.copperWall || block == Blocks.copperWallLarge || block == Blocks.titaniumWall || block == Blocks.titaniumWallLarge || block == Blocks.plastaniumWall || block == Blocks.doorLarge || block == Blocks.door || block == Blocks.plastaniumWallLarge || block == Blocks.shockMine || block == Blocks.surgeWallLarge || block == Blocks.thoriumWallLarge ||block == Blocks.thoriumWall || block == Blocks.phaseWall || block == Blocks.surgeWall || block == Blocks.phaseWallLarge) {
					//
				}
				// 电池 节点 二极管
				else if (block == Blocks.battery || block == Blocks.batteryLarge || block == Blocks.powerNode || block == Blocks.powerNodeLarge || block == Blocks.surgeTower || block == Blocks.diode) {
					//
				}
				// 其他建筑
				else {
					if(Config.BUILDING_RESTRICTION) {
						int team = e.player.getTeam().id;
						if(!BUILDING_NUMBER.containsKey(team)) {
							BUILDING_NUMBER.put(team,1);
							return;
						}
						if(BUILDING_NUMBER.get(team) >= Config.BUILDING_REJECT_QUANTITY) {
							Call.onTileDestroyed(e.tile);
							if (Data.ISMSG) {
								newThredSe(() -> Extend.addMesgTeam(e.player.getTeam(),"Building_Reject.quantity"));
								Data.ISMSG = false;
							}
							return;
						}
						if(BUILDING_NUMBER.get(team) >= Config.BUILDING_WARNING_QUANTITY) {
                            if (Data.ISMSG) {
                                Extend.addMesgTeam(e.player.getTeam(),"Building_Warning.quantity",BUILDING_NUMBER.get(team));
                                Data.ISMSG = false;
                            }
                        }
						int temp = ((int)BUILDING_NUMBER.get(team))+1;
						BUILDING_NUMBER.put(team,temp);
					}
					playerdata.breakCount++;
				}
				
			}
		});

		// 拆除时
		Events.on(BuildSelectEvent.class, e -> {
			if (e.builder instanceof Player && e.builder.buildRequest() != null && !e.builder.buildRequest().block.name.matches(".*build.*") && e.tile.block() != Blocks.air && e.breaking) {
				Block block = e.builder.buildRequest().block;
				Player player = (Player)e.builder;
				if (block == Blocks.itemBridge || block == Blocks.phaseConveyor || block == Blocks.bridgeConduit || block == Blocks.phaseConduit) {
                    return;
                }
				if (block == Blocks.conveyor || block == Blocks.titaniumConveyor || block == Blocks.armoredConveyor) {
                    return;
                }
				if (block == Blocks.conduit || block == Blocks.pulseConduit || block == Blocks.platedConduit) {
                    return;
                }
				if (block == Blocks.junction || block == Blocks.router || block == Blocks.distributor || block == Blocks.sorter || block == Blocks.invertedSorter || block == Blocks.overflowGate || block == Blocks.underflowGate) {
                    return;
                }
				if (block == Blocks.copperWall || block == Blocks.copperWallLarge || block == Blocks.titaniumWall || block == Blocks.titaniumWallLarge || block == Blocks.plastaniumWall || block == Blocks.doorLarge || block == Blocks.door || block == Blocks.plastaniumWallLarge || block == Blocks.shockMine || block == Blocks.surgeWallLarge || block == Blocks.thoriumWallLarge || block == Blocks.thoriumWall || block == Blocks.phaseWall || block == Blocks.surgeWall || block == Blocks.phaseWallLarge) {
                    return;
                }
				if (block == Blocks.battery || block == Blocks.batteryLarge || block == Blocks.powerNode || block == Blocks.powerNodeLarge || block == Blocks.surgeTower || block == Blocks.diode) {
                    return;
                }
				PlayerData playerdata = Maps.getPlayerData(player.uuid);
				playerdata.dismantledCount++;
				if(Config.BUILDING_RESTRICTION) {
					int team = player.getTeam().id;
					if(!BUILDING_NUMBER.containsKey(team)) {
                        return;
                    }
					int temp = ((int)BUILDING_NUMBER.get(team))-1;
					BUILDING_NUMBER.put(team,temp);
				}
			}
			
		});

		// ?
		Events.on(UnitDestroyEvent.class, e -> {
			if (e.unit instanceof Player) {
				Player player = (Player) e.unit;
				PlayerData playerdata = Maps.getPlayerData(player.uuid);
				if (!state.teams.get(player.getTeam()).cores.isEmpty()){
					if(playerdata.authority == 0) {
                        return;
                    }
					playerdata.deadCount++;
				}
			}

			if (playerGroup != null && playerGroup.size() > 0) {
				for (int i = 0; i < playerGroup.size(); i++) {
					Player player = playerGroup.all().get(i);
					PlayerData playerdata = Maps.getPlayerData(player.uuid);
					if (!state.teams.get(player.getTeam()).cores.isEmpty()){
						if(playerdata.authority == 0) {
                            return;
                        }
						playerdata.killCount++;
					}
				}
			}
		});

		Events.on(UnitCreateEvent.class, e-> {	
			if (Config.SOLDIER_RESTRICTION) {
					int count = Vars.unitGroup.count(ex -> ex.getTeam().equals(e.unit.getTeam()));
					if (count >= Config.SOLDIER_REJECT_QUANTITY) {
						if (Data.ISMSG) {
							Extend.addMesgTeam(e.unit.getTeam(),"Soldier_Reject.quantity");
							Data.ISMSG = false;
						}
						e.unit.kill();
						return;
					}
					if (count >= Config.SOLDIER_WARNING_QUANTITY) {
                        if (Data.ISMSG) {
                            Extend.addMesgTeam(e.unit.getTeam(),"Soldier_Warning.quantity",count);
                            Data.ISMSG = false;
                        }
                    }
			}
		});

		// EXPION
		Events.on(ValidateException.class, e -> {
			Call.onWorldDataBegin(e.player.con);
			netServer.sendWorldData(e.player);
			e.player.sendMessage(Maps.getPlayerData(e.player.uuid).info.getinput("error"));
		});

		// 游戏结束时
		Events.on(GameOverEvent.class, e -> {
			if (state.rules.pvp) {
				int index = 5;
				for (int a = 0; a < 5; a++) {
					if (state.teams.get(Team.all()[index]).cores.isEmpty()) {
						index--;
					}
				}
				if (index == 1) {
					for (int i = 0; i < playerGroup.size(); i++) {
						Player player = playerGroup.all().get(i);
						PlayerData playerdata = Maps.getPlayerData(player.uuid);
						if (player.getTeam().name.equals(e.winner.name)) {
							if(playerdata.authority>0) {
                                playerdata.pvpwinCount++;
                            }
						} else {
							if(playerdata.authority>0) {
                                playerdata.pvploseCount++;
                            }
						}
					}
				}
			}

			SAVA_TEAM.clear();

			Map map = maps.getNextMap(world.getMap());
			if(map != null) {
				List<String> mapsList = (List<String>)Lists.getMapsList();
				for(int i = 0; i < mapsList.size(); i++){
					String [] data = mapsList.get(i).split("\\s+");
					if(map.name().equalsIgnoreCase(data[0].replace('_', ' ')) || map.name().equalsIgnoreCase(data[0])) {
						Gamemode mode = Gamemode.survival;
						try{
							mode = Gamemode.valueOf(data[2]);
						}catch(IllegalArgumentException ex){
						}
						final Gamemode gamemode = mode;
						Extend.addMesgAll("Gameover.Game",data[0],data[1],data[2]);
						Extend.loadmaps(true, () -> world.loadMap(map, map.applyRules(gamemode)),gamemode);
					}
				}
			}
			info("Selected next map to be {0}.", map.name());	

			BUILDING_NUMBER.clear();
			
		});

		// 退出时
		Events.on(PlayerLeave.class, e -> {
			final PlayerData playerdata = Maps.getPlayerData(e.player.uuid);
			playerdata.backTime = getLocalTimeFromU();
			playerdata.breakCount++;
			playerdata.online= false;
			final long time = playerdata.backTime-playerdata.joinTime;
			playerdata.playTime += time;
			if (playerdata.login) {
                newThredDb(() -> savePlayer(playerdata,playerdata.user));
            }
		});
	}

	private static void killTiles(Team team){
		for(int x = 0; x < world.width(); x++) {
            for(int y = 0; y < world.height(); y++){
                Tile tile = world.tile(x, y);
                if(tile.entity != null && tile.getTeam() == team){
                    Time.run(Mathf.random(60f * 6), tile.entity::kill);
                }
            }
        }
	}

	public static void syncTeam() {
		Data.SERVICE.schedule(() -> {
			if (playerGroup.size() > 2) {
                if (state.rules.pvp) {
                    if (Config.LOGIN_RADICAL) {
                        for (Player it : Vars.playerGroup.all()) {
                            if (!state.teams.get(Team.all()[it.getTeam().id]).cores.isEmpty()) {
                                if (Maps.getPlayerData(it.uuid).authority > 0) {
                                    SAVA_TEAM.put(it.uuid, it.getTeam());
                                }
                            }
                        }
                    } else {
                        for (Player it : Vars.playerGroup.all()) {
                            if (!state.teams.get(Team.all()[it.getTeam().id]).cores.isEmpty()) {
                                SAVA_TEAM.put(it.uuid, it.getTeam());
                            }
                        }
                    }
                }
            }
		},8,TimeUnit.SECONDS);
	}

		//Call.onInfoToast(player.con,getinput("join.tourist",String.valueOf(TimeZone.getTimeZone((String)doGet("http://ip-api.com/line/"+Vars.netServer.admins.getInfo(player.uuid).lastIP+"?fields=timezone")).getRawOffset())),20f);

}