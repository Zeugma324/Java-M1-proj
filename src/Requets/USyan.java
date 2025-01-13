package Requets;
import connexion.Connect;
import java.sql.*;
import java.util.Scanner;
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


    // US3.4 Je veux paramètrer notre algorithme de recommandation de produits et gérer sa priorité.
    public static void RecommandationProduits(int userId) throws SQLException {
        String habitQuery = "SELECT ca.main_category AS category_name, " +
                "       COUNT(p.id_produit) AS purchase_count " +
                "FROM panier pa " +
                "JOIN produit p ON pa.id_produit = p.id_produit " +
                "JOIN categories ca ON p.category = ca.id_cat " +
                "WHERE pa.id_user = " + userId + " " +
                "GROUP BY ca.main_category " +
                "ORDER BY purchase_count DESC LIMIT 1";

        String highPriorityQueryTemplate = "SELECT p.id_produit, p.name, p.actual_price, p.ratings " +
                "FROM produit p " +
                "JOIN categories ca ON p.category = ca.id_cat " +
                "WHERE ca.main_category = '%s' " +
                "AND p.id_produit NOT IN ( " +
                "    SELECT pa.id_produit FROM panier pa WHERE pa.id_user = " + userId + " " +
                ") LIMIT 5";

        String lowPriorityQueryTemplate = "SELECT p.id_produit, p.name, p.actual_price, p.ratings " +
                "FROM produit p " +
                "JOIN categories ca ON p.category = ca.id_cat " +
                "WHERE ca.main_category != '%s' " +
                "AND p.id_produit NOT IN ( " +
                "    SELECT pa.id_produit FROM panier pa WHERE pa.id_user = " + userId + " " +
                ") LIMIT 5";

        try (ResultSet habitResult = Connect.executeQuery(habitQuery)) {
            if (habitResult.next()) {

                String mainCategory = habitResult.getString("category_name");
                System.out.println("Votre principale catégorie de consommation est : " + mainCategory);
                String highPriorityQuery = String.format(highPriorityQueryTemplate, mainCategory);

                try (ResultSet highPriorityResult = Connect.executeQuery(highPriorityQuery)) {
                    System.out.println("\n=== High Priority Recommendations ===");
                    boolean hasHighPriority = false;

                    while (highPriorityResult.next()) {
                        hasHighPriority = true;
                        long productId = highPriorityResult.getLong("id_produit");
                        String productName = highPriorityResult.getString("name");
                        double price = highPriorityResult.getDouble("actual_price");
                        double ratings = highPriorityResult.getDouble("ratings");

                        System.out.printf("Product ID: %d, Name: %s, Price: %.2f, Ratings: %.2f%n",
                                productId, productName, price, ratings);
                    }

                    if (!hasHighPriority) {
                        System.out.println("Aucune recommandation hautement prioritaire n’est disponible.");
                    }
                }

                String lowPriorityQuery = String.format(lowPriorityQueryTemplate, mainCategory);

                try (ResultSet lowPriorityResult = Connect.executeQuery(lowPriorityQuery)) {
                    System.out.println("\n=== Recommandations de faible priorité ===");
                    boolean hasLowPriority = false;

                    while (lowPriorityResult.next()) {
                        hasLowPriority = true;
                        long productId = lowPriorityResult.getLong("id_produit");
                        String productName = lowPriorityResult.getString("name");
                        double price = lowPriorityResult.getDouble("actual_price");
                        double ratings = lowPriorityResult.getDouble("ratings");

                        System.out.printf("Product ID: %d, Name: %s, Price: %.2f, Ratings: %.2f%n",
                                productId, productName, price, ratings);
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



    // US3.5 Je veux consulter les différents profils de consommateurs.
    //public static void DifferentsProfils() {
    //   System.out.println("\n=== Profils des Consommateurs ===");
    //
    //
    //   // 查询用户主要购买的产品类型
    //   String categoryQuery = "SELECT pa.id_user, ca.main_category AS favorite_category, " +
    //           "       COUNT(p.id_produit) AS purchase_count " +
    //           "FROM panier pa " +
    //           "JOIN produit p ON pa.id_produit = p.id_produit " +
    //           "JOIN categories ca ON p.category = ca.id_cat " +
    //           "GROUP BY pa.id_user, ca.main_category " +
    //           "ORDER BY pa.id_user, purchase_count DESC";
    //
    //
    //   // 查询用户最常使用的发货方式
    //   String deliveryQuery = "SELECT pa.id_user, l.moyen_livraison, COUNT(l.id_commande) AS usage_count " +
    //           "FROM commande c " +
    //           "JOIN livraison l ON c.id_commande = l.id_commande " +
    //           "JOIN PanierCommande PC ON c.id_commande=PC.id_commande"+
    //           " JOIN panier pa ON PC.panier_id=p.id_panier"+
    //           " GROUP BY c.id_user, l.moyen_livraison " +
    //           "ORDER BY c.id_user, usage_count DESC";
    //
    //
    //   try (Connection con = DriverManager.getConnection(url, user, mdp);
    //        Statement categoryStmt = con.createStatement();
    //        Statement deliveryStmt = con.createStatement()) {
    //
    //
    //       // 查询用户购买偏好
    //       ResultSet rsCategory = categoryStmt.executeQuery(categoryQuery);
    //       System.out.println("\n=== Catégories de produits préférées ===");
    //       while (rsCategory.next()) {
    //           int userId = rsCategory.getInt("id_user");
    //           String category = rsCategory.getString("favorite_category");
    //           int count = rsCategory.getInt("purchase_count");
    //
    //
    //           System.out.printf("User ID: %d, Favorite Category: %s, Purchases: %d%n",
    //                   userId, category, count);
    //       }
    //
    //
    //       // 查询用户发货偏好
    //       ResultSet rsDelivery = deliveryStmt.executeQuery(deliveryQuery);
    //       System.out.println("\n=== Méthodes de livraison préférées ===");
    //       while (rsDelivery.next()) {
    //           int userId = rsDelivery.getInt("id_user");
    //           String deliveryMethod = rsDelivery.getString("moyen_livraison");
    //           int usageCount = rsDelivery.getInt("usage_count");
    //
    //
    //           System.out.printf("User ID: %d, Preferred Delivery: %s, Usage: %d%n",
    //                   userId, deliveryMethod, usageCount);
    //       }
    //
    //
    //   } catch (SQLException e) {
    //       System.err.println("Erreur lors de la récupération des profils de consommatrices.");
    //       e.printStackTrace();
    //   }
    //}
    //
    //

    /// / US 3.6 Je veux détecter les nouvelles habitudes de consommation de mes clients.
    //public static void DetecterNouvellesHabitudes(Scanner scanner) {
    //   System.out.print("Entrer ID de utilisateur: ");
    //   int userId = scanner.nextInt();
    //
    //
    //   // 查询用户历史订单的产品类别
    //   String historicCategoriesQuery = "SELECT DISTINCT ca.main_category " +
    //           "FROM panier pa " +
    //           "JOIN produit p ON pa.id_produit = p.id_produit " +
    //           "JOIN categories ca ON p.category = ca.id_cat " +
    //           "WHERE pa.id_user = ?";
    //
    //
    //   // 查询用户最新订单的产品类别
    //   String currentCategoriesQuery = "SELECT DISTINCT ca.main_category " +
    //           "FROM commande c " +
    //           "JOIN panier pa ON c.id_commande = pa.id_panier " +
    //           "JOIN produit p ON pa.id_produit = p.id_produit " +
    //           "JOIN categories ca ON p.category = ca.id_cat " +
    //           "WHERE pa.id_user = ? AND c.Date_commande = ( " +
    //           "    SELECT MAX(Date_commande) FROM commande WHERE id_user = ? " +
    //           ")";
    //
    //
    //   try (Connection con = DriverManager.getConnection(url, user, mdp);
    //        PreparedStatement pstHistoric = con.prepareStatement(historicCategoriesQuery);
    //        PreparedStatement pstCurrent = con.prepareStatement(currentCategoriesQuery)) {
    //
    //
    //       // 查询历史订单类别
    //       pstHistoric.setInt(1, userId);
    //       ResultSet rsHistoric = pstHistoric.executeQuery();
    //       Set<String> historicCategories = new HashSet<>();
    //       while (rsHistoric.next()) {
    //           historicCategories.add(rsHistoric.getString("main_category"));
    //       }
    //
    //
    //       // 查询当前订单类别
    //       pstCurrent.setInt(1, userId);
    //       pstCurrent.setInt(2, userId);
    //       ResultSet rsCurrent = pstCurrent.executeQuery();
    //       Set<String> currentCategories = new HashSet<>();
    //       while (rsCurrent.next()) {
    //           currentCategories.add(rsCurrent.getString("main_category"));
    //       }
    //
    //
    //       // 计算新类别（当前类别中不在历史类别中的）
    //       currentCategories.removeAll(historicCategories);
    //
    //
    //       // 输出结果
    //       if (currentCategories.isEmpty()) {
    //           System.out.println("Aucun nouveau usage de consommation détecté.");
    //       } else {
    //           System.out.println("De nouvelles habitudes de consommation détectées :");
    //           for (String category : currentCategories) {
    //               System.out.println("- " + category);
    //           }
    //       }
    //
    //
    //   } catch (SQLException e) {
    //       System.err.println("Erreur lors de la détection de nouvelles habitudes de consommation.");
    //       e.printStackTrace();
    //   }
    //}










    public static void main(String[] arg) throws SQLException {
            Scanner scanner = new Scanner(System.in);
            int choice;

            while (true) {
                System.out.println("\n=== Les besion des clients ===");
                System.out.println("1: US0.1");
                System.out.println("2: US2.1--Consulter les produits fréquemment commandés");
                System.out.println("3: US2.2--Habitudes De Consommation");
                System.out.println("4: US2.3--valider les préférences et trouver les produits de remplacements");
                System.out.println("5: US3.4--recommandation personnalisé");
                System.out.println("6: Quitter");
                System.out.print("Entrez votre choix : ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Veuillez entrer un nombre valide.");
                    scanner.next(); // 清除无效输入
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
                        System.out.println("habitudes de consommation de utilisateur" + userId + " :");
                        ValiderPreferences(userId);
                    }

                    case 5 -> {
                        System.out.print("entrez votre ID de utilisateur: ");
                        int userId = scanner.nextInt();

                        if (userId == 0) {
                            System.out.println("Retour au menu principal...");
                            continue;
                        }
                        System.out.println("habitudes de consommation de utilisateur" + userId + " :");
                        RecommandationProduits(userId);
                    }

                    case 6 -> {
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



