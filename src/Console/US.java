package Console;

import BD_Connect.ProduitBD;
import Objects.Produit;
import connexion.Connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;

public class US {

    // US 0.1 - Visualiser les détails d’un produit
    public static void visualiserProduit(int idProd) throws SQLException {
        Produit prod = ProduitBD.loadProduit(idProd);
        if (prod == null) {
            System.out.println("Aucun produit trouvé avec l'ID " + idProd);
        } else {
            System.out.println("========== Détails du produit ==========");
            System.out.println("ID            : " + prod.getId());
            System.out.println("Nom           : " + prod.getLibelle());
            System.out.println("Rating        : " + prod.getRating());
            System.out.println("Prix remisé   : " + prod.getDiscount_price());
            System.out.println("Prix de base  : " + prod.getActual_price());
            System.out.println("Catégorie     : " + prod.getSub_category() + " (" + prod.getMain_category() + ")");
            System.out.println("========================================");
        }
    }

    // US 0.2 - Rechercher un produit par mot-clé
    public static void rechercherProduit(String keyword) throws SQLException {
        String query = "SELECT id_produit FROM produit WHERE name LIKE '%" + keyword + "%'";
        ResultSet result = Connect.executeQuery(query);
        ArrayList<Produit> produits = new ArrayList<>();

        System.out.println("\nProduits trouvés pour le mot-clé : " + keyword);
        while (result.next()) {
            produits.add(ProduitBD.loadProduit(result.getInt("id_produit")));
        }

        if (produits.isEmpty()) {
            System.out.println("Aucun produit ne correspond à ce mot-clé.");
            return;
        }

        // Tri (US 0.4) si nécessaire :
        produits.stream()
                .sorted(selectComparator())
                .forEach(System.out::println);

        Connect.closeConnexion();
    }

    // US 0.3 - Consulter les produits par catégorie
    public static void consulterProduitsParCategorie(int idCat) throws SQLException {
        String query = "SELECT id_produit FROM produit WHERE category = " + idCat;
        ResultSet result = Connect.executeQuery(query);
        ArrayList<Produit> liste = new ArrayList<>();

        while (result.next()) {
            Produit p = ProduitBD.loadProduit(result.getInt("id_produit"));
            if (p != null) liste.add(p);
        }

        if (liste.isEmpty()) {
            System.out.println("Aucun produit trouvé dans la catégorie " + idCat);
            return;
        }

        // Tri (US 0.4) :
        liste.stream()
             .sorted(selectComparator())
             .forEach(System.out::println);
    }

    // US 0.4 - Trier une liste de produits
    public static Comparator<Produit> selectComparator() {
        System.out.println("Choisissez un champ de tri (1.libelle, 2.rating, 3.price) :");
        String trier = System.console() != null 
                         ? System.console().readLine() 
                         : "1";  // Valeur par défaut si System.console() est null (IDE)

        System.out.println("Ordre de tri (1.asc, 2.desc) :");
        String order = System.console() != null 
                         ? System.console().readLine() 
                         : "1";

        Comparator<Produit> comparator;
        switch (trier) {
            case "1": 
                comparator = Comparator.comparing(Produit::getLibelle);
                break;
            case "2": 
                comparator = Comparator.comparingDouble(Produit::getRating);
                break;
            case "3": 
                comparator = Comparator.comparingInt(Produit::getDiscount_price);
                break;
            default:
                comparator = Comparator.comparing(Produit::getLibelle);
                break;
        }

        if ("2".equals(order)) {
            comparator = comparator.reversed();
        }
        return comparator;
    }
}
