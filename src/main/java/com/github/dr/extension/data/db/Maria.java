package com.github.dr.extension.data.db;

import com.github.dr.extension.data.global.Data;
import com.github.dr.extension.util.log.Log;
import com.github.dr.extension.util.pool.db.Jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.dr.extension.util.ExtractUtil.booleantoInt;
import static com.github.dr.extension.util.ExtractUtil.inttoBoolean;

/**
 * @author Dr
 * @Date 2020.5.11 6:12:30
 */
public class Maria extends AbstractSql {
	/**
	 * 处理并发 测试
	 * TODO : 处理并发
	 * MYSQL : SET SERIALIZABLE
	 * 暂时这样看看 对于并发非常不友好
	 * 玩家进入 ----登录/注册----> 玩家退出 ------> 新线程写入
	 */
	//Connection C = Data.C;

	/**
	 * 解决并发下的相同名字
	 * 添加唯一键 (依然会锁表 :( )
	 */
    @Override
	public boolean initPlayersSqlite(String user) {
		PreparedStatement playerdata = null;
		PreparedStatement playerpriv = null;
		boolean result = true;
		Connection C = null;
		try {
			C = Jdbc.getConnection();
			playerdata = C.prepareStatement("INSERT INTO PlayerData VALUES ('0',?,'0','0','0','1','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','1','0','0','0','0','0','0')");
			playerpriv = C.prepareStatement("INSERT INTO PlayerPrivate VALUES (?,'0','0','0')");
			playerdata.setString(1,user);
			playerpriv.setString(1,user);
			playerdata.execute();
			playerpriv.execute();
			C.commit();
			result = false;
		} catch (SQLIntegrityConstraintViolationException e) {
			//Log.info(e);
			// 名字并发重复
		} catch (SQLException e) {
			Log.fatal(e);
		} finally {
			close(playerdata);
			close(playerpriv);
			close(C);
		}
		return result;
	}

	@Override
	public void savePlayer(com.github.dr.extension.data.db.PlayerData data, String user) {
		PreparedStatement playerdata = null;
		PreparedStatement playerpriv = null;
		Connection C = null;
		try {
			C = Jdbc.getConnection();
			playerdata = C.prepareStatement("UPDATE PlayerData SET UUID=?,User=?,IP=?,GMT=?,Country=?,Time_format=?,Language=?,LastLogin=?,Buildcount=?,Dismantledcount=?,Cumulative_build=?,Pipe_build=?,Kickcount=?,Translate=?,Level=?,Exp=?,Reqexp=?,Reqtotalexp=?,Playtime=?,Pvpwincount=?,Pvplosecount=?,Authority=?,Authority_effective_time=?,Lastchat=?,Deadcount=?,Killcount=?,Joincount=?,Breakcount=? WHERE User=?");
			playerpriv = C.prepareStatement("UPDATE PlayerPrivate SET User=?,Mail=?,PasswordHash=?,CSPRNG=? WHERE User=?");
			playerdata.setString(1,data.uuid);
			playerdata.setString(2,data.user);
			playerdata.setLong(3,data.ip);
			playerdata.setInt(4,data.gmt);
			playerdata.setString(5,data.country);
			playerdata.setByte(6,data.timeFormat);
			playerdata.setString(7,data.language);
			playerdata.setLong(8,data.lastLogin);
			playerdata.setInt(9,data.buildCount);
			playerdata.setInt(10,data.dismantledCount);
			playerdata.setInt(11,data.cumulativeBuild);
			playerdata.setInt(12,data.pipeBuild);
			playerdata.setInt(13,data.kickCount);
			playerdata.setInt(14,booleantoInt(data.translate));
			playerdata.setInt(15,data.level);
			playerdata.setShort(16,data.exp);
			playerdata.setLong(17,data.reqexp);
			playerdata.setLong(18,data.reqtotalExp);
			playerdata.setLong(19,data.playTime);
			playerdata.setInt(20,data.pvpwinCount);
			playerdata.setInt(21,data.pvploseCount);
			playerdata.setInt(22,data.authority);
			playerdata.setLong(23,data.authorityEffectiveTime);
			playerdata.setLong(24,data.lastChat);
			playerdata.setInt(25,data.deadCount);
			playerdata.setInt(26,data.killCount);
			playerdata.setInt(27,data.joinCount);
			playerdata.setInt(28,data.breakCount);
			playerdata.setString(29,user);
			playerdata.execute();
			playerpriv.setString(1,data.user);
			playerpriv.setString(2,data.mail);
			//playerpriv.setInt(3,booleantoInt(data.online));
			playerpriv.setString(3,data.passwordHash);
			playerpriv.setString(4,data.csprng);
			playerpriv.setString(5,user);
			playerpriv.execute();
			C.commit();
		} catch (SQLException e) {
			Log.error(e);
		} finally {
			close(playerdata);
			close(playerpriv);
			close(C);
		}
	}


    @Override
	public void getSqlite(com.github.dr.extension.data.db.PlayerData data, String user) {
		PreparedStatement playerdata = null;
		PreparedStatement playerpriv = null;
		ResultSet rs = null;
		ResultSet rss = null;
		Connection C = null;
		try {
			C = Jdbc.getConnection();
			playerdata = C.prepareStatement("SELECT * FROM PlayerData WHERE User=? FOR UPDATE");
			playerdata.setString(1,user);
			rs = playerdata.executeQuery();
			while (rs.next()) {
				// 防止游戏玩一半登录 导致数据飞天
				data.uuid 						= rs.getString("UUID");
				data.user 						= rs.getString("User");
				data.ip 						= rs.getLong("IP");
				data.gmt 						= rs.getInt("GMT");
				data.country 					= rs.getString("Country");
				data.timeFormat 				= rs.getByte("Time_format");
				data.language 					= rs.getString("Language");
				data.lastLogin 					= rs.getLong("LastLogin");
				data.buildCount 				+=rs.getInt("Buildcount");
				data.dismantledCount 			+=rs.getInt("Dismantledcount");
				data.cumulativeBuild 			+=rs.getInt("Cumulative_build");
				data.pipeBuild					+=rs.getInt("Pipe_build");
				data.kickCount 					+=rs.getInt("Kickcount");
				data.translate 					= inttoBoolean(rs.getInt("Translate"));
				data.level 						= rs.getInt("Level");
				data.exp 						= rs.getShort("Exp");
				data.reqexp 					= rs.getLong("Reqexp");
				data.reqtotalExp 				= rs.getLong("Reqtotalexp");
				data.playTime 					+=rs.getLong("Playtime");
				data.pvpwinCount 				+=rs.getInt("Pvpwincount");
				data.pvploseCount 				+=rs.getInt("Pvplosecount");
				data.authority 					= rs.getInt("Authority");
				data.authorityEffectiveTime 	= rs.getLong("Authority_effective_time");
				data.lastChat 					= rs.getLong("Lastchat");
				data.deadCount 					+= rs.getInt("Deadcount");
				data.killCount 					+= rs.getInt("Killcount");
				data.joinCount 					+= rs.getInt("Joincount");
				data.breakCount 				+= rs.getInt("Breakcount");
			}
			playerpriv = C.prepareStatement("SELECT * FROM PlayerPrivate WHERE User=?");
			playerpriv.setString(1,user);
			rss = playerpriv.executeQuery();
			while (rss.next()) {
				data.mail 						= rss.getString("Mail");
				//data.online 					= inttoBoolean(rss.getInt("Online"));
				data.passwordHash 				= rss.getString("PasswordHash");
				data.csprng 					= rss.getString("CSPRNG");
			}
		} catch (SQLException e) {
			Log.error(e);
		} finally {
			close(rs,playerdata);
			close(rss,playerpriv);
			close(C);
		}
	}

	@Override
	public Map<String,Object> getOnlineUser(String user) {
    	return null;
	}

	@Override
	public Map<String,Object> getOnlineUuid(String uuid) {
    	return null;
	}

	@Override
	public boolean isSqliteUser(String user) {
        boolean result = true;
        PreparedStatement stmt = null;
        ResultSet rs = null;
		Connection C = null;
        try {
			C = Jdbc.getConnection();
			stmt = C.prepareStatement("SELECT COUNT(User) FROM PlayerPrivate where User=? LOCK IN SHARE MODE");
            // 真的奇怪?
            stmt.setString(1,user);
            rs = stmt.executeQuery();
            rs.next();
            if(rs.getInt(1)>0) {
                result = false;
            }
            //用户名存在 避免冲突
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            close(rs,stmt);
			close(C);
        }
        return result;
    }

    @Override
	public boolean isOnlineUser(String user) {
    	return false;
	}

	//
	// 		KEY START
	//

    @Override
	public void addKey(String keys, int authority, long time, long expire, int total) {
		PreparedStatement key = null;
		Connection C = null;
		try {
			C = Jdbc.getConnection();
			key = C.prepareStatement("INSERT INTO KeyData VALUES (?,?,?,?,?,?) FOR UPDATE");
			key.setString(1,keys);
			key.setInt(2,authority);
			key.setInt(3,total);
			key.setInt(4,total);
			key.setLong(5,time);
			key.setLong(6,expire);
			key.execute();
			C.commit();
		} catch (Exception e) {
			Log.fatal(e);
		} finally {
			close(key);
			close(C);
		}
	}


    @Override
	public void saveKey(String keys, int authority, int total, int surplus, long time, long expire) {
		PreparedStatement key = null;
		Connection C = null;
		try {
			C = Jdbc.getConnection();
			key = C.prepareStatement("UPDATE KeyData SET KEY=?,Authority=?,Total=?,Surplus=?,Time=?,Expire=? WHERE KEY=? FOR UPDATE");
			key.setString(1,keys);
			key.setInt(2,authority);
			key.setInt(3,total);
			key.setInt(4,surplus);
			key.setLong(5,time);
			key.setLong(6,expire);
			key.setString(7,keys);
			key.execute();
			C.commit();
		} catch (SQLException e) {
			Log.error(e);
		} finally {
			close(key);
			close(C);
		}
	}


    @Override
	public List<Map<String,Object>> getKey() {
		PreparedStatement key = null;
		ResultSet rs = null;
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Connection C = null;
		try {
			C = Jdbc.getConnection();
			key = C.prepareStatement("SELECT * FROM KeyData LOCK IN SHARE MODE");
			rs = key.executeQuery();
			while (rs.next()) {
				Map<String,Object> data = new HashMap<String,Object>(8);
				data.put("Key",rs.getString("KEY"));
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
			close(C);
		}
		return result;
	}


    @Override
	public Map<String,Object> getKey(String keys) {
		PreparedStatement key = null;
		ResultSet rs = null;
		Map<String,Object> result = new HashMap<String,Object>(8);
		Connection C = null;
		try {
			C = Jdbc.getConnection();
			key = C.prepareStatement("SELECT * FROM KeyData WHERE KEY=? LOCK IN SHARE MODE");
			key.setString(1,keys);
			rs = key.executeQuery();
			while (rs.next()) {
				result.put("Key",rs.getString("KEY"));
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
			close(C);
		}
		return result;
	}


    @Override
	public void rmKey() {
		rmKey(null);
	}


    @Override
	public void rmKey(String keys) {
		PreparedStatement key = null;
		Connection C = null;
		try {
			C = Jdbc.getConnection();
			if (keys != null) {
				key = C.prepareStatement("DELETE FROM KeyData WHERE KEY=? FOR UPDATE");
				key.setString(1,keys);
			} else {
                key = C.prepareStatement("DELETE FROM KeyData FOR UPDATE");
            }
			key.execute();
			C.commit();
		} catch (SQLException e) {
			Log.error(e);
		} finally {
			close(key);
			close(C);
		}
	}


    @Override
	public boolean isSqliteKey(String keys) {
		boolean result = true;
		PreparedStatement key = null;
		ResultSet rs = null;
		Connection C = null;
		try {
			C = Jdbc.getConnection();
			key = C.prepareStatement("SELECT COUNT(KEY) FROM KeyData WHERE KEY=?");
			key.setString(1,keys);
			rs = key.executeQuery();
			rs.next();
			if(rs.getInt(1)>0) {
                result = false;
            }
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			close(rs,key);
			close(C);
		}
		return result;
	}
}