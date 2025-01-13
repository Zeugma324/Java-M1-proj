package BD_Connect;

import Objects.*;
import connexion.Connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;

public class ProduitBD {

    public static TreeMap<Integer, String> id_catAndCat;
    public static TreeMap<String, String> subCatAndMainCat;
    public static TreeMap<String, Integer> catAndId_cat;

    static {
        try {
            String query = "SELECT id_cat, main_category, sub_category FROM categorie";
            ResultSet res = Connect.executeQuery(query);

            while (res.next()) {
                id_catAndCat.put(res.getInt("id_cat"), res.getString("sub_category"));
                catAndId_cat.put(res.getString("sub_category"), res.getInt("id_cat"));
                subCatAndMainCat.put(res.getString("sub_category"), res.getString("main_category"));
            }
            Connect.closeConnexion();
        } catch (SQLException e) {
        }
    }

    private void update(Produit produit, String key, String value) throws SQLException {
        String query = "UPDATE produit SET "+key+" = '"+value+"' WHERE id_produit = "+ produit.getId();
        Connect.executeUpdate(query);
    }

    private void update( Produit produit, String key, int value) throws SQLException {
        String query = "UPDATE produit SET "+key+" = '"+value+"' WHERE id_produit = "+ produit.getId();
        Connect.executeUpdate(query);
    }

    public static Produit loadProduit(int id_produit) throws SQLException {
        String query = "SELECT * FROM produit WHERE id_produit = "+id_produit;
        ResultSet res = Connect.executeQuery(query);
        if(res.next()){
            return (new Produit(
                    res.getInt("id_produit"),
                    res.getString("name"),
                    res.getDouble("ratings"),
                    res.getInt("no_of_ratings"),
                    res.getInt("discount_price"),
                    res.getInt("actual_price"),
                    res.getInt("category")
            ));
        }
        return null;
    }

    public static Produit addProduit(String name, double rating, int no_of_ratings, int discount_price, int actual_price, int category) throws SQLException {
        String query = "INSERT INTO produit(name, ratings, no_of_ratings, discount_price, actual_price, category) " +
                "VALUES ('" + name + "', '" + rating + "', '" + no_of_ratings + "', '" + discount_price + "', '" + actual_price + "', '" + category + "')";
        int id = Connect.creationWithAutoIncrement(query);
        return new Produit(id, name, rating, no_of_ratings, discount_price, actual_price, category);
    }


}
