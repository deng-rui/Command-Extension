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
	private static final Map<String, PlayerData> PLAYER_DATA = new ConcurrentHashMap<String, PlayerData>();
	// 权限内容
	private static final Map<Integer, List<String>> POWER_DATA = new ConcurrentHashMap<Integer, List<String>>();
	// 投票权重
	private static final Map<Integer, Integer> VOTE_AUTHORITY = new HashMap<Integer, Integer>();

	private static final Map<String, LocaleUtil> LOCALE = new ConcurrentHashMap<String, LocaleUtil>(6);


    final public static Map getMapPlayerData() {
		return PLAYER_DATA;
	}
	//

    final public static PlayerData getPlayerData(String uuid) {
		return PLAYER_DATA.get(uuid);
	}

    final public static List<String> getPowerData(int id) {
		return POWER_DATA.get(id);
	}

    final public static int getVoteAuthority(int id) {
		return VOTE_AUTHORITY.get(id);
	}
	final public static LocaleUtil getLocale(String id) {
		return LOCALE.get(id);
	}
	//读取


    final public static void setPlayerData(String uuid, PlayerData playerdata) {
		PLAYER_DATA.put(uuid,playerdata);
	}

    final public static void setPowerData(int id, List<String> list) {
		POWER_DATA.put(id,list);
	}

    final public static void setVoteAuthority(int id, int vote) {
		VOTE_AUTHORITY.put(id,vote);
	}
	final public static void setLocale(String id, LocaleUtil locale) {
		LOCALE.put(id,locale);
	}
	//设置


    final public static void removePlayerData(String uuid) {
		PLAYER_DATA.remove(uuid);
	}
	//删除


    final public static boolean isPlayerData(String uuid) {
		return PLAYER_DATA.containsKey(uuid);
	}

    final public static boolean isPowerDataboolean(int id) {
		return POWER_DATA.containsKey(id);
	}
	//是否存在

}