package com.github.dr.extension.data.db;

import com.github.dr.extension.data.global.Data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * @author Dr
 * @Date 2020.5.13 5:18:54
 */
public abstract class AbstractSql {
	Connection C = Data.C;

	//public abstract void getSqlite1(com.github.dr.extension.data.db.PlayerData data, String user);

	/**
	 * 初始化用户
	 * @param user 		用户名
	 * @return 			是否成功 不成功则为并发下抢占失败
	 */
    public abstract boolean initPlayersSqlite(String user);

	/**
	 * 更新玩家数据
	 * @param data 		玩家PlayerData数据集
	 * @param user 		玩家用户名
	 */
	public abstract void savePlayer(com.github.dr.extension.data.db.PlayerData data, String user);

	/**
	 * 获取玩家数据
	 * @param data 		玩家PlayerData数据集
	 * @param user 		玩家用户名
	 */
    public abstract void getSqlite(com.github.dr.extension.data.db.PlayerData data, String user);

    /**
	 * 获取玩家在线数据
	 * @param user 		玩家用户名
	 * @return 			MAP
	 */
    public abstract Map<String,Object> getOnlineUser(String user);

    /**
	 * 获取玩家在线数据
	 * @param uuid 		玩家用户名
	 * @return 			MAP
	 */
	public abstract Map<String,Object> getOnlineUuid(String uuid);

	/**
	 * 用户名是否存在
	 * @param user 		用户名
	 * @return 			是否存在 (并发下无效 只能靠初始化返回 双保险)
	 */
	public abstract boolean isSqliteUser(String user);

	/**
	 * 用户名是否存在
	 * @param user 		用户名
	 * @return 			是否存在 (并发下无效)
	 */
	public abstract boolean isOnlineUser(String user);

	//
	// 		KEY START
	//

	/**
	 * 添加KeyCode
	 * @param keys 		被加入的key
	 * @param authority Key权限等级
	 * @param time	 	Key激活时长
	 * @param expire 	Key到期时间
	 * @param total		Key最多可被使用次数
	 */
    public abstract void addKey(String keys, int authority, long time, long expire, int total);

	/**
	 * 更新Key
	 * @param keys 		KeyCode
	 * @param authority Key权限等级
	 * @param total 	Key最多可被使用次数
	 * @param surplus	Key已被使用次数
	 * @param time 		Key激活时长
	 * @param expire 	Key到期时间
	 */
    public abstract void saveKey(String keys, int authority, int total, int surplus, long time, long expire);

	/**
	 * 获取全部Key
	 * @return 			Key列表
	 */
    public abstract List<Map<String,Object>> getKey();

	/**
	 * 获取指定Key信息
	 * @param keys 		KeyCode
	 * @return 			指定Key信息
	 */
    public abstract Map<String,Object> getKey(String keys);

	/**
	 * 删除全部Key
	 */
    public abstract void rmKey();

	/**
	 * 删除指定Key
	 * @param keys		KeyCode
	 */
    public abstract void rmKey(String keys);

	/**
	 * 查询是否存在KeyCode
	 * @param keys 		KeyCode
	 * @return			true/false
	 */
	public abstract boolean isSqliteKey(String keys);

	/**
	 * 关闭流
	 * @param conn
	 */
	void close(Connection conn) {
		close(null,null,conn);
	}
	/**
	 * 关闭流
	 * @param stmt
	 */
	void close(Statement stmt) {
		close(stmt,null,null);
	}

	/**
	 * 关闭流
	 * @param rs
	 * @param stmt
	 */
	void close(ResultSet rs, Statement stmt) {
		close(rs,stmt,null);
	}

	/**
	 * 关闭流
	 * @param stmt
	 * @param conn
	 */
	void close(Statement stmt, Connection conn) {
		close(null,stmt,conn);
	}

	/**
	 * 关闭流
	 * @param stmt
	 * @param stmt
	 */
	void close(Statement stmt0, Statement stmt1) {
		close(stmt0,stmt1,null);
	}

	/**
	 * 关闭流
	 * @param rs
	 * @param stmt
	 * @param conn
	 */
	void close(ResultSet rs, Statement stmt, Connection conn) {
		try {
			if (rs != null) {
                rs.close();
            }
		} catch (Exception e) {  
			rs = null;  
		} finally {
			try {
				if (stmt != null) {
                    stmt.close();
                }
			} catch (Exception e) {  
				stmt = null;  
			} finally {
				try {
					if (conn != null) {
                        conn.close();
                    }
				} catch (Exception e) {  
					conn = null;  
				}
			}
		}
	}

	/**
	 * 关闭流
	 * @param stmt
	 * @param stmt
	 * @param stmt
	 */
	void close(Statement stmt0, Statement stmt1, Statement stmt2) {
		try {
			if (stmt0 != null) {
                stmt0.close();
            }
		} catch (Exception e) {  
			stmt0 = null;  
		} finally {
			try {
				if (stmt1 != null) {
                    stmt1.close();
                }
			} catch (Exception e) {  
				stmt1 = null;  
			} finally {
				try {
					if (stmt2 != null) {
                    	stmt2.close();
                	}
				} catch (Exception e) {  
					stmt2 = null;  
				}
			}
		}
	}
}