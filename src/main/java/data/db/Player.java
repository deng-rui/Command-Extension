package extension.data.db;

import java.util.*;
import java.sql.*;
//Java

import arc.*;
import arc.Core;
//Arc

import extension.data.global.Config;
import extension.util.Log;
import extension.util.file.FileUtil;
//GA-Exted

import static extension.util.ExtractUtil.InttoBoolean;
import static extension.util.ExtractUtil.BooleantoInt;
//Static

public class Player {

	private static Connection c;

	static {
		try {
			c = DriverManager.getConnection("jdbc:sqlite:"+FileUtil.File(Config.Plugin_Data_Path).getPath("Data.db"));
			c.setAutoCommit(false);
		} catch (Exception e) {
			Log.fatal(e);
		}
	}

	public static void InitializationPlayersSQLite(String UUID, String User, String NAME, long IP, long GMT, String Country, String Language, long LastLogin, String PasswordHash, String CSPRNG) {
		PreparedStatement playerdata = null;
		PreparedStatement playerpriv = null;
		try {	
			playerdata = c.prepareStatement("INSERT INTO PlayerData VALUES (?,?,?,?,?,?,'1',?,?,'0','0','0','0','0','0','0','0','0','0','0','0','1','0','0','0','0','0')");
			playerpriv = c.prepareStatement("INSERT INTO PlayerPrivate VALUES (?,'NULL','1',?,?)");
			playerdata.setString(1,UUID);
			playerdata.setString(2,User);
			playerdata.setString(3,NAME);
			playerdata.setLong(4,IP);
			playerdata.setLong(5,GMT);
			playerdata.setString(6,Country);
			playerdata.setString(7,Language);
			playerdata.setLong(8,LastLogin);
			playerdata.execute();
			playerpriv.setString(1,User);
			playerpriv.setString(2,PasswordHash);
			playerpriv.setString(3,CSPRNG);
			playerpriv.addBatch();
			playerpriv.execute();
			c.commit();
		} catch (Exception e) {
			Log.fatal(e);
		} finally {
			close(playerdata);
			close(playerpriv);
		}
	}

	public static void savePlayer_Data(PlayerData data, String uoru) {
		PreparedStatement stmt = null;
		try {
			stmt = c.prepareStatement("UPDATE PlayerData SET UUID=?,User=?,NAME=?,IP=?,GMT=?,Country=?,Time_format=?,Language=?,LastLogin=?,Buildcount=?,Dismantledcount=?,Cumulative_build=?,Kickcount=?,Sensitive=?,Translate=?,Level=?,Exp=?,Reqexp=?,Reqtotalexp=?,Playtime=?,Pvpwincount=?,Pvplosecount=?,Authority=?,Lastchat=?,Deadcount=?,Killcount=?,Joincount=?,Breakcount=? WHERE User=?");
			stmt.setString(1,data.UUID);
			stmt.setString(2,data.User);
			stmt.setString(3,data.NAME);
			stmt.setLong(4,data.IP);
			stmt.setLong(5,data.GMT);
			stmt.setString(6,data.Country);
			stmt.setInt(7,data.Time_format);
			stmt.setString(8,data.Language);
			stmt.setLong(9,data.LastLogin);
			stmt.setInt(10,data.Buildcount);
			stmt.setInt(11,data.Dismantledcount);
			stmt.setInt(12,data.Cumulative_build);
			stmt.setInt(13,data.Kickcount);
			stmt.setInt(14,BooleantoInt(data.Translate));
			stmt.setInt(15,data.Level);
			stmt.setLong(16,data.Exp);
			stmt.setLong(17,data.Reqexp);
			stmt.setLong(18,data.Reqtotalexp);
			stmt.setLong(19,data.Playtime);
			stmt.setInt(20,data.Pvpwincount);
			stmt.setInt(21,data.Pvplosecount);
			stmt.setInt(21,data.Authority);
			stmt.setLong(22,data.Lastchat);
			stmt.setInt(24,data.Deadcount);
			stmt.setInt(25,data.Killcount);
			stmt.setInt(26,data.Joincount);
			stmt.setInt(27,data.Breakcount);
			stmt.setString(28,uoru);
			stmt.execute();
			c.commit();
		} catch (SQLException e) {
			Log.error(e);
		} finally {
			close(stmt);
		}
	}

	public static void savePlayer_Private(PlayerData data, String user) {
		PreparedStatement stmt = null;
		try {
			stmt = c.prepareStatement("UPDATE PlayerPrivate SET User=?,Mail=?,Online=?,PasswordHash=?,CSPRNG=? WHERE User=?");
			stmt.setString(1,data.User);
			stmt.setString(2,data.Mail);
			stmt.setInt(3,BooleantoInt(data.Online));
			stmt.setString(4,data.PasswordHash);
			stmt.setString(5,data.CSPRNG);
			stmt.setString(6,user);
			stmt.execute();
			c.commit();
		} catch (SQLException e) {
			Log.error(e);
		} finally {
			close(stmt);
		}
	}

	public static void getSQLite(PlayerData data, String user) {
		PreparedStatement playerdata = null;
		PreparedStatement playerpriv = null;
		ResultSet rs = null;
		ResultSet rss = null;
		try {
			playerdata = c.prepareStatement("SELECT * FROM PlayerData WHERE User=?");
			playerdata.setString(1,user);
			rs = playerdata.executeQuery();
			while ( rs.next() ) {
				data.UUID 				= rs.getString("UUID");
				data.User 				= rs.getString("User");
				data.NAME 				= rs.getString("NAME");
				data.IP 				= rs.getLong("IP");
				data.GMT 				= rs.getLong("GMT");
				data.Country 			= rs.getString("Country");
				data.Time_format 		= rs.getInt("Time_format");
				data.Language 			= rs.getString("Language");
				data.Lastchat 			= rs.getLong("LastLogin");
				//data.Online 			= rs.getInt("Online");
				data.Buildcount 		= rs.getInt("Buildcount");
				data.Dismantledcount 	= rs.getInt("Dismantledcount");
				data.Cumulative_build 	= rs.getInt("Cumulative_build");
				data.Kickcount 			= rs.getInt("Kickcount");
				data.Translate 			= InttoBoolean(rs.getInt("Translate"));
				data.Level 				= rs.getInt("Level");
				data.Exp 				= rs.getLong("Exp");
				data.Reqexp 			= rs.getLong("Reqexp");
				data.Reqtotalexp 		= rs.getLong("Reqtotalexp");
				data.Playtime 			= rs.getLong("Playtime");
				data.Pvpwincount 		= rs.getInt("Pvpwincount");
				data.Pvplosecount 		= rs.getInt("Pvplosecount");
				data.Authority 			= rs.getInt("Authority");
				data.Lastchat 			= rs.getLong("Lastchat");
				data.Deadcount 			= rs.getInt("Deadcount");
				data.Killcount 			= rs.getInt("Killcount");
				data.Joincount 			= rs.getInt("Joincount");
				data.Breakcount 		= rs.getInt("Breakcount");
			}
			playerpriv = c.prepareStatement("SELECT * FROM PlayerPrivate WHERE User=?");
			playerpriv.setString(1,user);
			rss = playerpriv.executeQuery();
			while (rss.next()) {
				data.Mail 				= rss.getString("Mail");
				data.Online 			= InttoBoolean(rss.getInt("Online"));
				data.PasswordHash 		= rss.getString("PasswordHash");
				data.CSPRNG 			= rss.getString("CSPRNG");
			}
		} catch (SQLException e) {
			Log.error(e);
		} finally {
			close(rs,playerdata);
			close(rss,playerpriv);
		}
	}

	public static boolean isSQLite_User(String user) {
		boolean result = true;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = c.prepareStatement("SELECT COUNT(User) FROM PlayerPrivate where User=?");
			// 真的奇怪?
			stmt.setString(1,user);
			rs = stmt.executeQuery();
			rs.next();
			if(rs.getInt(1)>0)result = false;
			//用户名存在 避免冲突
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			close(rs,stmt);
		}
		return result;
	}

	private static void close(Statement stmt) {
		close(null,stmt,null);
	}

	private static void close(ResultSet rs,Statement stmt) {
		close(rs,stmt,null);
	}

	private static void close(Statement stmt,Connection conn) {
		close(null,stmt,conn);
	}

	private static void close(ResultSet rs,Statement stmt,Connection conn) {
		try {
			if (rs != null) rs.close();
		} catch (Exception e) {  
			rs = null;  
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (Exception e) {  
				stmt = null;  
			} finally {
				try {
					if (conn != null) conn.close();
				} catch (Exception e) {  
					conn = null;  
				}
			}
		}
	}
}