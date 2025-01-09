package Requets;

import java.sql.*;

public class Produits {
    static String url = "jdbc:mysql://sql7.freesqldatabase.com:3306/sql7756463";
    static String user = "sql7756463";
    static String mdp = "iFgwWVZFHW";

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
        String query_libelle = "SELECT name FROM produit ";
        String query_main_category = "SELECT main_category FROM produit JOIN categories ON produit.category = categories.Id_cat";

        String query_sub_category = "SELECT sub_category FROM produit JOIN categories ON produit.category = categories.Id_cat";
        String query_rating = "SELECT ratings FROM produit ";
        String query_nb_reviews = "SELECT no_of_ratings FROM produit ";
        String query_discounted_price = "SELECT discount_price FROM produit ";
        String query_actual_price = "SELECT actual_price FROM produit ";
        String query_discount_percentage = "SELECT discount_percentage FROM produit ";



        try (Connection con = DriverManager.getConnection(url, user, mdp);
             Statement stm = con.createStatement();)
        {
            ResultSet res_libelle = stm.executeQuery(query_libelle + " " + where);
            if (res_libelle.next()) {
                libelle = res_libelle.getString("name");
            }

            ResultSet res_main_category = stm.executeQuery(query_main_category + " " + where);
            if (res_main_category.next()) {
                main_category = res_main_category.getString("main_category");
            }
            ResultSet res_sub_category = stm.executeQuery(query_sub_category + " " + where);
            if (res_sub_category.next()) {
                sub_category = res_sub_category.getString("sub_category");
            }
            ResultSet res_rating = stm.executeQuery(query_rating + " " + where);
            if (res_rating.next()) {
                rating = res_rating.getDouble("ratings");
            }
            ResultSet res_nb_reviews = stm.executeQuery(query_nb_reviews + " " + where);
            if (res_nb_reviews.next()) {
                nb_reviews = res_nb_reviews.getInt("no_of_ratings");
            }
            ResultSet res_discounted_price = stm.executeQuery(query_discounted_price + " " + where);
            if (res_discounted_price.next()) {
                discounted_price = res_discounted_price.getInt("discount_price");
            }
            ResultSet res_actual_price = stm.executeQuery(query_actual_price + " " + where);
            if (res_actual_price.next()) {
                actual_price = res_actual_price.getInt("actual_price");
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
    public static void main(String[] args) {
        visualiser(1);
    }
}
