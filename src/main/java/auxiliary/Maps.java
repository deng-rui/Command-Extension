package extension.auxiliary;

import java.util.*;

public class Maps {
	private static Map<String, Integer> Player = Collections.synchronizedMap(new HashMap<String, Integer>());
	private static Map<String, String> PlayerDate_Temp = Collections.synchronizedMap(new HashMap<String, String>());
	//Safety! Inefficient :(

	public static int getplayer_int(String key) {
		return Player.get(key);
	}

	public static String getPlayerDate_Temp(String uuid, String key) {
		return PlayerDate_Temp.get(uuid+key);
	}
	//读取

	public static void setplayer(String key, int i) {
		Player.put(key, i);
	}

	public static void setPlayerDate_Temp(String uuid, String key, String i) {
		PlayerDate_Temp.put(uuid+key, i);
	}
	//设置

	public static boolean getplayer_boolean(String key) {
		return Player.containsKey(key);
	}

	public static boolean PlayerDate_Temp_boolean(String uuid, String key) {
		return PlayerDate_Temp.containsKey(uuid+key);
	}
	//是否存在

}