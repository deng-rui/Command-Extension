package extension.core;

import java.util.TimeZone;
//Java

import mindustry.game.Team;
//Mindustry

import mindustry.entities.type.Player;
import mindustry.gen.Call;
import mindustry.Vars;
//Mindustry

import extension.util.GoogletranslateApi;
//GA-Exted

import static extension.data.global.Maps.*;
import static extension.data.json.Json.getData;
import static extension.net.HttpRequest.doGet;
import static extension.util.LocaleUtil.getinput;
//Static

import com.alibaba.fastjson.JSONObject;
//Json

public class Event {

	public static String PlayerChatEvent_translate(String check, String text) {
		GoogletranslateApi googletranslateApi = new GoogletranslateApi();
		if(!check.equals("/")) {
			boolean valid = text.matches("\\w+");
			JSONObject date = getData("mods/GA/setting.json");
			boolean translateo = (boolean) date.get("translateo");
			// check if enable translate
			if (!valid && translateo) {
				try{
					Thread.currentThread().sleep(2000);
					String translationa = googletranslateApi.translate(text,"en");
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
		if (!Player_Sensitive_words_boolean(player.uuid)) {
			setPlayer_Sensitive_words(player.uuid, 1);
			player.sendMessage(getinput("Sensitive.Thesaurus.info",String.valueOf(getPlayer_Sensitive_words_int(player.uuid)),text));
		}else{
			setPlayer_Sensitive_words(player.uuid, getPlayer_Sensitive_words_int(player.uuid)+1);
			player.sendMessage(getinput("Sensitive.Thesaurus.info",String.valueOf(getPlayer_Sensitive_words_int(player.uuid)),text));
		}
		if (3 <= getPlayer_Sensitive_words_int(player.uuid)) {
			Call.onKick(player.con, getinput("Sensitive.Thesaurus.message.kick",text));
			setPlayer_Sensitive_words(player.uuid,0);
		}
		//player.sendMessage(getinput("Sensitive_Thesaurus",player.name));
	}

	public static void PlayerJoin_Logins(Player player) {
		try {
			Thread.currentThread().sleep(2500);
		}catch(InterruptedException ie){
			ie.printStackTrace();
		} 
		player.setTeam(Team.derelict);
		Call.onPlayerDeath(player);
		//Call.onInfoMessage();
		setPlayer_power_Data(player.uuid,0);
		//Call.onInfoToast(player.con,getinput("join.tourist",String.valueOf(TimeZone.getTimeZone((String)doGet("http://ip-api.com/line/"+Vars.netServer.admins.getInfo(player.uuid).lastIP+"?fields=timezone")).getRawOffset())),20f);
	}

	public static void GameOverEvent_PVP_Data() {
	//
	}

}