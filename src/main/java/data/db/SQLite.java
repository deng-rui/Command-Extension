package extension.data.db;

import java.util.*;
import java.sql.*;
//Java

import arc.Core;
//Arc

import extension.data.global.Config;
import extension.data.global.Data;
import extension.util.file.FileUtil;
import extension.util.log.Log;
//GA-Exted

//Static

import com.alibaba.fastjson.JSONObject;
//Json

public class SQLite {

	public static void InitializationSQLite() {
		try {
			String sql;
			Connection c = DriverManager.getConnection("jdbc:sqlite:"+FileUtil.File(Data.Plugin_Data_Path).getPath("Data.db"));
			Statement stmt = c.createStatement();
			// 时间可以改成BUGINT
			sql = "CREATE TABLE PlayerData (" +
				  "UUID 					TEXT,"+
				  "User 					TEXT,"+
				  "IP 						BIGINT(4),"+
				  "GMT 						INT,"+
				  "Country 					TEXT,"+
				  "Time_format 				TINYINT(1),"+
				  "Language 				CHAR(5),"+
				  "LastLogin 				BIGINT,"+
				  //
				  "Buildcount 				INTEGER,"+
				  "Dismantledcount 			INTEGER,"+
				  "Cumulative_build 		INTEGER,"+
				  "Pipe_build 				INTEGER,"+
				  //玩家普通信息
				  "Kickcount				INTEGER,"+
				  //被踢次数
				  "Translate 				TINYINT(1),"+
				  //翻译权限
				  "Level 					INTEGER,"+
				  "Exp 						BIGINT,"+
				  "Reqexp 					BIGINT,"+
				  "Reqtotalexp 				BIGINT,"+
				  //等级
				  "Playtime 				BIGINT,"+
				  //游戏时长
				  "Pvpwincount 				INTEGER,"+
				  "Pvplosecount 			INTEGER,"+
				  //胜利 输数
				  "Authority 				TINYINT(1),"+
				  "Authority_effective_time BIGINT,"+
				  //权限1
				  "Lastchat 				BIGINT,"+
				  //最后聊天时间 聊天计数
				  "Deadcount				INTEGER,"+
				  "Killcount 				INTEGER,"+
				  "Joincount 				INTEGER,"+
				  "Breakcount 				INTEGER)"; 
				  //玩家死亡 击杀 加入 退出次数
				  //TEST阶段 仅在GA-PVP使用
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE PlayerPrivate (" +
				  "User 					TEXT,"+
				  "Mail 					CHAR(50),"+
				  "Online 					TINYINT(1),"+
				  "PasswordHash 			TEXT,"+
				  "CSPRNG 					TEXT)";
			//Cryptographically Secure Pseudo-Random Number Generator
			//安全系列3
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE KeyData (" +
				  "KEY 						TEXT,"+
				  "Authority 				TINYINT(1),"+
				  "Total 					TINYINT,"+
				  "Surplus 					TINYINT,"+
				  "Time 					BIGINT,"+
				  "Expire 					BIGINT)";
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
			Log.info("WSQL");
		} catch (Exception e ) {
			Log.fatal("Create SQL",e);
		}
	}

}