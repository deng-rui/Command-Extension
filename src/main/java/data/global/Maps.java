package extension.data.global;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import extension.data.db.PlayerData;
import extension.util.LocaleUtil;

public class Maps {
	// 安全性 :(
	//玩家数据
	private static final Map<String, PlayerData> Player_Data = new ConcurrentHashMap<String, PlayerData>();
	// 权限内容
	private static final Map<Integer, List<String>> Power_Data = new ConcurrentHashMap<Integer, List<String>>();
	// 投票权重
	private static final Map<Integer, Integer> Vote_Authority = new HashMap<Integer, Integer>();

	private static final Map<String, LocaleUtil> Locale = new ConcurrentHashMap<String, LocaleUtil>(6);

	public static Map getMapPlayer_Data() {
		return Player_Data;
	}
	//
	public static PlayerData getPlayer_Data(String uuid) {
		return Player_Data.get(uuid);
	}
	public static List<String> getPower_Data(int id) {
		return Power_Data.get(id);
	}
	public static int getVote_Authority(int id) {
		return Vote_Authority.get(id);
	}
	public static LocaleUtil getLocale(String id) {
		return Locale.get(id);
	}
	//读取

	public static void setPlayer_Data(String uuid, PlayerData playerdata) {
		Player_Data.put(uuid,playerdata);
	}
	public static void setPower_Data(int id, List<String> list) {
		Power_Data.put(id,list);
	}
	public static void setVote_Authority(int id, int vote) {
		Vote_Authority.put(id,vote);
	}
	public static void setLocale(String id, LocaleUtil locale) {
		Locale.put(id,locale);
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