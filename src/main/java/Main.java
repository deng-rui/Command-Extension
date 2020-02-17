package extension;

import java.util.Set;
//Java

import arc.Core;
import arc.Events;
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
import mindustry.net.Administration.PlayerInfo ;
import mindustry.net.Packets.KickReason;
import mindustry.net.NetConnection;
import mindustry.plugin.Plugin;
import mindustry.Vars;
//Mindustry

import static mindustry.Vars.state;
import static mindustry.Vars.netServer;
import static mindustry.Vars.logic;
//Mindustry-Static


import extension.util.translation.Googletranslate;
import extension.util.translation.Baidutranslate;
//GA-Exted

import static extension.auxiliary.Strings.*;
import static extension.auxiliary.Maps.*;
import static extension.auxiliary.Language.*;
import static extension.tool.HttpRequest.doGet;
import static extension.tool.HttpRequest.doCookie;
import static extension.tool.Librarydependency.*;
import static extension.tool.Json.*;
import static extension.tool.SQLite.*;
import static extension.tool.SQLite.player.*;
import static extension.tool.Password.*;
import static extension.util.Extend.*;
import static extension.util.Extend.ClientCommands.*;
import static extension.util.Extend.Event.*;
//import static extension.util.Extend.Init.*;
import static extension.util.Sensitive_Thesaurus.*;
import static extension.util.Translation_support.*;
//Static


public class Main extends Plugin {

	Googletranslate googletranslate = new Googletranslate();
	Baidutranslate baidutranslate = new Baidutranslate();
//改进全局变量
//VOTE
	@SuppressWarnings("unchecked")
	//:(
	public Main() {

		if(!Core.settings.getDataDirectory().child("mods/GA/setting.json").exists())Initialization();
		Initia();
		//初始化SQL
		importLib("org.xerial","sqlite-jdbc","3.30.1",Core.settings.getDataDirectory().child("mods/GA/Lib/"));
		notWork("sqlite-jdbc","3.30.1",Core.settings.getDataDirectory().child("mods/GA/Lib/"));
		// InitializationSQLite();
		// addSQLite();
		// getSQLite("Dr");

		//加载

		Events.on(PlayerChatEvent.class, e -> {
			String result = PlayerChatEvent_translate(String.valueOf(e.message.charAt(0)),e.message);
			if (null != result)Call.sendMessage("["+e.player.name+"]"+"[green] : [] "+result+"   -From Google Translator");
			//自动翻译
			Set<String> set = Sensitive_Thesaurus(removeAll_EN(e.message));
			if (0 < set.size())PlayerChatEvent_Sensitive_Thesaurus(e.player, set.iterator().next());
			Set<String> set1 = Sensitive_Thesaurus(removeAll_CN(e.message));
			if (0 < set1.size())PlayerChatEvent_Sensitive_Thesaurus(e.player, set1.iterator().next());
			//中英分检测
		});

		Events.on(PlayerJoin.class, e -> {
			Set<String> set = Sensitive_Thesaurus(removeAll_EN(e.player.name));
			if (0 < set.size())Call.onKick(e.player.con, getinput("Sensitive.Thesaurus.join.kick",set.iterator().next()));
			Set<String> set1 = Sensitive_Thesaurus(removeAll_CN(e.player.name));
			if (0 < set1.size())Call.onKick(e.player.con, getinput("Sensitive.Thesaurus.join.kick",set1.iterator().next()));
			//中英分检测
			PlayerJoin_Logins(e.player);
			setPlayerDate_Temp(e.player.uuid,"Playtime-start",String.valueOf(System.currentTimeMillis()));
			//Logins
			//官方接口全靠猜...
			//Call.onInfoMessage(e.player.con,getinput("join.start",timee(),getGC_1()));
			if (Vars.state.rules.pvp){
				if("禁止".equalsIgnoreCase(getGC_1())){
					state.rules.playerDamageMultiplier = 0f;
					state.rules.playerHealthMultiplier = 0.5f;
				}else{
					state.rules.playerDamageMultiplier = 0.33f;
					state.rules.playerHealthMultiplier = 1f;
				}
			}
		});

		Events.on(GameOverEvent.class, e -> {
			if (Vars.state.rules.pvp)setGC();
		});

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

		});
		
	}

		//downLoadFromUrl("org.xerial","sqlite-jdbc","3.30.1","China",Core.settings.getDataDirectory().child("mods/GA/Lib/"));
		//;
		
		
	@Override
	public void registerServerCommands(CommandHandler handler){
		handler.register("gac","<ON/OFF>", "NOT", (arg) -> {
				if("Y".equalsIgnoreCase(arg[0]))setGC_1("允许");
				if ("N".equalsIgnoreCase(arg[0]))setGC_1("禁止");
		});

		handler.register("aaa","<1>", "NOT", (arg) -> {
				aaa(arg[0]);
		});

		handler.register("aab","<1> <2> <3>", "NOT", (arg) -> {
				aab(arg[0],arg[1],arg[2]);
		});
	};

	@Override
	public void registerClientCommands(CommandHandler handler) {
		handler.removeCommand("vote");
		handler.removeCommand("votekick");

		handler.<Player>register("info",getinput("info"), (args, player) -> {/*
			String ip = Vars.netServer.admins.getInfo(player.uuid).lastIP;
			String Country = doGet("http://ip-api.com/line/"+ip+"?fields=country");
			player.sendMessage(getinput("info.load"));
			try{
				Thread.currentThread().sleep(2000);
				}catch(InterruptedException ie){
					ie.printStackTrace();
				}
			*/
			Object[] Playerdate = {};
			Call.onInfoMessage(player.con,getinput("join.start",Playerdate));
		});

		handler.<Player>register("status",getinput("status"), (args, player) -> {
			player.sendMessage("FPS:"+status("getfps")+"  Occupied memory:"+status("getmemory")+"MB");
			player.sendMessage(getinput("status.number",String.valueOf(Vars.playerGroup.size())));
			player.sendMessage(getinput("status.ban",status("getbancount")));
		});


		handler.<Player>register("getpos",getinput("getpos"), (args, player) -> player.sendMessage(getinput("getpos.info",String.valueOf(Math.round(player.x/8)),String.valueOf(Math.round(player.y/8)))));

		handler.<Player>register("gc",getinput("gc"), (args, player) -> Call.onInfoMessage(player.con,getinput("gc.info")));

		handler.<Player>register("tpp","<player> <player>",getinput("tpp"), (args, player) -> {
			if(!player.isAdmin){
				player.sendMessage(getinput("admin.no"));
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
			Player other = Vars.playerGroup.find(p->p.name.equalsIgnoreCase(args[0]));
			if(!player.isAdmin){
				player.sendMessage(getinput("admin.no"));
			} else {
				if(other == null){
					player.sendMessage(getinput("tp.fail"));
					return;
				}
				player.setNet(other.x, other.y);
			}
		});

		handler.<Player>register("suicide",getinput("suicide"), (args, player) -> {
				player.onPlayerDeath(player);
				Call.sendMessage(getinput("suicide.tips",player.name));
		});

		handler.<Player>register("team",getinput("team"), (args, player) ->{
			//change team
			if(!player.isAdmin){
				player.sendMessage(getinput("admin.no"));
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
				//kill player
				Call.onPlayerDeath(player);
			}

		});

		handler.<Player>register("difficulty", "<difficulty>", getinput("difficulty"), (args, player) -> {
			if(!player.isAdmin){
				player.sendMessage("[green]Careful: [] You're not admin!");
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
			
			if(!player.isAdmin){
				player.sendMessage(getinput("admin.no"));
			} else {
				Events.fire(new GameOverEvent(Team.crux));
			}

		});


		handler.<Player>register("host","<mapname> [mode]",getinput("host"), (args, player) -> {
			if(!player.isAdmin){
				player.sendMessage(getinput("admin.no"));
			} else {
				host(args[0],args[1],player);
			}
		});
		//It can be used normally. :)

		handler.<Player>register("runwave",getinput("runwave"), (args, player) -> {
			if(!player.isAdmin){
				player.sendMessage(getinput("admin.no"));
			} else {
				logic.runWave();
			}
		});

		handler.<Player>register("time",getinput("time"), (args, player) -> player.sendMessage(getinput("time.info",timee())));

		handler.<Player>register("tr","<text> <Output-language>",getinput("tr"), (args, player) -> {
			//No spaces are allowed in the input language??
			player.sendMessage(getinput("tr.tips"));
			player.sendMessage(getinput("tr.tips1"));
			String text = args[0].replace('-',' ');	
			try {
				Thread.currentThread().sleep(2500);
				}catch(InterruptedException ie){
					ie.printStackTrace();
				} //[Original-language],args[2]
			try{
				String translationm = googletranslate.translate(text,args[1]);
				//String translationm = baidutranslate.translate(text,args[1]);
				Call.sendMessage("["+player.name+"]"+"[green] : [] "+translationm+"   -From Google Translator");
				}catch(Exception e){
					return;
				}
			
			});
/*
		handler.<Player>register("vote", "<gameover/kick> [playername...]", "Vote", (args, player) -> {
			switch(args[0]) {
				case "kick":
				Player other = Vars.playerGroup.find(p -> p.name.equalsIgnoreCase(args[1]));
					vote(args[0]);
					break;
				case "gameover": 
					vote(args[0]);
					break;
				default:
					player.sendMessage(getinput("vote.err.no"));
					break;
			}
		});

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
 *Googletranslate.Java				Googletranslate		https://github.com/PopsiCola/GoogleTranslate
 *Main.Java 						assigner 			Tencent qun(QQ qun)
 *Sensitive_Thesaurus.Java 			参考DFA算法 			http://blog.csdn.net/chenssy/article/details/26961957
*/
