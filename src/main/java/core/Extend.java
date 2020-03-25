package extension.core;

import extension.data.global.Maps;
//GA-Exted

import static extension.data.db.Player.getSQLite_UUID;
//Static

public class Extend {
	public static boolean Authority_control(String uuid, String a) {
		//if(!Maps.Player_power_Data_boolean(uuid))Maps.setPlayer_power_Data(uuid,Integer.valueOf(String.valueOf(getSQLite_UUID(uuid).get(SQL_type("Authority")))).intValue());
		//查询玩家权限指示是否存在  否则数据库中读取玩家权限并设置
		if(!Maps.Power_Data_boolean(Maps.getPlayer_power_Data(uuid)))return false;
		//查询权限数字是否是有效
		return (boolean)Maps.getPower_Data(Maps.getPlayer_power_Data(uuid)).contains(a);
		//查询当前获取的权限是否足够使用 (List)
		//套娃（；´д｀）ゞ
	}
	//算法写的烂 bug满天飞
	//没啥好办法:(
}