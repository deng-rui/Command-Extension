package extension.data.db;

import com.alibaba.fastjson.JSONObject;
import extension.data.global.Config;
import extension.data.global.Maps;
import extension.util.LocaleUtil;
import mindustry.entities.type.Player;

import java.util.TimeZone;
import java.util.regex.Pattern;

import static extension.net.HttpRequest.doGet;
import static extension.util.DateUtil.getLocalTimeFromU;
import static extension.util.ExtractUtil.languageDetermination;
import static extension.util.ExtractUtil.ipToLong;

public class PlayerData {
	// 奇怪的public

    public String uuid;
    public String user;
    public String name;
    public String mail;
	//
    public long ip;
    public int gmt;
    public String country;
    public byte timeFormat;
    public String language;
    public long lastLogin;
    public int kickCount;
	//int Sensitive;
    public boolean translate;
    public int level;
	// MAX = 32767
    public short exp;
    public long reqexp;
    public long reqtotalExp;
    public long playTime;
    public int pvpwinCount;
    public int pvploseCount;
    public int authority;
    public long authorityEffectiveTime;
    public long lastChat;
    public int deadCount;
    public int killCount;
    public int joinCount;
    public int breakCount;
	//
    public int buildCount;
    public int dismantledCount;
    public int cumulativeBuild;
    public int pipeBuild;
	/* */
    public boolean online;
    public String passwordHash;
    public String csprng;
	/* */
    public boolean login;
    public long joinTime;
    public long backTime;
    public LocaleUtil info;
	private final static Pattern REG = Pattern.compile("^(127\\.0\\.0\\.1)|(localhost)|(10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|(172\\.((1[6-9])|(2\\d)|(3[01]))\\.\\d{1,3}\\.\\d{1,3})|(192\\.168\\.\\d{1,3}\\.\\d{1,3})$");

    public PlayerData(String uuid, String nae, int authority) {
		this.uuid 					= uuid;
		user 						= "NO Login";
		this.name 					= name;
		mail 						= "NO Login";
		//
		ip 							= 0;
		gmt 						= 0;
		country 					= "NO Login";
		language 					= Config.SERVER_LANGUAGE;
		timeFormat 					= 1;
		lastLogin 					= 0;
		/* */
		kickCount 					= 0;
		//int sensitive;
		translate 					= false;
		level 						= 0;
		exp 						= 0;
		reqexp 						= 0;
		reqtotalExp 				= 0;
		playTime 					= 0;
		pvpwinCount 				= 0;
		pvploseCount 				= 0;
		this.authority 				= authority;
		authorityEffectiveTime 		= 0;
		lastChat 					= 0;
		deadCount 					= 0;
		killCount 					= 0;
		joinCount 					= 1;
		breakCount 					= 0;
		//
		buildCount 					= 0;
		dismantledCount 			= 0;
		cumulativeBuild 			= 0;
		pipeBuild 					= 0;
		/* */
		online 						= true;
		passwordHash 				= null;
		csprng 						= null;
		/* */
		login 						= false;
		joinTime 					= getLocalTimeFromU();
		backTime 					= 0;
		info 						= Maps.getLocale(Config.SERVER_LANGUAGE);
	}

	public static void playerip(PlayerData data,Player player,String ip) {
		if(REG.matcher(ip).find()) {
			data.country = "Intranet";
			player.sendMessage(data.info.getinput("register.ip.nat"));
		}else{
			try {
				JSONObject result = JSONObject.parseObject(doGet("http://ip-api.com/json/"+ip+"?fields=country,timezone"));
				data.gmt = TimeZone.getTimeZone((String)result.get("timezone")).getRawOffset();
				data.country = (String)result.get("country");
			} catch (Exception e) {
				data.gmt 						= 0;
				data.country 					= Config.SERVER_LANGUAGE;
				player.sendMessage(data.info.getinput("Passwd.Net",data.country));
			}
		}
		data.ip 					= ipToLong(ip);
		data.language 				= languageDetermination(data.country);
		data.info 					= Maps.getLocale(data.language);
		player.sendMessage(data.info.getinput("Load.Language",data.language));
	}
}
	