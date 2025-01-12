package Objects;


import connexion.Connect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Produit {
    private int id;
    private String name;
    private double rating;
    private int no_of_ratings;
    private int discount_price;
    private int actual_price;
    private String main_category;
    private String sub_category;


    Produit(int idProduit) throws SQLException {
        String query = "SELECT * FROM produit P " +
                "JOIN categories C ON P.category = C.Id_cat " +
                "WHERE P.id_produit = " + idProduit;
        try (Connection conn = Connect.getConnexion();
             Statement stmt = conn.createStatement();
             ResultSet res = stmt.executeQuery(query)) {
            if (res.next()) {
                this.id = idProduit;
                this.name = res.getString("name");
                this.rating = res.getDouble("ratings");
                this.no_of_ratings = res.getInt("no_of_ratings");
                this.discount_price = res.getInt("discount_price");
                this.actual_price = res.getInt("actual_price");
                this.main_category = res.getString("main_category");
                this.sub_category = res.getString("sub_category");
            }
        }
    }


    private void update( String key, String value) throws SQLException {
        String query = "UPDATE produit SET "+key+" = '"+value+"' WHERE id_produit = "+ id;
        Connect.executeUpdate(query);
    }

    private void update( String key, int value) throws SQLException {
        String query = "UPDATE produit SET "+key+" = '"+value+"' WHERE id_produit = "+ id;
        Connect.executeUpdate(query);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) throws SQLException {
        this.name = name;
        update("name",name);
    }

    public double getRating() {
        return rating;
    }

    public int getNo_of_ratings() {
        return no_of_ratings;
    }

    public int getDiscount_price() {
        return discount_price;
    }

    private void setDiscount_price(int discount_price) throws SQLException {
        this.discount_price = discount_price;
        update("discount_price", discount_price);
    }

    public int getActual_price() {
        return actual_price;
    }

    private void setActual_price(int actual_price) throws SQLException {
        this.actual_price = actual_price;
        update("actual_price", actual_price);
    }

    public String getMain_category() {
        return main_category;
    }

    public String getSub_category() {
        return sub_category;
    }

    private void setSub_category(String sub_category) throws SQLException {
        this.sub_category = sub_category;
        String id_cat = "SELECT Id_cat FROM categories WHERE sub_category = '" + sub_category + "'";
        String query = "UPDATE produit SET category = (" + id_cat + ") WHERE id_produit = " + id;
        Connect.executeUpdate(query);

        String query2 = "SELECT main_category FROM categories WHERE sub_category = '" + sub_category + "'";
        ResultSet res = Connect.executeQuery(query2);
        if (res.next()) {
            this.main_category = res.getString("main_category");
        }
    }

    public static Produit findProduit(int idProduit) throws SQLException {
        return new Produit(idProduit);
    }

    private static int findIdCat(String sub_category) throws SQLException {
        String query = "SELECT Id_cat FROM categories WHERE sub_category = '" + sub_category + "'";
        ResultSet res = Connect.executeQuery(query);
        if (res.next()) {
            return res.getInt("Id_cat");
        }
        return -1;
    }

    private static int addProduitDb(String name, double rating, int no_of_ratings, int discount_price, int actual_price, String sub_category) throws SQLException {
        int Id_cat = findIdCat(sub_category);
        if(Id_cat != -1) {
            String query = "INSERT INTO produit (name, ratings, no_of_ratings, discount_price, actual_price, category) " +
                    "VALUES ('" + name + "', " + rating + ", " + no_of_ratings + ", " + discount_price + ", " + actual_price + ", " + Id_cat + ")";
            return Connect.creationWithAutoIncrement(query);
        }
        return -1;
    }



    public static Produit createProduit(int Id_produit, String name, double rating, int no_of_ratings, int discount_price, int actual_price, String sub_category ) throws SQLException {
         int id = addProduitDb(name,rating,no_of_ratings,discount_price,actual_price,sub_category);
         return new Produit(id);
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rating=" + rating +
                ", no_of_ratings=" + no_of_ratings +
                ", discount_price=" + discount_price +
                ", actual_price=" + actual_price +
                ", main_category='" + main_category + '\'' +
                ", sub_category='" + sub_category + '\'' +
                '}';
    }


}




