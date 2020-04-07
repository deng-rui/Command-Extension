package extension;

import java.util.List;
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
import mindustry.game.Difficulty;
import mindustry.game.EventType.GameOverEvent;
import mindustry.plugin.Plugin;
import mindustry.Vars;
//Mindustry

import static arc.util.Log.info;
import static mindustry.Vars.logic;
import static mindustry.Vars.maps;
import static mindustry.Vars.net;
import static mindustry.Vars.state;
import static mindustry.Vars.world;
import static mindustry.Vars.playerGroup;
//Mindustry-Static

import extension.data.db.PlayerData;
import extension.data.global.Config;
import extension.data.global.Lists;
import extension.data.global.Maps;
import extension.core.Event;
import extension.core.Threads;
import extension.core.Vote;
import extension.util.Log;
import extension.util.translation.Bing;
import extension.util.translation.Google;
//GA-Exted

import static extension.core.ClientCommandsx.*;
import static extension.core.Extend.*;
import static extension.core.Initialization.MapList;
import static extension.core.Initialization.Start_Initialization;
import static extension.core.Initialization.Override_Initialization;
import static extension.core.Initialization.ReLoadConfig;
import static extension.util.LocaleUtil.getinput;
import static extension.util.LocaleUtil.getinputt;
import static extension.util.IsUtil.Blank;
import static extension.util.IsUtil.NotisNumeric;
//Static
import static extension.util.log.Error.Error;

public class Main extends Plugin {
	
	private final Google googletranslation = new Google();
	//动态难度
	//PVP限制

	public Main() {
		
		//Log 权限
		Log.Set("ALL");

		//初始化 所需依赖
		Start_Initialization();

		//加载Event
		Event.Main();

		
	}

	@Override
	public void init(){
		Override_Initialization();
	}	

	@Override
	public void registerServerCommands(CommandHandler handler){
		handler.removeCommand("exit");
		handler.removeCommand("gameover");
		handler.removeCommand("reloadmaps");

		handler.register("testinfo","[GA]" ,"GA TEST", (arg) -> {
			//info("{0}", getinput(arg[0]));
		System.out.println((arg.length > 0) ? arg[0] : null);
		});


		handler.register("reloadmaps", "reload maps", (arg) -> {
			int beforeMaps = maps.all().size;
			maps.reload();
			if(maps.all().size > beforeMaps){
				info("&lc{0}&ly new map(s) found and reloaded.", maps.all().size - beforeMaps);
			}else{
				info("&lyMaps reloaded.");
			}
			MapList();
		});

		handler.register("gameover", "Force a game over", (arg)-> {
			info("&lyCore destroyed.");
			Events.fire(new GameOverEvent(Team.crux));
		});

		handler.register("reloadconfig", "reload Command-Extension Config.ini", (arg)-> {
			info("&lyReLoad Config.ini End.");
			ReLoadConfig();
		});

		handler.register("exit", "Exit the server application", (arg)-> {
			info("Shutting down server.");
			net.dispose();
			Threads.close();
			Core.app.exit();
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
		if (Config.Login) {
			handler.<Player>register("login", "<id> <password>", "Login to account", (args, player) -> {
				if (!Authority_control(player,"login"))
					player.sendMessage(getinput("authority.no"));
				else
					login(player,args[0],args[1]);
			});

			handler.<Player>register("register", "<new_id> <new_password> <password_repeat> [your_mail]", "Login to account", (args, player) -> {
				if (!Authority_control(player,"register"))
					player.sendMessage(getinput("authority.no"));
				else
					register(player,args[0],args[1],args[2],(args.length > 3) ? args[3] : null);
			});

			handler.<Player>register("ftpasswd", "<Email_at_registration> [Verification_Code]", "Forget password", (args, player) -> {
				if (!Authority_control(player,"ftpasswd"))
					player.sendMessage(getinput("authority.no"));
				else
					ftpasswd(player,args[0],(args.length > 1) ? args[1] : null);
			});
		}
		//

		handler.<Player>register("info","[page]",getinput("info"), (args, player) -> {
			if (!Authority_control(player,"info")) {
				player.sendMessage(getinput("authority.no"));
			} else 
				Call.onInfoMessage(player.con,getinputt("info.info.1",PlayerdatatoObject(Maps.getPlayer_Data(player.uuid))));
		});

		handler.<Player>register("status",getinput("status"), (args, player) -> {
			if (!Authority_control(player,"status"))
				player.sendMessage(getinput("authority.no"));
			else
				player.sendMessage(getinput("status.info",playerGroup.size(),world.getMap().name(),Core.graphics.getFramesPerSecond(),Core.app.getJavaHeap()/1024/1024));
		});

		handler.<Player>register("tpp","<player> <player>",getinput("tpp"), (args, player) -> {
			if (!Authority_control(player,"tpp")) {
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
			if (!Authority_control(player,"tp")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				Player other = playerGroup.find(p->p.name.equalsIgnoreCase(args[0]));
				if(null == other){
					player.sendMessage(getinput("tp.fail"));
					return;
				}
				player.setNet(other.x, other.y);
			}
		});

		handler.<Player>register("suicide",getinput("suicide"), (args, player) -> {
			if (!Authority_control(player,"suicide")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				player.onPlayerDeath(player);
				Call.sendMessage(getinput("suicide.tips",player.name));
			}
		});

		handler.<Player>register("team",getinput("team"), (args, player) ->{
			if (!Authority_control(player,"team")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				if (!state.rules.pvp){
					player.sendMessage(getinput("team.fail"));
					return;
				}
				int index = player.getTeam().id+1;
				player.sendMessage(String.valueOf(index));
				while (index != player.getTeam().id){
					if (index >= Team.all().length){
						index = 0;
					}
					if (!state.teams.get(Team.all()[index]).cores.isEmpty()){
						player.setTeam(Team.all()[index]);
						break;
					}
					index++;
				}
				Call.onPlayerDeath(player);
			}
		});

		handler.<Player>register("difficulty", "<difficulty>", getinput("difficulty"), (args, player) -> {
			if (!Authority_control(player,"difficulty")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				try {
					Difficulty.valueOf(args[0]);
					player.sendMessage(getinput("difficulty.success",args[0]));
				}catch(IllegalArgumentException e) {
					player.sendMessage(getinput("difficulty.fail",args[0]));
				}
			}
		});

		handler.<Player>register("gameover",getinput("gameover"), (args, player) -> {
			if (!Authority_control(player,"gameover")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				Events.fire(new GameOverEvent(Team.crux));
			}
		});


		handler.<Player>register("host","<map_number>",getinput("host"), (args, player) -> {
			if (Authority_control(player,"host")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				host(player,args[0]);
			}
		});

		handler.<Player>register("runwave",getinput("runwave"), (args, player) -> {
			if (!Authority_control(player,"runwave")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				logic.runWave();
			}
		});

		handler.<Player>register("time",getinput("time"), (args, player) -> player.sendMessage(getinput("time.info",timee())));

		handler.<Player>register("tr","<text> [Output-language]",getinput("tr"), (args, player) -> {
			if (!Authority_control(player,"tr")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				//No spaces are allowed in the input language??
				player.sendMessage(getinput("tr.tips"));
				player.sendMessage(getinput("tr.tips1"));
				try {
					Thread.currentThread().sleep(2500);
				}catch(InterruptedException ie){
					ie.printStackTrace();
				}
				// 默认EN
				Call.sendMessage("["+player.name+"]"+"[green] : [] "+googletranslation.translate(args[0].replace('_',' '),(Blank(args[1])) ? "en" : args[1])+"   -From Google Translator");
			}	
		});

		handler.<Player>register("maps", "[page] [mode]", getinput("maps"), (args, player) -> {
			if (!Authority_control(player,"maps")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				if(NotisNumeric(args.length > 0?args[0]:"1")) {
					player.sendMessage(getinput("nber.err"));
					return;
				}
				List<String> MapsList = (List<String>)Lists.getMaps_List();
				int Maximum = 6;
				//6为list最大承载 可自行改
				int page = args.length > 0 ? Integer.parseInt(args[0]):1;
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

		handler.<Player>register("vote", "<help> [parameter]", getinput("vote"), (args, player) -> {
			if (!Authority_control(player,"vote")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				if(!Vote.sted) {
					player.sendMessage(getinput("vote.already_begun"));
					return;
				}
				switch(args[0]) {
					case "help":
						Call.onInfoToast(player.con,getinput("vote.help"),40f);
						break;
					case "gameover":
					case "skipwave":
						new Vote(player,args[0]);
						break;
					case "kick":
						new Vote(player,args[0],args[1]);
						break;
					case "ff":
						new Vote(player,args[0],player.getTeam());
						break;
					case "host":
						if(NotisNumeric(args[1])) {
							player.sendMessage(getinput("nber.err"));
							return;
						}
						if (!(Lists.getMaps_List().size() >= Integer.parseInt(args[1])))
							player.sendMessage(getinput("vote.host.maps.err",args[1]));
						new Vote(player,args[0],args[1]);
						break;
					default:
						player.sendMessage(getinput("vote.err",args[0]));
						break;
				}
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
 *BadWord.Java 					参考DFA算法 			http://blog.csdn.net/chenssy/article/details/26961957
*/
