package com.github.dr.extension.util.pool.db;

import com.github.dr.extension.data.global.Config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Dr
 */
public class Jdbc {
	/**
	 * 在线SQL 使得可以多服同步
	 * 也只支持MYSQL/MARIADB
	 * SQLITE并发感人
	 */
	private static final JdbcPool pool = new JdbcPool(10, "jdbc:mariadb://"+ Config.DB_IP+":"+Config.DB_PORT+"/"+Config.DB_NAME,Config.DB_USER,Config.DB_PASSWD);

	public static synchronized Connection getConnection() throws SQLException{
		return pool.getConnection();
	}

	public static void backConnection(Connection conn) throws SQLException{
		pool.backConnection(conn);
	}

	public void heartbeat() {
		pool.heartbeat();
	}

	public static void close(Statement stmt,Connection conn) {
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
