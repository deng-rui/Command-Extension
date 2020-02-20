package extension.auxiliary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Maps {
	private static Map<String, Integer> Player_Sensitive_words = Collections.synchronizedMap(new HashMap<String, Integer>());
	//违纪数
	private static Map<String, String> PlayerData_Temp = Collections.synchronizedMap(new HashMap<String, String>());
	//玩家进入时缓存时间 /未login 无法查询数据库
	private static Map<String, List> PlayerData_SQL_Temp = Collections.synchronizedMap(new HashMap<String, List>());
	//玩家数据temp 读取-修改-写入
	private static Map<String, Integer> Player_power_Data = Collections.synchronizedMap(new HashMap<String, Integer>());
	//玩家权限
	private static Map<Integer, List> Power_Data = Collections.synchronizedMap(new HashMap<Integer, List>());
	//权限内容
	//Safety! Inefficient :(

	public static int getPlayer_Sensitive_words_int(String key) {
		return Player_Sensitive_words.get(key);
	}

	public static String getPlayerData_Temp(String uuid, String key) {
		return PlayerData_Temp.get(uuid+key);
	}

	public static int getPlayer_power_Data(String uuid) {
		return Player_power_Data.get(uuid);
	}

	public static List getPower_Data(int id) {
		return Power_Data.get(id);
	}

	public static List getPlayerData_SQL_Temp(String uuid) {
		return PlayerData_SQL_Temp.get(uuid);
	}
	//读取

	public static void setPlayer_Sensitive_words(String key, int i) {
		Player_Sensitive_words.put(key, i);
	}

	public static void setPlayerData_Temp(String uuid, String key, String i) {
		PlayerData_Temp.put(uuid+key, i);
	}

	public static void setPlayer_power_Data(String uuid, int i) {
		Player_power_Data.put(uuid, i);
	}

	public static void setPower_Data(int id, List list) {
		Power_Data.put(id, list);
	}

	public static void setPlayerData_SQL_Temp(String uuid, List list) {
		PlayerData_SQL_Temp.put(uuid, list);
	}
	//设置

	public static void removePlayerData_Temp(String uuid, String key) {
		PlayerData_Temp.remove(uuid+key);
	}

	public static void removePlayerData_SQL_Temp(String uuid) {
		PlayerData_SQL_Temp.remove(uuid);
	}

	//删除

	public static boolean Player_Sensitive_words_boolean(String key) {
		return Player_Sensitive_words.containsKey(key);
	}

	public static boolean PlayerData_Temp_boolean(String uuid, String key) {
		return PlayerData_Temp.containsKey(uuid+key);
	}

	public static boolean Player_power_Data_boolean(String uuid) {
		return Player_power_Data.containsKey(uuid);
	}

	public static boolean Power_Data_boolean(int id) {
		return Power_Data.containsKey(id);
	}
	//是否存在

}