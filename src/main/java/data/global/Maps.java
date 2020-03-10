package extension.data.global;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Maps {
	// 安全性 :(
	// 违纪数
	private static Map<String, Integer> Player_Sensitive_words = Collections.synchronizedMap(new HashMap<String, Integer>());
	// 玩家进入时缓存时间 /未login 无法查询数据库
	private static Map<String, String> Player_Data_Temp = Collections.synchronizedMap(new HashMap<String, String>());
	// 玩家数据temp 读取-修改-写入
	private static Map<String, List<String>> Player_Data_SQL_Temp = Collections.synchronizedMap(new HashMap<String, List<String>>());
	// 玩家权限
	private static Map<String, Integer> Player_power_Data = Collections.synchronizedMap(new HashMap<String, Integer>());
	// 权限内容
	private static Map<Integer, List> Power_Data = Collections.synchronizedMap(new HashMap<Integer, List>());	

	public static int getPlayer_Sensitive_words_int(String key) {
		return Player_Sensitive_words.get(key);
	}

	public static String getPlayer_Data_Temp(String uuid, String key) {
		return Player_Data_Temp.get(uuid+key);
	}

	public static int getPlayer_power_Data(String uuid) {
		return Player_power_Data.get(uuid);
	}

	public static List getPower_Data(int id) {
		return Power_Data.get(id);
	}

	public static List<String> getPlayer_Data_SQL_Temp(String uuid) {
		return Player_Data_SQL_Temp.get(uuid);
	}
	//读取

	public static void setPlayer_Sensitive_words(String key, int i) {
		Player_Sensitive_words.put(key, i);
	}

	public static void setPlayer_Data_Temp(String uuid, String key, String i) {
		Player_Data_Temp.put(uuid+key, i);
	}

	public static void setPlayer_power_Data(String uuid, int i) {
		Player_power_Data.put(uuid, i);
	}

	public static void setPower_Data(int id, List list) {
		Power_Data.put(id, list);
	}

	public static void setPlayer_Data_SQL_Temp(String uuid, List<String> list) {
		Player_Data_SQL_Temp.put(uuid, list);
	}
	//设置

	public static void removePlayer_Data_Temp(String uuid, String key) {
		Player_Data_Temp.remove(uuid+key);
	}

	public static void removePlayer_Data_SQL_Temp(String uuid) {
		Player_Data_SQL_Temp.remove(uuid);
	}
	//删除

	public static boolean Player_Sensitive_words_boolean(String key) {
		return Player_Sensitive_words.containsKey(key);
	}

	public static boolean Player_Data_Temp_boolean(String uuid, String key) {
		return Player_Data_Temp.containsKey(uuid+key);
	}

	public static boolean Player_power_Data_boolean(String uuid) {
		return Player_power_Data.containsKey(uuid);
	}

	public static boolean Power_Data_boolean(int id) {
		return Power_Data.containsKey(id);
	}
	//是否存在

}