package extension.tool;

import arc.Core;
import java.sql.*;

public class SQLite {
	
	public static void InitializationSQLite() {
		Statement stmt = null;
		try {
			Connection c = connectSQLite();
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			String sql ="CREATE TABLE TEST (" +
						"UUID 			TEXT 	NOT NULL,"+
						"NAME 			TEXT 	NOT NULL,"+
						"IP 			TEXT 	NOT NULL,"+
						"GMT 			TEXT 	NOT NULL,"+
						"Country 		TEXT 	NOT NULL,"+
						"Language 		TEXT 	NOT NULL,"+
						//玩家普通信息-6
						"Kickcount		INT 	DEFAULT 0,"+
						"Sensitive 		INT 	DEFAULT 0,"+
						//被踢 敏感词次数-2
						"Translate 		BIT 	DEFAULT 0,"+
						//翻译权限-2
						"Level 			INT 	DEFAULT 0,"+
						"Exp 			INT 	DEFAULT 0,"+
						"Reqexp 		INT 	DEFAULT 0,"+
						"Reqtotalexp 	INT 	DEFAULT 0,"+
						//等级-4
						"Playtime 		REAL 	DEFAULT 0,"+
						//游戏时长-1
						"Pvpwincount 	INT 	DEFAULT 0,"+
						"Pvplosecount 	INT 	DEFAULT 0,"+
						//胜利 输数-2
						"Authority 		TEXT 	NOT NULL,"+
						//权限-1
						"Lastchat 		TEXT 	NOT NULL,"+
						"Chatcount 		INT 	DEFAULT 0,"+
						//聊天计数-2
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
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		System.out.println("Table created successfully");
	}

	public static void InitializationPlayers(String UUID,String NAME,String IP,String GMT,String Country,String Language) {
		Statement stmt = null;
		String sql;
		try {
			Connection c = connectSQLite();
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");
			stmt = c.createStatement();
			sql ="INSERT INTO TEST (UUID,NAME,IP,GMT,Country,Language,Kickcount,Sensitive,Translate,Level,Exp,Reqexp,Reqtotalexp,Playtime,Pvpwincount,Pvplosecount,Authority,Lastchat,Chatcount,Deadcount,Killcount,Joincount,Breakcount) " +
					  "VALUES ("+UUID+","+NAME+","+IP+","+GMT+","+Country+","+Language+",'','','1','','','','','','','','','','','','','')"; 
			stmt.executeUpdate(sql);
		  stmt.close();
		  c.commit();
		  c.close();
		} catch ( Exception e ) {
		  System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		  System.exit(0);
		}
		System.out.println("Records created successfully");
	}

	public static void addSQLite() {
		Statement stmt = null;
		String sql;
		try {
			Connection c = connectSQLite();
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");
			stmt = c.createStatement();
			sql ="INSERT INTO TEST (UUID,NAME,IP,GMT,Country,Language,Kickcount,Sensitive,Translate,Level,Exp,Reqexp,Reqtotalexp,Playtime,Pvpwincount,Pvplosecount,Authority,Lastchat,Chatcount,Deadcount,Killcount,Joincount,Breakcount) " +
					  "VALUES ('ZNSDsdjdemDRtest==','Dr','1.1.1.1','GMT+8','ZH-CN','ZH-CN','','','','1','','','','','','','','','','','','','')"; 
			stmt.executeUpdate(sql);
			sql ="INSERT INTO TEST (UUID,NAME,IP,GMT,Country,Language,Kickcount,Sensitive,Translate,Level,Exp,Reqexp,Reqtotalexp,Playtime,Pvpwincount,Pvplosecount,Authority,Lastchat,Chatcount,Deadcount,Killcount,Joincount,Breakcount) " +
					  "VALUES ('DRtest==','DrX','1.1.1.1X','GMT+8X','ZH-CNX','ZH-CN','','','','0','','','','','','','','','','','','','')"; 
		  stmt.executeUpdate(sql);
		  stmt.close();
		  c.commit();
		  c.close();
		} catch ( Exception e ) {
		  System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		  System.exit(0);
		}
		System.out.println("Records created successfully");
	}

	public static void getSQLite() {
		Statement stmt = null;
		try {		
			Connection c = connectSQLite();
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM TEST;" );
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
	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	System.exit(0);
	}
	System.out.println("Operation done successfully");
  }

	public static Connection connectSQLite() {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection c = DriverManager.getConnection("jdbc:sqlite:"+Core.settings.getDataDirectory().child("mods/GA/TEST.db"));
			return c;
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		return null;
	}

}