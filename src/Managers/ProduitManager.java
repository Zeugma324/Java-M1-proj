package Managers;

import java.util.*;
import Objects.*;
import static Objects.Produit.*;
import connexion.Connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;



public class ProduitManager {
    // requets
    // US 0.1 Je veux visualiser les détails d'un produit
    // US 0.1 : Visualiser les détails d'un produit
    public static void visualiserProduit(int idProd) throws SQLException {
        Produit prod1 = findProduit(idProd);
        System.out.println(prod1);
    }

    // US 0.2 Je veux rechercher un produit par mot-clé.
    public static void rechercherProduit(String keyword) throws SQLException {
        String query = "SELECT produit.id_produit " +
                "FROM produit " +
                "WHERE produit.name LIKE '%" + keyword + "%'" +
                "LEFT JOIN stock ON produit.id_produit = stock.id_produit " +
                " ORDER BY produit.ratings DESC";

        ResultSet result = Connect.executeQuery(query);

        ArrayList<Produit> produits = new ArrayList<>();
        System.out.println("Produits trouvés pour le mot-clé : " + keyword);
        while (result.next()) {
            produits.add(findProduit(result.getInt("id_produit")));
        }
        produits.forEach(produit -> System.out.println(produit));
    }

    // US 0.3 : Consulter les produits par catégorie
    public static void consulterProduitsParCategorie(int idCat) throws SQLException {
        String query = "SELECT produit.id_produit " +
                "FROM produit " +
                "WHERE produit.category = " + idCat;
        ResultSet result = Connect.executeQuery(query);
        ArrayList<Integer> ids = new ArrayList<>();
        while(result.next()) {
            ids.add(result.getInt("id_produit"));
        }
        ids.stream()
                .map(id -> {
                    try {
                        return findProduit(id);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(produit -> produit != null)
                .forEach(System.out::println);
    }

    //US 0.4 - Trier un produit par quelque chose
    public static void trierProduits(int idCat) throws SQLException {
        String query = "SELECT produit.id_produit " +
                "FROM produit " +
                "JOIN categories ON produit.category = categories.Id_cat " +
                "WHERE categories.Id_cat = " + idCat ;
        ResultSet result = Connect.executeQuery(query);
        ArrayList<Integer> ids = new ArrayList<>();
        while(result.next()) {
            ids.add(result.getInt("id_produit"));
        }

        ids.stream()
                .map(id -> {
                    try {
                        return findProduit(id);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sorted(selectComparator())
                .forEach(produit -> System.out.println(produit));

    }
    public static Comparator selectComparator(){

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
            case "1" -> Comparator.comparing(Produit::getName);
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
