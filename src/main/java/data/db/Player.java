package extension.data.db;

import java.util.*;
import java.sql.*;
//Java

import arc.*;
import arc.Core;
//Arc

import extension.util.LogUtil;
//GA-Exted

import static extension.data.db.SQLite.SQL_type;
//Static

public class Player {
	public static void InitializationPlayersSQLite(String UUID, String NAME, String IP, String GMT, String Country, String Language, String LastLogin, String User, String PasswordHash, String CSPRNG) {
		try {
			Connection c = connectSQLite();
			c.setAutoCommit(false);
			String sql = "INSERT INTO Player (UUID,NAME,IP,GMT,Country,Time_format,Language,LastLogin,User,PasswordHash,CSPRNG,Kickcount,Sensitive,Translate,Level,Exp,Reqexp,Reqtotalexp,Playtime,Pvpwincount,Pvplosecount,Authority,Lastchat,Deadcount,Killcount,Joincount,Breakcount) VALUES (?,?,?,?,?,'0',?,?,?,?,?,'0','0','0','0','0','0','0','0','0','0','1','0','0','0','0','0')";
			PreparedStatement stmt = c.prepareStatement(sql);
			stmt.setString(1,UUID);
			stmt.setString(2,NAME);
			stmt.setString(3,IP);
			stmt.setString(4,GMT);
			stmt.setString(5,Country);
			stmt.setString(6,Language);
			stmt.setString(7,LastLogin);
			stmt.setString(8,User);
			stmt.setString(9,PasswordHash);
			stmt.setString(10,CSPRNG);
			stmt.execute();
			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e ) {
			LogUtil.fatal(e);
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
		} catch (Exception e ) {
			LogUtil.error(e);
		}
	}

	public static void savePlayer_Data(List data, boolean uu, String uoru) {
		try {
			Connection c = connectSQLite();
			c.setAutoCommit(false);
			String sql=sql = "UPDATE Player SET UUID=?,NAME=?,IP=?,GMT=?,Country=?,Time_format=?,Language=?,LastLogin=?,User=?,PasswordHash=?,CSPRNG=?,Kickcount=?,Sensitive=?,Translate=?,Level=?,Exp=?,Reqexp=?,Reqtotalexp=?,Playtime=?,Pvpwincount=?,Pvplosecount=?,Authority=?,Lastchat=?,Deadcount=?,Killcount=?,Joincount=?,Breakcount=? WHERE UUID=?";
			if(uu)sql="UPDATE Player SET UUID=?,NAME=?,IP=?,GMT=?,Country=?,Time_format=?,Language=?,LastLogin=?,User=?,PasswordHash=?,CSPRNG=?,Kickcount=?,Sensitive=?,Translate=?,Level=?,Exp=?,Reqexp=?,Reqtotalexp=?,Playtime=?,Pvpwincount=?,Pvplosecount=?,Authority=?,Lastchat=?,Deadcount=?,Killcount=?,Joincount=?,Breakcount=? WHERE User=?";
			PreparedStatement stmt = c.prepareStatement(sql);
			stmt.setString(1,(String)data.get(SQL_type("UUID")));
			stmt.setString(2,(String)data.get(SQL_type("NAME")));
			stmt.setString(3,(String)data.get(SQL_type("IP")));
			stmt.setString(4,(String)data.get(SQL_type("GMT")));
			stmt.setString(5,(String)data.get(SQL_type("Country")));
			stmt.setString(6,(String)data.get(SQL_type("Time_format")));
			stmt.setString(7,(String)data.get(SQL_type("Language")));
			stmt.setString(8,(String)data.get(SQL_type("LastLogin")));
			stmt.setString(9,(String)data.get(SQL_type("User")));
			stmt.setString(10,(String)data.get(SQL_type("PasswordHash")));
			stmt.setString(11,(String)data.get(SQL_type("CSPRNG")));
			stmt.setString(12,(String)data.get(SQL_type("Kickcount")));
			stmt.setString(13,(String)data.get(SQL_type("Sensitive")));
			stmt.setString(14,(String)data.get(SQL_type("Translate")));
			stmt.setString(15,(String)data.get(SQL_type("Level")));
			stmt.setString(16,(String)data.get(SQL_type("Exp")));
			stmt.setString(17,(String)data.get(SQL_type("Reqexp")));
			stmt.setString(18,(String)data.get(SQL_type("Reqtotalexp")));
			stmt.setString(19,(String)data.get(SQL_type("Playtime")));
			stmt.setString(20,(String)data.get(SQL_type("Pvpwincount")));
			stmt.setString(21,(String)data.get(SQL_type("Pvplosecount")));
			stmt.setString(22,(String)data.get(SQL_type("Authority")));
			stmt.setString(23,(String)data.get(SQL_type("Lastchat")));
			stmt.setString(24,(String)data.get(SQL_type("Deadcount")));
			stmt.setString(25,(String)data.get(SQL_type("Killcount")));
			stmt.setString(26,(String)data.get(SQL_type("Joincount")));
			stmt.setString(27,(String)data.get(SQL_type("Breakcount")));
			stmt.setString(28,uoru);
			stmt.execute();
			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e ) {
			LogUtil.error(e);
		}
	}

	public static List getSQLite_UUID(String uuid) {
		try {
			List<String> Players = new ArrayList<String>();
			Connection c = connectSQLite();
			c.setAutoCommit(false);
			PreparedStatement stmt = c.prepareStatement("SELECT * FROM Player WHERE UUID=?");
			stmt.setString(1,uuid);
			ResultSet rs = stmt.executeQuery();
			while ( rs.next() ) {
			Players.add(rs.getString("UUID"));
			Players.add(rs.getString("NAME"));
			Players.add(rs.getString("IP"));
			Players.add(rs.getString("GMT"));
			Players.add(rs.getString("Country"));
			Players.add(rs.getString("Time_format"));
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
			Players.add(rs.getString("Deadcount"));
			Players.add(rs.getString("Killcount"));
			Players.add(rs.getString("Joincount"));
			Players.add(rs.getString("Breakcount"));
			}
			/*
			for(int i=0;i<Players.size();i++){
			System.out.println(Players.get(i));
			}*/
			rs.close();
			stmt.close();
			c.close();
			return Players;
		} catch ( Exception e ) {
			LogUtil.error(e);
		}
		return null;
	}

	public static List getSQLite_USER(String user) {
		try {
			List<String> Players = new ArrayList<String>();
			Connection c = connectSQLite();
			c.setAutoCommit(false);
			PreparedStatement stmt = c.prepareStatement("SELECT * FROM Player WHERE User=?");
			stmt.setString(1,user);
			ResultSet rs = stmt.executeQuery();
			while ( rs.next() ) {
				Players.add(rs.getString("UUID"));
				Players.add(rs.getString("NAME"));
				Players.add(rs.getString("IP"));
				Players.add(rs.getString("GMT"));
				Players.add(rs.getString("Country"));
				Players.add(rs.getString("Time_format"));
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
				Players.add(rs.getString("Deadcount"));
				Players.add(rs.getString("Killcount"));
				Players.add(rs.getString("Joincount"));
				Players.add(rs.getString("Breakcount"));
			}
		/*
		for(int i=0;i<Players.size();i++){
		System.out.println(Players.get(i));
		}*/
			rs.close();
			stmt.close();
			c.close();
			return Players;
		} catch ( Exception e ) {
			LogUtil.error(e);
		}
		return null;
	}

	public static boolean isSQLite_User(String user) {
		boolean result = true;
		try {
			String temp;
			Connection c = connectSQLite();
			c.setAutoCommit(false);
			PreparedStatement stmt = c.prepareStatement("SELECT COUNT(*) FROM Player where User=?");
			stmt.setString(1,user);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			if(rs.getInt(1)>0)result = false;
			rs.close();
			stmt.close();
			c.close();
			//用户名存在 避免冲突
		} catch ( Exception e ) {
			System.out.println(e);
		}
		return result;
	}

	public static Connection connectSQLite() throws Exception {
		return (Connection)DriverManager.getConnection("jdbc:sqlite:"+Core.settings.getDataDirectory().child("mods/GA/Data.db"));
	}

}