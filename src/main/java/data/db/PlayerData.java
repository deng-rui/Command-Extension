package extension.data.db;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.regex.Pattern;

import mindustry.entities.type.Player;
//

import extension.data.global.Maps;
import extension.util.LocaleUtil;
//

import static extension.net.HttpRequest.doGet;
import static extension.util.DateUtil.getLocalTimeFromUTC;
import static extension.util.ExtractUtil.ipToLong;
import static extension.util.ExtractUtil.Language_determination;
//

import com.alibaba.fastjson.JSONObject;

public class PlayerData {
	// 奇怪的public
	public String UUID;
	public String User;
	public String NAME;
	public String Mail;
	//
	public long IP;
	public int GMT;
	public String Country;
	public byte Time_format;
	public String Language;
	public long LastLogin;
	public int Kickcount;
	//int Sensitive;
	public boolean Translate;
	public int Level;
	// MAX = 32767
	public short Exp;
	public long Reqexp;
	public long Reqtotalexp;
	public long Playtime;
	public int Pvpwincount;
	public int Pvplosecount;
	public int Authority;
	public long Authority_effective_time;
	public long Lastchat;
	public int Deadcount;
	public int Killcount;
	public int Joincount;
	public int Breakcount;
	//
	public int Buildcount;
	public int Dismantledcount;
	public int Cumulative_build;
	public int Pipe_build;
	/* */
	public boolean Online;
	public String PasswordHash;
	public String CSPRNG;
	/* */
	public boolean Login;
	public long Jointime;
	public long Backtime;
	public LocaleUtil Info;

	public PlayerData(String UUID,String NAME,int Authority) {
		this.UUID 					= UUID;
		this.User 					= "NO Login";
		this.NAME 					= NAME;
		this.Mail 					= "NO Login";
		//
		IP 							= 0;
		GMT 						= 0;
		Country 					= "NO Login";
		Language 					= "en_US";
		Time_format 				= 1;
		LastLogin 					= 0;
		/* */
		Kickcount 					= 0;
		//int Sensitive;
		Translate 					= false;
		Level 						= 0;
		Exp 						= 0;
		Reqexp 						= 0;
		Reqtotalexp 				= 0;
		Playtime 					= 0;
		Pvpwincount 				= 0;
		Pvplosecount 				= 0;
		this.Authority 				= Authority;
		Authority_effective_time 	= 0;
		Lastchat 					= 0;
		Deadcount 					= 0;
		Killcount 					= 0;
		Joincount 					= 1;
		Breakcount 					= 0;
		//
		Buildcount 					= 0;
		Dismantledcount 			= 0;
		Cumulative_build 			= 0;
		Pipe_build 					= 0;
		/* */
		Online 						= true;
		PasswordHash 				= null;
		CSPRNG 						= null;
		/* */
		Login 						= false;
		Jointime 					= getLocalTimeFromUTC();
		Backtime 					= 0;
		Info 						= Maps.getLocale("en_US");
	}

	public static void playerip(PlayerData data,Player player,String ip) {
		Pattern reg = Pattern.compile("^(127\\.0\\.0\\.1)|(localhost)|(10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|(172\\.((1[6-9])|(2\\d)|(3[01]))\\.\\d{1,3}\\.\\d{1,3})|(192\\.168\\.\\d{1,3}\\.\\d{1,3})$");
		if(reg.matcher(ip).find()) {
			data.Country = "Intranet";
			player.sendMessage(data.Info.getinput("register.ip.nat"));
		}else{
			try {
				JSONObject result = JSONObject.parseObject(doGet("http://ip-api.com/json/"+ip+"?fields=country,timezone"));
				data.GMT = TimeZone.getTimeZone((String)result.get("timezone")).getRawOffset();
				data.Country = (String)result.get("country");
			} catch (Exception e) {
				player.sendMessage(data.Info.getinput("passwd.net"));
			}
		}
		data.IP 					= ipToLong(ip);
		data.Language 				= Language_determination(data.Country);
		data.Info 					= Maps.getLocale(data.Language);
		player.sendMessage(data.Info.getinput("Load.Language",data.Language));
	}
}
	