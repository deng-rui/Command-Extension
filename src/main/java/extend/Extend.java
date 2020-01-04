package extension.extend;

import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.ArrayList;
import java.lang.Math;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
//Java

import arc.*;
import arc.Core;
import arc.files.*;
import arc.util.*;
import arc.util.Timer;
import arc.util.CommandHandler.*;
import arc.util.Timer.*;
import arc.struct.Array;
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
import mindustry.game.EventType;
import mindustry.game.EventType.PlayerJoin;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.maps.Map;
import mindustry.maps.*;
import mindustry.net.Administration.PlayerInfo ;
import mindustry.net.Packets.KickReason;
import mindustry.net.NetConnection;
import mindustry.net.Packets.KickReason ;
import mindustry.plugin.*;
import mindustry.plugin.Plugin;
import mindustry.type.*;
import mindustry.Vars;
//Mindustry

import static java.lang.System.out;

import static mindustry.Vars.*;
import static mindustry.Vars.player;
//Static

public class Extend{

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
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yy-M-d a h:m.ss");
		String nowString = now.format(dateTimeFormatter);
		return nowString;
	}

/*
	public String vote(String name) {

		String result = null;

			if (!this.enable) {
				result = "disabled";
				return result;
			}
			votes.add(name);
			int cur = votes.size();
			int req = (int) Math.ceil(0.6 * Vars.playerGroup.size());
			if (cur < req) {
				result = "N";
				return result;
			}

			votes.clear();
			result = "Y";
			return result;
	}

	public static String setting_language() {
		
	}
*/	
}

