package extension.util;

import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.lang.Math;
import java.lang.reflect.Method;
//Java

import arc.*;
import arc.Core;
import arc.files.*;
import arc.util.*;
import arc.util.Timer;
import arc.struct.Array;
//Arc

import mindustry.*;
import mindustry.core.*;
import mindustry.core.GameState.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.type.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.maps.Map;
import mindustry.maps.*;
import mindustry.net.Administration.PlayerInfo ;
import mindustry.net.Packets.KickReason;
import mindustry.net.NetConnection;
import mindustry.net.Packets.KickReason ;
import mindustry.plugin.*;
import mindustry.type.*;
import mindustry.Vars;
//Mindustry

import static mindustry.Vars.*;
//Mindustry-Static

import extension.util.translation.Googletranslate;
import extension.auxiliary.Language;
//GA-Exted

import static extension.auxiliary.Booleans.*;
import static extension.auxiliary.Strings.*;
import static extension.auxiliary.Maps.*;
import static extension.tool.Json.*;
//Static

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//Json

public class Extend{

	public static class ClientCommands{
		//private HashSet<Player> votes = new HashSet<>();
		private ArrayList<String> votes = new ArrayList<>();
		private boolean enable = true;

		public static String status(String then) {
			float fps = Math.round((int)60f / Time.delta());
			float memory = Core.app.getJavaHeap() / 1024 / 1024;
			
			int idb = 0;
			int ipb = 0;

			Array<PlayerInfo> bans = netServer.admins.getBanned();
			for(PlayerInfo info : bans){
				idb++;
			}

			Array<String> ipbans = netServer.admins.getBannedIPs();
			for(String string : ipbans){
				ipb++;
			}
			int bancount = idb + ipb;
			switch(then){
			case "getfps":
			Float floatee = new Float(fps);
			return floatee.toString();
			case "getmemory":
			Float floatee1 = new Float(memory);
			return floatee1.toString();
			case "getbancount":
			Float floatee2 = new Float(bancount);
			return floatee2.toString();
			default :
			return null;
			//Laziness,Avoid opening more if
			}
		}

		public static String host(String mapp,String gamemodes,String wait) {
			String resultt = null;
			if("sandbox".equalsIgnoreCase(gamemodes)){
			}else if ("pvp".equalsIgnoreCase(gamemodes)){
			}else if ("attack".equalsIgnoreCase(gamemodes)){
			}else if ("survival".equalsIgnoreCase(gamemodes)){
			}else{
				resultt = "N";
				//player.sendMessage(":"+gamemode+"!");
				return resultt;
			}
			resultt = "Y";
			//Call.sendMessage("[red]![]");
			if (wait != "Y") {
				return resultt;
			}else{
			try{
				Thread.currentThread().sleep(5000);
				}catch(InterruptedException ie){
					ie.printStackTrace();
				}

			netServer.kickAll(KickReason.gameover);
			state.set(State.menu);
			net.closeServer();
					
			//
			//stop games
			Map result = maps.all().find(map -> map.name().equalsIgnoreCase(mapp.replace('_', ' ')) || map.name().equalsIgnoreCase(mapp));
			Gamemode preset = Gamemode.survival;
			try{
				preset = Gamemode.valueOf(gamemodes);
				}catch(IllegalArgumentException e){
					return null;
				}	
			logic.reset();
			world.loadMap(result,result.applyRules(preset));
			state.rules = result.applyRules(preset);
			logic.play();
			try{
				net.host(Core.settings.getInt("port"));
			}catch(BindException e){
				state.set(State.menu);
			}catch(IOException e){
				state.set(State.menu);
			}
			}
			return null;
		}

		public static String timee() {
			TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd a h:m:ss");
			String nowString = now.format(dateTimeFormatter);
			return nowString;
		}

		public static void vote(String type) {
			setvote(true);
			long start = System.currentTimeMillis();
			final long end = start + 60 * 60 * 1000;
			final Timer timer = new Timer();
			int i = 1;
		}

		public static void setting_language() {

		}
	}

	public static class Event{

		public static String PlayerChatEvent_translate(String check, String text) {
			Googletranslate googletranslate = new Googletranslate();
			if(!check.equals("/")) {
				boolean valid = text.matches("\\w+");
				JSONObject date = getData("mods/GA/setting.json");
				boolean translateo = (boolean) date.get("translateo");
				// check if enable translate
				if (!valid && translateo) {
					try{
						Thread.currentThread().sleep(2000);
						String translationa = googletranslate.translate(text,"en");
						return translationa;
					}catch(InterruptedException ie){
						ie.printStackTrace();
					}catch(Exception ie){
						return null;
					}
				}
			}
			return null;
		}

		public static void PlayerChatEvent_Sensitive_Thesaurus(Player player, String text) {
			Language language = new Language();
			if (!getplayer_boolean(player.uuid)) {
				setplayer(player.uuid, 1);
				player.sendMessage(language.getinput("Sensitive.Thesaurus.info",String.valueOf(getplayer_int(player.uuid))));
			}else{
				setplayer(player.uuid, getplayer_int(player.uuid)+1);
				player.sendMessage(language.getinput("Sensitive.Thesaurus.info",String.valueOf(getplayer_int(player.uuid))));
			}
			if (3 <= getplayer_int(player.uuid)) {
				Call.onKick(player.con, language.getinput("Sensitive.Thesaurus.kick",text));
				setplayer(player.uuid, 0);
			}
			//player.sendMessage(language.getinput("Sensitive_Thesaurus",player.name));
		}

	}
}

