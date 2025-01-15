package Requets;

import connexion.Connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class USyan {


    //US01 Je veux visualiser les détails d'un produit : prix unitaire, prix au kg, nutriscore, libellé article, poids, conditionnement, ...
    public static void VisualiserProduit(int productId) {
        String query = "SELECT discount_price, actual_price, ratings, name FROM produit p" +
                ",categories ca WHERE ca.Id_cat=p.category AND id_produit =" + productId;

        try (ResultSet res = Connect.executeQuery(query)){
            if (res.next()) {
                String name = res.getString("name");
                double discount_price = res.getDouble("discount_price");
                double actual_price = res.getDouble("actual_price");
                String ratings = res.getString("ratings");

                System.out.println("Détails du produit :");
                System.out.println("Libellé_article : " + name);
                System.out.println("discount_price : " + discount_price + "$");
                System.out.println("actual_price : " + actual_price + "$");
                System.out.println("Ratings : " + ratings);
            } else {
                System.out.println("Aucun produit trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'exécution de la requête.");
            e.printStackTrace();
        }
    }

    // US2.1 Je veux consulter la liste des produits que je commande le plus fréquemment.
    public static void ProduitsFrequents(int userId) throws SQLException {
            String query = "SELECT p.id_produit, p.name, COUNT(pa.id_produit) AS purchase_count, " +
                    "       p.ratings, p.discount_price " +
                    "FROM panier pa " +
                    "JOIN produit p ON pa.id_produit = p.id_produit " +
                    "WHERE pa.id_user = " + userId + " " +
                    "GROUP BY p.id_produit, p.name, p.ratings, p.discount_price " +
                    "ORDER BY purchase_count DESC";

            try (ResultSet res = Connect.executeQuery(query)){
                if (res.next()) {
                int nb_colonnes = res.getMetaData().getColumnCount();
                for (int i = 1; i <= nb_colonnes; i++) {
                    System.out.print(res.getMetaData().getColumnName(i) + "\t");
                }
                System.out.println();
                boolean hasResults = false;
                while (res.next()) {
                    hasResults = true;
                    for (int i = 1; i <= nb_colonnes; i++) {
                        System.out.print(res.getString(i) + "\t");
                    }
                    System.out.println();
                }
                if (!hasResults) {
                    System.out.println("Aucun produit trouvé pour l'utilisateur ID " + userId + ".");
                }
            }
            }
    }
     // US2.2 Je veux consulter mes habitudes de consommation (bio, nutriscore, catégorie de produits, marques).
       public static void HabitudesDeConsommation(int userId) throws SQLException {
           String query = "SELECT ca.main_category AS category_name, " +
                   "       COUNT(p.id_produit) AS purchase_count, " +
                   "       AVG(p.ratings) AS avg_nutriscore " +
                   "FROM panier pa " +
                   "JOIN produit p ON pa.id_produit = p.id_produit " +
                   "JOIN categories ca ON p.category = ca.id_cat " +
                   "WHERE pa.id_user = " + userId +
                   " GROUP BY ca.main_category " +
                   " ORDER BY purchase_count DESC";
           try(ResultSet res = Connect.executeQuery(query)){
               System.out.println("\n=== Habitudes de Consommation ===");
               boolean hasResults = false;
               while (res.next()) {
                   hasResults = true;
                   String categoryName = res.getString("category_name");
                   int purchaseCount = res.getInt("purchase_count");
                   double avgNutriscore = res.getDouble("avg_nutriscore");

                   System.out.printf("Catégorie : %s, Achats : %d, Nutriscore Moyen : %.2f%n",
                           categoryName, purchaseCount, avgNutriscore);
               }
               if (!hasResults) {
                   System.out.println("Aucune habitude de consommation trouvée pour l'utilisateur ID " + userId + ".");
               }
       }
       }

    // US2.3 Je veux valider les préférences que me propose le système afin d'avoir des produits de remplacements qui correspondent mieux à mes habitudes.
    public static void ValiderPreferences(int userId) throws SQLException {
        String categoryQuery = "SELECT ca.main_category AS category_name, " +
                "       COUNT(p.id_produit) AS purchase_count " +
                "FROM panier pa " +
                "JOIN produit p ON pa.id_produit = p.id_produit " +
                "JOIN categories ca ON p.category = ca.id_cat " +
                "WHERE pa.id_user = " + userId + " " +
                "GROUP BY ca.main_category " +
                "ORDER BY purchase_count DESC LIMIT 1";
        String recommendationQueryTemplate = "SELECT p.id_produit, p.name, p.actual_price, p.ratings " +
                "FROM produit p " +
                "JOIN categories ca ON p.category = ca.id_cat " +
                "WHERE ca.main_category = '%s' " +
                "AND p.id_produit NOT IN ( " +
                "    SELECT pa.id_produit FROM panier pa WHERE pa.id_user = " + userId + " " +
                ") LIMIT 5";

        try (ResultSet categoryResult = Connect.executeQuery(categoryQuery)) {
            if (categoryResult.next()) {
                String mainCategory = categoryResult.getString("category_name");
                System.out.println("Votre principale catégorie de consommation est : " + mainCategory);

                String recommendationQuery = String.format(recommendationQueryTemplate, mainCategory);

                try (ResultSet recommendationResult = Connect.executeQuery(recommendationQuery)) {
                    System.out.println("Produits recommandés pour vous :");
                    boolean hasRecommendations = false;

                    while (recommendationResult.next()) {
                        hasRecommendations = true;
                        long productId = recommendationResult.getLong("id_produit");
                        String productName = recommendationResult.getString("name");
                        double price = recommendationResult.getDouble("actual_price");
                        double ratings = recommendationResult.getDouble("ratings");

                        System.out.printf("Product ID: %d, Name: %s, Price: %.2f, Ratings: %.2f%n",
                                productId, productName, price, ratings);
                    }

                    if (!hasRecommendations) {
                        System.out.println("Aucune recommandation n’est disponible pour cette catégorie.");
                    } else {

                        Scanner scanner = new Scanner(System.in);
                        System.out.print("Acceptez-vous ces recommandations ? (oui/non) : ");
                        String response = scanner.next();

                        if ("oui".equalsIgnoreCase(response)) {
                            System.out.println("Recommandations enregistrées. Merci !");
                        } else {
                            System.out.println("Vous avez rejeté les recommandations.");
                        }
                    }
                }
            } else {
                System.out.println("Aucune donnée de consommation n’a été trouvée pour cet utilisateur.");
            }
        }
    }


    // US3.1 Je veux importer automatiquement des produits afin de mettre à jour mon catalogue produit.
    public static void ImporterProduits(List<Map<String, Object>> produits) throws SQLException {
        for (Map<String, Object> produit : produits) {
            int idProduit = (int) produit.get("id_produit");
            String name = (String) produit.get("name");
            String categoryName = (String) produit.get("category");  
            double price = (double) produit.get("actual_price");
            double ratings = (double) produit.get("ratings");

            String categoryQuery = "SELECT id_cat FROM categories WHERE main_category = '" + categoryName + "'";
            int categoryId = -1;

            try (ResultSet categoryResult = Connect.executeQuery(categoryQuery)) {
                if (categoryResult.next()) {
                    categoryId = categoryResult.getInt("id_cat");
                } else {
                    System.out.println("La catégorie " + categoryName + " n'existe pas dans la base de données.");
                    continue;
                }
            }

            String checkQuery = "SELECT COUNT(*) AS count FROM produit WHERE id_produit = " + idProduit;

            String insertQuery = String.format(
                    "INSERT INTO produit (id_produit, name, category, actual_price, ratings) " +
                            "VALUES (%d, '%s', %d, %.2f, %.2f)",
                    idProduit, name, categoryId, price, ratings
            );

            String updateQuery = String.format(
                    "UPDATE produit SET name = '%s', category = %d, actual_price = %.2f, ratings = %.2f " +
                            "WHERE id_produit = %d",
                    name, categoryId, price, ratings, idProduit
            );

            try (ResultSet checkResult = Connect.executeQuery(checkQuery)) {
                checkResult.next();
                boolean exists = checkResult.getInt("count") > 0;

                if (exists) {
                    Connect.executeUpdate(updateQuery);
                    System.out.println("Produit mis à jour : " + name);
                } else {
                    Connect.executeUpdate(insertQuery);
                    System.out.println("Nouveau produit ajouté : " + name);
                }
            }
        }
    }

    // US3.4 Je veux paramètrer notre algorithme de recommandation de produits et gérer sa priorité.
    public static void RecommanderProduits(int userId) throws SQLException {
        String habitQuery = "SELECT ca.main_category AS category_name, COUNT(p.id_produit) AS purchase_count " +
                "FROM panier pa " +
                "JOIN produit p ON pa.id_produit = p.id_produit " +
                "JOIN categories ca ON p.category = ca.id_cat " +
                "WHERE pa.id_user = " + userId + " " +
                "GROUP BY ca.main_category " +
                "ORDER BY purchase_count DESC LIMIT 1";

        String highPriorityQueryTemplate = "SELECT p.id_produit, ca.main_category AS category_name,p.name, p.actual_price, p.ratings " +
                "FROM produit p " +
                "JOIN categories ca ON p.category = ca.id_cat " +
                "WHERE ca.main_category = '%s' " +
                "AND p.id_produit NOT IN ( " +
                "    SELECT pa.id_produit FROM panier pa WHERE pa.id_user = " + userId + " " +
                ") " +
                "ORDER BY p.ratings DESC LIMIT 5";

        String lowPriorityQueryTemplate = "SELECT p.id_produit, p.name, ca.main_category AS category_name,p.actual_price, p.ratings " +
                "FROM produit p " +
                "JOIN categories ca ON p.category = ca.id_cat " +
                "WHERE ca.main_category != '%s' " +
                "AND p.id_produit NOT IN ( " +
                "    SELECT pa.id_produit FROM panier pa WHERE pa.id_user = " + userId + " " +
                ") " +
                "ORDER BY p.ratings DESC LIMIT 5";

        try (ResultSet habitResult = Connect.executeQuery(habitQuery)) {
            if (habitResult.next()) {
                String mainCategory = habitResult.getString("category_name");
                System.out.println("Votre principale catégorie de consommation est : " + mainCategory);

                String highPriorityQuery = String.format(highPriorityQueryTemplate, mainCategory);

                try (ResultSet highPriorityResult = Connect.executeQuery(highPriorityQuery)) {
                    System.out.println("\n=== Produits Hautement Prioritaires ===");
                    boolean hasHighPriority = false;

                    while (highPriorityResult.next()) {
                        hasHighPriority = true;
                        long productId = highPriorityResult.getLong("id_produit");
                        String productName = highPriorityResult.getString("name");
                        String main_category = highPriorityResult.getString("category_name");
                        double price = highPriorityResult.getDouble("actual_price");
                        double ratings = highPriorityResult.getDouble("ratings");

                        System.out.printf("Product ID: %d, Category: %s%n, Name: %s,  Price: %.2f, Ratings: %.2f%n",
                                productId, main_category, productName,  price, ratings);
                    }

                    if (!hasHighPriority) {
                        System.out.println("Aucune recommandation hautement prioritaire n’est disponible.");
                    }
                }

                String lowPriorityQuery = String.format(lowPriorityQueryTemplate, mainCategory);

                try (ResultSet lowPriorityResult = Connect.executeQuery(lowPriorityQuery)) {
                    System.out.println("\n=== Produits de Faible Priorité ===");
                    boolean hasLowPriority = false;

                    while (lowPriorityResult.next()) {
                        hasLowPriority = true;
                        long productId = lowPriorityResult.getLong("id_produit");
                        String productName = lowPriorityResult.getString("name");
                        String main_cat = lowPriorityResult.getString("category_name");
                        double price = lowPriorityResult.getDouble("actual_price");
                        double ratings = lowPriorityResult.getDouble("ratings");

                        System.out.printf("Product ID: %d, Category: %s%n, Name: %s, Price: %.2f, Ratings: %.2f%n",
                                productId, main_cat, productName,  price, ratings);
                    }

                    if (!hasLowPriority) {
                        System.out.println("Aucune recommandation de faible priorité n’est disponible.");
                    }
                }
            } else {
                System.out.println("Aucune donnée de consommation n’a été trouvée pour cet utilisateur.");
            }
        }
    }


    // US3.6 没有成功，难度 5 颗星
    // Je veux détecter les nouvelles habitudes de consommation de mes clients.
    // 关于这个用户故事，我想假设她一个产品的种类购买次数不超过5次的就算是新增的消费习惯，比如她以前经常购买冰箱和电视机产品，这个属于电器,
    // 那么她购买电器超过5次，就算他的消费习惯，要是她现在突然买了一个手机，属于数码，但数码购买次数不超过5次，就算是新的消费习惯。
    public static void DetecterNouvellesHabitudes(int userId) throws SQLException {
        String categoryPurchaseCountQuery = "SELECT ca.main_category AS category_name, COUNT(*) AS purchase_count " +
                "FROM categories ca " +
                "JOIN produit p ON ca.id_cat = p.category " +
                "JOIN panier pa ON p.Id_produit = pa.Id_produit " +
                "WHERE pa.id_user = " + userId + " " +
                "GROUP BY ca.main_category";

        String currentCategoriesQuery = "SELECT DISTINCT ca.main_category AS category_name " +
                "FROM categories ca " +
                "JOIN produit p ON ca.id_cat = p.category " +
                "JOIN panier pa ON p.id_produit = pa.id_produit " +
                "JOIN PanierCommande PC ON pa.id_panier = PC.panier_id " +
                "JOIN commande c ON PC.id_commande = c.id_commande " +
                "WHERE pa.id_user = " + userId + " AND c.Date_commande = ( " +
                "    SELECT MAX(Date_commande) FROM commande c " +
                "    JOIN PanierCommande PC ON c.id_commande = PC.id_commande " +
                "    JOIN panier pa ON PC.panier_id = pa.id_panier " +
                "    WHERE pa.id_user = " + userId + ")";

        Map<String, Integer> categoryPurchaseCounts = new HashMap<>();

        try (ResultSet rsHistoric = Connect.executeQuery(categoryPurchaseCountQuery)) {
            while (rsHistoric.next()) {
                String categoryName = rsHistoric.getString("category_name");
                int purchaseCount = rsHistoric.getInt("purchase_count");
                categoryPurchaseCounts.put(categoryName, purchaseCount);
            }
        }

        Set<String> currentCategories = new HashSet<>();
        try (ResultSet rsCurrent = Connect.executeQuery(currentCategoriesQuery)) {
            while (rsCurrent.next()) {
                currentCategories.add(rsCurrent.getString("category_name"));
            }
        }

        List<String> nouvellesHabitudes = new ArrayList<>();
        for (String category : currentCategories) {
            int purchaseCount = categoryPurchaseCounts.getOrDefault(category, 0);
            if (purchaseCount <= 2) {
                nouvellesHabitudes.add(category);
            }
        }
        if (nouvellesHabitudes.isEmpty()) {
            System.out.println("Aucun nouveau usage de consommation détecté.");
        } else {
            System.out.println("De nouvelles habitudes de consommation détectées :");
            for (String category : nouvellesHabitudes) {
                System.out.println("- " + category);
            }
        }
    }


    // US 4.1 Je veux consulter la liste des commandes à préparer par ordre de priorité
    //查看按照优先顺序准备的订单列表，一个订单可能有很多个产品，比如说我想要看最近需要发货的订单都有哪些，
    // 即什么订单比较着急发货，按照时间进行升序排序.
    public static void ListeCommandes() throws SQLException {
        String query="SELECT c.id_commande,l.date_livrasion "+ "FROM commande c "+
                "JOIN livrasion l ON c.id_commande=l.id_commande "+
                "order by l.date_livrasion ASC";
        try (ResultSet resultSet = Connect.executeQuery(query)) {
            System.out.println("=== Liste des commandes à préparer par ordre de priorité ===");
            boolean hasResults = false;
            while (resultSet.next()) {
                hasResults = true;
                int idCommande = resultSet.getInt("id_commande");
                Date dateLivraison = resultSet.getDate("date_livraison");

                System.out.printf("Commande ID: %d, Date de Livraison: %s%n", idCommande, dateLivraison);
            }
            if (!hasResults) {
                System.out.println("Aucune commande à préparer trouvée.");
            }
        }
    }

    // US 4.2 Je marque une commande en préparation pour un retrait ou un envoi
















    public static void main(String[] arg) throws SQLException {
            Scanner scanner = new Scanner(System.in);
            int choice;

            while (true) {
                System.out.println("\n=== Les besion des clients ===");
                System.out.println("1: US0.1");
                System.out.println("2: US2.1--Consulter les produits fréquemment commandés");
                System.out.println("3: US2.2--Habitudes De Consommation");
                System.out.println("4: US2.3--Valider les préférences et trouver les produits de remplacements");
                System.out.println("5: US3.1--Importer automatiquement afin de mettre à jour mon catalogue produit");
                System.out.println("6: US3.4--Recommandation personnalisé");
                System.out.println("7: US3.6--Détecter les nouvelles habitudes de consommation de mes clients");
                System.out.println("8: US4.1--La liste des commandes à préparer par ordre de priorité.");
                System.out.println("9: Quitter");
                System.out.print("Entrez votre choix : ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Veuillez entrer un nombre valide.");
                    scanner.next();
                    continue;
                }

                choice = scanner.nextInt();

                switch (choice) {
                    case 1 ->{
                        System.out.print("Les imformations du produit : ");
                        VisualiserProduit(1);
                    }
                    case 2 -> {
                        System.out.print("entrez votre ID de utilisateur: ");
                        int userId = scanner.nextInt();

                        if (userId == 0) {
                            System.out.println("Retour au menu principal...");
                            continue;
                        }
                        System.out.println("Voici les produits fréquemment commandés par l'utilisateur ID " + userId + " :");
                        ProduitsFrequents(userId);
                    }
                    case 3 -> {
                        System.out.print("entrez votre ID de utilisateur: ");
                        int userId = scanner.nextInt();

                        if (userId == 0) {
                            System.out.println("Retour au menu principal...");
                            continue;
                        }
                        System.out.println("habitudes de consommation de utilisateur" + userId + " :");
                        HabitudesDeConsommation(userId);
                    }
                    case 4 -> {
                        System.out.print("entrez votre ID de utilisateur: ");
                        int userId = scanner.nextInt();

                        if (userId == 0) {
                            System.out.println("Retour au menu principal...");
                            continue;
                        }
                        System.out.println("Les préférences de utilisateur" + userId + " :");
                        ValiderPreferences(userId);
                    }
                    case 5 ->{
                        List<Map<String, Object>> nouveauxProduits = List.of(
                                Map.of(
                                        "id_produit", 101,
                                        "name", "Fridge",
                                        "category", "Appliances",
                                        "actual_price", 699.99,
                                        "ratings", 4.8
                                ),
                                Map.of(
                                        "id_produit", 202,
                                        "name", "Smartphone",
                                        "category", "Electronics",
                                        "actual_price", 799.99,
                                        "ratings", 4.6
                                )
                        );
                        ImporterProduits(nouveauxProduits);
                    }
                    case 6 ->{
                        System.out.print("entrez votre ID de utilisateur: ");
                        int userId = scanner.nextInt();

                        if (userId == 0) {
                            System.out.println("Retour au menu principal...");
                            continue;
                        }
                        System.out.println("recommandation de produits de utilisateur" + userId + " :");
                        RecommanderProduits(userId);
                    }
                    case 7 ->{
                        System.out.print("entrez votre ID de utilisateur: ");
                        int userId = scanner.nextInt();

                        if (userId == 0) {
                            System.out.println("Retour au menu principal...");
                            continue;
                        }
                        System.out.println("Nouvelles habitudes de consommation de utilisateur" + userId + " :");
                        DetecterNouvellesHabitudes(userId);
                    }
                    case 8 ->{
                        System.out.print("US4.1: ");
                        ListeCommandes(); }
                    case 9 -> {
                        System.out.println("Quitter l'application...");
                        scanner.close();
                        return;
                    }
                    default -> System.out.println("Choix invalide. Veuillez réessayer.");
                }
            }
        }
}


//        Scanner scanner = new Scanner(System.in);
//        int choice;
//        while (true) {
//            System.out.println("\n=== Les besion des clients ===");
//            System.out.println("1: US0.1--visualiser les détails d'un produit");
//            System.out.println("2: US2.1--commande le plus fréquemment");
//            System.out.println("3: US2.2--habitudes de consommation");
//            System.out.println("4: US2.3--valider les préférences et trouver les produits de remplacements");
//            System.out.println("5: US3.4--recommandation personnalisé");
//            System.out.println("6: US3.5--profils des consommateurs");
//            System.out.println("7: US3.6--nouvelles habitudes de consommation");
//            System.out.println("8: US4.1--la liste des commandes à préparer par ordre de priorité");
//            System.out.println("9: US4.2--marquer une commande comme prête");
//            System.out.println("10: Exit");
//            System.out.print("Entrez votre choix:");
//            choice = scanner.nextInt();
//            switch (choice) {
                // case 1 ->
        // VisualiserProduit(1);
                // case 2 ->
                        // ProduitsFrequents();
                //case 3 ->
                        // HabitudesDeConsommation();
                //case 4 -> ValiderPreferences(scanner);
                //case 5 -> RecommandationProduits(scanner);
               // case 6 -> DifferentsProfils();
               // case 7 -> DetecterNouvellesHabitudes(scanner);
//                case 3 -> {
//                    System.out.println("Quitter l’application...");
//                    return;
//                }
//                default -> System.out.println("Invalid choice. Please try again.");



