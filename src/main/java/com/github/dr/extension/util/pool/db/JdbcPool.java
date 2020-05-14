package com.github.dr.extension.util.pool.db;

import java.sql.Connection;
import java.sql.DriverManager;
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
				// 将连接加入连接池中
				FREE_CONNECTION.offer(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

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
	 * @param conn
	 */
	protected void backConnection(Connection conn) {
		if (conn != null) {
			FREE_CONNECTION.offer(conn);
		}
	}
}