package Requets;

import java.sql.*;

public class Produits {
    static String url = "jdbc:mysql://sql7.freesqldatabase.com:3306/sql7756463";
    static String user = "sql7756463";
    static String mdp = "iFgwWVZFHW";

    //US 0.1 Je veux visualiser les détails d'un produit : prix unitaire, prix au kg, nutriscore, libellé article, poids, condionnement, ...
    public static void visualiser ( int idProd){
        String libelle = "";
        String main_category = "";
        String sub_category = "";
        Double rating = 0.0;
        int nb_reviews = 0;
        int discounted_price = 0;
        int actual_price = 0;
        int discount_percentage;

        String where = "WHERE id_produit = " + idProd;

        String query_all = "SELECT * FROM produit JOIN categories ON produit.category = categories.Id_cat";

        try (Connection con = DriverManager.getConnection(url, user, mdp);
             Statement stm = con.createStatement();)
        {

            ResultSet res_all = stm.executeQuery(query_all + " " + where);
            if (res_all.next()) {
                libelle = res_all.getString("name");
                main_category = res_all.getString("main_category");
                sub_category = res_all.getString("sub_category");
                rating = res_all.getDouble("ratings");
                nb_reviews = res_all.getInt("no_of_ratings");
                discounted_price = res_all.getInt("discount_price");
                actual_price = res_all.getInt("actual_price");

            }
        } catch (SQLException e) {
            System.out.println("Requete/Syntaxe incorrect");
            e.printStackTrace();
        }
        if (actual_price != 0) {
            discount_percentage = discounted_price * 100 / actual_price;
        } else {
            discount_percentage = 0; // 或其他默认值
        }

        System.out.println("Produit libelle : " + libelle);
        System.out.println("Produit main_category : " + main_category);
        System.out.println("Produit sub_category : " + sub_category);
        System.out.println("Produit rating : " + rating);
        System.out.println("Produit nb_reviews : " + nb_reviews);
        System.out.println("Produit discounted_price : " + discounted_price);
        System.out.println("Produit actual_price : " + actual_price);
        System.out.println("Produit discount_percentage : " + discount_percentage + "%");


    }

    //US 0.4 Je veux trier une liste de produits
    public static void trierProduits(int idCat){
        String where = "WHERE category = " + idCat;
        String query = "SELECT name FROM produit JOIN categories ON produit.category = categories.Id_cat";

        try (Connection con = DriverManager.getConnection(url, user, mdp);
            Statement stm = con.createStatement();)
        {
            ResultSet res_all = stm.executeQuery(query + " " + where);
            int count = 1;
            while (res_all.next()) {
                System.out.print(count + ". ");
                System.out.println(res_all.getString("name"));
                count++;
            }

        }catch (SQLException e) {
            System.out.println("Requete/Syntaxe incorrect");
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        // exemple de utilisation de US 0.1
//        visualiser(3);

        // exemple de utilisation de US 0.4
        trierProduits(3);
    }
}
