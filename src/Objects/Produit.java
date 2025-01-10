package Objects;


import connexion.Connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Produit {
    private int id;
    private String name;
    private double rating;
    private int no_of_ratings;
    private int discount_price;
    private int actual_price;
    private int category;
    private int qteStocke;


    public Produit(int id, String name, double rating, int no_of_ratings, int discount_price, int actual_price,
                   int category, int qteStocke) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.no_of_ratings = no_of_ratings;
        this.discount_price = discount_price;
        this.actual_price = actual_price;
        this.category = category;
        this.qteStocke = qteStocke;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getNo_of_ratings() {
        return no_of_ratings;
    }

    public void setNo_of_ratings(int no_of_ratings) {
        this.no_of_ratings = no_of_ratings;
    }

    public int getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(int discount_price) {
        this.discount_price = discount_price;
    }

    public int getActual_price() {
        return actual_price;
    }

    public void setActual_price(int actual_price) {
        this.actual_price = actual_price;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getQteStocke() {
        return qteStocke;
    }

    public void setQteStocke(int qteStocke) {
        this.qteStocke = qteStocke;
    }

    @Override
    public String toString() {
        return "Produit {" +
                "\n  id=" + id +
                ",\n  name='" + name + '\'' +
                ",\n  rating=" + rating +
                ",\n  no_of_ratings=" + no_of_ratings +
                ",\n  discount_price=" + discount_price +
                ",\n  actual_price=" + actual_price +
                ",\n  category=" + category +
                ",\n  qteStocke=" + qteStocke +
                "\n}";
    }

    // requets
    // US 0.1 Je veux visualiser les détails d'un produit
    // US 0.1 : Visualiser les détails d'un produit
    public static void visualiserProduit(int idProd) throws SQLException {
        String query = "SELECT produit.name, produit.actual_price, produit.discount_price, " +
                "categories.main_category, categories.sub_category, stock.quantity " +
                "FROM produit " +
                "JOIN categories ON produit.category = categories.id_cat " +
                "LEFT JOIN stock ON produit.id_produit = stock.id_produit " +
                "WHERE produit.id_produit = " + idProd;
        try (ResultSet result = Connect.executeQuery(query)) {
            if (result.next()) {
                String name = result.getString("name");
                double actualPrice = result.getDouble("actual_price");
                double discountPrice = result.getDouble("discount_price");
                String mainCategory = result.getString("main_category");
                String subCategory = result.getString("sub_category");
                int stockQuantity = result.getInt("quantity");
                System.out.println("Nom du produit : " + name);
                System.out.println("Prix réel : " + actualPrice);
                System.out.println("Prix remisé : " + discountPrice);
                System.out.println("Catégorie principale : " + mainCategory);
                System.out.println("Sous-catégorie : " + subCategory);
                System.out.println("Quantité en stock : " + stockQuantity);
            } else {
                System.out.println("Aucun produit trouvé avec cet ID.");
            }
        }
    }

    // US 0.2 Je veux rechercher un produit par mot-clé.
    public static void rechercherProduit(String keyword) throws SQLException {
        String query = "SELECT produit.id_produit, produit.name, produit.actual_price, produit.discount_price " +
                "FROM produit " +
                "WHERE produit.name LIKE '%" + keyword + "%'"
                +"ORDER BY produit.ratings DESC";

        ResultSet result = Connect.executeQuery(query);

        System.out.println("Produits trouvés pour le mot-clé : " + keyword);
        while (result.next()) {
            int id = result.getInt("id_produit");
            String name = result.getString("name");
            double actualPrice = result.getDouble("actual_price");
            double discountPrice = result.getDouble("discount_price");

            System.out.println("ID : " + id + ", Nom : " + name + ", Prix réel : " + actualPrice + ", Prix remisé : " + discountPrice);
        }
    }

    // US 0.3 : Consulter les produits par catégorie
    public static void consulterProduitsParCategorie(int idCat) throws SQLException {
        String query = "SELECT produit.id_produit, produit.name, produit.actual_price, produit.discount_price " +
                "FROM produit " +
                "WHERE produit.category = " + idCat;
        try (ResultSet result = Connect.executeQuery(query)) {
            System.out.println("Produits dans la catégorie ID : " + idCat);
            while (result.next()) {
                int id = result.getInt("id_produit");
                String name = result.getString("name");
                double actualPrice = result.getDouble("actual_price");
                double discountPrice = result.getDouble("discount_price");
                System.out.println("ID : " + id + ", Nom : " + name + ", Prix réel : " + actualPrice + ", Prix remisé : " + discountPrice);
            }
        }
    }

    //US 0.4 - Trier un produit par quelque chose
    public static void trierProduitsParPrix(int idCat) throws SQLException {
        String query = "SELECT produit.id_produit, produit.name, produit.actual_price, produit.ratings, " +
                "produit.no_of_ratings, produit.discount_price, categories.Id_cat, stock.quantity " +
                "FROM produit " +
                "JOIN categories ON produit.category = categories.Id_cat " +
                "LEFT JOIN stock ON produit.id_produit = stock.id_produit " +
                "WHERE categories.Id_cat = " + idCat +
                " ORDER BY produit.actual_price DESC"; // Trier par prix le plus cher
        try (ResultSet res_all = Connect.executeQuery(query)) {
            List<Produit> produits = new ArrayList<>();
            while (res_all.next()) {
                int id = res_all.getInt("id_produit");
                String name = res_all.getString("name");
                double ratings = res_all.getDouble("ratings");
                int noOfRatings = res_all.getInt("no_of_ratings");
                int discountPrice = res_all.getInt("discount_price");
                int actualPrice = res_all.getInt("actual_price");
                int category = res_all.getInt("Id_cat");
                int qteStocke = res_all.getInt("quantity");
                Produit produit = new Produit(id, name, ratings, noOfRatings, discountPrice, actualPrice, category, qteStocke);
                produits.add(produit);
            }
            produits.forEach(produit -> System.out.println(produit));
        }
    }

    // Quantité Stock test
    public static void afficherQuantiteEnStock(int idProd) throws SQLException {
        String query = "SELECT s.quantity " +
                "FROM stock s " +
                "JOIN produit p ON s.id_produit = p.id_produit " +
                "WHERE p.id_produit = " + idProd;
        try (ResultSet result = Connect.executeQuery(query)) {
            System.out.println("Quantité en stock pour le produit ID " + idProd + ":");
            if (result.next()) {
                int quantity = result.getInt("quantity");
                System.out.println("Quantité : " + quantity);
            } else {
                System.out.println("Aucun produit trouvé avec cet ID.");
            }
        }
    }
    public static void main(String[] args) throws SQLException {
        trierProduitsParPrix(1);
    }
}




