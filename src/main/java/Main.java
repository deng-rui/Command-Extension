package extension;

import java.io.*;
import java.net.*;
import java.util.*;
//Java

import arc.*;
import arc.util.*;
import arc.util.Timer;
import arc.util.CommandHandler.*;
//Arc

import mindustry.*;
import mindustry.core.*;
import mindustry.core.GameState.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.type.*;
import mindustry.game.*;
import mindustry.game.Team;
import mindustry.game.Difficulty;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.net.Administration.PlayerInfo ;
import mindustry.net.Packets.KickReason;
import mindustry.net.NetConnection;
import mindustry.plugin.Plugin;
import mindustry.Vars;
//Mindustry

import static mindustry.Vars.*;
//Mindustry-Static

import extension.util.translation.Googletranslate;
import extension.util.translation.Baidutranslate;
import extension.auxiliary.Language;
//GA-Exted

import static extension.auxiliary.Strings.*;
import static extension.tool.HttpRequest.doGet;
import static extension.tool.HttpRequest.doCookie;
import static extension.tool.Librarydependency.*;
import static extension.tool.Json.*;
import static extension.tool.SQLite.*;
import static extension.util.Extend.*;
import static extension.util.Extend.ClientCommands.*;
import static extension.util.Extend.Event.*;
import static extension.util.Extend.Init.*;
import static extension.util.Sensitive_Thesaurus.*;
import static extension.util.Translation_support.*;
//Static


public class Main extends Plugin{

	Googletranslate googletranslate = new Googletranslate();
	Baidutranslate baidutranslate = new Baidutranslate();
	Language language = new Language();
//改进全局变量
//VOTE
	@SuppressWarnings("unchecked")
	//:(
	public Main() {

		if(!Core.settings.getDataDirectory().child("mods/GA/setting.json").exists())Initialization();

		//importLib("org.xerial","sqlite-jdbc","3.30.1",Core.settings.getDataDirectory().child("mods/GA/Lib/"));
		//加载

		Events.on(EventType.PlayerChatEvent.class, e -> {
			String result = PlayerChatEvent_translate(String.valueOf(e.message.charAt(0)),e.message);
			if (null != result)Call.sendMessage("["+e.player.name+"]"+"[green] : [] "+result+"   -From Google Translator");
			//自动翻译
			Set<String> set = Sensitive_Thesaurus(removeAll_EN(e.message));
			if (0 < set.size())PlayerChatEvent_Sensitive_Thesaurus(e.player, set.iterator().next());
			Set<String> set1 = Sensitive_Thesaurus(removeAll_CN(e.message));
			if (0 < set1.size())PlayerChatEvent_Sensitive_Thesaurus(e.player, set1.iterator().next());
			//中英分检测
		});

		Events.on(EventType.PlayerJoin.class, e -> {
			Set<String> set = Sensitive_Thesaurus(removeAll_EN(e.player.name));
			if (0 < set.size())Call.onKick(e.player.con, language.getinput("Sensitive.Thesaurus.join.kick",set.iterator().next()));
			Set<String> set1 = Sensitive_Thesaurus(removeAll_CN(e.player.name));
			if (0 < set1.size())Call.onKick(e.player.con, language.getinput("Sensitive.Thesaurus.join.kick",set1.iterator().next()));
			//中英分检测
			Call.onInfoMessage(e.player.con,language.getinput("join.start",timee(),getGC_1()));
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

		Events.on(EventType.UnitCreateEvent.class, e -> {
		});

		Events.on(GameOverEvent.class, e -> {
			if (Vars.state.rules.pvp)setGC();
		});

		Events.on(ServerLoadEvent.class, e-> {
			netServer.admins.addChatFilter((player, message) -> {
				return netServer_addChatFilter_Sensitive_Thesaurus(player,message);
			});
		});

		addSQLite();
		getSQLite();

		
	}

		//downLoadFromUrl("org.xerial","sqlite-jdbc","3.30.1","China",Core.settings.getDataDirectory().child("mods/GA/Lib/"));
		//InitializationSQLite();
		
		
	@Override
	public void registerServerCommands(CommandHandler handler){
		handler.register("gac","<ON/OFF>", "NOT", (arg) -> {
				if("Y".equalsIgnoreCase(arg[0]))setGC_1("允许");
				if ("N".equalsIgnoreCase(arg[0]))setGC_1("禁止");
		});

	};

	@Override
	public void registerClientCommands(CommandHandler handler){
		handler.removeCommand("vote");
		handler.removeCommand("votekick");

		handler.<Player>register("info",language.getinput("info"), (args, player) -> {
			String ip = Vars.netServer.admins.getInfo(player.uuid).lastIP;
			String Country = doGet("http://ip-api.com/line/"+ip+"?fields=country");
			player.sendMessage(language.getinput("info.load"));
			try{
				Thread.currentThread().sleep(2000);
				}catch(InterruptedException ie){
					ie.printStackTrace();
				}
			player.sendMessage(language.getinput("info.name",player.name));
			player.sendMessage(language.getinput("info.uuid",player.uuid));
			player.sendMessage(language.getinput("info.equipment",String.valueOf(player.isMobile)));
			player.sendMessage(language.getinput("info.ip",ip));
			player.sendMessage(language.getinput("info.country",Country));
		});

		handler.<Player>register("status",language.getinput("status"), (args, player) -> {
			player.sendMessage("FPS:"+status("getfps")+"  Occupied memory:"+status("getmemory")+"MB");
			player.sendMessage(language.getinput("status.number",String.valueOf(Vars.playerGroup.size())));
			player.sendMessage(language.getinput("status.ban",status("getbancount")));
		});


		handler.<Player>register("getpos",language.getinput("getpos"), (args, player) -> player.sendMessage(language.getinput("getpos.info",String.valueOf(Math.round(player.x/8)),String.valueOf(Math.round(player.y/8)))));

		handler.<Player>register("gc",language.getinput("gc"), (args, player) -> Call.onInfoMessage(player.con,language.getinput("gc.info")));

		handler.<Player>register("tpp","<player> <player>",language.getinput("tpp"), (args, player) -> {
			if(!player.isAdmin){
				player.sendMessage(language.getinput("admin.no"));
			} else {
				try{
					int x = Integer.parseInt(args[0])*8;
					int y = Integer.parseInt(args[1])*8;
					player.setNet((float)x, (float)y);
					player.set((float)x, (float)y);
				} catch (Exception e){
				player.sendMessage(language.getinput("tpp.fail"));
				}
			}
		});

		handler.<Player>register("tp","<player...>",language.getinput("tp"), (args, player) -> {
			Player other = Vars.playerGroup.find(p->p.name.equalsIgnoreCase(args[0]));
			if(!player.isAdmin){
				player.sendMessage(language.getinput("admin.no"));
			} else {
				if(other == null){
					player.sendMessage(language.getinput("tp.fail"));
					return;
				}
				player.setNet(other.x, other.y);
			}
		});

		handler.<Player>register("suicide",language.getinput("suicide"), (args, player) -> {
				player.onPlayerDeath(player);
				Call.sendMessage(language.getinput("suicide.tips",player.name));
		});

		handler.<Player>register("team",language.getinput("team"), (args, player) ->{
			//change team
			if(!player.isAdmin){
				player.sendMessage(language.getinput("admin.no"));
				} else {
				if (!Vars.state.rules.pvp){
					player.sendMessage(language.getinput("team.fail"));
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

		handler.<Player>register("difficulty", "<difficulty>", language.getinput("difficulty"), (args, player) -> {
			if(!player.isAdmin){
				player.sendMessage("[green]Careful: [] You're not admin!");
			} else {
				try{
					Difficulty.valueOf(args[0]);
					player.sendMessage(language.getinput("difficulty.success",args[0]));
				}catch(IllegalArgumentException e){
					player.sendMessage(language.getinput("difficulty.fail",args[0]));
				}
			}
		});

		handler.<Player>register("gameover","",language.getinput("gameover"), (args, player) -> {
			
			if(!player.isAdmin){
				player.sendMessage(language.getinput("admin.no"));
			} else {
				Events.fire(new GameOverEvent(Team.crux));
			}

		});


		handler.<Player>register("host","<mapname> [mode]",language.getinput("host"), (args, player) -> {
			if(!player.isAdmin){
				player.sendMessage(language.getinput("admin.no"));
			} else {
				String result=host(args[0],args[1],"N");
				if (result != "Y") {
					player.sendMessage(language.getinput("host.mode",args[1]));
				}else{
					Call.sendMessage(language.getinput("host.re"));
					host(args[0],args[1],"Y");
				}
			}
		});
		//It can be used normally. :)

		handler.<Player>register("runwave",language.getinput("runwave"), (args, player) -> {
			if(!player.isAdmin){
				player.sendMessage(language.getinput("admin.no"));
			} else {
				logic.runWave();
			}
		});

		handler.<Player>register("time",language.getinput("time"), (args, player) -> player.sendMessage(language.getinput("time.info",timee())));

		handler.<Player>register("tr","<text> <Output-language>",language.getinput("tr"), (args, player) -> {
			//No spaces are allowed in the input language??
			player.sendMessage(language.getinput("tr.tips"));
			player.sendMessage(language.getinput("tr.tips1"));
			String text = args[0].replace('-',' ');	
			try{
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
					player.sendMessage(language.getinput("vote.err.no"));
					break;
			}
		});

		handler.<Player>register("setting","<text> [text]",language.getinput("setting"), (args, player) -> {
			if(!player.isAdmin){
				player.sendMessage(language.getinput("admin.no"));
				return;
			}
			switch(args[0]) {
				case "help":
					player.sendMessage(language.getinput("setting.help"));
					break;
				case "Automatic-translation":
					JSONObject date = getData();
					if (args.length == 1 && args[0].equals("on")) {
						date.put("translateo", true);
						Core.settings.getDataDirectory().child("mods/GA/setting.json").writeString((String.valueOf(date)));
						player.sendMessage(language.getinput("setting.trr.on"));
					}else{
						date.put("translateo", false);
						Core.settings.getDataDirectory().child("mods/GA/setting.json").writeString((String.valueOf(date)));
						player.sendMessage(language.getinput("setting.trr.off"));
					}
					break;
				case "language":
					player.sendMessage(language.getinput("setting.language.info"));
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
 *Main.Java 						info 				https://github.com/Kieaer/Essentials
 *Sensitive_Thesaurus.Java 			参考DFA算法 			http://blog.csdn.net/chenssy/article/details/26961957
*/
