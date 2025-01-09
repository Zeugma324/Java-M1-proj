package connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Connect {

		
		private static final String url = "jdbc:mysql://localhost:3306/products_db";
		private static final String user = "root";
		private static final String mdp = "mdp";
		
		public static Connection getConnexion() throws SQLException {
			return DriverManager.getConnection(url, user, mdp);
		}
		
		public static ResultSet executeQuery(String query) throws SQLException {
			Connection conn = getConnexion();
			Statement stm = conn.createStatement();
			return stm.executeQuery(query);
		}
    }

