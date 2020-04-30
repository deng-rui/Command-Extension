package extension.core;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.Set;

import arc.Events;
import arc.math.Mathf;
import arc.util.Time;
//Arc

import mindustry.content.Blocks;
import mindustry.entities.type.Player;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.game.Gamemode;
import mindustry.game.EventType.BlockBuildEndEvent;
import mindustry.game.EventType.BuildSelectEvent;
import mindustry.game.EventType.PlayerConnect;
import mindustry.game.EventType.GameOverEvent;
import mindustry.game.EventType.ServerLoadEvent;
import mindustry.game.EventType.PlayerChatEvent;
import mindustry.game.EventType.PlayerJoin;
import mindustry.game.EventType.PlayerLeave;
import mindustry.game.EventType.UnitDestroyEvent;
import mindustry.game.EventType.UnitCreateEvent;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Call;
import mindustry.maps.Map;
import mindustry.net.Administration.PlayerInfo;
import mindustry.net.Packets.KickReason;
import mindustry.net.NetConnection;
import mindustry.net.ValidateException;
import mindustry.Vars;
import mindustry.world.Block;
import mindustry.world.Tile;
//Mindustry

import static arc.util.Log.info;
import static extension.util.DateUtil.getLocalTimeFromU;
import static mindustry.Vars.maps;
import static mindustry.Vars.state;
import static mindustry.Vars.world;
import static mindustry.Vars.netServer;
import static mindustry.Vars.playerGroup;
//Mindustry-Static

import extension.core.ex.Extend;
import extension.core.ex.Vote;
import extension.data.db.PlayerData;
import extension.data.global.Config;
import extension.data.global.Data;
import extension.data.global.Lists;
import extension.data.global.Maps;
import extension.util.translation.Google;
import extension.util.LocaleUtil;
import extension.util.log.Log;
//GA-Exted

import static extension.core.ex.Threads.NewThred_DB;
import static extension.core.ex.Threads.NewThred_SE;
import static extension.data.db.Player.savePlayer;
import static extension.data.json.Json.getData;
import static extension.net.HttpRequest.doGet;
import static extension.util.alone.BadWord.*;
import static extension.util.String_filteringUtil.*;
//Static

import com.alibaba.fastjson.JSONObject;

public class Event {
	// 

	private final java.util.Map<Integer, Integer> Building_number = new ConcurrentHashMap<Integer, Integer>();
	private static final java.util.Map<String, Team> Sava_Team = new ConcurrentHashMap<String, Team>();

	public void register() {

		// 服务器加载完成时
		Events.on(ServerLoadEvent.class, e-> {
			netServer.admins.addChatFilter((player, message) -> {
				String result = replaceBadWord(message,2,"*");
				// 自动翻译
				// 检查是否启用翻译
				if (Maps.getPlayer_Data(player.uuid).translate) {
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
					if (Sava_Team.containsKey(player.uuid)) {
                        return Sava_Team.get(player.uuid);
                    }
					Teams.TeamData re = (Teams.TeamData)Vars.state.teams.getActive().min(data -> {
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
                            Sava_Team.put(player.uuid, re.team);
                        }
                    }
							//*/
					return (null == re) ? null : re.team;
				}
				return Vars.state.rules.defaultTeam;
			});
			// linglan
		});

		// 连接时
		Events.on(PlayerConnect.class, e -> {
			// 中英分检测  过滤屏蔽词
			Set<String> set = (Set<String>)badWord(removeAll_EN(e.player.name));
			if (0 < set.size()) {
                Call.onKick(e.player.con,new LocaleUtil(null).getinput("Sensitive.Thesaurus.join.kick",set.iterator().next()));
            }
			Set<String> set1 = (Set<String>)badWord(removeAll_CN(e.player.name));
			if (0 < set1.size()) {
                Call.onKick(e.player.con,new LocaleUtil(null).getinput("Sensitive.Thesaurus.join.kick",set1.iterator().next()));
            }
		});

		// 加入服务器时
		Events.on(PlayerJoin.class, e -> {
			if(Config.LOGIN_RADICAL) {
				if(!Maps.Player_Data_boolean(e.player.uuid)) {
					PlayerData playerdata = new PlayerData(e.player.uuid,e.player.name,0);
					// 如果不需要时区 我可能会考虑db-ip.com
					NewThred_SE(() -> PlayerData.playerip(playerdata,e.player,Vars.netServer.admins.getInfo(e.player.uuid).lastIP));
					Call.onInfoToast(e.player.con,playerdata.info.getinput("join.tourist",getLocalTimeFromU(0,1)),30f);
					Maps.setPlayer_Data(e.player.uuid,playerdata);
				}
				if(Maps.getPlayer_Data(e.player.uuid).authority == 0) {
					// 设置队伍 登陆
					e.player.kill();
					e.player.setTeam(Team.derelict);
					return;
				}
				PlayerData playerdata = Maps.getPlayer_Data(e.player.uuid);
				if (e.player.isAdmin) {
                    playerdata.authority = 2;
                }
				playerdata.online = true;
				playerdata.joinCount++;
				playerdata.joinTime = getLocalTimeFromU();
				NewThred_DB(() -> savePlayer(playerdata,playerdata.user));
				Call.onInfoToast(e.player.con,playerdata.info.getinput("join.start",getLocalTimeFromU(playerdata.gmt,playerdata.timeFormat)),40f);
			} else {
				if(!Maps.Player_Data_boolean(e.player.uuid)) {
					PlayerData playerdata = new PlayerData(e.player.uuid,e.player.name,1);
					NewThred_SE(() -> PlayerData.playerip(playerdata,e.player,Vars.netServer.admins.getInfo(e.player.uuid).lastIP));
					Call.onInfoMessage(e.player.con,playerdata.info.getinput("gc"));
					Maps.setPlayer_Data(e.player.uuid,playerdata);
					return;
				}
				PlayerData playerdata = Maps.getPlayer_Data(e.player.uuid);
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
			if((int)Maps.getPlayer_Data(e.player.uuid).Authority > 0) {
				String msg = String.valueOf(e.message).toLowerCase();
				if("y".equals(msg) || "n".equals(msg) || "cy".equals(msg) || "cn".equals(msg)) {
					if(!Vote.sted) {
						if (Vote.playerList.contains(e.player.uuid)) {
                            e.player.sendMessage(Maps.getPlayer_Data(e.player.uuid).info.getinput("vote.rey"));
                        } else {
							if (Vote.isteam) {
								if (Vote.team.equals(e.player.getTeam())) {
                                    Data.vote.ToVote(e.player,msg);
                                } else {
                                    e.player.sendMessage(Maps.getPlayer_Data(e.player.uuid).info.getinput("vote.team"));
                                }
							} else {
                                Data.vote.ToVote(e.player,msg);
                            }
						}	
					} else {
                        e.player.sendMessage(Maps.getPlayer_Data(e.player.uuid).info.getinput("vote.noy"));
                    }
				}
			}

			PlayerData playerdata = Maps.getPlayer_Data(e.player.uuid);
			playerdata.lastChat = getLocalTimeFromU(playerdata.gmt);
		});

		// :(
		Events.on(Trigger.update, () -> {
			for(Player player : playerGroup.all()) {
                if (player.getTeam() != Team.derelict && player.getTeam().cores().isEmpty()) {
                    Sava_Team.remove(player.uuid);
                    player.kill();
                    killTiles(player.getTeam());
                    player.setTeam(Team.derelict);
                    player.sendMessage(Maps.getPlayer_Data(player.uuid).Info.getinput("Gameover.Team"));
                }
            }
		});

		// 建造时
		Events.on(BlockBuildEndEvent.class, e -> {

			if (!e.breaking && e.player != null && e.player.buildRequest() != null && !Vars.state.teams.get(e.player.getTeam()).cores.isEmpty() && e.tile != null && e.player.buildRequest() != null) {
				// 桥
				Block block = e.player.buildRequest().block;
				if (block == Blocks.itemBridge || block == Blocks.phaseConveyor || block == Blocks.bridgeConduit || block == Blocks.phaseConduit) {
                    return;
                }
				PlayerData playerdata = Maps.getPlayer_Data(e.player.uuid);
				// 传送带
				if (block == Blocks.conveyor || block == Blocks.titaniumConveyor || block == Blocks.armoredConveyor) {
					playerdata.Cumulative_build++;
				}
				// 管道
				else if (block == Blocks.conduit || block == Blocks.pulseConduit || block == Blocks.platedConduit) {
					playerdata.Pipe_build++;
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
					if(Config.Building_Restriction) {
						int team = e.player.getTeam().id;
						if(!Building_number.containsKey(team)) {
							Building_number.put(team,1);
							return;
						}
						if(Building_number.get(team) >= Config.Building_Reject_quantity) {
							Call.onTileDestroyed(e.tile);
							if (Data.ismsg) {
								NewThred_SE(() -> Extend.addMesg_Team(e.player.getTeam(),"Building_Reject.quantity"));
								Data.ismsg = false;
							}
							return;
						}
						if(Building_number.get(team) >= Config.Building_Warning_quantity) {
                            if (Data.ismsg) {
                                Extend.addMesg_Team(e.player.getTeam(),"Building_Warning.quantity",Building_number.get(team));
                                Data.ismsg = false;
                            }
                        }
						int temp = ((int)Building_number.get(team))+1;
						Building_number.put(team,temp);
					}
					playerdata.Buildcount++;
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
				PlayerData playerdata = Maps.getPlayer_Data(player.uuid);
				playerdata.Dismantledcount++;
				if(Config.Building_Restriction) {
					int team = player.getTeam().id;
					if(!Building_number.containsKey(team)) {
                        return;
                    }
					int temp = ((int)Building_number.get(team))-1;
					Building_number.put(team,temp);
				}
			}
			
		});

		// ?
		Events.on(UnitDestroyEvent.class, e -> {
			if (e.unit instanceof Player) {
				Player player = (Player) e.unit;
				PlayerData playerdata = Maps.getPlayer_Data(player.uuid);
				if (!state.teams.get(player.getTeam()).cores.isEmpty()){
					if(playerdata.Authority == 0) {
                        return;
                    }
					playerdata.Deadcount++;
				}
			}

			if (playerGroup != null && playerGroup.size() > 0) {
				for (int i = 0; i < playerGroup.size(); i++) {
					Player player = playerGroup.all().get(i);
					PlayerData playerdata = Maps.getPlayer_Data(player.uuid);
					if (!state.teams.get(player.getTeam()).cores.isEmpty()){
						if(playerdata.Authority == 0) {
                            return;
                        }
						playerdata.Killcount++;
					}
				}
			}
		});

		Events.on(UnitCreateEvent.class, e-> {	
			if (Config.Soldier_Restriction) {
					int count = Vars.unitGroup.count(ex -> ex.getTeam().equals(e.unit.getTeam()));
					if (count >= Config.Soldier_Reject_quantity) {
						if (Data.ismsg) {
							Extend.addMesg_Team(e.unit.getTeam(),"Soldier_Reject.quantity");
							Data.ismsg = false;
						}
						e.unit.kill();
						return;
					}
					if (count >= Config.Soldier_Warning_quantity) {
                        if (Data.ismsg) {
                            Extend.addMesg_Team(e.unit.getTeam(),"Soldier_Warning.quantity",count);
                            Data.ismsg = false;
                        }
                    }
			}
		});

		// EXPION
		Events.on(ValidateException.class, e -> {
			Call.onWorldDataBegin(e.player.con);
			Vars.netServer.sendWorldData(e.player);
			e.player.sendMessage(Maps.getPlayer_Data(e.player.uuid).Info.getinput("error"));
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
						PlayerData playerdata = Maps.getPlayer_Data(player.uuid);
						if (player.getTeam().name.equals(e.winner.name)) {
							if(playerdata.Authority>0) {
                                playerdata.Pvpwincount++;
                            }
						} else {
							if(playerdata.Authority>0) {
                                playerdata.Pvplosecount++;
                            }
						}
					}
				}
			}

			Sava_Team.clear();

			Map map = maps.getNextMap(world.getMap());
			if(map != null) {
				List<String> MapsList = (List<String>)Lists.getMaps_List();
				for(int i = 0; i < MapsList.size(); i++){
					String [] data = MapsList.get(i).split("\\s+");
					if(map.name().equalsIgnoreCase(data[0].replace('_', ' ')) || map.name().equalsIgnoreCase(data[0])) {
						Gamemode mode = Gamemode.survival;
						try{
							mode = Gamemode.valueOf(data[2]);
						}catch(IllegalArgumentException ex){
						}
						final Gamemode gamemode = mode;
						Extend.addMesg_All("Gameover.Game",data[0],data[1],data[2]);
						Extend.loadmaps(true, () -> world.loadMap(map, map.applyRules(gamemode)),gamemode);
					}
				}
			}
			info("Selected next map to be {0}.", map.name());	

			Building_number.clear();
			
		});

		// 退出时
		Events.on(PlayerLeave.class, e -> {
			final PlayerData playerdata = Maps.getPlayer_Data(e.player.uuid);
			playerdata.Backtime = getLocalTimeFromUTC();
			playerdata.Breakcount++;
			playerdata.Online = false;
			final long time = playerdata.Backtime-playerdata.Jointime;
			playerdata.Playtime = playerdata.Playtime+time;
			if (playerdata.Login) {
                NewThred_DB(() -> savePlayer(playerdata,playerdata.User));
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
		Data.service.schedule(() -> {
			if (playerGroup.size() > 2) {
                if (state.rules.pvp) {
                    if (Config.Login_Radical) {
                        for (Player it : Vars.playerGroup.all()) {
                            if (!state.teams.get(Team.all()[it.getTeam().id]).cores.isEmpty()) {
                                if (Maps.getPlayer_Data(it.uuid).Authority > 0) {
                                    Sava_Team.put(it.uuid, it.getTeam());
                                }
                            }
                        }
                    } else {
                        for (Player it : Vars.playerGroup.all()) {
                            if (!state.teams.get(Team.all()[it.getTeam().id]).cores.isEmpty()) {
                                Sava_Team.put(it.uuid, it.getTeam());
                            }
                        }
                    }
                }
            }
		},8,TimeUnit.SECONDS);
	}

		//Call.onInfoToast(player.con,getinput("join.tourist",String.valueOf(TimeZone.getTimeZone((String)doGet("http://ip-api.com/line/"+Vars.netServer.admins.getInfo(player.uuid).lastIP+"?fields=timezone")).getRawOffset())),20f);

}