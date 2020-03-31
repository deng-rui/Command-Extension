package extension.core;

import java.lang.Math;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
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

import static extension.core.Extend.loadmaps;
import static extension.data.db.Player.getSQLite;
import static extension.data.db.Player.isSQLite_User;
import static extension.data.db.Player.savePlayer;
import static extension.data.db.Player.InitializationPlayersSQLite;
import static extension.util.alone.Password.newPasswd;
import static extension.util.alone.Password.Passwdverify;
import static extension.util.DateUtil.getLocalTimeFromUTC;
import static extension.util.IsBlankUtil.Blank;
import static extension.util.LocaleUtil.getinput;
import static extension.util.LocaleUtil.Language_determination;
//Static

public class ClientCommandsx {

	public static void login(Player player, String usr, String pw) {
		PlayerData playerdata = Maps.getPlayer_Data(player.uuid);
		if(playerdata.Login) {
			player.sendMessage(getinput("login.yes"));
			return;
		}
		if((boolean)isSQLite_User(usr)) {
			player.sendMessage(getinput("login.usrno"));
			return;
		}
		PlayerData temp = new PlayerData("temp","temp",0);
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
		getSQLite(playerdata,usr);
		playerdata.Joincount++;
		playerdata.Login=true;
		playerdata.LastLogin=getLocalTimeFromUTC(playerdata.GMT);
		if(!(playerdata.UUID).equals(player.uuid)) {
			playerdata.UUID = player.uuid;
			player.sendMessage(getinput("uuid.update"));
		}
		if (Vars.state.rules.pvp){
			player.setTeam(netServer.assignTeam(player, playerGroup.all()));
		} else {
			player.setTeam(Team.sharded);
		}
		if (Config.Login_Radical) {
			if (Vars.state.rules.pvp)
				player.setTeam(netServer.assignTeam(player, playerGroup.all()));
			else
				player.setTeam(Team.sharded);
			Call.onPlayerDeath(player);
		}
		Call.onInfoToast(player.con,getinput("join.start",getLocalTimeFromUTC(playerdata.GMT,playerdata.Time_format)),20f);
		Maps.setPlayer_Data(player.uuid,playerdata);
	}

	public static void register(Player player, String newusr, String newpw, String renewpw, String mail) {
		PlayerData playerdata = Maps.getPlayer_Data(player.uuid);
		if(playerdata.Login) {
			player.sendMessage(getinput("register.yes"));
			return;
		}
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
		if(!(boolean)Passwd_date.get("resualt")) {
			player.sendMessage(getinput("passwd.err"));
			return;
		}
		if (Config.Login_Radical) {
			if (Vars.state.rules.pvp)
				player.setTeam(netServer.assignTeam(player, playerGroup.all()));
			else
				player.setTeam(Team.sharded);
			Call.onPlayerDeath(player);
		}
		InitializationPlayersSQLite(newusr);	
		playerdata.User=newusr;
		playerdata.Login=true;
		playerdata.Authority=1;
		playerdata.Mail=(mail == null) ? "NULL":mail;
		playerdata.PasswordHash=(String)Passwd_date.get("passwordHash");
		playerdata.CSPRNG=(String)Passwd_date.get("salt");
		playerdata.Joincount++;
		if(!Config.Login_IP) 
			PlayerData.playerip(Maps.getPlayer_Data(player.uuid),player,Vars.netServer.admins.getInfo(player.uuid).lastIP);
		playerdata.LastLogin=getLocalTimeFromUTC(playerdata.GMT);
		Call.onInfoToast(player.con,getinput("join.start",getLocalTimeFromUTC(playerdata.GMT,playerdata.Time_format)),20f);
	}

	public static void ftpasswd(Player player, String mail, String cod) {
		//not 
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

	public static void host(Player player, String mapss) {
		if (!(Lists.getMaps_List().size() >= Integer.parseInt(mapss))) {
			player.sendMessage(getinput("vote.host.maps.err",mapss));
			return;
		}
		List<String> MapsList = (List<String>)Lists.getMaps_List();
		String [] data = MapsList.get(Integer.parseInt(mapss)).split("\\s+");
		Map result = maps.all().find(map -> map.name().equalsIgnoreCase(data[0].replace('_', ' ')) || map.name().equalsIgnoreCase(data[0]));
		Gamemode mode = Gamemode.survival;
		try{
			mode = Gamemode.valueOf(data[2]);
		}catch(IllegalArgumentException ex){
			player.sendMessage(getinput("host.mode",data[2]));
			return;
		}
		final Gamemode gamemode = mode;
		Call.sendMessage(getinput("host.re"));
		loadmaps(true, () -> world.loadMap(result, result.applyRules(gamemode)),gamemode);
	}

	public static String timee() {
		return getLocalTimeFromUTC(0,1);
	}

	public static void setting_language() {

	}

}