package connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Connect {

	public static final String url = "jdbc:mysql://sql7.freesqldatabase.com/sql7756463";
	public static final String user = "sql7756463";
	public static final String mdp = "iFgwWVZFHW";


	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, user, mdp);
	}


	public static ResultSet executeQuery(String query) throws SQLException {
		Connection con = getConnection();
		Statement stm = con.createStatement();
		return stm.executeQuery(query);
	}
}
