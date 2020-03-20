package extension.util.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import extension.util.db.JdbcPoolUtil;

public class JdbcUtil {
	private static JdbcPoolUtil pool = new JdbcPoolUtil(10);

	public static Connection getConnection() throws SQLException{
		return pool.getConnection();
	}

	public static void backConnection(Connection conn) throws SQLException{
		pool.backConnection(conn);
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
