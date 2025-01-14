package Managers;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import BD_Connect.ProduitBD;
import Objects.*;
import connexion.Connect;


public class UserManager {
    // US2.1 Je veux consulter la liste des produits que je commande le plus fréquemment.
    public static ArrayList<Produit> AfficherProduitFrequents(User user, int limit) throws SQLException {
        String query = String.format( "SELECT p.name, p.actual_price, COUNT(pa.id_produit) AS purchase_count " +
                "FROM panier pa " +
                "JOIN produit p ON pa.id_produit = p.id_produit " +
                "WHERE pa.id_user = '%s' GROUP BY pa.id_produit " +
                "ORDER BY purchase_count DESC ", user.getIdUser());

        ResultSet result = Connect.executeQuery(query);

        ArrayList<Produit> produits = new ArrayList<>();
        if (!result.next()) {
            System.out.println("No frequent products found for this user.");
        }else{
            while(result.next()){
                produits.add(ProduitBD.loadProduit(result.getInt("id_produit")));
            }
        }
        produits.stream()
                .limit(limit)
                .forEach(produit -> System.out.println("ID : " + produit.getId() + " Libelle : " + produit.getLibelle()));
        return produits;
    }

    public static HashMap<Produit, Integer> historyPanier(User user) throws SQLException {
        String query = String.format("SELECT id_produit, qte_produit FROM panier WHERE id_user = '%s' ", user.getIdUser());
        ResultSet result = Connect.executeQuery(query);
        HashMap<Produit, Integer> produits = new HashMap<>();
        while(result.next()){
            Produit produit = ProduitBD.loadProduit(result.getInt("id_produit"));
            if(produits.containsKey(produit)){
                produits.put(produit, produits.get(produit) + result.getInt("qte_produit"));
            }else{
                produits.put(produit, result.getInt("qte_produit"));
            }
        }
        return produits;
    }

    // US2.2 Je veux consulter mes habitudes de consommation (bio, nutriscore, catégorie de produits, marques).
    public static void AfficherHabitudes(User user) throws SQLException {
        HashMap<Produit, Integer> produits = historyPanier(user);
        HashMap<String, Integer> category_habitude = produits.entrySet().stream()
                .forEach(produit ->
                {
                })
    }

    // US2.3 Je veux valider les préférences que me propose le système afin d'avoir des produits de remplacements
    // qui correspondent mieux à mes habitudes.






    private static String readLine() {
        String res = System.console().readLine();
        if (res == null) {
            return readLine("Isnput is null, please try again");
        } else {
            return res;
        }
    }

    private static String readLine(String message) {
        print(message);
        String res = System.console().readLine();
        if (res == null) {
            return readLine("Isnput is null, please try again");
        } else {
            return res;
        }
    }

    private static void print(String str) {
        System.out.println(str);
    }
}
