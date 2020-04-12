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

	public static void InitializationPlayersSQLite(String User) {
		PreparedStatement playerdata = null;
		PreparedStatement playerpriv = null;
		try {	
			playerdata = c.prepareStatement("INSERT INTO PlayerData VALUES ('0',?,'0','0','0','1','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','1','0','0','0','0','0')");
			playerpriv = c.prepareStatement("INSERT INTO PlayerPrivate VALUES (?,'0','1','0','0')");
			playerdata.setString(1,User);
			playerpriv.setString(1,User);
			playerdata.execute();
			playerpriv.execute();
			c.commit();
		} catch (Exception e) {
			Log.fatal(e);
		} finally {
			close(playerdata);
			close(playerpriv);
		}
	}

	public static void savePlayer(PlayerData data, String user) {
		PreparedStatement playerdata = null;
		PreparedStatement playerpriv = null;
		try {
			playerdata = c.prepareStatement("UPDATE PlayerData SET UUID=?,User=?,IP=?,GMT=?,Country=?,Time_format=?,Language=?,LastLogin=?,Buildcount=?,Dismantledcount=?,Cumulative_build=?,Pipe_build=?,Kickcount=?,Translate=?,Level=?,Exp=?,Reqexp=?,Reqtotalexp=?,Playtime=?,Pvpwincount=?,Pvplosecount=?,Authority=?,Lastchat=?,Deadcount=?,Killcount=?,Joincount=?,Breakcount=? WHERE User=?");
			playerpriv = c.prepareStatement("UPDATE PlayerPrivate SET User=?,Mail=?,Online=?,PasswordHash=?,CSPRNG=? WHERE User=?");
			playerdata.setString(1,data.UUID);
			playerdata.setString(2,data.User);
			playerdata.setLong(3,data.IP);
			playerdata.setLong(4,data.GMT);
			playerdata.setString(5,data.Country);
			playerdata.setInt(6,data.Time_format);
			playerdata.setString(7,data.Language);
			playerdata.setLong(8,data.LastLogin);
			playerdata.setInt(9,data.Buildcount);
			playerdata.setInt(10,data.Dismantledcount);
			playerdata.setInt(11,data.Cumulative_build);
			playerdata.setInt(12,data.Pipe_build);
			playerdata.setInt(13,data.Kickcount);
			playerdata.setInt(14,BooleantoInt(data.Translate));
			playerdata.setInt(15,data.Level);
			playerdata.setLong(16,data.Exp);
			playerdata.setLong(17,data.Reqexp);
			playerdata.setLong(18,data.Reqtotalexp);
			playerdata.setLong(19,data.Playtime);
			playerdata.setInt(20,data.Pvpwincount);
			playerdata.setInt(21,data.Pvplosecount);
			playerdata.setInt(22,data.Authority);
			playerdata.setLong(23,data.Lastchat);
			playerdata.setInt(24,data.Deadcount);
			playerdata.setInt(25,data.Killcount);
			playerdata.setInt(26,data.Joincount);
			playerdata.setInt(27,data.Breakcount);
			playerdata.setString(28,user);
			playerdata.execute();
			playerpriv.setString(1,data.User);
			playerpriv.setString(2,data.Mail);
			playerpriv.setInt(3,BooleantoInt(data.Online));
			playerpriv.setString(4,data.PasswordHash);
			playerpriv.setString(5,data.CSPRNG);
			playerpriv.setString(6,user);
			playerpriv.execute();
			c.commit();
		} catch (SQLException e) {
			Log.error(e);
		} finally {
			close(playerdata);
			close(playerpriv);
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
				// 防止游戏玩一半登录 导致数据飞天
				data.UUID 				= new String(rs.getString("UUID"));
				data.User 				= new String(rs.getString("User"));
				data.IP 				= rs.getLong("IP");
				data.GMT 				= rs.getLong("GMT");
				data.Country 			= new String(rs.getString("Country"));
				data.Time_format 		= rs.getByte("Time_format");
				data.Language 			= new String(rs.getString("Language"));
				data.LastLogin 			= rs.getLong("LastLogin");
				//data.Online 			= rs.getInt("Online");
				data.Buildcount 		= data.Buildcount + rs.getInt("Buildcount");
				data.Dismantledcount 	= data.Dismantledcount + rs.getInt("Dismantledcount");
				data.Cumulative_build 	= data.Cumulative_build + rs.getInt("Cumulative_build");
				data.Pipe_build			= data.Pipe_build + rs.getInt("Pipe_build");
				data.Kickcount 			= data.Kickcount + rs.getInt("Kickcount");
				data.Translate 			= InttoBoolean(rs.getInt("Translate"));
				data.Level 				= rs.getInt("Level");
				data.Exp 				= rs.getLong("Exp");
				data.Reqexp 			= rs.getLong("Reqexp");
				data.Reqtotalexp 		= rs.getLong("Reqtotalexp");
				data.Playtime 			= data.Playtime + rs.getLong("Playtime");
				data.Pvpwincount 		= data.Pvpwincount + rs.getInt("Pvpwincount");
				data.Pvplosecount 		= data.Pvplosecount + rs.getInt("Pvplosecount");
				data.Authority 			= rs.getInt("Authority");
				data.Lastchat 			= rs.getLong("Lastchat");
				data.Deadcount 			= data.Deadcount + rs.getInt("Deadcount");
				data.Killcount 			= data.Killcount + rs.getInt("Killcount");
				data.Joincount 			= data.Joincount + rs.getInt("Joincount");
				data.Breakcount 		= data.Breakcount + rs.getInt("Breakcount");
			}
			playerpriv = c.prepareStatement("SELECT * FROM PlayerPrivate WHERE User=?");
			playerpriv.setString(1,user);
			rss = playerpriv.executeQuery();
			while (rss.next()) {
				data.Mail 				= new String(rss.getString("Mail"));
				data.Online 			= InttoBoolean(rss.getInt("Online"));
				data.PasswordHash 		= new String(rss.getString("PasswordHash"));
				data.CSPRNG 			= new String(rss.getString("CSPRNG"));
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