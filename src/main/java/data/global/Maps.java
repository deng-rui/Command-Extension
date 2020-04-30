package extension.data.global;

import extension.data.db.PlayerData;
import extension.util.LocaleUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Maps {
	// 安全性 :(
	//玩家数据
	private static final Map<String, PlayerData> Player_Data = new ConcurrentHashMap<String, PlayerData>();
	// 权限内容
	private static final Map<Integer, List<String>> Power_Data = new ConcurrentHashMap<Integer, List<String>>();
	// 投票权重
	private static final Map<Integer, Integer> Vote_Authority = new HashMap<Integer, Integer>();

	private static final Map<String, LocaleUtil> Locale = new ConcurrentHashMap<String, LocaleUtil>(6);


    final public static Map getMapPlayer_Data() {
		return Player_Data;
	}
	//

    final public static PlayerData getPlayer_Data(String uuid) {
		return Player_Data.get(uuid);
	}

    final public static List<String> getPower_Data(int id) {
		return Power_Data.get(id);
	}

    final public static int getVote_Authority(int id) {
		return Vote_Authority.get(id);
	}
	final public static LocaleUtil getLocale(String id) {
		return Locale.get(id);
	}
	//读取


    final public static void setPlayer_Data(String uuid, PlayerData playerdata) {
		Player_Data.put(uuid,playerdata);
	}

    final public static void setPower_Data(int id, List<String> list) {
		Power_Data.put(id,list);
	}

    final public static void setVote_Authority(int id, int vote) {
		Vote_Authority.put(id,vote);
	}
	final public static void setLocale(String id, LocaleUtil locale) {
		Locale.put(id,locale);
	}
	//设置


    final public static void removePlayer_Data(String uuid) {
		Player_Data.remove(uuid);
	}
	//删除


    final public static boolean Player_Data_boolean(String uuid) {
		return Player_Data.containsKey(uuid);
	}

    final public static boolean Power_Data_boolean(int id) {
		return Power_Data.containsKey(id);
	}
	//是否存在

}