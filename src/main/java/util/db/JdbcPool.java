package extension.util.db;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.logging.Logger;
//Java

import javax.sql.DataSource;
//Javax

import extension.util.db.JdbcConn;
//GA-Exted

public class JdbcPool implements DataSource {

	private LinkedList<Connection> pool = new LinkedList<Connection>();

	public JdbcPool(int end,String file) {
		// 一次性创建10个连接
		for (int i = 0; i < end; i++) {
			try {
				Connection conn = DriverManager.getConnection("jdbc:sqlite:"+file);
				// 将连接加入连接池中
				pool.add(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public synchronized Connection getConnection() throws SQLException {
		// 取出连接池中一个连接
		// 删除第一个连接返回
		final Connection conn = pool.removeFirst();
		System.out.println("取出一个连接剩余 " + pool.size() + "个连接！");
		return new JdbcConn(conn);
	}

	// 将连接放回连接池
	public synchronized void backConnection(Connection conn) {
		if (conn != null) pool.add(conn);
		System.out.println("将连接 放回到连接池中 数量:" + pool.size());
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return null;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

}