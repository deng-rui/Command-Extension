package extension.auxiliary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Maps {
	private static Map<String, Integer> Player_Sensitive_words = Collections.synchronizedMap(new HashMap<String, Integer>());
	private static Map<String, String> PlayerDate_Temp = Collections.synchronizedMap(new HashMap<String, String>());
	private static Map<String, Integer> Player_power_Date = Collections.synchronizedMap(new HashMap<String, Integer>());
	private static Map<Integer, List> Power_Date = Collections.synchronizedMap(new HashMap<Integer, List>());
	//Safety! Inefficient :(

	public static int getPlayer_Sensitive_words_int(String key) {
		return Player_Sensitive_words.get(key);
	}

	public static String getPlayerDate_Temp(String uuid, String key) {
		return PlayerDate_Temp.get(uuid+key);
	}

	public static int getPlayer_power_Date(String uuid) {
		return Player_power_Date.get(uuid);
	}

	public static List getPower_Date(String uuid) {
		return Power_Date.get(uuid);
	}
	//读取

	public static void setPlayer_Sensitive_words(String key, int i) {
		Player_Sensitive_words.put(key, i);
	}

	public static void setPlayerDate_Temp(String uuid, String key, String i) {
		PlayerDate_Temp.put(uuid+key, i);
	}

	public static void setPlayer_power_Date(String uuid, int i) {
		Player_power_Date.put(uuid, i);
	}

	public static void setPower_Date(int i, List list) {
		Power_Date.put(i, list);
	}
	//设置

	public static boolean Player_Sensitive_words_boolean(String key) {
		return Player_Sensitive_words.containsKey(key);
	}

	public static boolean PlayerDate_Temp_boolean(String uuid, String key) {
		return PlayerDate_Temp.containsKey(uuid+key);
	}

	public static boolean Player_power_Date_boolean(String uuid) {
		return Player_power_Date.containsKey(uuid);
	}
	//是否存在

}