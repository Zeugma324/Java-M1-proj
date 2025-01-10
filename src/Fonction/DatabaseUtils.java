package Fonction;

import connexion.Connect;

import java.sql.Connection;
import java.sql.*;

public class DatabaseUtils {

    // execute querys and return result set
    public static ResultSet executeQuery(String query) {
        try (Connection con = Connect.getConnection();
            Statement stm = con.createStatement()){
            return stm.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // insertions and update
    public static int executeUpdate(String query) {
        try (Connection con = Connect.getConnection();
             Statement stm = con.createStatement()){
            return stm.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Check if record exists
    public static boolean recordExists(String query) {
        try (ResultSet rs = executeQuery(query)) {
            return rs != null && rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
