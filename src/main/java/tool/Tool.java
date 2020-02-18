package extension.tool;

import static extension.auxiliary.Maps.*;
import static extension.tool.SQLite.player.getSQLite;

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
			case "China":
			return "zh_CN";
			case "Hong Kong":
			return "zh_HK";
			case "Macao":
			return "zh_MO";
			case "Taiwan":
			return "zh_TW";
			case "Russia":
			return "ru-RU";
			default :
			return "en_US";
			//I didn't find a better way....
			}
	}
	
	public static boolean Authority_control(String uuid, String a) {
		System.out.println("?:");
		System.out.println(String.valueOf(getSQLite(uuid).get(19)));
		if(!Player_power_Date_boolean(uuid))setPlayer_power_Date(uuid,Integer.valueOf(String.valueOf(getSQLite(uuid).get(19))).intValue());
		if(!Power_Date_boolean(getPlayer_power_Date(uuid)))return false;
		//System.out.println(getPower_Date(getPlayer_power_Date(uuid)).contains(a));
		return (boolean)getPower_Date(getPlayer_power_Date(uuid)).contains(a);
	}
	//算法写的烂 bug满天飞
	//没啥好办法:(
}