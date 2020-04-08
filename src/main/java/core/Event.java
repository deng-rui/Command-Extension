package extension.core;

import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TimeZone;
//Java

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

import static mindustry.Vars.maps;
import static mindustry.Vars.state;
import static mindustry.Vars.world;
import static mindustry.Vars.netServer;
import static mindustry.Vars.playerGroup;
import static mindustry.core.NetClient.onSetRules;
//Mindustry-Static

import extension.data.db.PlayerData;
import extension.data.global.Config;
import extension.data.global.Lists;
import extension.data.global.Maps;
import extension.util.translation.Google;
import extension.util.Log;
//GA-Exted

import static extension.core.Extend.loadmaps;
import static extension.core.Initialization.Follow_up_Initialization;
import static extension.data.db.Player.savePlayer;
import static extension.data.json.Json.getData;
import static extension.net.HttpRequest.doGet;
import static extension.util.alone.BadWord.*;
import static extension.util.DateUtil.getLocalTimeFromUTC;
import static extension.util.LocaleUtil.getinput;
import static extension.util.String_filteringUtil.*;
//Static

import com.alibaba.fastjson.JSONObject;
//Json

public class Event {
	// 

	private static final java.util.Map<Integer, Integer> Building_number = Collections.synchronizedMap(new HashMap<Integer, Integer>());

	public static void Main() {

		// 服务器加载完成时
		Events.on(ServerLoadEvent.class, e-> {
			netServer.admins.addChatFilter((player, message) -> {
				return replaceBadWord(message,2,"*");
			});

			netServer.assigner = ((player, players) -> {
				if (Vars.state.rules.pvp) {
					Teams.TeamData re = (Teams.TeamData)Vars.state.teams.getActive().min(data -> {
						int count = 0;
						for (final Player other : players)
							if (other.getTeam() == data.team && other != player)
								count++;
						if (!data.hasCore())count = 256;
						return (float)count;
					});
					return (null == re) ? null : re.team;
				}
				return Vars.state.rules.defaultTeam;
			});
			// linglan

			Follow_up_Initialization();
			// 部分加载需要服务器加载完毕 例如maps
		});

		// 连接时
		Events.on(PlayerConnect.class, e -> {
			// 中英分检测  过滤屏蔽词
			Set<String> set = (Set<String>)BadWord(removeAll_EN(e.player.name));
			if (0 < set.size())
				Call.onKick(e.player.con, getinput("Sensitive.Thesaurus.join.kick",set.iterator().next()));
			Set<String> set1 = (Set<String>)BadWord(removeAll_CN(e.player.name));
			if (0 < set1.size())
				Call.onKick(e.player.con, getinput("Sensitive.Thesaurus.join.kick",set1.iterator().next()));		
		});

		// 加入服务器时
		Events.on(PlayerJoin.class, e -> {
			if(Config.Login && Config.Login_Radical) {
				if(!Maps.Player_Data_boolean(e.player.uuid)) {
					Call.onInfoToast(e.player.con,getinput("join.tourist",getLocalTimeFromUTC(0,1)),30f);
					PlayerData playerdata = new PlayerData(e.player.uuid,e.player.name,0);
					Maps.setPlayer_Data(e.player.uuid,playerdata);
				}
				if(Maps.getPlayer_Data(e.player.uuid).Authority == 0) {
					// 设置队伍 登陆
					e.player.kill();
					e.player.setTeam(Team.derelict);
					return;
				}	
				PlayerData playerdata = Maps.getPlayer_Data(e.player.uuid);
				if (e.player.isAdmin) playerdata.Authority = 2;
				playerdata.Online = true;
				playerdata.Joincount++;
				playerdata.Jointime = getLocalTimeFromUTC();
				Call.onInfoToast(e.player.con,getinput("join.start",getLocalTimeFromUTC(playerdata.GMT,playerdata.Time_format)),40f);
			} else {
				if(!Maps.Player_Data_boolean(e.player.uuid)) {
					PlayerData playerdata = new PlayerData(e.player.uuid,e.player.name,1);
					Maps.setPlayer_Data(e.player.uuid,playerdata);
					Call.onInfoMessage(e.player.con,getinput("gc"));
        			//Call.onInfoPopup(e.player.con,"info",30f,3,10,1,10,10);
					return;
				}
				PlayerData playerdata = Maps.getPlayer_Data(e.player.uuid);
				if (e.player.isAdmin) playerdata.Authority = 2;
				playerdata.Online = true;
				playerdata.Joincount++;
				playerdata.Jointime = getLocalTimeFromUTC();
				Call.onInfoToast(e.player.con,getinput("join.start",getLocalTimeFromUTC(playerdata.GMT,playerdata.Time_format)),40f);
			}
			if(Config.Login_IP) 
				PlayerData.playerip(Maps.getPlayer_Data(e.player.uuid),e.player,Vars.netServer.admins.getInfo(e.player.uuid).lastIP);
			state.rules.playerDamageMultiplier = 0f;
			onSetRules(state.rules);
			//Call.onPlayerDeath(e.player);
		});

		// 发送消息时
		Events.on(PlayerChatEvent.class, e -> {
			// 自动翻译
			Google googletranslation = new Google();
			if (!"/".equals(e.message.charAt(0))) {
			boolean valid = e.message.matches("\\w+");
			JSONObject date = getData("mods/GA/Setting.json");
			// 检查是否启用翻译
			boolean translateo = (boolean) date.get("translateo");
				if (!valid && translateo) {
					final String translationa = googletranslation.translate(e.message,"en");
					Call.sendMessage("["+e.player.name+"]"+"[green] : [] "+translationa+"   -From Google Translator");
				}
			}

			// 去除语言检测
			if((int)Maps.getPlayer_Data(e.player.uuid).Authority > 0) {
				if(String.valueOf(e.message).equalsIgnoreCase("y")) {
					if(Vote.sted)
						e.player.sendMessage(getinput("vote.noy"));
					else {
						if (Vote.playerlist.contains(e.player.uuid)) {
							e.player.sendMessage(getinput("vote.rey"));
						} else {
							if (Vote.isteam)
								if (!(e.player.getTeam().equals(Vote.team)))
									return;
							Vote.playerlist.add(e.player.uuid);
							e.player.sendMessage(getinput("vote.y"));
							new Vote();
						}
					}
				}	
			}

			PlayerData playerdata = Maps.getPlayer_Data(e.player.uuid);
			playerdata.Lastchat = getLocalTimeFromUTC(playerdata.GMT);
		});

		// :(
		Events.on(Trigger.update, () -> {
			for(Player player : playerGroup.all())
				if (player.getTeam() != Team.derelict && player.getTeam().cores().isEmpty()) {
					player.kill();
					killTiles(player.getTeam());
					player.setTeam(Team.derelict);
				}
		});

		// 建造时
		Events.on(BlockBuildEndEvent.class, e -> {

			if (!e.breaking && e.player != null && e.player.buildRequest() != null && !Vars.state.teams.get(e.player.getTeam()).cores.isEmpty() && e.tile != null && e.player.buildRequest() != null) {
				// 桥
				Block block = e.player.buildRequest().block;
				if (block == Blocks.itemBridge || block == Blocks.phaseConveyor || block == Blocks.bridgeConduit || block == Blocks.phaseConduit) return;
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
						if(Building_number.get(team) >= Config.Building_Warning_quantity) e.player.sendMessage(getinput("Building_Warning.quantity",Building_number.get(team)));
						if(Building_number.get(team) >= Config.Building_Reject_quantity) {
							Call.onTileDestroyed(e.tile);
							e.player.sendMessage(getinput("Building_Reject.quantity"));
							return;
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
				if (block == Blocks.itemBridge || block == Blocks.phaseConveyor || block == Blocks.bridgeConduit || block == Blocks.phaseConduit) return;
				if (block == Blocks.conveyor || block == Blocks.titaniumConveyor || block == Blocks.armoredConveyor) return;
				if (block == Blocks.conduit || block == Blocks.pulseConduit || block == Blocks.platedConduit) return;
				if (block == Blocks.junction || block == Blocks.router || block == Blocks.distributor || block == Blocks.sorter || block == Blocks.invertedSorter || block == Blocks.overflowGate || block == Blocks.underflowGate) return;
				if (block == Blocks.copperWall || block == Blocks.copperWallLarge || block == Blocks.titaniumWall || block == Blocks.titaniumWallLarge || block == Blocks.plastaniumWall || block == Blocks.doorLarge || block == Blocks.door || block == Blocks.plastaniumWallLarge || block == Blocks.shockMine || block == Blocks.surgeWallLarge || block == Blocks.thoriumWallLarge || block == Blocks.thoriumWall || block == Blocks.phaseWall || block == Blocks.surgeWall || block == Blocks.phaseWallLarge) return;	
				if (block == Blocks.battery || block == Blocks.batteryLarge || block == Blocks.powerNode || block == Blocks.powerNodeLarge || block == Blocks.surgeTower || block == Blocks.diode) return;
				PlayerData playerdata = Maps.getPlayer_Data(player.uuid);
				playerdata.Dismantledcount++;
				if(Config.Building_Restriction) {
					int team = player.getTeam().id;
					if(!Building_number.containsKey(team)) 
						return;
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
					if(playerdata.Authority == 0) return;
					playerdata.Deadcount++;
				}
			}

			if (playerGroup != null && playerGroup.size() > 0) {
				for (int i = 0; i < playerGroup.size(); i++) {
					Player player = playerGroup.all().get(i);
					PlayerData playerdata = Maps.getPlayer_Data(player.uuid);
					if (!state.teams.get(player.getTeam()).cores.isEmpty()){
						if(playerdata.Authority == 0) return;
						playerdata.Killcount++;
					}
				}
			}
		});

		Events.on(UnitCreateEvent.class, e-> {	
			if(Config.Soldier_Restriction) {
				int count = Vars.unitGroup.count(ex -> ex.getTeam().equals(e.unit.getTeam()));
				if (count >= Config.Soldier_Warning_quantity) 
					for (Player it : Vars.playerGroup.all()) 
						if (it.getTeam().equals(e.unit.getTeam())) 
							it.sendMessage(getinput("Soldier_Warning.quantity",count));
				if (count >= Config.Soldier_Reject_quantity) {
					for (Player it : Vars.playerGroup.all()) 
						if (it.getTeam().equals(e.unit.getTeam())) 
							it.sendMessage(getinput("Soldier_Reject.quantity"));
					e.unit.kill();
					return;
				}
			}
				
		});

		// EXPION
		Events.on(ValidateException.class, e -> {
			Call.onWorldDataBegin(e.player.con);
			Vars.netServer.sendWorldData(e.player);
			e.player.sendMessage(getinput("error"));
		});

		// 游戏结束时
		Events.on(GameOverEvent.class, e -> {
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
						Call.onInfoMessage(getinput("gameover.game",data[0],data[1],data[2]));
						loadmaps(true, () -> world.loadMap(map, map.applyRules(gamemode)),gamemode);
					}
				}
			}

			Building_number.clear();
			//info("Selected next map to be {0}.", map.name());

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
							if(playerdata.Authority>0)
								playerdata.Pvpwincount++;
						} else {
							if(playerdata.Authority>0)
								playerdata.Pvplosecount++;
						}
					}
				}
			}
		});

		// 退出时
		Events.on(PlayerLeave.class, e -> {
			final PlayerData playerdata = Maps.getPlayer_Data(e.player.uuid);
			playerdata.Backtime = getLocalTimeFromUTC();
			playerdata.Breakcount++;
			playerdata.Online 	= false;
			final long time = playerdata.Backtime-playerdata.Jointime;
			playerdata.Playtime = playerdata.Playtime+time;
			savePlayer(playerdata,playerdata.User);
		});
	}

	private static void killTiles(Team team){
		for(int x = 0; x < world.width(); x++)
			for(int y = 0; y < world.height(); y++){
				Tile tile = world.tile(x, y);
				if(tile.entity != null && tile.getTeam() == team){
					Time.run(Mathf.random(60f * 6), tile.entity::kill);
				}
			}
	}

		//Call.onInfoToast(player.con,getinput("join.tourist",String.valueOf(TimeZone.getTimeZone((String)doGet("http://ip-api.com/line/"+Vars.netServer.admins.getInfo(player.uuid).lastIP+"?fields=timezone")).getRawOffset())),20f);

}