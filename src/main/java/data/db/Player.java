package extension.data.db;

import java.util.*;
import java.sql.*;
//Java

import arc.*;
import arc.Core;
//Arc

import extension.data.global.Config;
import extension.data.global.Data;
import extension.util.file.FileUtil;
import extension.util.log.Log;
//GA-Exted

import static extension.util.ExtractUtil.InttoBoolean;
import static extension.util.ExtractUtil.BooleantoInt;
//Static

public class Player {

	private static Connection c;

	static {
		try {
			c = DriverManager.getConnection("jdbc:sqlite:"+FileUtil.File(Data.Plugin_Data_Path).getPath("Data.db"));
			c.setAutoCommit(false);
		} catch (Exception e) {
			Log.fatal(e);
		}
	}

	public static void InitializationPlayersSQLite(String User) {
		PreparedStatement playerdata = null;
		PreparedStatement playerpriv = null;
		try {	
			playerdata = c.prepareStatement("INSERT INTO PlayerData VALUES ('0',?,'0','0','0','1','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','1','0','0','0','0','0','0')");
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
			playerdata = c.prepareStatement("UPDATE PlayerData SET UUID=?,User=?,IP=?,GMT=?,Country=?,Time_format=?,Language=?,LastLogin=?,Buildcount=?,Dismantledcount=?,Cumulative_build=?,Pipe_build=?,Kickcount=?,Translate=?,Level=?,Exp=?,Reqexp=?,Reqtotalexp=?,Playtime=?,Pvpwincount=?,Pvplosecount=?,Authority=?,Authority_effective_time=?,Lastchat=?,Deadcount=?,Killcount=?,Joincount=?,Breakcount=? WHERE User=?");
			playerpriv = c.prepareStatement("UPDATE PlayerPrivate SET User=?,Mail=?,Online=?,PasswordHash=?,CSPRNG=? WHERE User=?");
			playerdata.setString(1,data.UUID);
			playerdata.setString(2,data.User);
			playerdata.setLong(3,data.IP);
			playerdata.setInt(4,data.GMT);
			playerdata.setString(5,data.Country);
			playerdata.setByte(6,data.Time_format);
			playerdata.setString(7,data.Language);
			playerdata.setLong(8,data.LastLogin);
			playerdata.setInt(9,data.Buildcount);
			playerdata.setInt(10,data.Dismantledcount);
			playerdata.setInt(11,data.Cumulative_build);
			playerdata.setInt(12,data.Pipe_build);
			playerdata.setInt(13,data.Kickcount);
			playerdata.setInt(14,BooleantoInt(data.Translate));
			playerdata.setInt(15,data.Level);
			playerdata.setShort(16,data.Exp);
			playerdata.setLong(17,data.Reqexp);
			playerdata.setLong(18,data.Reqtotalexp);
			playerdata.setLong(19,data.Playtime);
			playerdata.setInt(20,data.Pvpwincount);
			playerdata.setInt(21,data.Pvplosecount);
			playerdata.setInt(22,data.Authority);
			playerdata.setLong(23,data.Authority_effective_time);
			playerdata.setLong(24,data.Lastchat);
			playerdata.setInt(25,data.Deadcount);
			playerdata.setInt(26,data.Killcount);
			playerdata.setInt(27,data.Joincount);
			playerdata.setInt(28,data.Breakcount);
			playerdata.setString(29,user);
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
				data.UUID 						= rs.getString("UUID");
				data.User 						= rs.getString("User");
				data.IP 						= rs.getLong("IP");
				data.GMT 						= rs.getInt("GMT");
				data.Country 					= rs.getString("Country");
				data.Time_format 				= rs.getByte("Time_format");
				data.Language 					= rs.getString("Language");
				data.LastLogin 					= rs.getLong("LastLogin");
				//data.Online 					= rs.getInt("Online");
				data.Buildcount 				= data.Buildcount + rs.getInt("Buildcount");
				data.Dismantledcount 			= data.Dismantledcount + rs.getInt("Dismantledcount");
				data.Cumulative_build 			= data.Cumulative_build + rs.getInt("Cumulative_build");
				data.Pipe_build					= data.Pipe_build + rs.getInt("Pipe_build");
				data.Kickcount 					= data.Kickcount + rs.getInt("Kickcount");
				data.Translate 					= InttoBoolean(rs.getInt("Translate"));
				data.Level 						= rs.getInt("Level");
				data.Exp 						= rs.getShort("Exp");
				data.Reqexp 					= rs.getLong("Reqexp");
				data.Reqtotalexp 				= rs.getLong("Reqtotalexp");
				data.Playtime 					= data.Playtime + rs.getLong("Playtime");
				data.Pvpwincount 				= data.Pvpwincount + rs.getInt("Pvpwincount");
				data.Pvplosecount 				= data.Pvplosecount + rs.getInt("Pvplosecount");
				data.Authority 					= rs.getInt("Authority");
				data.Authority_effective_time 	= rs.getLong("Authority_effective_time");
				data.Lastchat 					= rs.getLong("Lastchat");
				data.Deadcount 					= data.Deadcount + rs.getInt("Deadcount");
				data.Killcount 					= data.Killcount + rs.getInt("Killcount");
				data.Joincount 					= data.Joincount + rs.getInt("Joincount");
				data.Breakcount 				= data.Breakcount + rs.getInt("Breakcount");
			}
			playerpriv = c.prepareStatement("SELECT * FROM PlayerPrivate WHERE User=?");
			playerpriv.setString(1,user);
			rss = playerpriv.executeQuery();
			while (rss.next()) {
				data.Mail 						= rss.getString("Mail");
				data.Online 					= InttoBoolean(rss.getInt("Online"));
				data.PasswordHash 				= rss.getString("PasswordHash");
				data.CSPRNG 					= rss.getString("CSPRNG");
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

	//
	// 		KEY START
	//
	public static void AddKey(String KEY,int Authority,long Time,long Expire,int Total) {
		PreparedStatement key = null;
		try {	
			key = c.prepareStatement("INSERT INTO KeyData VALUES (?,?,?,?,?,?)");
			key.setString(1,KEY);
			key.setInt(2,Authority);
			key.setInt(3,Total);
			key.setInt(4,Total);
			key.setLong(5,Time);
			key.setLong(6,Expire);
			key.execute();
			c.commit();
		} catch (Exception e) {
			Log.fatal(e);
		} finally {
			close(key);
		}
	}

	public static void SaveKey(String KEY,int Authority,int Total,int Surplus,long Time,long Expire) {
		PreparedStatement key = null;
		try {
			key = c.prepareStatement("UPDATE KeyData SET KEY=?,Authority=?,Total=?,Surplus=?,Time=?,Expire=? WHERE KEY=?");
			key.setString(1,KEY);
			key.setInt(2,Authority);
			key.setInt(3,Total);
			key.setInt(4,Surplus);
			key.setLong(5,Time);
			key.setLong(6,Expire);
			key.setString(7,KEY);
			key.execute();
			c.commit();
		} catch (SQLException e) {
			Log.error(e);
		} finally {
			close(key);
		}
	}

	public static List<Map<String,Object>> GetKey() {
		PreparedStatement key = null;
		ResultSet rs = null;
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		try {
			key = c.prepareStatement("SELECT * FROM KeyData");
			rs = key.executeQuery();
			while (rs.next()) {
				Map<String,Object> data = new HashMap<String,Object>();
				data.put("KEY",rs.getString("KEY"));
				data.put("Authority",rs.getInt("Authority"));
				data.put("Total",rs.getInt("Total"));
				data.put("Surplus",rs.getInt("Surplus"));
				data.put("Time",rs.getLong("Time"));
				data.put("Expire",rs.getLong("Expire"));
				result.add(data);
			}
		} catch (SQLException e) {
			Log.error(e);
		} finally {
			close(rs,key);
		}
		return result;
	}

	public static Map<String,Object> GetKey(String KEY) {
		PreparedStatement key = null;
		ResultSet rs = null;
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			key = c.prepareStatement("SELECT * FROM KeyData WHERE KEY=?");
			key.setString(1,KEY);
			rs = key.executeQuery();
			while (rs.next()) {
				result.put("KEY",rs.getString("KEY"));
				result.put("Authority",rs.getInt("Authority"));
				result.put("Total",rs.getInt("Total"));
				result.put("Surplus",rs.getInt("Surplus"));
				result.put("Time",rs.getLong("Time"));
				result.put("Expire",rs.getLong("Expire"));
			}
		} catch (SQLException e) {
			Log.error(e);
		} finally {
			close(rs,key);
		}
		return result;
	}

	public static void RmKey() {
		RmKey(null);
	}

	public static void RmKey(String KEY) {
		PreparedStatement key = null;
		try {
			if (KEY != null) {
				key = c.prepareStatement("DELETE FROM KeyData WHERE KEY=?");
				key.setString(1,KEY);
			} else
				key = c.prepareStatement("DELETE FROM KeyData");
			key.execute();
			c.commit();
		} catch (SQLException e) {
			Log.error(e);
		} finally {
			close(key);
		}
	}

	public static boolean isSQLite_Key(String KEY) {
		boolean result = true;
		PreparedStatement key = null;
		ResultSet rs = null;
		try {
			key = c.prepareStatement("SELECT COUNT(KEY) FROM KeyData WHERE KEY=?");
			key.setString(1,KEY);
			rs = key.executeQuery();
			rs.next();
			if(rs.getInt(1)>0)
				result = false;
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			close(rs,key);
		}
		return result;
	}




	// 手动迁移
	public static void getS(PlayerData data, String user) {
		PreparedStatement playerdata = null;
		PreparedStatement playerpriv = null;
		ResultSet rs = null;
		ResultSet rss = null;
		try {
			Connection cc = DriverManager.getConnection("jdbc:sqlite:"+FileUtil.File(Data.Plugin_Data_Path).getPath("Dataa.db"));
			cc.setAutoCommit(false);
			playerdata = cc.prepareStatement("SELECT * FROM PlayerData WHERE User=?");
			playerdata.setString(1,user);
			rs = playerdata.executeQuery();
			while ( rs.next() ) {
				// 防止游戏玩一半登录 导致数据飞天
				data.UUID 						= rs.getString("UUID");
				data.User 						= rs.getString("User");
				data.IP 						= rs.getLong("IP");
				data.GMT 						= rs.getInt("GMT");
				data.Country 					= rs.getString("Country");
				data.Time_format 				= rs.getByte("Time_format");
				data.Language 					= rs.getString("Language");
				data.LastLogin 					= rs.getLong("LastLogin");
				//data.Online 					= rs.getInt("Online");
				data.Buildcount 				= data.Buildcount + rs.getInt("Buildcount");
				data.Dismantledcount 			= data.Dismantledcount + rs.getInt("Dismantledcount");
				data.Cumulative_build 			= data.Cumulative_build + rs.getInt("Cumulative_build");
				data.Pipe_build					= data.Pipe_build + rs.getInt("Pipe_build");
				data.Kickcount 					= data.Kickcount + rs.getInt("Kickcount");
				data.Translate 					= InttoBoolean(rs.getInt("Translate"));
				data.Level 						= rs.getInt("Level");
				data.Exp 						= rs.getShort("Exp");
				data.Reqexp 					= rs.getLong("Reqexp");
				data.Reqtotalexp 				= rs.getLong("Reqtotalexp");
				data.Playtime 					= data.Playtime + rs.getLong("Playtime");
				data.Pvpwincount 				= data.Pvpwincount + rs.getInt("Pvpwincount");
				data.Pvplosecount 				= data.Pvplosecount + rs.getInt("Pvplosecount");
				data.Authority 					= rs.getInt("Authority");
				data.Lastchat 					= rs.getLong("Lastchat");
				data.Deadcount 					= data.Deadcount + rs.getInt("Deadcount");
				data.Killcount 					= data.Killcount + rs.getInt("Killcount");
				data.Joincount 					= data.Joincount + rs.getInt("Joincount");
				data.Breakcount 				= data.Breakcount + rs.getInt("Breakcount");
			}
			playerpriv = cc.prepareStatement("SELECT * FROM PlayerPrivate WHERE User=?");
			playerpriv.setString(1,user);
			rss = playerpriv.executeQuery();
			while (rss.next()) {
				data.Mail 						= rss.getString("Mail");
				data.Online 					= InttoBoolean(rss.getInt("Online"));
				data.PasswordHash 				= rss.getString("PasswordHash");
				data.CSPRNG 					= rss.getString("CSPRNG");
			}
		} catch (SQLException e) {
			Log.error(e);
		} finally {
			close(rs,playerdata);
			close(rss,playerpriv);
		}
	}
	//

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