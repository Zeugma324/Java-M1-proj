package connexion;
import java.sql.*;

public class conn {
	public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/products_db";
        String user = "root";
        String mdp = "mdp";

        String query = "SELECT Count(BrandName), BrandName FROM Message_Group GROUP BY BrandName";

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