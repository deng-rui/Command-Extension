package extension;

import java.util.List;
import java.util.Set;
import java.lang.Math;
//Java

import arc.Core;
import arc.Events;
import arc.math.Mathf;
import arc.util.CommandHandler;
//Arc

import mindustry.entities.type.Player;
import mindustry.gen.Call;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.game.Difficulty;
import mindustry.game.EventType.GameOverEvent;
import mindustry.game.EventType.ServerLoadEvent;
import mindustry.game.EventType.PlayerChatEvent;
import mindustry.game.EventType.PlayerJoin;
import mindustry.game.EventType.PlayerLeave;
import mindustry.game.EventType.UnitDestroyEvent;
import mindustry.net.Administration.PlayerInfo ;
import mindustry.net.Packets.KickReason;
import mindustry.net.NetConnection;
import mindustry.plugin.Plugin;
import mindustry.Vars;
//Mindustry

import static arc.util.Log.info;
import static mindustry.Vars.state;
import static mindustry.Vars.netServer;
import static mindustry.Vars.logic;
import static mindustry.Vars.maps;
import static mindustry.Vars.playerGroup;
//Mindustry-Static

import extension.core.Vote;
import extension.util.LogUtil;
import extension.util.file.FileUtil;
import extension.util.translation.Bing;
import extension.util.translation.Google;
import extension.data.global.Lists;
import extension.data.global.Maps;
import extension.data.global.Strings;
//GA-Exted

import static extension.core.ClientCommandsx.*;
import static extension.core.Event.*;
import static extension.core.Initialization.MapList;
import static extension.core.Initialization.Start_Initialization;
import static extension.core.Initialization.Follow_up_Initialization;
import static extension.data.db.SQLite.Authority_control;
import static extension.data.db.SQLite.SQL_type;
import static extension.data.db.Player.getSQLite_UUID;
import static extension.util.BadWordUtil.*;
import static extension.util.LocaleUtil.getinput;
import static extension.util.String_filteringUtil.*;
//Static

public class Main extends Plugin {

	Google googletranslation = new Google();
	//动态难度
	//PVP限制

	//@SuppressWarnings("unchecked")
	public Main() {

		//Log
		LogUtil.Set("ALL");

		try{
					System.out.println(new Bing().translate("fuck world","zh-Hans"));
				}catch(Exception e){
					LogUtil.warn(e);
				}
		
		//初始化
		Start_Initialization();

		//发言时
		Events.on(PlayerChatEvent.class, e -> {
			String result = PlayerChatEvent_translate(String.valueOf(e.message.charAt(0)),e.message);
			if (null != result)Call.sendMessage("["+e.player.name+"]"+"[green] : [] "+result+"   -From Google Translator");
			//自动翻译
			Set<String> set = (Set<String>)BadWordUtil(removeAll_EN(e.message));
			if (0 < set.size())
				PlayerChatEvent_Sensitive_Thesaurus(e.player, set.iterator().next());
			Set<String> set1 = (Set<String>)BadWordUtil(removeAll_CN(e.message));
			if (0 < set1.size())
				PlayerChatEvent_Sensitive_Thesaurus(e.player, set1.iterator().next());
			//中英分检测
			List<String> Pvpwincount = (List<String>)Maps.getPlayer_Data_SQL_Temp(e.player.uuid);
			if(Maps.getPlayer_power_Data(e.player.uuid)>0) {
				if(!String.valueOf(e.message).equalsIgnoreCase("y"))return;
				if (Vote.playerlist.contains(e.player.uuid)) {
					e.player.sendMessage("vote y");
				} else {
					Vote.playerlist.add(e.player.uuid);
					new Vote();	
				}
			}
		});

		//加入服务器时
		Events.on(PlayerJoin.class, e -> {
			Set<String> set = (Set<String>)BadWordUtil(removeAll_EN(e.player.name));
			if (0 < set.size())
				Call.onKick(e.player.con, getinput("Sensitive.Thesaurus.join.kick",set.iterator().next()));
			Set<String> set1 = (Set<String>)BadWordUtil(removeAll_CN(e.player.name));
			if (0 < set1.size())
				Call.onKick(e.player.con, getinput("Sensitive.Thesaurus.join.kick",set1.iterator().next()));
			//中英分检测
			PlayerJoin_Logins(e.player);
			Maps.setPlayer_Data_Temp(e.player.uuid,"Playtime-start",String.valueOf(System.currentTimeMillis()));
		});

		//退出时
		Events.on(PlayerLeave.class, e -> {
			Maps.removePlayer_Data_Temp(e.player.uuid,"Playtime-start");
		});

		//Gameover
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
						if (player.getTeam().name.equals(e.winner.name)) {
							List<String> Pvpwincount = (List<String>)Maps.getPlayer_Data_SQL_Temp(player.uuid);
							System.out.println(player.uuid);
							if(Maps.getPlayer_power_Data(player.uuid)>0)
								Maps.setPlayer_Data_SQL_Temp(player.uuid,Lists.updatePlayerData(Pvpwincount,SQL_type("Pvpwincount"),String.valueOf(Integer.parseInt(Pvpwincount.get(SQL_type("Pvpwincount")))+1)));
						} else {
							List<String> Pvpwincount = (List<String>)Maps.getPlayer_Data_SQL_Temp(player.uuid);
							System.out.println(player.uuid);
							if(Maps.getPlayer_power_Data(player.uuid)>0)
								Maps.setPlayer_Data_SQL_Temp(player.uuid,Lists.updatePlayerData(Pvpwincount,SQL_type("Pvplosecount"),String.valueOf(Integer.parseInt(Pvpwincount.get(SQL_type("Pvplosecount")))+1)));
						}
					}
				}
			}
		});

		//服务器加载完成时
		Events.on(ServerLoadEvent.class, e-> {
			netServer.admins.addChatFilter((player, message) -> {
				return replaceBadWord(message,2,"*");
			});

			netServer.assigner = ((player, players) -> {
				if (Vars.state.rules.pvp) {
					Teams.TeamData re = (Teams.TeamData)Vars.state.teams.getActive().min(data -> {
						int count = 0;
						for (final Player other : players)if (other.getTeam() == data.team && other != player)count++;
						if (!data.hasCore())count = 256;
						return (float)count;
					});
					return (null == re) ? null : re.team;
				}
				return Vars.state.rules.defaultTeam;
			});
			//linglan

			Follow_up_Initialization();
			//部分加载需要服务器加载完毕 例如maps
		});
		
	}
		
	@Override
	public void registerServerCommands(CommandHandler handler){
		handler.removeCommand("reloadmaps");

		handler.register("reloadmaps", "NOT", (arg) -> {
			int beforeMaps = maps.all().size;
			maps.reload();
			if(maps.all().size > beforeMaps){
				info("&lc{0}&ly new map(s) found and reloaded.", maps.all().size - beforeMaps);
			}else{
				info("&lyMaps reloaded.");
			}
			MapList();
		});

	};

	@Override
	public void registerClientCommands(CommandHandler handler) {
		//handler.removeCommand("help");
		handler.removeCommand("vote");
		handler.removeCommand("votekick");

		/*
		handler.<Player>register("help", "[page]", "Displays this command list !", (args, player) -> {
			if(args.length > 0 && !Strings.canParseInt(args[0])){
				player.sendMessage("[scarlet]'page' must be a number.");
				return;
			}
			int commandsPerPage = 6;
			int page = args.length > 0 ? Strings.parseInt(args[0]) : 1;
			int pages = Mathf.ceil((float)clientCommands.getCommandList().size / commandsPerPage);
			page --;
			if(page >= pages || page < 0){
				player.sendMessage("[scarlet]'page' must be a number between[orange] 1[] and[orange] " + pages + "[scarlet].");
				return;
			}
			StringBuilder result = new StringBuilder();
			result.append(Strings.format("[orange]-- Commands Page[lightgray] {0}[gray]/[lightgray]{1}[orange] --\n\n", (page+1), pages));

			for(int i = commandsPerPage * page; i < Math.min(commandsPerPage * (page + 1), clientCommands.getCommandList().size); i++){
				Command command = clientCommands.getCommandList().get(i);
				result.append("[orange] /").append(command.text).append("[white] ").append(command.paramText).append("[lightgray] - ").append(command.description).append("\n");
			}
			player.sendMessage(result.toString());
		});
		*/

		handler.<Player>register("login", "<id> <password>", "Login to account", (args, player) -> {
			if(!Authority_control(player.uuid,"login")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				login(player,args[0],args[1]);
			}
		});

		handler.<Player>register("register", "<new_id> <new_password> <password_repeat>", "Login to account", (args, player) -> {
			if(!Authority_control(player.uuid,"register")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				register(player,args[0],args[1],args[2]);
			}
		});

		handler.<Player>register("info",getinput("info"), (args, player) -> {
			if(!Authority_control(player.uuid,"info")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				List data=getSQLite_UUID(player.uuid);
				//Call.onInfoMessage(player.con,getinput("join.start",Playerdate));
			}
		});

		handler.<Player>register("status",getinput("status"), (args, player) -> {
			if(!Authority_control(player.uuid,"status")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				player.sendMessage("FPS:"+status("getfps")+"  Occupied memory:"+status("getmemory")+"MB");
				player.sendMessage(getinput("status.number",String.valueOf(Vars.playerGroup.size())));
				player.sendMessage(getinput("status.ban",status("getbancount")));
			}
		});


		handler.<Player>register("getpos",getinput("getpos"), (args, player) -> {
			if(!Authority_control(player.uuid,"getpos")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				player.sendMessage(getinput("getpos.info",String.valueOf(Math.round(player.x/8)),String.valueOf(Math.round(player.y/8))));
			}
		});

		handler.<Player>register("tpp","<player> <player>",getinput("tpp"), (args, player) -> {
			if(!Authority_control(player.uuid,"tpp")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				try {
					int x = Integer.parseInt(args[0])*8;
					int y = Integer.parseInt(args[1])*8;
					player.setNet((float)x, (float)y);
					player.set((float)x, (float)y);
				} catch (Exception e){
					player.sendMessage(getinput("tpp.fail"));
				}
			}
		});

		handler.<Player>register("tp","<player...>",getinput("tp"), (args, player) -> {
			if(!Authority_control(player.uuid,"tp")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				Player other = Vars.playerGroup.find(p->p.name.equalsIgnoreCase(args[0]));
				if(other == null){
					player.sendMessage(getinput("tp.fail"));
					return;
				}
				player.setNet(other.x, other.y);
			}
		});

		handler.<Player>register("suicide",getinput("suicide"), (args, player) -> {
			if(!Authority_control(player.uuid,"suicide")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				player.onPlayerDeath(player);
				Call.sendMessage(getinput("suicide.tips",player.name));
			}
		});

		handler.<Player>register("team",getinput("team"), (args, player) ->{
			if(!Authority_control(player.uuid,"team")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				if (!Vars.state.rules.pvp){
					player.sendMessage(getinput("team.fail"));
					return;
				}
				int index = player.getTeam().id+1;
				player.sendMessage(String.valueOf(index));
				while (index != player.getTeam().id){
					if (index >= Team.all().length){
						index = 0;
					}
					if (!Vars.state.teams.get(Team.all()[index]).cores.isEmpty()){
						player.setTeam(Team.all()[index]);
						break;
					}
					index++;
				}
				Call.onPlayerDeath(player);
			}
		});

		handler.<Player>register("difficulty", "<difficulty>", getinput("difficulty"), (args, player) -> {
			if(!Authority_control(player.uuid,"difficulty")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				try {
					Difficulty.valueOf(args[0]);
					player.sendMessage(getinput("difficulty.success",args[0]));
				}catch(IllegalArgumentException e){
					player.sendMessage(getinput("difficulty.fail",args[0]));
				}
			}
		});

		handler.<Player>register("gameover","",getinput("gameover"), (args, player) -> {
			if(!Authority_control(player.uuid,"gameover")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				Events.fire(new GameOverEvent(Team.crux));
			}
		});


		handler.<Player>register("host","<mapname> [mode]",getinput("host"), (args, player) -> {
			if(!Authority_control(player.uuid,"host")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				host(args[0],args[1],player);
			}
		});

		handler.<Player>register("runwave",getinput("runwave"), (args, player) -> {
			if(!Authority_control(player.uuid,"runwave")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				logic.runWave();
			}
		});

		handler.<Player>register("time",getinput("time"), (args, player) -> player.sendMessage(getinput("time.info",timee())));

		handler.<Player>register("tr","<text> <Output-language>",getinput("tr"), (args, player) -> {
			if(!Authority_control(player.uuid,"tr")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				//No spaces are allowed in the input language??
				player.sendMessage(getinput("tr.tips"));
				player.sendMessage(getinput("tr.tips1"));
				String text = args[0].replace('-',' ');	
				try {
					Thread.currentThread().sleep(2500);
				}catch(InterruptedException ie){
					ie.printStackTrace();
				} 
				try{
					String translationm = googletranslation.translate(text,args[1]);
					Call.sendMessage("["+player.name+"]"+"[green] : [] "+translationm+"   -From Google Translator");
				}catch(Exception e){
					return;
				}
			}	
		});

		handler.<Player>register("maps", "[page] [mode]", getinput("maps"), (args, player) -> {
			if(!Authority_control(player.uuid,"maps")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				List<String> MapsList = (List<String>)Lists.getMaps_List();
				int Maximum = 6;
				//6为list最大承载 可自行改
				int page = args.length > 0 ? Integer.parseInt(args[0]) : 1;
				int pages = Mathf.ceil((float)MapsList.size() / Maximum);
				page --;
				if(page >= pages || page < 0){
					player.sendMessage(getinput("maps.page.err",pages));
					return;
				}
				if(args.length == 2) {
					player.sendMessage(getinput("maps.page",(page+1),pages));
					for(int i = Maximum * page; i < Math.min(Maximum * (page + 1), MapsList.size()); i++){
						String [] data = MapsList.get(i).split("\\s+");
						if(data[3].equalsIgnoreCase(args[1]))player.sendMessage(getinput("maps.mode.info",String.valueOf(i),data[0],data[1]));
					}
					return;
				}
				player.sendMessage(getinput("maps.page",(page+1),pages));
				for(int i = Maximum * page; i < Math.min(Maximum * (page + 1), MapsList.size()); i++){
					String [] data = MapsList.get(i).split("\\s+");
					player.sendMessage(getinput("maps.info",String.valueOf(i),data[0],data[1],data[2]));
				}
			}
		});

		handler.<Player>register("vote", "<gameover/kick/skipwave/host> [name/number]", getinput("vote"), (args, player) -> {
				switch(args[0]) {
					case "gameover":
					case "skipwave":
						new Vote(player,args[0]);
						break;
					case "kick":
						new Vote(player,args[0],args[1]);
						break;
					case "host":
						if (Lists.getMaps_List().size() >= Integer.parseInt(args[1])) {
							new Vote(player,args[0],args[1]);
							return;
						}
						player.sendMessage(getinput("vote.host.maps.err",args[1]));
						break;
					default:
						player.sendMessage(getinput("vote.errr",args[0]));
						break;
				}
		});
/*
		handler.<Player>register("setting","<text> [text]",getinput("setting"), (args, player) -> {
			if(!player.isAdmin){
				player.sendMessage(getinput("admin.no"));
				return;
			}
			switch(args[0]) {
				case "help":
					player.sendMessage(getinput("setting.help"));
					break;
				case "Automatic-translation":
					JSONObject date = getData();
					if (args.length == 1 && args[0].equals("on")) {
						date.put("translateo", true);
						Core.settings.getDataDirectory().child("mods/GA/setting.json").writeString((String.valueOf(date)));
						player.sendMessage(getinput("setting.trr.on"));
					}else{
						date.put("translateo", false);
						Core.settings.getDataDirectory().child("mods/GA/setting.json").writeString((String.valueOf(date)));
						player.sendMessage(getinput("setting.trr.off"));
					}
					break;
				case "language":
					player.sendMessage(getinput("setting.info"));
					String result = setting_language(args[0],args[1]);
					if(result = "Y")
					break;
				default:
					break;
			}
		});
*/
	}

}

/*2020/1/4 10:64:33
 *本项目使用算法
 *名称								使用算法	  			来源
 *UTF8Control.Java					UTF8Control  		https://answer-id.com/52120414
 *GoogletranslateApi.Java			Googletranslate		https://github.com/PopsiCola/GoogleTranslate
 *Main.Java 						assigner 			Tencent qun(QQ qun)
 *BadWordUtil.Java 					参考DFA算法 			http://blog.csdn.net/chenssy/article/details/26961957
*/
