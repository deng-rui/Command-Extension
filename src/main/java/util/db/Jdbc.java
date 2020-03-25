package extension.util.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import extension.util.db.JdbcPool;

import extension.data.global.Config;
import extension.util.file.FileUtil;
//GA-Exted

public class Jdbc {
	// 可以更改为在线SQL 使得可以多服同步
	private static JdbcPool pool = new JdbcPool(10,FileUtil.File(Config.Plugin_Data_Path).getPath("Data.db"));

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
