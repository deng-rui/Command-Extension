package com.github.dr.extension.util.pool.db;

import com.github.dr.extension.util.log.Log;

import java.sql.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Dr
 */
public class JdbcPool {

	private final LinkedBlockingQueue<Connection> FREE_CONNECTION = new LinkedBlockingQueue<Connection>();

	protected JdbcPool(int end,String url,String user,String passwd) {
		// 一次性创建end个连接
		for (int i = 0; i < end; i++) {
			try {
				Connection conn = DriverManager.getConnection(url,user,passwd);
				// 关闭自动提交
            	conn.setAutoCommit(false);
				// 将连接加入连接池中
				FREE_CONNECTION.offer(conn);
			} catch (Exception e) {
				Log.fatal("SQL-POOL",e);
				throw new RuntimeException();
			}
		}
	}

	/**
	 * 获取连接
	 * @return     connection
	 */
	protected Connection getConnection() {
		Connection connection = FREE_CONNECTION.poll();
 		if (connection == null) {
 			try {
 				connection = FREE_CONNECTION.poll(2000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
 		}
 		int a = FREE_CONNECTION.size();
 		return new JdbcConn(connection);
	}



	/**
	 * 放回 
	 * @param conn  connection
	 */
	protected void backConnection(Connection conn) {
		if (conn != null) {
			FREE_CONNECTION.offer(conn);
		}
	}

	protected void heartbeat() {
		if (FREE_CONNECTION.size() > 0) {
			PreparedStatement heartbeat = null;
			Connection C = null;
			try {
				C = Jdbc.getConnection();
				heartbeat = C.prepareStatement("SELECT 1");
				heartbeat.executeQuery();
				C.commit();
			} catch (Exception e) {
				Log.fatal(e);
			} finally {
				try {
					if (heartbeat != null) {
		                heartbeat.close();
		            }
				} catch (Exception e) {  
					heartbeat = null;  
				} finally {
					try {
						if (C != null) {
		                    C.close();
		                }
					} catch (Exception e) {  
					}
				}
			}
		}
	}
}