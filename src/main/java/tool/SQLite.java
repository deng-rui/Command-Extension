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
						"Kickcount		INT 	NOT NULL,"+
						"Sensitive 		INT 	NOT NULL,"+
						//被踢 敏感词次数-2
						"Placecount 	INT 	NOT NULL,"+
						"Translate 		BIT 	DEFAULT 1,"+
						//翻译权限-2
						"Level 			INT 	NOT NULL,"+
						"Exp 			INT 	NOT NULL,"+
						"Reqexp 		INT 	NOT NULL,"+
						"Reqtotalexp 	INT 	NOT NULL,"+
						//等级-4
						"Playtime 		REAL 	NOT NULL,"+
						//游戏时长-1
						"Pvpwincount 	INT 	NOT NULL,"+
						"Pvplosecount 	INT 	NOT NULL,"+
						//胜利 输数-2
						"Authority 		TEXT 	NOT NULL,"+
						//权限-1
						"Lastchat 		INT 	NOT NULL,"+
						"Chatcount 		INT 	NOT NULL,"+
						//聊天计数-2
						"Deadcount		INT 	NOT NULL,"+
						"Killcount 		INT 	NOT NULL,"+
						"Joincount 		INT 	NOT NULL,"+
						"Breakcount 	INT 	NOT NULL)"; 
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

	public static void addSQLite() {
		Statement stmt = null;
		String sql;
		try {
			Connection c = connectSQLite();
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");
			stmt = c.createStatement();
			sql ="INSERT INTO TEST (UUID,NAME,IP,GMT,Country,Language,Kickcount,Sensitive,Placecount,Translate,Level,Exp,Reqexp,Reqtotalexp,Playtime,Pvpwincount,Pvplosecount,Authority,Lastchat,Chatcount,Deadcount,Killcount,Joincount,Breakcount) " +
					  "VALUES ('ZNSDsdjdemDRtest==','Dr','1.1.1.1','GMT+8','ZH-CN','ZH-CN','','','','1','','','','','','','','','','','','','','');"; 
			stmt.executeUpdate(sql);
			sql ="INSERT INTO TEST (UUID,NAME,IP,GMT,Country,Language,Kickcount,Sensitive,Placecount,Translate,Level,Exp,Reqexp,Reqtotalexp,Playtime,Pvpwincount,Pvplosecount,Authority,Lastchat,Chatcount,Deadcount,Killcount,Joincount,Breakcount) " +
					  "VALUES ('DRtest==','DrX','1.1.1.1X','GMT+8X','ZH-CNX','ZH-CN','','','','0','','','','','','','','','','','','','','');"; 
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