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
import mindustry.entities.type.Player;
import mindustry.game.*;
import mindustry.game.Team;
import mindustry.game.Difficulty;
import mindustry.game.EventType.*;
import mindustry.game.EventType.PlayerJoin;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.net.Administration.PlayerInfo ;
import mindustry.net.Packets.KickReason;
import mindustry.net.NetConnection;
import mindustry.plugin.Plugin;
import mindustry.Vars;
//Mindustry

import static mindustry.Vars.*;
import static mindustry.Vars.player;
//
import extension.extend.translation.Googletranslate;
//import extension.extend.translation.Tencenttranslate;
import extension.auxiliary.Language;
//GA-Exted
import static extension.extend.tool.HttpRequest.doGet;
import static extension.extend.Extend.*;
import static extension.extend.Json.*;
//Static

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
//Json


public class Main extends Plugin{

	Googletranslate googletranslate = new Googletranslate();
	Language language = new Language();

	public Main(){

		Events.on(EventType.PlayerChatEvent.class, e -> {
			String check = String.valueOf(e.message.charAt(0));
			//check if command
			if(!check.equals("/")) {
				boolean valid = e.message.matches("\\w+");
				JSONObject date = getData();
				boolean translateo = (boolean) date.get("translateo");
				// check if enable translate
				if (!valid && translateo) {
					try{
						Thread.currentThread().sleep(2000);
						String translationa = googletranslate.translate(e.message,"en");
						Call.sendMessage("["+e.player.name+"]"+"[green] : [] "+translationa+"   -From Google Translator");
						}catch(InterruptedException ie){
							ie.printStackTrace();
						}catch(Exception ie){
							return;
						}
				}
			}
		});

		if(!Core.settings.getDataDirectory().child("plugins/GA/setting.json").exists()){
			addjson();
		}
		//language.language();

	}

		

	@Override
	public void registerClientCommands(CommandHandler handler){

		handler.<Player>register("info",language.getinput("info",null,null), (args, player) -> {
			String ip = Vars.netServer.admins.getInfo(player.uuid).lastIP;
			String Country = doGet("http://ip-api.com/line/"+ip+"?fields=country");
			player.sendMessage(language.getinput("info.load",null,null));
			try{
				Thread.currentThread().sleep(2000);
				}catch(InterruptedException ie){
					ie.printStackTrace();
				}
			player.sendMessage(language.getinput("info.name",player.name,null));
			player.sendMessage(language.getinput("info.uuid",player.uuid,null));
			player.sendMessage(language.getinput("info.equipment",String.valueOf(player.isMobile),null));
			player.sendMessage(language.getinput("info.ip",ip,null));
			player.sendMessage(language.getinput("info.country",Country,null));
		});

		handler.<Player>register("status",language.getinput("status",null,null), (args, player) -> {
			player.sendMessage("FPS:"+status("getfps")+"  Occupied memory:"+status("getmemory")+"MB");
			player.sendMessage(language.getinput("status.number",String.valueOf(Vars.playerGroup.size()),null));
			player.sendMessage(language.getinput("status.ban",status("getbancount"),null));
		});


		handler.<Player>register("getpos",language.getinput("getpos",null,null), (args, player) -> player.sendMessage(language.getinput("getpos.info",String.valueOf(Math.round(player.x/8)),String.valueOf(Math.round(player.y/8)))));

		handler.<Player>register("tpp","<player> <player>",language.getinput("tpp",null,null), (args, player) -> {
			if(!player.isAdmin){
				player.sendMessage(language.getinput("admin.no",null,null));
			} else {
				try{
					int x = Integer.parseInt(args[0])*8;
					int y = Integer.parseInt(args[1])*8;
					player.setNet((float)x, (float)y);
					player.set((float)x, (float)y);
				} catch (Exception e){
				player.sendMessage(language.getinput("tpp.fail",null,null));
				}
			}
		});

		handler.<Player>register("tp","<player...>",language.getinput("tp",null,null), (args, player) -> {
			Player other = Vars.playerGroup.find(p->p.name.equalsIgnoreCase(args[0]));
			if(!player.isAdmin){
				player.sendMessage(language.getinput("admin.no",null,null));
			} else {
				if(other == null){
					player.sendMessage(language.getinput("tp.fail",null,null));
					return;
				}
				player.setNet(other.x, other.y);
			}
		});

		handler.<Player>register("suicide",language.getinput("suicide",null,null), (args, player) -> {
				player.onPlayerDeath(player);
				Call.sendMessage(language.getinput("suicide.tips",player.name,null));
		});

		handler.<Player>register("team",language.getinput("team",null,null), (args, player) ->{
			//change team
			if(!player.isAdmin){
				player.sendMessage(language.getinput("admin.no",null,null));
				} else {
				if (!Vars.state.rules.pvp){
					player.sendMessage(language.getinput("team.fail",null,null));
					return;
				}
				int index = player.getTeam().id+1;
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

		handler.<Player>register("difficulty", "<difficulty>", language.getinput("difficulty",null,null), (args, player) -> {
			if(!player.isAdmin){
				player.sendMessage("[green]Careful: [] You're not admin!");
			} else {
				try{
					Difficulty.valueOf(args[0]);
					player.sendMessage(language.getinput("difficulty.success",args[0],null));
				}catch(IllegalArgumentException e){
					player.sendMessage(language.getinput("difficulty.fail",args[0],null));
				}
			}
		});

		handler.<Player>register("gameover","",language.getinput("gameover",null,null), (args, player) -> {
			
			if(!player.isAdmin){
				player.sendMessage(language.getinput("admin.no",null,null));
			} else {
				Events.fire(new GameOverEvent(Team.crux));
			}

		});


		handler.<Player>register("host","<mapname> [mode]",language.getinput("host",null,null), (args, player) -> {
			if(!player.isAdmin){
				player.sendMessage(language.getinput("admin.no",null,null));
			} else {
				String result=host(args[0],args[1],"N");
				if (result != "Y") {
					player.sendMessage(language.getinput("host.mode",args[1],null));
				}else{
					Call.sendMessage(language.getinput("host.re",null,null));
					host(args[0],args[1],"Y");
				}
			}
		});
		//It can be used normally. :)

		handler.<Player>register("runwave",language.getinput("runwave",null,null), (args, player) -> {
			if(!player.isAdmin){
				player.sendMessage(language.getinput("admin.no",null,null));
			} else {
				logic.runWave();
			}
		});

		handler.<Player>register("time",language.getinput("time",null,null), (args, player) -> player.sendMessage(language.getinput("time.info",timee(),null)));

		handler.<Player>register("tr","<text> <Output-language>",language.getinput("tr",null,null), (args, player) -> {
			//No spaces are allowed in the input language??
			player.sendMessage(language.getinput("tr.tips",null,null));
			player.sendMessage(language.getinput("tr.tips1",null,null));
			String text = args[0].replace('-',' ');	
			try{
				Thread.currentThread().sleep(2500);
				}catch(InterruptedException ie){
					ie.printStackTrace();
				}
			try{
				String translationm = googletranslate.translate(text,args[1]);
				Call.sendMessage("["+player.name+"]"+"[green] : [] "+translationm+"   -From Google Translator");
				}catch(Exception e){
					return;
				}
			
			});
/*
		handler.<Player>register("vote", "<gameover/kick> [playername...]", "Vote", (args, player) -> {
			String result=null;
			if (args[0] == "gameover" || args[0] == "kick" || args[0] == "ban") {
				result=extend.vote(player.name);
				Call.sendMessage("[green] Gameover vote started!");
				if (result == "N") {
					Call.sendMessage("[green] [red]vote failed.");
					return;
				}else if (result == "Found") {
					player.sendMessage("[green] Vote not processing!");
					return;
				}
			}
				switch(args[0]) {
				case "gameover":
					Call.sendMessage("[green] Gameover vote passed!");
					Events.fire(new GameOverEvent(Team.crux));
					break;
				case "kick":
					Player target = playerGroup.find(p -> p.name.equals(args[1]));
					target.con.kick(KickReason.kick);
					break;
				default:
					break;
			}
		});

		handler.<Player>register("setting","<text> [text]",language.getinput("setting",null,null), (args, player) -> {
			if(!player.isAdmin){
				player.sendMessage(language.getinput("admin.no",null,null));
				return;
			}
			switch(args[0]) {
				case "help":
					player.sendMessage(language.getinput("setting.help",null,null));
					break;
				case "Automatic-translation":
					JSONObject date = getData();
					if (args.length == 1 && args[0].equals("on")) {
						date.put("translateo", true);
						Core.settings.getDataDirectory().child("plugins/GA/setting.json").writeString((String.valueOf(date)));
						player.sendMessage(language.getinput("setting.trr.on",null,null));
					}else{
						date.put("translateo", false);
						Core.settings.getDataDirectory().child("plugins/GA/setting.json").writeString((String.valueOf(date)));
						player.sendMessage(language.getinput("setting.trr.off",null,null));
					}
					break;
				case "language":
					player.sendMessage(language.getinput("setting.language.info",null,null));
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
 *名称					使用算法	  			来源
 *Language.Java			UTF8Control  		https://answer-id.com/52120414
 *Googletranslate.Java	Googletranslate		https://github.com/PopsiCola/GoogleTranslate
 *Main.Java 			info 				https://github.com/Kieaer/Essentials
*/
