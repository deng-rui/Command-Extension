package extension.auxiliary;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Maps {
	private static Map<String, Integer> Player_Sensitive_words = Collections.synchronizedMap(new HashMap<String, Integer>());
	private static Map<String, String> PlayerDate_Temp = Collections.synchronizedMap(new HashMap<String, String>());
	private static Map<String, Integer> Player_power_Date_Temp = Collections.synchronizedMap(new HashMap<String, Integer>());
	//Safety! Inefficient :(

	public static int getPlayer_Sensitive_words_int(String key) {
		return Player_Sensitive_words.get(key);
	}

	public static String getPlayerDate_Temp(String uuid, String key) {
		return PlayerDate_Temp.get(uuid+key);
	}

	public static String getPlayer_power_Date_Temp(String uuid) {
		return Player_power_Date_Temp.get(uuid);
	}
	//读取

	public static void setPlayer_Sensitive_words(String key, int i) {
		Player_Sensitive_words.put(key, i);
	}

	public static void setPlayerDate_Temp(String uuid, String key, String i) {
		PlayerDate_Temp.put(uuid+key, i);
	}

	public static void setPlayer_power_Date_Temp(String uuid, String i) {
		Player_power_Date_Temp.put(uuid, i);
	}
	//设置

	public static boolean getPlayer_Sensitive_words_boolean(String key) {
		return Player_Sensitive_words.containsKey(key);
	}

	public static boolean PlayerDate_Temp_boolean(String uuid, String key) {
		return PlayerDate_Temp.containsKey(uuid+key);
	}

	public static boolean Player_power_Date_Temp_boolean(String uuid, String key) {
		return Player_power_Date_Temp.containsKey(uuid+key);
	}
	//是否存在

}