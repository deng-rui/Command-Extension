package extension.data.global;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import extension.data.db.PlayerData;

public class Maps {
	// 安全性 :(
	//玩家数据
	private static final Map<String, PlayerData> Player_Data = Collections.synchronizedMap(new HashMap<String, PlayerData>());
	// 权限内容
	private static final Map<Integer, List<String>> Power_Data = Collections.synchronizedMap(new HashMap<Integer, List<String>>());	

	public static Map getMapPlayer_Data() {
		return Player_Data;
	}
	public static PlayerData getPlayer_Data(String uuid) {
		return Player_Data.get(uuid);
	}
	public static List<String> getPower_Data(int id) {
		return Power_Data.get(id);
	}
	//读取

	public static void setPlayer_Data(String uuid, PlayerData playerdata) {
		Player_Data.put(uuid,playerdata);
	}
	public static void setPower_Data(int id, List<String> list) {
		Power_Data.put(id, list);
	}
	//设置

	public static void removePlayer_Data(String uuid) {
		Player_Data.remove(uuid);
	}
	//删除

	public static boolean Player_Data_boolean(String uuid) {
		return Player_Data.containsKey(uuid);
	}
	public static boolean Power_Data_boolean(int id) {
		return Power_Data.containsKey(id);
	}
	//是否存在

}