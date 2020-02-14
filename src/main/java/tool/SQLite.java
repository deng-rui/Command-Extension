package extension.tool;

import arc.*;
import arc.Core;
import java.sql.*;

public class SQLite {

	//map
	public static class player {
		public static void InitializationSQLite() {
			try {
				Connection c = connectSQLite();
				Statement stmt = c.createStatement();
				System.out.println("Opened database successfully");
				String sql ="CREATE TABLE TEST (" +
							"UUID 			TEXT 	NOT NULL,"+
							"NAME 			TEXT 	NOT NULL,"+
							"IP 			TEXT 	NOT NULL,"+
							"GMT 			TEXT 	NOT NULL,"+
							"Country 		TEXT 	NOT NULL,"+
							"Language 		TEXT 	NOT NULL,"+
							//玩家普通信息
							"Password 		TEXT 	NOT NULL,"+
							"CSPRNG 		TEXT 	NOT NULL,"+
							//Cryptographically Secure Pseudo-Random Number Generator
							//安全系列
							"Kickcount		INT 	DEFAULT 0,"+
							"Sensitive 		INT 	DEFAULT 0,"+
							//被踢次数 敏感词总次数
							"Translate 		BIT 	DEFAULT 0,"+
							//翻译权限
							"Level 			INT 	DEFAULT 0,"+
							"Exp 			INT 	DEFAULT 0,"+
							"Reqexp 		INT 	DEFAULT 0,"+
							"Reqtotalexp 	INT 	DEFAULT 0,"+
							//等级
							"Playtime 		REAL 	DEFAULT 0,"+
							//游戏时长
							"Pvpwincount 	INT 	DEFAULT 0,"+
							"Pvplosecount 	INT 	DEFAULT 0,"+
							//胜利 输数
							"Authority 		TEXT 	NOT NULL,"+
							//权限
							"Lastchat 		TEXT 	NOT NULL,"+
							"Chatcount 		INT 	DEFAULT 0,"+
							//最后聊天时间 聊天计数
							"Deadcount		INT 	DEFAULT 0,"+
							"Killcount 		INT 	DEFAULT 0,"+
							"Joincount 		INT 	DEFAULT 0,"+
							"Breakcount 	INT 	DEFAULT 0)"; 
							//玩家死亡 击杀 加入 退出次数-4
							//TEST阶段 仅在GA-PVP使用-24
				stmt.executeUpdate(sql);
				stmt.close();
				c.close();
			} catch ( Exception e ) {
			}
		}

		public static void InitializationPlayersSQLite(String UUID,String NAME,String IP,String GMT,String Country,String Language) {
			try {
				Connection c = connectSQLite();
				c.setAutoCommit(false);
				Statement stmt = c.createStatement();
				String sql ="INSERT INTO TEST (UUID,NAME,IP,GMT,Country,Language,Password,CSPRNG,Kickcount,Sensitive,Translate,Level,Exp,Reqexp,Reqtotalexp,Playtime,Pvpwincount,Pvplosecount,Authority,Lastchat,Chatcount,Deadcount,Killcount,Joincount,Breakcount) " +
							"VALUES ("+UUID+","+NAME+","+IP+","+GMT+","+Country+","+Language+",'','','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0')"; 
				stmt.executeUpdate(sql);
				stmt.close();
				c.commit();
				c.close();
			} catch ( Exception e ) {
			}
		}

		public static void addSQLite() {
			try {
				Connection c = connectSQLite();
				c.setAutoCommit(false);
				Statement stmt = c.createStatement();
				String sql ="INSERT INTO TEST (UUID,NAME,IP,GMT,Country,Language,Password,CSPRNG,Kickcount,Sensitive,Translate,Level,Exp,Reqexp,Reqtotalexp,Playtime,Pvpwincount,Pvplosecount,Authority,Lastchat,Chatcount,Deadcount,Killcount,Joincount,Breakcount) " +
						  "VALUES ('ZNSDsdjdemDRtest==','Dr','1.1.1.1','GMT+8','ZH-CN','ZH-CN','','','','','','1','','','','','','','','','','','','','')"; 
				stmt.executeUpdate(sql);
				stmt.close();
				c.commit();
				c.close();
			} catch ( Exception e ) {
			}
		}

		public static void getSQLite() {
			try {		
				Connection c = connectSQLite();
				c.setAutoCommit(false);
				Statement stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM TEST");
				while ( rs.next() ) {
					System.out.println( "UUID = " + rs.getString("UUID"));
					System.out.println( "NAME = " + rs.getString("NAME"));
					System.out.println( "IP = " + rs.getString("IP"));
					System.out.println( "GMT = " + rs.getString("GMT"));
					System.out.println( "Language = " + rs.getString("Language"));
					System.out.println();
				}
				rs.close();
				stmt.close();
				c.close();
			} catch ( Exception e ) {
			}
		}

		
	}

	public static class setting {

	}

	public static Connection connectSQLite() {
			try {
				Connection c = DriverManager.getConnection("jdbc:sqlite:"+Core.settings.getDataDirectory().child("mods/GA/TEST.db"));
				return c;
			} catch ( Exception e ) {
			}
			return null;
		}

}