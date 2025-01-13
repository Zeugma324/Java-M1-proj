package Requets;

import java.sql.*;
import java.util.*;


public class USyan {
    static String url = "jdbc:mysql://sql7.freesqldatabase.com:3306/sql7756463";
    static String user = "sql7756463";
    static String mdp = "iFgwWVZFHW";

    //US01 Je veux visualiser les détails d'un produit : prix unitaire, prix au kg, nutriscore, libellé article, poids, conditionnement, ...
    public static void VisualiserProduit(int productId) {
        String query = "SELECT discount_price, actual_price, ratings, name FROM produit p" +
                ",categories ca WHERE ca.Id_cat=p.category AND id_produit =" + productId;

        // 数据库连接和查询
        try (Connection con = DriverManager.getConnection(url, user, mdp);
             Statement stm = con.createStatement()) {
            ResultSet resultSet = stm.executeQuery(query);
            // 处理查询结果
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                double discount_price = resultSet.getDouble("discount_price");
                double actual_price = resultSet.getDouble("actual_price");
                String ratings = resultSet.getString("ratings");

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
    public static void ProduitsFrequents(Scanner scanner) {
        System.out.print("Enter User ID: ");
        int userId = scanner.nextInt();

        // SQL 查询：统计用户购买的产品及其次数
        String query = "SELECT p.name, p.actual_price, COUNT(pa.id_produit) AS purchase_count " +
                "FROM panier pa " +
                "JOIN produit p ON pa.id_produit = p.id_produit " +
                "WHERE pa.id_user = ? " +
                "GROUP BY pa.id_produit " +
                "ORDER BY purchase_count DESC";

        try (Connection con = DriverManager.getConnection(url, user, mdp);
             PreparedStatement pst = con.prepareStatement(query)) {

            // 设置查询参数
            pst.setInt(1, userId);

            // 执行查询
            ResultSet rs = pst.executeQuery();

            // 输出结果
            System.out.println("\n=== Frequently Ordered Products ===");
            boolean hasProducts = false;

            while (rs.next()) {
                hasProducts = true;
                String productName = rs.getString("name");
                double productPrice = rs.getDouble("actual_price");
                int purchaseCount = rs.getInt("purchase_count");

                System.out.printf("Product: %s, Price: %.2f, Times Ordered: %d%n",
                        productName, productPrice, purchaseCount);
            }

            if (!hasProducts) {
                System.out.println("No frequent products found for this user.");
            }

        } catch (SQLException e) {
            System.err.println("Error while fetching frequently ordered products.");
            e.printStackTrace();
        }
    }


    // US2.2 Je veux consulter mes habitudes de consommation (bio, nutriscore, catégorie de produits, marques).
    private static void HabitudesDeConsommation(Scanner scanner) {
        System.out.print("Entrer ID de utilisateur: ");
        int userId = scanner.nextInt();

        // SQL 查询：统计每个类别的购买次数和平均 Nutriscore
        String query = "SELECT ca.main_category AS category_name, " +
                "       COUNT(p.id_produit) AS purchase_count, " +
                "       AVG(p.ratings) AS avg_nutriscore " +
                "FROM panier pa " +
                "JOIN produit p ON pa.id_produit = p.id_produit " +
                "JOIN categories ca ON p.category = ca.id_cat " +
                "WHERE pa.id_user = ? " +
                "GROUP BY ca.main_category " +
                "ORDER BY purchase_count DESC";

        try (Connection con = DriverManager.getConnection(url, user, mdp);
             PreparedStatement pst = con.prepareStatement(query)) {

            // 设置查询参数
            pst.setInt(1, userId);

            // 执行查询
            ResultSet rs = pst.executeQuery();

            // 输出结果
            System.out.println("\n===  ===");
            boolean hasResults = false;

            while (rs.next()) {
                hasResults = true;
                String categoryName = rs.getString("category_name");
                int purchaseCount = rs.getInt("purchase_count");
                double avgNutriscore = rs.getDouble("avg_nutriscore");

                System.out.printf("Category: %s, Purchases: %d, Avg Nutriscore: %.2f%n",
                        categoryName, purchaseCount, avgNutriscore);
            }

            if (!hasResults) {
                System.out.println("Aucune habitude de consommation n’a été trouvée pour cet utilisateur.");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des habitudes de consommation.");
            e.printStackTrace();
        }
    }


    // US2.3 Je veux valider les préférences que me propose le système afin d'avoir des produits de remplacements qui correspondent mieux à mes habitudes.
    public static void ValiderPreferences(Scanner scanner) {
        System.out.print("Entrer ID de utilisateur: ");
        int userId = scanner.nextInt();

        // 查询用户的主要消费类别
        String categoryQuery = "SELECT ca.main_category AS category_name, " +
                "COUNT(p.id_produit) AS purchase_count " +
                "FROM panier pa " +
                "JOIN produit p ON pa.id_produit = p.id_produit " +
                "JOIN categories ca ON p.category = ca.id_cat " +
                "WHERE pa.id_user = ? " +
                "GROUP BY ca.main_category " +
                "ORDER BY purchase_count DESC LIMIT 1";

        // 查询推荐的替代产品
        String recommendationQuery = "SELECT p.id_produit, p.name, p.actual_price, p.ratings " +
                "FROM produit p " +
                "JOIN categories ca ON p.category = ca.id_cat " +
                "WHERE ca.main_category = ? " +
                "AND p.id_produit NOT IN ( " +
                "    SELECT pa.id_produit FROM panier pa WHERE pa.id_user = ? " +
                ") LIMIT 5";

        try (Connection con = DriverManager.getConnection(url, user, mdp);
             PreparedStatement pstCategory = con.prepareStatement(categoryQuery);
             PreparedStatement pstRecommendation = con.prepareStatement(recommendationQuery)) {

            // 设置查询参数并执行类别查询
            pstCategory.setInt(1, userId);
            ResultSet rsCategory = pstCategory.executeQuery();

            if (rsCategory.next()) {
                String mainCategory = rsCategory.getString("category_name");
                System.out.println("Votre principale catégorie de consommation est : " + mainCategory);

                // 设置推荐查询的参数并执行查询
                pstRecommendation.setString(1, mainCategory);
                pstRecommendation.setInt(2, userId);
                ResultSet rsRecommendation = pstRecommendation.executeQuery();

                System.out.println("Produits recommandés pour vous :");
                boolean hasRecommendations = false;

                while (rsRecommendation.next()) {
                    hasRecommendations = true;
                    long productId = rsRecommendation.getLong("id_produit");
                    String productName = rsRecommendation.getString("name");
                    double price = rsRecommendation.getDouble("actual_price");
                    double ratings = rsRecommendation.getDouble("ratings");

                    System.out.printf("Product ID: %d, Name: %s, Price: %.2f, Ratings: %.2f%n",
                            productId, productName, price, ratings);
                }

                if (!hasRecommendations) {
                    System.out.println("Aucune recommandation n’est disponible pour cette catégorie.");
                } else {
                    System.out.print("Acceptez-vous ces recommandation? (oui/non):");
                    String response = scanner.next();

                    if ("oui".equalsIgnoreCase(response)) {
                        System.out.println("Recommandations enregistrées. Merci!");
                        // 可在这里扩展保存用户偏好的逻辑，例如插入到数据库。
                    } else {
                        System.out.println("Vous avez rejeté les recommandations.");
                    }
                }

            } else {
                System.out.println("Aucune donnée de consommation n’a été trouvée pour cet utilisateur");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du traitement des recommandations.");
            e.printStackTrace();
        }
    }

    // US3.4 Je veux paramètrer notre algorithme de recommandation de produits et gérer sa priorité.
    public static void RecommandationProduits(Scanner scanner) {
        System.out.print("Entrer ID de utilisateur: ");
        int userId = scanner.nextInt();

        // 查询用户主要消费的类别
        String habitQuery = "SELECT ca.main_category AS category_name, " +
                "       COUNT(p.id_produit) AS purchase_count " +
                "FROM panier pa " +
                "JOIN produit p ON pa.id_produit = p.id_produit " +
                "JOIN categories ca ON p.category = ca.id_cat " +
                "WHERE pa.id_user = ? " +
                "GROUP BY ca.main_category " +
                "ORDER BY purchase_count DESC LIMIT 1";

        // 查询推荐产品（高优先级类别）
        String highPriorityQuery = "SELECT p.id_produit, p.name, p.actual_price, p.ratings " +
                "FROM produit p " +
                "JOIN categories ca ON p.category = ca.id_cat " +
                "WHERE ca.main_category = ? " +
                "AND p.id_produit NOT IN ( " +
                "    SELECT pa.id_produit FROM panier pa WHERE pa.id_user = ? " +
                ") LIMIT 5";

        // 查询推荐产品（其他类别）
        String lowPriorityQuery = "SELECT p.id_produit, p.name, p.actual_price, p.ratings " +
                "FROM produit p " +
                "JOIN categories ca ON p.category = ca.id_cat " +
                "WHERE ca.main_category != ? " +
                "AND p.id_produit NOT IN ( " +
                "    SELECT pa.id_produit FROM panier pa WHERE pa.id_user = ? " +
                ") LIMIT 5";

        try (Connection con = DriverManager.getConnection(url, user, mdp);
             PreparedStatement pstHabit = con.prepareStatement(habitQuery);
             PreparedStatement pstHighPriority = con.prepareStatement(highPriorityQuery);
             PreparedStatement pstLowPriority = con.prepareStatement(lowPriorityQuery)) {

            // 查询用户主要消费类别
            pstHabit.setInt(1, userId);
            ResultSet rsHabit = pstHabit.executeQuery();

            if (rsHabit.next()) {
                String mainCategory = rsHabit.getString("category_name");
                System.out.println("Your main consumption category is: " + mainCategory);

                // 查询高优先级推荐产品
                System.out.println("\n=== High Priority Recommendations ===");
                pstHighPriority.setString(1, mainCategory);
                pstHighPriority.setInt(2, userId);
                ResultSet rsHighPriority = pstHighPriority.executeQuery();

                boolean hasHighPriority = false;
                while (rsHighPriority.next()) {
                    hasHighPriority = true;
                    long productId = rsHighPriority.getLong("id_produit");
                    String productName = rsHighPriority.getString("name");
                    double price = rsHighPriority.getDouble("actual_price");
                    double ratings = rsHighPriority.getDouble("ratings");

                    System.out.printf("Product ID: %d, Name: %s, Price: %.2f, Ratings: %.2f%n",
                            productId, productName, price, ratings);
                }

                if (!hasHighPriority) {
                    System.out.println("Aucune recommandation hautement prioritaire n’est disponible.");
                }

                // 查询低优先级推荐产品
                System.out.println("\n=== Recommandations de faible priorité ===");
                pstLowPriority.setString(1, mainCategory);
                pstLowPriority.setInt(2, userId);
                ResultSet rsLowPriority = pstLowPriority.executeQuery();

                boolean hasLowPriority = false;
                while (rsLowPriority.next()) {
                    hasLowPriority = true;
                    long productId = rsLowPriority.getLong("id_produit");
                    String productName = rsLowPriority.getString("name");
                    double price = rsLowPriority.getDouble("actual_price");
                    double ratings = rsLowPriority.getDouble("ratings");

                    System.out.printf("Product ID: %d, Name: %s, Price: %.2f, Ratings: %.2f%n",
                            productId, productName, price, ratings);
                }

                if (!hasLowPriority) {
                    System.out.println("Aucune recommandation de faible priorité n’est disponible.");
                }

            } else {
                System.out.println("Aucune donnée de consommation n’a été trouvée pour cette utilisatrice.");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du traitement des recommandations.");
            e.printStackTrace();
        }
    }

    // US3.5 Je veux consulter les différents profils de consommateurs.
    public static void DifferentsProfils() {
        System.out.println("\n=== Profils des Consommateurs ===");

        // 查询用户主要购买的产品类型
        String categoryQuery = "SELECT pa.id_user, ca.main_category AS favorite_category, " +
                "       COUNT(p.id_produit) AS purchase_count " +
                "FROM panier pa " +
                "JOIN produit p ON pa.id_produit = p.id_produit " +
                "JOIN categories ca ON p.category = ca.id_cat " +
                "GROUP BY pa.id_user, ca.main_category " +
                "ORDER BY pa.id_user, purchase_count DESC";

        // 查询用户最常使用的发货方式
        String deliveryQuery = "SELECT pa.id_user, l.moyen_livraison, COUNT(l.id_commande) AS usage_count " +
                "FROM commande c " +
                "JOIN livraison l ON c.id_commande = l.id_commande " +
                "JOIN PanierCommande PC ON c.id_commande=PC.id_commande"+
                " JOIN panier pa ON PC.panier_id=p.id_panier"+
                " GROUP BY c.id_user, l.moyen_livraison " +
                "ORDER BY c.id_user, usage_count DESC";

        try (Connection con = DriverManager.getConnection(url, user, mdp);
             Statement categoryStmt = con.createStatement();
             Statement deliveryStmt = con.createStatement()) {

            // 查询用户购买偏好
            ResultSet rsCategory = categoryStmt.executeQuery(categoryQuery);
            System.out.println("\n=== Catégories de produits préférées ===");
            while (rsCategory.next()) {
                int userId = rsCategory.getInt("id_user");
                String category = rsCategory.getString("favorite_category");
                int count = rsCategory.getInt("purchase_count");

                System.out.printf("User ID: %d, Favorite Category: %s, Purchases: %d%n",
                        userId, category, count);
            }

            // 查询用户发货偏好
            ResultSet rsDelivery = deliveryStmt.executeQuery(deliveryQuery);
            System.out.println("\n=== Méthodes de livraison préférées ===");
            while (rsDelivery.next()) {
                int userId = rsDelivery.getInt("id_user");
                String deliveryMethod = rsDelivery.getString("moyen_livraison");
                int usageCount = rsDelivery.getInt("usage_count");

                System.out.printf("User ID: %d, Preferred Delivery: %s, Usage: %d%n",
                        userId, deliveryMethod, usageCount);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des profils de consommatrices.");
            e.printStackTrace();
        }
    }

    // US 3.6 Je veux détecter les nouvelles habitudes de consommation de mes clients.
    public static void DetecterNouvellesHabitudes(Scanner scanner) {
        System.out.print("Entrer ID de utilisateur: ");
        int userId = scanner.nextInt();

        // 查询用户历史订单的产品类别
        String historicCategoriesQuery = "SELECT DISTINCT ca.main_category " +
                "FROM panier pa " +
                "JOIN produit p ON pa.id_produit = p.id_produit " +
                "JOIN categories ca ON p.category = ca.id_cat " +
                "WHERE pa.id_user = ?";

        // 查询用户最新订单的产品类别
        String currentCategoriesQuery = "SELECT DISTINCT ca.main_category " +
                "FROM commande c " +
                "JOIN panier pa ON c.id_commande = pa.id_panier " +
                "JOIN produit p ON pa.id_produit = p.id_produit " +
                "JOIN categories ca ON p.category = ca.id_cat " +
                "WHERE pa.id_user = ? AND c.Date_commande = ( " +
                "    SELECT MAX(Date_commande) FROM commande WHERE id_user = ? " +
                ")";

        try (Connection con = DriverManager.getConnection(url, user, mdp);
             PreparedStatement pstHistoric = con.prepareStatement(historicCategoriesQuery);
             PreparedStatement pstCurrent = con.prepareStatement(currentCategoriesQuery)) {

            // 查询历史订单类别
            pstHistoric.setInt(1, userId);
            ResultSet rsHistoric = pstHistoric.executeQuery();
            Set<String> historicCategories = new HashSet<>();
            while (rsHistoric.next()) {
                historicCategories.add(rsHistoric.getString("main_category"));
            }

            // 查询当前订单类别
            pstCurrent.setInt(1, userId);
            pstCurrent.setInt(2, userId);
            ResultSet rsCurrent = pstCurrent.executeQuery();
            Set<String> currentCategories = new HashSet<>();
            while (rsCurrent.next()) {
                currentCategories.add(rsCurrent.getString("main_category"));
            }

            // 计算新类别（当前类别中不在历史类别中的）
            currentCategories.removeAll(historicCategories);

            // 输出结果
            if (currentCategories.isEmpty()) {
                System.out.println("Aucun nouveau usage de consommation détecté.");
            } else {
                System.out.println("De nouvelles habitudes de consommation détectées :");
                for (String category : currentCategories) {
                    System.out.println("- " + category);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la détection de nouvelles habitudes de consommation.");
            e.printStackTrace();
        }
    }

    // US4.1 Je veux consulter la liste des commandes à préparer par ordre de priorité.

















    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.println("\n=== Les besion des clients ===");
            System.out.println("1: US0.1--visualiser les détails d'un produit");
            System.out.println("2: US2.1--commande le plus fréquemment");
            System.out.println("3: US2.2--habitudes de consommation");
            System.out.println("4: US2.3--valider les préférences et trouver les produits de remplacements");
            System.out.println("5: US3.4--recommandation personnalisé");
            System.out.println("6: US3.5--profils des consommateurs");
            System.out.println("7: US3.6--nouvelles habitudes de consommation");
            System.out.println("8: Exit");
            System.out.print("Entrez votre choix:");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> VisualiserProduit(1);
                case 2 -> ProduitsFrequents(scanner);
                case 3 -> HabitudesDeConsommation(scanner);
                case 4 -> ValiderPreferences(scanner);
                case 5 -> RecommandationProduits(scanner);
                case 6 -> DifferentsProfils();
                case 7 -> DetecterNouvellesHabitudes(scanner);
                case 8 -> {
                    System.out.println("Exiting the application...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
    }
    }
}