package connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Connect {

	public static void main(String[] args) {
		String url = "jdbc:mysql://sql7.freesqldatabase.com/sql7756463";
        String user = "sql7756463";
        String mdp = "iFgwWVZFHW";

        String query = "SELECT DISTINCT main_category FROM Produits";

        try (Connection con = DriverManager.getConnection(url, user, mdp);
        	 Statement stm = con.createStatement();)
        {
            ResultSet res = stm.executeQuery(query);
            int nb_colonnes = res.getMetaData().getColumnCount();
            
            for(int i = 1; i <= nb_colonnes; i++) {
       			System.out.print(res.getMetaData().getColumnName(i) + "\t");
       		}
            System.out.println();
           	while(res.next()){
           		for(int i = 1; i <= nb_colonnes; i++) {
           			System.out.print(res.getString(i) + "\t");
           		}
       			System.out.println("");
           	}
        } catch (SQLException e) {
            System.out.println("Requete/Syntaxe incorrect");
            e.printStackTrace();
        }
    }

}
