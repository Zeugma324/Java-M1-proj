package Console;

import BD_Connect.ProduitBD;
import Objects.Produit;
import connexion.Connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;

public class US {

    // =========================
    // US 0.1 - Visualiser un produit
    // =========================
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
            System.out.println("Catégorie     : " + prod.getSub_category() 
                                             + " (" + prod.getMain_category() + ")");
            System.out.println("========================================");
        }
    }

    // =========================
    // US 0.2 - Rechercher un produit par mot-clé
    // =========================
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

        // Tri (US 0.4) si nécessaire
        produits.stream()
                .sorted(selectComparator())
                .forEach(System.out::println);

        Connect.closeConnexion();
    }

    // =========================
    // US 0.3 - Consulter les produits par catégorie
    // =========================
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

        // Tri (US 0.4)
        liste.stream()
             .sorted(selectComparator())
             .forEach(System.out::println);
    }

    // =========================
    // US 0.4 - Choix du comparateur (tri)
    // =========================
    public static Comparator<Produit> selectComparator() {
        System.out.println("Choisissez un champ de tri (1.libelle, 2.rating, 3.price) :");
        String trier = System.console() != null 
                         ? System.console().readLine() 
                         : "1";  

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

   
    // =========================
    // US 7 - Définir les users VIP
    // =========================
    public static void VIPusers() throws SQLException {
        String sql = "SELECT Id_user FROM panier " +
                     "JOIN PanierCommande ON panier.Id_panier = PanierCommande.panier_id " +
                     "GROUP BY panier.Id_user " +
                     "HAVING COUNT(Id_commande) > 5";

        ResultSet result = Connect.executeQuery(sql);
        System.out.println("Utilisateurs VIP (plus de 5 commandes) :");
        while (result.next()) {
            int userID = result.getInt("Id_user");
            System.out.println("User ID : " + userID);
        }
        Connect.closeConnexion();
    }

    // =========================
    // US 8 - Temps moyen de réalisation d'un panier
    // =========================
    public static void AVGTempRealiserPanier() throws SQLException {
        String sql = "SELECT AVG(TIMESTAMPDIFF(DAY, Date_debut, Date_fin)) AS TempMoyenP " +
                     "FROM panier WHERE Date_fin IS NOT NULL";

        ResultSet result = Connect.executeQuery(sql);
        if (result.next()) {
            double AVGTempDays = result.getDouble("TempMoyenP");
            System.out.println("Temps moyen de réalisation d'un panier : " 
                               + AVGTempDays + " jours");
        } else {
            System.out.println("Aucun panier finalisé trouvé.");
        }
        Connect.closeConnexion();
    }

    // =========================
    // US 9 - Temps moyen de préparation des commandes
    // =========================
    public static void AVGTempPrepareCom() throws SQLException {
        String sql = "SELECT AVG(TIMESTAMPDIFF(DAY, L.date_livrasion, P.Date_fin)) AS TempMoyenC " +
                     "FROM panier P " +
                     "JOIN PanierCommande PC ON P.Id_panier = PC.panier_id " +
                     "JOIN livrasion L ON L.id_commande = PC.Id_commande " +
                     "WHERE P.Date_fin IS NOT NULL " +
                     "AND L.date_livrasion IS NOT NULL";

        ResultSet result = Connect.executeQuery(sql);
        if (result.next()) {
            double AVGTempDays = result.getDouble("TempMoyenC");
            System.out.println("Temps moyen de préparation des commandes : " 
                               + AVGTempDays + " jours");
        } else {
            System.out.println("Aucune commande livrée trouvée.");
        }
        Connect.closeConnexion();
    }
    //STATS
    public static void averagePriceByCategory() throws SQLException {
        // Requête : on joint la table produit et la table categories
        // pour récupérer le sub_category et/ou main_category.
        // On calcule ensuite la moyenne des prix (discount_price) par catégorie.

        String sql = """
            SELECT c.id_cat,
                   c.sub_category,
                   c.main_category,
                   AVG(p.discount_price) AS avg_discount_price
            FROM produit p
            JOIN categories c ON p.category = c.id_cat
            GROUP BY c.id_cat, c.sub_category, c.main_category
            ORDER BY id_cat ASC
            """;

        ResultSet rs = Connect.executeQuery(sql);

        System.out.println("===== Prix moyen par catégorie =====");
        while (rs.next()) {
            int idCat = rs.getInt("id_cat");
            String subCat = rs.getString("sub_category");
            String mainCat = rs.getString("main_category");
            double avgPrice = rs.getDouble("avg_discount_price");

            System.out.printf("Catégorie #%d | %s (%s) | Prix moyen : %.2f\n",
                              idCat, subCat, mainCat, avgPrice);
        }
        Connect.closeConnexion();
    }

// STATS
    
    public static void compareRatingAcrossCategories() throws SQLException {
        // Requête : note (ratings) moyenne par catégorie
        String sql = """
            SELECT c.id_cat,
                   c.sub_category,
                   c.main_category,
                   AVG(p.ratings) AS avg_rating,
                   COUNT(p.id_produit) AS nb_produits
            FROM produit p
            JOIN categories c ON p.category = c.id_cat
            GROUP BY c.id_cat, c.sub_category, c.main_category
            ORDER BY id_cat DESC
            """;

        ResultSet rs = Connect.executeQuery(sql);

        System.out.println("===== Note moyenne par catégorie =====");
        while (rs.next()) {
            int idCat = rs.getInt("id_cat");
            String subCat = rs.getString("sub_category");
            String mainCat = rs.getString("main_category");
            double avgRating = rs.getDouble("avg_rating");
            int nbProduits = rs.getInt("nb_produits");

            System.out.printf(
                "Catégorie #%d | %s (%s) | Note moyenne : %.2f (sur %d produits)\n",
                idCat, subCat, mainCat, avgRating, nbProduits
            );
        }
        Connect.closeConnexion();
    }
}
    


