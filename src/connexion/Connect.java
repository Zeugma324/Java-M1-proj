
package connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Connect {


	private static final String url = "jdbc:mysql://sql7.freesqldatabase.com/sql7756463";
	private static final String user = "sql7756463";
	private static final String mdp = "iFgwWVZFHW";

	public static Connection getConnexion() throws SQLException {
		return DriverManager.getConnection(url, user, mdp);
	}

	public static ResultSet executeQuery(String query) throws SQLException {
		Connection conn = Connect.getConnexion();
		Statement stm = conn.createStatement();
		return stm.executeQuery(query);
	}

	public static void executeUpdate(String query) throws SQLException {
		Connection conn = Connect.getConnexion();
		Statement stm = conn.createStatement();
		stm.executeUpdate(query);
	}

	public static boolean recordExists(String query) {
		try (ResultSet rs = executeQuery(query)) {
			return rs != null && rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
