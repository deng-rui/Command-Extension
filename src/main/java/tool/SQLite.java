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
							"UUID 			TEXT,"+
							"NAME 			TEXT,"+
							"IP 			TEXT,"+
							"GMT 			TEXT,"+
							"Country 		TEXT,"+
							"Language 		TEXT,"+
							"LastLogin 		TEXT,"+
							//玩家普通信息7
							"User 			TEXT,"+
							"PasswordHash 	TEXT,"+
							"CSPRNG 		TEXT,"+
							//Cryptographically Secure Pseudo-Random Number Generator
							//安全系列3
							"Kickcount		INTEGER,"+
							"Sensitive 		INTEGER,"+
							//被踢次数 敏感词总次数2
							"Translate 		BIT,"+
							//翻译权限1
							"Level 			INT,"+
							"Exp 			INT,"+
							"Reqexp 		INT,"+
							"Reqtotalexp 	INT,"+
							//等级4
							"Playtime 		REAL,"+
							//游戏时长1
							"Pvpwincount 	INTEGER,"+
							"Pvplosecount 	INTEGER,"+
							//胜利 输数2
							"Authority 		TEXT,"+
							//权限1
							"Lastchat 		TEXT,"+
							"Chatcount 		INTEGER,"+
							//最后聊天时间 聊天计数2
							"Deadcount		INTEGER,"+
							"Killcount 		INTEGER,"+
							"Joincount 		INTEGER,"+
							"Breakcount 	INTEGER)"; 
							//玩家死亡 击杀 加入 退出次数4
							//TEST阶段 仅在GA-PVP使用
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
				String sql ="INSERT INTO TEST (UUID,NAME,IP,GMT,Country,Language,LastLogin,User,PasswordHash,CSPRNG,Kickcount,Sensitive,Translate,Level,Exp,Reqexp,Reqtotalexp,Playtime,Pvpwincount,Pvplosecount,Authority,Lastchat,Chatcount,Deadcount,Killcount,Joincount,Breakcount) " +
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
				String sql;
				Connection c = connectSQLite();
				c.setAutoCommit(false);
				Statement stmt = c.createStatement();
				System.out.println("Opened database successfully");
				sql ="INSERT INTO TEST (UUID,NAME,IP,GMT,Country,Language,LastLogin,User,PasswordHash,CSPRNG,Kickcount,Sensitive,Translate,Level,Exp,Reqexp,Reqtotalexp,Playtime,Pvpwincount,Pvplosecount,Authority,Lastchat,Chatcount,Deadcount,Killcount,Joincount,Breakcount) " +
					  "VALUES ('ZNSDsdjdemDRtest==','Dr','1.1.1.1','GMT+8','ZH-CN','ZH-CN','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0')"; 
				stmt.executeUpdate(sql);
				System.out.println("1");
				sql ="INSERT INTO TEST (UUID,NAME,IP,GMT,Country,Language,LastLogin,User,PasswordHash,CSPRNG,Kickcount,Sensitive,Translate,Level,Exp,Reqexp,Reqtotalexp,Playtime,Pvpwincount,Pvplosecount,Authority,Lastchat,Chatcount,Deadcount,Killcount,Joincount,Breakcount) " +
					 "VALUES ('ZNSDsdjdXemDRtest==','DXr','1.1.1.1','GMT+8','ZH-CN','ZH-CN','','','','','','','','','','','','','','','','','','','','','')"; 
				stmt.executeUpdate(sql);
				System.out.println("2");
				sql ="INSERT INTO TEST (UUID,NAME,IP,GMT,Country,Language,LastLogin,User,PasswordHash,CSPRNG,Kickcount,Sensitive,Translate,Level,Exp,Reqexp,Reqtotalexp,Playtime,Pvpwincount,Pvplosecount,Authority,Lastchat,Chatcount,Deadcount,Killcount,Joincount,Breakcount) " +
					 "VALUES ('ZNSDsdjdemXXDRtest==','DXXr','1.1.1.1','GMT+8','ZH-CN','ZH-CN','','','','','','','','','','','','','','','','','','','','','')"; 
				stmt.executeUpdate(sql);
				sql ="INSERT INTO TEST (UUID,NAME,IP,GMT,Country,Language,LastLogin,User,PasswordHash,CSPRNG,Kickcount,Sensitive,Translate,Level,Exp,Reqexp,Reqtotalexp,Playtime,Pvpwincount,Pvplosecount,Authority,Lastchat,Chatcount,Deadcount,Killcount,Joincount,Breakcount) " +
					 "VALUES ('ZNSDsdjdXXXemDRtest==','DXXXr','1.1.1.1','GMT+8','ZH-CN','ZH-CN','','','','','','','','','','','','','','','','','','','','','')"; 
				stmt.executeUpdate(sql);
				stmt.close();
				c.commit();
				c.close();
			} catch ( Exception e ) {
				System.out.println(e);
			}
		}

		public static void getSQLite(String name) {
			try {		
				Connection c = connectSQLite();
				c.setAutoCommit(false);
				PreparedStatement stmt = c.prepareStatement("SELECT * FROM TEST WHERE NAME=?");
            	stmt.setString(1,name);
				ResultSet rs = stmt.executeQuery();
				while ( rs.next() ) {
					System.out.println( "UUID = " + rs.getString("UUID"));
					System.out.println( "NAME = " + rs.getString("NAME"));
					System.out.println( "IP = " + rs.getString("IP"));
					System.out.println( "GMT = " + rs.getString("GMT"));
					System.out.println( "Language = " + rs.getString("Language"));
					System.out.println( "UUID = " + rs.getString("User"));
					System.out.println( "NAME = " + rs.getString("Kickcount"));
					System.out.println( "IP = " + rs.getString("Sensitive"));
					System.out.println( "GMT = " + rs.getString("Reqexp"));
					System.out.println( "Language = " + rs.getString("Reqexp"));
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