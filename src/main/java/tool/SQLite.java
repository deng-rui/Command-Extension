package extension.tool;

import java.util.*;

import arc.*;
import arc.Core;

import java.sql.*;

public class SQLite {

	public static void InitializationSQLite() {
		try {
			String sql;
			Connection c = connectSQLite();
			Statement stmt = c.createStatement();
			//sql = "CREATE TABLE Players (" +
			sql = "CREATE TABLE TEST (" +
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
						//TEST阶段 仅在GA-PVP使用27
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE Settings (" +
						"UUID 			TEXT,"+
						"NAME 			TEXT,"+
						"IP 			TEXT,"+
						"GMT 			TEXT,"+
						"Country 		TEXT,"+
						"Language 		TEXT,"+
						"LastLogin 		TEXT,"+
						//玩家普通信息7
						//TEST阶段 仅在GA-PVP使用27
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch ( Exception e ) {
		}
	}

	public static class player {
		public static void InitializationPlayersSQLite(String UUID,String NAME,String IP,String GMT,String Country,String Language) {
			try {
				Connection c = connectSQLite();
				c.setAutoCommit(false);
				String sql ="INSERT INTO TEST (UUID,NAME,IP,GMT,Country,Language,LastLogin,User,PasswordHash,CSPRNG,Kickcount,Sensitive,Translate,Level,Exp,Reqexp,Reqtotalexp,Playtime,Pvpwincount,Pvplosecount,Authority,Lastchat,Chatcount,Deadcount,Killcount,Joincount,Breakcount) VALUES (?,?,?,?,?,?,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0')"; 
				PreparedStatement stmt = c.prepareStatement(sql);
            	stmt.setString(1,UUID);
            	stmt.setString(2,NAME);
            	stmt.setString(3,IP);
            	stmt.setString(4,GMT);
            	stmt.setString(5,Country);
            	stmt.setString(6,Language);
            	stmt.execute();
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
				List<String> Players = new ArrayList<String>();		
				Connection c = connectSQLite();
				c.setAutoCommit(false);
				PreparedStatement stmt = c.prepareStatement("SELECT * FROM TEST WHERE NAME=?");
            	stmt.setString(1,name);
				ResultSet rs = stmt.executeQuery();
				while ( rs.next() ) {
					Players.add(rs.getString("UUID"));
					Players.add(rs.getString("NAME"));
					Players.add(rs.getString("IP"));
					Players.add(rs.getString("GMT"));
					Players.add(rs.getString("Language"));
					Players.add(rs.getString("LastLogin"));
					Players.add(rs.getString("User"));
					Players.add(rs.getString("PasswordHash"));
					Players.add(rs.getString("CSPRNG"));
					//Players.add(rs.getInt("Kickcount"));
					//Players.add(rs.getInt("Sensitive"));
					Players.add(rs.getString("Kickcount"));
					Players.add(rs.getString("Sensitive"));
					Players.add(rs.getString("Translate"));
					//Players.add(rs.getInt("Level"));
					//Players.add(rs.getInt("Exp"));
					//Players.add(rs.getInt("Reqexp"));
					//Players.add(rs.getInt("Reqtotalexp"));
					//Players.add(rs.getInt("Playtime"));
					//Players.add(rs.getInt("Pvpwincount"));
					//Players.add(rs.getInt("Pvplosecount"));
					//Players.add(rs.getInt("Authority"));
					Players.add(rs.getString("Level"));
					Players.add(rs.getString("Exp"));
					Players.add(rs.getString("Reqexp"));
					Players.add(rs.getString("Reqtotalexp"));
					Players.add(rs.getString("Playtime"));
					Players.add(rs.getString("Pvpwincount"));
					Players.add(rs.getString("Pvplosecount"));
					Players.add(rs.getString("Authority"));
					Players.add(rs.getString("Lastchat"));
					//Players.add(rs.getInt("Chatcount"));
					//Players.add(rs.getInt("Deadcount"));
					//Players.add(rs.getInt("Killcount"));
					//Players.add(rs.getInt("Joincount"));
					//Players.add(rs.getInt("Breakcount"));
					Players.add(rs.getString("Chatcount"));
					Players.add(rs.getString("Deadcount"));
					Players.add(rs.getString("Killcount"));
					Players.add(rs.getString("Joincount"));
					Players.add(rs.getString("Breakcount"));

					
				}
				for(int i=0;i<Players.size();i++){
    System.out.println(Players.get(i));
} 
				rs.close();
				stmt.close();
				c.close();
				//return Players;
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