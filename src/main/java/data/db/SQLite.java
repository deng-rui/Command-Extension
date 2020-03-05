package extension.data.db;

import java.util.*;
import java.sql.*;
//Java

import arc.Core;
//Arc

import static extension.data.db.Player.getSQLite_UUID;
import static extension.data.global.Maps.getPlayer_power_Data;
import static extension.data.global.Maps.getPower_Data;
import static extension.data.global.Maps.Player_power_Data_boolean;
import static extension.data.global.Maps.Power_Data_boolean;
import static extension.data.global.Maps.setPlayer_power_Data;
import static extension.data.global.Maps.setPower_Data;
import static extension.data.json.Json.getData;
//Static

import com.alibaba.fastjson.JSONObject;
//Java

public class SQLite {

	public static void InitializationSQLite() {
		try {
			String sql;
			Connection c = connectSQLite();
			Statement stmt = c.createStatement();
			//sql = "CREATE TABLE Players (" +
			sql = "CREATE TABLE Player (" +
				  "UUID 			TEXT,"+//
				  "NAME 			TEXT,"+//
				  "IP 				TEXT,"+//
				  "GMT 				TEXT,"+//
				  "Country 			TEXT,"+//
				  "Time_format 		TEXT,"+//
				  "Language 		TEXT,"+//
				  "LastLogin 		TEXT,"+//
				  //玩家普通信息8
				  "User 			TEXT,"+//
				  "PasswordHash 	TEXT,"+//
				  "CSPRNG 			TEXT,"+//
				  //Cryptographically Secure Pseudo-Random Number Generator
				  //安全系列3
				  "Kickcount		INTEGER,"+
				  "Sensitive 		INTEGER,"+
				  //被踢次数 敏感词总次数2
				  "Translate 		BIT,"+
				  //翻译权限1
				  "Level 			INT,"+
				  "Exp 				INT,"+
				  "Reqexp 			INT,"+
				  "Reqtotalexp 		INT,"+
				  //等级4
				  "Playtime 		REAL,"+
				  //游戏时长1
				  "Pvpwincount 		INTEGER,"+//
				  "Pvplosecount 	INTEGER,"+//
				  //胜利 输数2
				  "Authority 		INTEGER,"+//
				  //权限1
				  "Lastchat 		TEXT,"+
				  //最后聊天时间 聊天计数2
				  "Deadcount		INTEGER,"+
				  "Killcount 		INTEGER,"+
				  "Joincount 		INTEGER,"+
				  "Breakcount 		INTEGER)"; 
				  //玩家死亡 击杀 加入 退出次数4
				  //TEST阶段 仅在GA-PVP使用27
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE Settings (" +
				  "Language 		TEXT)";
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch ( Exception e ) {
		}
	}

	public static int SQL_type(String string) {
		switch(string){
			case "UUID" :return 0;
			case "NAME" :return 1;
			case "IP" :return 2;
			case "GMT" :return 3;
			case "Country" :return 4;
			case "Time_format" :return 5;
			case "Language" :return 6;
			case "LastLogin" :return 7;
			case "User" :return 8;
			case "PasswordHash" :return 9;
			case "CSPRNG" :return 10;
			case "Kickcount" :return 11;
			case "Sensitive" :return 12;
			case "Translate" :return 13;
			case "Level" :return 14;
			case "Exp" :return 15;
			case "Reqexp" :return 16;
			case "Reqtotalexp" :return 17;
			case "Playtime" :return 18;
			case "Pvpwincount" :return 19;
			case "Pvplosecount" :return 20;
			case "Authority" :return 21;
			case "Lastchat" :return 22;
			case "Deadcount" :return 23;
			case "Killcount" :return 24;
			case "Joincount" :return 25;
			case "Breakcount" :return 26;
			//I didn't find a better way....
		}
		return 100;
	}

	public static boolean Authority_control(String uuid, String a) {
		if(!Player_power_Data_boolean(uuid))setPlayer_power_Data(uuid,Integer.valueOf(String.valueOf(getSQLite_UUID(uuid).get(SQL_type("Authority")))).intValue());
		//查询玩家权限指示是否存在  否则数据库中读取玩家权限并设置
		if(!Power_Data_boolean(getPlayer_power_Data(uuid)))return false;
		//查询权限数字是否是有效
		return (boolean)getPower_Data(getPlayer_power_Data(uuid)).contains(a);
		//查询当前获取的权限是否足够使用 (List)
		//套娃（；´д｀）ゞ
	}
	//算法写的烂 bug满天飞
	//没啥好办法:(

	public static void Player_Privilege_classification() {
		JSONObject date = getData("mods/GA/Authority.json");
		for (int i = 0; i < 11; i++) {
			setPower_Data(i,(List)date.get(i));
		}
	}

	public static Connection connectSQLite() {
		try {
			Connection c = DriverManager.getConnection("jdbc:sqlite:"+Core.settings.getDataDirectory().child("mods/GA/Data.db"));
			return c;
		} catch ( Exception e ) {
		}
		return null;
	}

}