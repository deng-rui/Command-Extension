package extension.auxiliary;

import java.util.*;

//@SuppressWarnings("unchecked")
public class Maps {
	private static Map<String, Integer> player = Collections.synchronizedMap(new HashMap<String, Integer>());
	//Safety! Inefficient :(

	public static int getplayer_int(String type) {
		return player.get(type);
	}
	//读取
	public static void setplayer(String key, int i) {
		player.put(key, i);
	}
	//
	public static boolean getplayer_boolean(String key) {
		return player.containsKey(key);
	}

}