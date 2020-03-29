package extension.core;

import java.lang.Math;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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

import extension.data.db.PlayerData;
import extension.data.global.Config;
import extension.data.global.Lists;
import extension.data.global.Maps;
//

import static extension.data.db.Player.getSQLite;
import static extension.data.db.Player.InitializationPlayersSQLite;
import static extension.data.db.Player.isSQLite_User;
import static extension.data.db.Player.savePlayer;
import static extension.net.HttpRequest.doGet;
import static extension.util.alone.Password.newPasswd;
import static extension.util.alone.Password.Passwdverify;
import static extension.util.DateUtil.getLocalTimeFromUTC;
import static extension.util.ExtractUtil.ipToLong;
import static extension.util.IsBlankUtil.Blank;
import static extension.util.LocaleUtil.getinput;
import static extension.util.LocaleUtil.Language_determination;
//Static

import com.alibaba.fastjson.JSONObject;
//Json

public class ClientCommandsx {

	public static void login(Player player, String usr, String pw) {
		if((boolean)isSQLite_User(usr)) {
			player.sendMessage(getinput("login.usrno"));
			return;
		}

		PlayerData temp = new PlayerData("temp");
		getSQLite(temp,usr);
		if(temp.Online) {
			player.sendMessage(getinput("login.in"));
			return;
		}
		try {
			if(!Passwdverify(pw,temp.PasswordHash,temp.CSPRNG)) {
			player.sendMessage(getinput("login.pwno"));
			return;
			}
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			player.sendMessage(getinput("passwd.err"));
			return;
		}
		temp.Online = true;
		temp.Joincount++;
		temp.LastLogin = getLocalTimeFromUTC(temp.GMT);
		savePlayer(temp,player.uuid);
		if(!(temp.UUID).equals(player.uuid)) {
			temp.UUID = player.uuid;
			player.sendMessage(getinput("uuid.update"));
		}
		if (Vars.state.rules.pvp){
			player.setTeam(netServer.assignTeam(player, playerGroup.all()));
		} else {
			player.setTeam(Team.sharded);
		}
		Call.onPlayerDeath(player);
		Call.onInfoToast(player.con,getinput("join.start",getLocalTimeFromUTC(temp.GMT,temp.Time_format)),20f);
		Maps.setPlayer_Data(player.uuid,temp);
	}

	public static void register(Player player, String newusr, String newpw, String renewpw, String mail) {
		String ip = Vars.netServer.admins.getInfo(player.uuid).lastIP;
		if(!newpw.equals(renewpw)) {
			player.sendMessage(getinput("register.pawno"));
			return;
		}
		if(!(boolean)isSQLite_User(newusr)) {
			player.sendMessage(getinput("register.usrerr"));
			return;
		}
		java.util.Map<String, Object> Passwd_date;
		try {
			Passwd_date = (java.util.Map<String, Object>)newPasswd(newpw);
		} catch (Exception e) {
			player.sendMessage(getinput("passwd.err"));
			return;
		}
		long GMT = 0;
		String country = "Intranet";
		if(!(boolean)Passwd_date.get("resualt"))return;
		if(ip.equals("127.0.0.1")) {
			player.sendMessage(getinput("register.ip.nat"));
		}else{
			if(!Config.Server_Networking) {
				player.sendMessage(getinput("register.no.network"));
				country = "NOT network";
				ip = "0";
			}else{
				try {
					JSONObject data = JSONObject.parseObject(doGet("http://ip-api.com/json/"+ip+"?fields=country,timezone"));
					GMT = TimeZone.getTimeZone((String)data.get("timezone")).getRawOffset();
					country = (String)data.get("country");
				} catch (Exception e) {
					player.sendMessage(getinput("passwd.net"));
					return;
				}
				
			}
			
		}	
		InitializationPlayersSQLite(player.uuid,newusr,player.name,ipToLong(ip),GMT,country,Language_determination(country),getLocalTimeFromUTC(GMT),Blank(mail)?mail:"NULL",(String)Passwd_date.get("passwordHash"),(String)Passwd_date.get("salt"));
		if (Vars.state.rules.pvp){
			player.setTeam(netServer.assignTeam(player, playerGroup.all()));
		} else {
			player.setTeam(Team.sharded);
		}
		Call.onPlayerDeath(player);
		PlayerData playerdata = Maps.getPlayer_Data(player.uuid);
		getSQLite(playerdata,newusr);
		playerdata.Joincount++;
		Call.onInfoToast(player.con,getinput("join.start",getLocalTimeFromUTC(GMT,1)),20f);
		
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
		}
	}

	public static void host(String mapp, String gamemodes, Player player) {
		if("sandbox".equalsIgnoreCase(gamemodes)){
		}else if ("pvp".equalsIgnoreCase(gamemodes)){
		}else if ("attack".equalsIgnoreCase(gamemodes)){
		}else if ("survival".equalsIgnoreCase(gamemodes)){
		}else{
			player.sendMessage(getinput("host.mode",gamemodes));
		return;
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

		Map result = maps.all().find(map -> map.name().equalsIgnoreCase(mapp.replace('_', ' ')) || map.name().equalsIgnoreCase(mapp));
		Gamemode preset = Gamemode.survival;
		try{
			preset = Gamemode.valueOf(gamemodes);
		}catch(IllegalArgumentException e){
			return;
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
	}

	public static String timee() {
		return getLocalTimeFromUTC(0,1);
	}

	public static void setting_language() {

	}

}