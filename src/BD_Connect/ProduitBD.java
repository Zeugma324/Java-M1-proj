package BD_Connect;

import Objects.Produit;
import connexion.Connect;

import java.sql.*;
import java.util.*;

public class ProduitBD {

    // Initialisation des TreeMap
    public static TreeMap<Integer, String> id_catAndCat = new TreeMap<>();
    public static TreeMap<String, String> subCatAndMainCat = new TreeMap<>();
    public static TreeMap<String, Integer> catAndId_cat = new TreeMap<>();

    public static HashMap<String, Integer> mainCatAndAvgNoRating = new HashMap<>();
    public static HashMap<String, Integer> mainCatAndAvgRating = new HashMap<>();

    // Bloc statique pour charger les catégories depuis la base de données
    static {
        try (Connection conn = Connect.getConnexion();
             Statement stmt = conn.createStatement();
             ResultSet res = stmt.executeQuery("SELECT id_cat, main_category, sub_category FROM categories")) {

            // Vider les TreeMap pour éviter les doublons ou les données corrompues
            id_catAndCat.clear();
            catAndId_cat.clear();
            subCatAndMainCat.clear();

            // Remplir les TreeMap avec les résultats
            while (res.next()) {
                id_catAndCat.put(res.getInt("id_cat"), res.getString("sub_category"));
                catAndId_cat.put(res.getString("sub_category"), res.getInt("id_cat"));
                subCatAndMainCat.put(res.getString("sub_category"), res.getString("main_category"));
            }

            System.out.println("Produits chargées avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du chargement des catégories depuis la table 'categories'", e);
        }
    }

    static {
        try(Connection conn = Connect.getConnexion();
            Statement stm = conn.createStatement();
        ){
            String query = "SELECT main_category, AVG(no_of_ratings) as AVG_PRIX " +
                    "FROM categories c JOIN produit p ON c.id_cat = p.category " +
                    "GROUP BY main_category";

            ResultSet rs = stm.executeQuery(query);
            while (rs.next()) {
                mainCatAndAvgNoRating.put(rs.getString("main_category"), rs.getInt("AVG_PRIX"));
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du chargement des catégories depuis la table 'categories'", e);
        }
    }

    static {
        try(Connection conn = Connect.getConnexion();
            Statement stm = conn.createStatement();
        ){
            String query = "SELECT main_category, AVG(ratings * 10) as AVG_rating " +
                    "FROM categories c JOIN produit p ON c.id_cat = p.category " +
                    "GROUP BY main_category";

            ResultSet rs = stm.executeQuery(query);
            while (rs.next()) {
                mainCatAndAvgNoRating.put(rs.getString("main_category"), rs.getInt("AVG_rating"));
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du chargement des catégories depuis la table 'categories'", e);
        }
    }


    // Méthode pour mettre à jour un produit (valeur String)
    private void update(Produit produit, String key, String value) throws SQLException {
        String query = "UPDATE produit SET " + key + " = ? WHERE id_produit = ?";

        try (Connection conn = Connect.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, value);
            pstmt.setInt(2, produit.getId());
            pstmt.executeUpdate();
        }
    }

    // Méthode pour mettre à jour un produit (valeur entière)
    private void update(Produit produit, String key, int value) throws SQLException {
        String query = "UPDATE produit SET " + key + " = ? WHERE id_produit = ?";

        try (Connection conn = Connect.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, value);
            pstmt.setInt(2, produit.getId());
            pstmt.executeUpdate();
        }
    }

    // Charger un produit depuis la base
    public static Produit loadProduit(int id_produit) throws SQLException {
        String query = "SELECT * FROM produit WHERE id_produit = ?";

        try (Connection conn = Connect.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id_produit);

            try (ResultSet res = pstmt.executeQuery()) {
                if (res.next()) {
                    return new Produit(
                            res.getInt("id_produit"),
                            res.getString("name"),
                            res.getDouble("ratings"),
                            res.getInt("no_of_ratings"),
                            res.getInt("discount_price"),
                            res.getInt("actual_price"),
                            res.getInt("category")
                    );
                }
            }
        }

        return null; // Aucun produit trouvé
    }

    // Ajouter un produit dans la base
    public static Produit addProduit(String name, double rating, int no_of_ratings, int discount_price, int actual_price, int category) throws SQLException {
        String query = "INSERT INTO produit (name, ratings, no_of_ratings, discount_price, actual_price, category) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Connect.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, rating);
            pstmt.setInt(3, no_of_ratings);
            pstmt.setInt(4, discount_price);
            pstmt.setInt(5, actual_price);
            pstmt.setInt(6, category);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new Produit(id, name, rating, no_of_ratings, discount_price, actual_price, category);
                }
            }
        }

        throw new SQLException("Échec de la création du produit, aucun ID généré.");
    }
}
