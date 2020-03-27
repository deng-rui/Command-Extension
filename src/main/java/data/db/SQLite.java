package extension.data.db;

import java.util.*;
import java.sql.*;
//Java

import arc.Core;
//Arc

import extension.data.global.Config;
import extension.util.file.FileUtil;
import extension.util.Log;
//GA-Exted

//Static

import com.alibaba.fastjson.JSONObject;
//Json

public class SQLite {

	public static void InitializationSQLite() {
		try {
			String sql;
			Connection c = DriverManager.getConnection("jdbc:sqlite:"+FileUtil.File(Config.Plugin_Data_Path).getPath("Data.db"));
			Statement stmt = c.createStatement();
			// 时间可以改成BUGINT
			sql = "CREATE TABLE PlayerData (" +
				  "UUID 			TEXT,"+//
				  "User 			TEXT,"+//
				  "NAME 			TEXT,"+//
				  "IP 				INT(4),"+//
				  "GMT 				INT,"+//
				  "Country 			TEXT,"+//
				  "Time_format 		INT,"+//
				  "Language 		CHAR(5),"+//
				  "LastLogin 		INT(4),"+//
				  "Buildcount 		INT,"+
				  "Dismantledcount 	INT,"+
				  "Cumulative_build INT,"+
				  //玩家普通信息
				  "Kickcount		INTEGER,"+
				  //被踢次数
				  "Translate 		TINYINT(1),"+
				  //翻译权限
				  "Level 			INT,"+
				  "Exp 				INT,"+
				  "Reqexp 			INT,"+
				  "Reqtotalexp 		INT,"+
				  //等级
				  "Playtime 		INT(4),"+
				  //游戏时长
				  "Pvpwincount 		INTEGER,"+//
				  "Pvplosecount 	INTEGER,"+//
				  //胜利 输数
				  "Authority 		TINYINT(1),"+//
				  //权限1
				  "Lastchat 		INT(4),"+
				  //最后聊天时间 聊天计数
				  "Deadcount		INTEGER,"+
				  "Killcount 		INTEGER,"+
				  "Joincount 		INTEGER,"+
				  "Breakcount 		INTEGER)"; 
				  //玩家死亡 击杀 加入 退出次数
				  //TEST阶段 仅在GA-PVP使用
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE PlayerPrivate (" +
				  "User 			TEXT,"+
				  "Mail 			CHAR(50),"+
				  "Online 			TINYINT(1),"+
				  "PasswordHash 	TEXT,"+
				  "CSPRNG 			TEXT)";
			//Cryptographically Secure Pseudo-Random Number Generator
			//安全系列3
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
			Log.info("WSQL");
		} catch (Exception e ) {
			Log.fatal("Create SQL",e);
		}
	}

}