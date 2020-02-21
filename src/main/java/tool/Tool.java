package extension.tool;

import static extension.auxiliary.Maps.*;
import static extension.tool.SQLite.player.getSQLite_UUID;

public class Tool {
	public static boolean Blank(String string) {
		if (string == null || "".equals(string.trim())) {
			return true;
		}
		return false;
	}

	public static boolean NotBlank(String string) {
		return !Blank(string);
	}

	public static String Language_determination(String string) {
		switch(string){
			case "China" :return "zh_CN";
			case "Hong Kong" :return "zh_HK";
			case "Macao" :return "zh_MO";
			case "Taiwan" :return "zh_TW";
			case "Russia" :return "ru-RU";
			default :return "en_US";
			//I didn't find a better way....
			}
	}
	
	public static boolean Authority_control(String uuid, String a) {
		if(!Player_power_Data_boolean(uuid))setPlayer_power_Data(uuid,Integer.valueOf(String.valueOf(getSQLite_UUID(uuid).get(SQL_type("Authority")))).intValue());
		if(!Power_Data_boolean(getPlayer_power_Data(uuid)))return false;
		return (boolean)getPower_Data(getPlayer_power_Data(uuid)).contains(a);
	}
	//算法写的烂 bug满天飞
	//没啥好办法:(

	public static int SQL_type(String string) {
		switch(string){
			case "UUID" :return 0;
			case "NAME" :return 1;
			case "IP" :return 2;
			case "GMT" :return 3;
			case "Country" :return 4;
			case "Time_format" :return 5;
			case "Language" :return 6;
			case "LastLogin" :return 7;
			case "User" :return 8;
			case "PasswordHash" :return 9;
			case "CSPRNG" :return 10;
			case "Kickcount" :return 11;
			case "Sensitive" :return 12;
			case "Translate" :return 13;
			case "Level" :return 14;
			case "Exp" :return 15;
			case "Reqexp" :return 16;
			case "Reqtotalexp" :return 17;
			case "Playtime" :return 18;
			case "Pvpwincount" :return 19;
			case "Pvplosecount" :return 20;
			case "Authority" :return 21;
			case "Lastchat" :return 22;
			case "Deadcount" :return 23;
			case "Killcount" :return 24;
			case "Joincount" :return 25;
			case "Breakcount" :return 26;
			//I didn't find a better way....
		}
		return 100;
	}
}