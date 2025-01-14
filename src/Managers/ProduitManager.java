package Managers;

import java.util.*;

import BD_Connect.ProduitBD;
import Objects.*;
import connexion.Connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;



public class ProduitManager {
    // requets
    // US 0.1 Je veux visualiser les détails d'un produit
    // US 0.1 : Visualiser les détails d'un produit
    public static void visualiserProduit(int idProd) throws SQLException {
        Produit prod = ProduitBD.loadProduit(idProd);
        System.out.println(prod);
    }

    // US 0.2 Je veux rechercher un produit par mot-clé.
    public static void rechercherProduit(String keyword) throws SQLException {
        String query = "SELECT produit.id_produit " +
                "FROM produit " +
                "LEFT JOIN stock ON produit.id_produit = stock.id_produit " +
                "WHERE produit.name LIKE '%" + keyword + "%';";

        ResultSet result = Connect.executeQuery(query);

        ArrayList<Produit> produits = new ArrayList<>();
        System.out.println("Produits trouvés pour le mot-clé : " + keyword);
        while (result.next()) {
            produits.add(ProduitBD.loadProduit(result.getInt("id_produit")));
        }
        produits.stream()
                .sorted(selectComparator())
                .forEach(produit -> System.out.println(produit));
        Connect.closeConnexion();
    }

    // US 0.3 : Consulter les produits par catégorie
    public static ArrayList<Produit> consulterProduitsParCategorie(int idCat) throws SQLException {
        String query = "SELECT * FROM produit WHERE category = " + idCat;
        ResultSet result = Connect.executeQuery(query);
        ArrayList<Produit> liste = new ArrayList<>();

        while (result.next()) {
            liste.add(new Produit(
                    result.getInt("id_produit"),
                    result.getString("name"),
                    result.getDouble("ratings"),
                    result.getInt("no_of_ratings"),
                    result.getInt("discount_price"),
                    result.getInt("actual_price"),
                    result.getInt("category")
            ));
        }

        if (liste.isEmpty()) {
            System.out.println("Aucun produit trouvé dans la catégorie " + idCat);
            return liste;
        }
        liste.stream()
                .sorted(selectComparator())
                .limit(10);
        return liste;
    }

    public static ArrayList<Produit> consulterProduitsParCategorieSansSort(int idCat) throws SQLException {
        String query = "SELECT produit.id_produit " +
                "FROM produit " +
                "WHERE produit.category = " + idCat;
        ResultSet result = Connect.executeQuery(query);
        ArrayList<Integer> ids = new ArrayList<>();
        while(result.next()) {
            ids.add(result.getInt("id_produit"));
        }
        ArrayList<Produit> produits = new ArrayList<>();
        ids.stream()
                .map(id -> {
                    try {
                        return ProduitBD.loadProduit(id);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(produit -> produit != null)
                .forEach(produit -> produits.add(produit));
        return produits;
    }
//    //US 0.4 - Trier un produit par quelque chose
//    public static void trierProduits(int idCat) throws SQLException {
//        String query = "SELECT produit.id_produit " +
//                "FROM produit " +
//                "JOIN categories ON produit.category = categories.Id_cat " +
//                "WHERE categories.Id_cat = " + idCat ;
//        ResultSet result = Connect.executeQuery(query);
//        ArrayList<Integer> ids = new ArrayList<>();
//        while(result.next()) {
//            ids.add(result.getInt("id_produit"));
//        }
//
//        ids.stream()
//                .map(id -> {
//                    try {
//                        return findProduit(id);
//                    } catch (SQLException e) {
//                        throw new RuntimeException(e);
//                    }
//                })
//                .sorted(selectComparator())
//                .forEach(produit -> System.out.println(produit));
//
//    }
    public static Comparator<Produit> selectComparator(){

        System.out.println("Choisissez un champ de tri (1.libelle, 2.rating, 3.price) :");

        String trier = System.console().readLine();
        if(!trier.equals("1") & !trier.equals("2") & !trier.equals("3")){
            System.out.println("Champ invalide");
            selectComparator();
        }

        System.out.println("Choisissez l'ordre de tri (1.ascending, 2.descending) :");
        String order = System.console().readLine();
        if(!order.equals("1") & !order.equals("2") ){
            System.out.println("Champ invalide");
            selectComparator();
        }
        System.out.println("------- il faut attenuate pour quelque seconds --------");
        Comparator<Produit> comparator = switch (trier) {
            case "1" -> Comparator.comparing(Produit::getLibelle);
            case "2" -> Comparator.comparingDouble(Produit::getRating);
            case "3" -> Comparator.comparingInt(Produit::getDiscount_price);
            default -> Comparator.comparingDouble(Produit::getRating);
        };

        if (order.equals("2")) {
            comparator = comparator.reversed();
        }

        return comparator;
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

}
