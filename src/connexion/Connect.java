
package connexion;

import java.sql.*;

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

	public static int creationWithAutoIncrement(String query) throws SQLException {
		int id = -1;
		try (Connection con = Connect.getConnexion();
			 PreparedStatement pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			int affectedRows = pstmt.executeUpdate();
			if (affectedRows == 0) {
				throw new SQLException("Creation failed, no rows affected.");
			}
			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					id = generatedKeys.getInt(1);
				} else {
					throw new SQLException("Creation failed, no ID obtained.");
				}
			}
		}
		return id;
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
