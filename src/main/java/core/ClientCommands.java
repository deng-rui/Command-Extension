package extension.core;

import java.lang.Math;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;
//Java

import arc.*;
import arc.Core;
import arc.files.*;
import arc.util.*;
import arc.struct.*;
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
import mindustry.net.Administration.*;
import mindustry.net.Packets.KickReason;
import mindustry.net.NetConnection;
import mindustry.plugin.*;
import mindustry.type.*;
import mindustry.Vars;
//Mindustry

import static mindustry.Vars.logic;
import static mindustry.Vars.maps;
import static mindustry.Vars.net;
import static mindustry.Vars.netServer;
import static mindustry.Vars.playerGroup;
import static mindustry.Vars.state;
import static mindustry.Vars.world;
//Mindustry-Static

import static extension.data.db.Player.getSQLite_USER;
import static extension.data.db.Player.getSQLite_UUID;
import static extension.data.db.Player.InitializationPlayersSQLite;
import static extension.data.db.Player.isSQLite_User;
import static extension.data.db.Player.savePlayer_Data;
import static extension.data.db.SQLite.SQL_type;
import static extension.data.global.Lists.updatePlayerData;
import static extension.data.global.Maps.*;
import static extension.net.HttpRequest.doGet;
import static extension.util.DateUtil.getLocalTimeFromUTC;
import static extension.util.LocaleUtil.getinput;
import static extension.util.LocaleUtil.Language_determination;
import static extension.util.PasswordUtil.newPasswd;
import static extension.util.PasswordUtil.Passwdverify;
//Static

import com.alibaba.fastjson.JSONObject;
//Json

public class ClientCommands {

	public static void login(Player player, String usr, String pw) {
		if((boolean)isSQLite_User(usr)) {
			player.sendMessage(getinput("login.usrno"));
			return;
		}
		List data = getSQLite_USER(usr);
			if(!(boolean)Passwdverify(pw,(String)data.get(SQL_type("PasswordHash")),(String)data.get(SQL_type("CSPRNG")))) {
			player.sendMessage(getinput("login.pwno"));
		return;
		}
		if(!data.get(SQL_type("UUID")).equals(player.uuid)) {
			savePlayer_Data(updatePlayerData(data,SQL_type("UUID"),player.uuid),true,usr);
			player.sendMessage(getinput("uuid.update"));
		}
		data = getSQLite_USER(usr);
		if (Vars.state.rules.pvp){
			player.setTeam(netServer.assignTeam(player, playerGroup.all()));
		} else {
			player.setTeam(Team.sharded);
		}
		Call.onPlayerDeath(player);
		setPlayer_power_Data(player.uuid,Integer.parseInt((String)data.get(SQL_type("Authority"))));
		setPlayer_Data_SQL_Temp(player.uuid,data);
		Call.onInfoToast(player.con,getinput("join.start",getLocalTimeFromUTC(Long.valueOf((String)data.get(SQL_type("GMT"))),Integer.parseInt((String)data.get(SQL_type("Time_format"))))),30f);
	}

	public static void register(Player player, String newusr, String newpw, String renewpw) {
		//String ip = Vars.netServer.admins.getInfo(player.uuid).lastIP;
		String ip = "1.1.1.1";
		if(!newpw.equals(renewpw)) {
			player.sendMessage(getinput("register.pawno"));
			return;
		}
		if(!(boolean)isSQLite_User(newusr)) {
			player.sendMessage(getinput("register.usrerr"));
			return;
		}
		java.util.Map<String, Object> Passwd_date = (java.util.Map<String, Object>)newPasswd(newpw);
		if(!(boolean)Passwd_date.get("resualt"))return;
		JSONObject date = JSONObject.parseObject(doGet("http://ip-api.com/json/"+ip+"?fields=country,timezone"));
		long GMT = TimeZone.getTimeZone((String)date.get("timezone")).getRawOffset();
		InitializationPlayersSQLite(player.uuid,player.name,ip,String.valueOf(GMT),(String)date.get("country"),Language_determination((String)date.get("country")),getLocalTimeFromUTC(GMT,0),newusr,(String)Passwd_date.get("passwordHash"),(String)Passwd_date.get("salt"));
		if (Vars.state.rules.pvp){
			player.setTeam(netServer.assignTeam(player, playerGroup.all()));
		} else {
			player.setTeam(Team.sharded);
		}
		Call.onPlayerDeath(player);
		setPlayer_power_Data(player.uuid,1);
		setPlayer_Data_SQL_Temp(player.uuid,(List)getSQLite_UUID(player.uuid));
		Call.onInfoToast(player.con,getinput("join.start",getLocalTimeFromUTC(GMT,0)),30f);
	}

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
		return String.valueOf(fps);
		case "getmemory":
		return String.valueOf(memory);
		case "getbancount":
		return String.valueOf(bancount);
		default :
		return null;
		//Laziness,Avoid opening more if
		}
	}

	public static String host(String mapp, String gamemodes, Player player) {
		if("sandbox".equalsIgnoreCase(gamemodes)){
		}else if ("pvp".equalsIgnoreCase(gamemodes)){
		}else if ("attack".equalsIgnoreCase(gamemodes)){
		}else if ("survival".equalsIgnoreCase(gamemodes)){
		}else{
			player.sendMessage(getinput("host.mode",gamemodes));
		return null;
		}
		Call.sendMessage(getinput("host.re"));;

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
			netServer.openServer();
		}catch(Exception e){
			state.set(State.menu);
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

	}

	public static void setting_language() {

	}

}