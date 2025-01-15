package Console;

import BD_Connect.ProduitBD;
import BD_Connect.UserDB;
import Objects.Produit;
import Objects.User;
import connexion.Connect;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static BD_Connect.ProduitBD.catAndId_cat;
import static Managers.ProduitManager.*;
import static Managers.UserManager.*;

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
        String query = "SELECT * FROM produit WHERE name LIKE '%" + keyword + "%'";
        ResultSet result = Connect.executeQuery(query);
        ArrayList<Produit> produits = new ArrayList<>();

        System.out.println("\nProduits trouvés pour le mot-clé : " + keyword);
        while (result.next()) {
            produits.add(new Produit(
                    result.getInt("id_produit"),
                    result.getString("name"),
                    result.getDouble("ratings"),
                    result.getInt("no_of_ratings"),
                    result.getInt("discount_price"),
                    result.getInt("actual_price"),
                    result.getInt("category")));
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
            return null;
        }

        // Tri (US 0.4)
        liste.stream()
                .sorted(selectComparator())
                .limit(10)
                .forEach(System.out::println);
        Connect.closeConnexion();
        return liste;
    }

    // =========================
    // US 0.4 - Choix du comparateur (tri)
    // =========================
    public static Comparator<Produit> selectComparator() {
        System.out.println("Choisissez un champ de tri (1.libelle, 2.rating, 3.price) :");
        String trier;

        if (System.console() != null) {
            trier = System.console().readLine();
        } else {
            trier = "2";
        }

        System.out.println("Ordre de tri (1.asc, 2.desc) :");
        String order;
        if (System.console() != null) {
            order = System.console().readLine();
        } else {
            order = "2";
        }

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

        if (!order.equals("1")) {
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

    // US2.1. Je veux consulter la liste des produits que je commande le plus fréquemment.
    public static void AfficherProduitFrequents(User user, int limit) throws SQLException {
        HashMap<Produit, Integer> produits = historyPanier(user);
        produits.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .limit(limit)
                .forEach(o -> System.out.println("Achat fois :" + o.getValue() + " ; Produit :" + o.getKey().getLibelle()));
    }

    //2.2 Je veux consulter mes habitudes de consommation (bio, nutriscore, catégorie de produits, marques).
    public static void affichierHabitudes(User user) throws SQLException {
        System.out.println("==== habitudes des consommations : =====");
        HashMap<Produit, Integer> produits = historyPanier(user);
        ArrayList<String> category_habits = calculateCategoryHabitude(produits);
        String discount_habit = calculateDiscountHabitude(produits);
        String popularity_habit = calculatePopularityHabitude(produits);

        System.out.println("======== Catégories préférées : ========");
        category_habits.forEach(System.out::println);

        System.out.println("========================================");
        System.out.println("Discount préféré :" + discount_habit);
        System.out.println("========================================");
        System.out.println("Popularité préférée :" + popularity_habit);
        System.out.println("========================================");
    }

    //US 2.3
    public static ArrayList<Produit> faireRecommandation(User user) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean[] preference = validerHabitudes(user);
        HashMap<Produit, Integer> produits_history = historyPanier(user);
        ArrayList<String> excluded_subCategories = new ArrayList<>();

        produits_history.keySet().stream()
                .forEach(produit -> excluded_subCategories.add(produit.getSub_category()));
        excluded_subCategories.stream().distinct().collect(Collectors.toList());

        ArrayList<String> category = calculateCategoryHabitude(produits_history);
        String discount = calculateDiscountHabitude(produits_history);
        String popularity = calculatePopularityHabitude(produits_history);

        ArrayList<Produit> toutLesProduitsPossible = listDeProduitsDansCategory(produits_history, user);
        Map<Produit, Integer> scoreMap = toutLesProduitsPossible.stream()
                .collect(Collectors.toMap(prod -> prod, prod -> {
                    try {
                        return compairingScore(prod, preference, category, discount, popularity);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }));
        scoreMap.entrySet().stream().forEach(e -> {
            System.out.println(e.getKey().getId() + " : " + e.getValue());
        });
        toutLesProduitsPossible.stream()
                .sorted((p1, p2) -> scoreMap.get(p2) - scoreMap.get(p1))
                .filter(prod -> !excluded_subCategories.contains(prod.getSub_category()))
                .limit(15)
                .forEach(System.out::println);

        return toutLesProduitsPossible;
    }

    //US 3.1. Je veux importer automatiquement des produits afin de mettre à jour mon catalogue produit.
    //Le csv est dans le dossier data.
    public static void importerProduit(String name_csv) throws SQLException, IOException {
        String csvFile = "src/data/" + name_csv + ".csv";
        char separator = ',';
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO produit(name, ratings, no_of_ratings, discount_price, actual_price, category) VALUES ");
        ArrayList<String[]> produitList = new ArrayList<>();

        for (String line : Files.readAllLines(Paths.get(csvFile))) {
            String[] data = line.split(String.valueOf(separator));
            produitList.add(data);
        }

        produitList.forEach(produit -> {
            try {
                String libelle = produit[0];
                String main_category = produit[1];
                String sub_category = produit[2];
                double rating = Double.parseDouble(produit[3]);
                int no_of_ratings = Integer.parseInt(produit[4]);
                int discount_price = Integer.parseInt(produit[5]);
                int actual_price = Integer.parseInt(produit[6]);

                // check if category already exist
                int category;
                if (catAndId_cat.containsKey(sub_category)) {
                    category = catAndId_cat.get(sub_category);
                } else {
                    // new category insert into table category
                    category = catAndId_cat.size() + 1;
                    catAndId_cat.put(sub_category, category);

                    String insertCategoryQuery = String.format(
                            "INSERT INTO categories (Id_cat, main_category, sub_category) VALUES (%d, '%s', '%s')",
                            category, main_category, sub_category);
                    Connect.executeUpdate(insertCategoryQuery);
                }

                // ecrit un quand query donc on peux faire tout les insert dan 1 grand query.
                query.append(String.format("('%s', %.2f, %d, %d, %d, %d), ",
                        libelle, rating, no_of_ratings, discount_price, actual_price, category));
            } catch (Exception e) {
                System.err.println("错误解析行: " + String.join(",", produit));
                e.printStackTrace();
            }
        });

        query.delete(query.length() - 2, query.length());
        query.append(';');
        Connect.executeUpdate(query.toString());
        Connect.closeConnexion();
    }

    //US 3.5
    public static void consulterProduit(Produit produit) throws SQLException {
        HashMap<User,Integer> consulterUserParProduit = consulterUserParProduit(produit);
        HashMap<String,Integer> agePercentage = groupUserParAge(consulterUserParProduit);
        HashMap<String,Integer> genderPercentage = groupUserParGender(consulterUserParProduit);
        HashMap<String,Integer> zodiaquePercentage = groupUserParZodiaque(consulterUserParProduit);
        System.out.println("========================================");
        System.out.println("Consulter produit : " + produit.getLibelle());
        System.out.println(" Répartition selon les groupes d'âge :");
        agePercentage.entrySet().stream()
                .sorted((o1,o2) -> o2.getValue() - o1.getValue())
                .forEach(entry -> System.out.printf("Âge "+ entry.getKey() + " : %" + entry.getValue()) );

        System.out.println("Répartition hommes/femmes :");
        genderPercentage.entrySet().stream()
                .sorted((o1,o2) -> o2.getValue() - o1.getValue())
                .forEach(entry -> System.out.printf(entry.getKey() + " : %" + entry.getValue()));

        System.out.println("Répartition selon les signes du zodiaque :");
        zodiaquePercentage.entrySet().stream()
                .sorted((o1,o2) -> o2.getValue() - o1.getValue())
                .forEach(entry -> System.out.printf(entry.getKey() + " : %" + entry.getValue()));
    }




    public static void main(String[] args) throws SQLException {
//        AfficherProduitFrequents(UserDB.findUserById(1),5);
//        US.affichierHabitudes(UserDB.findUserById(1));

//        faireRecommandation(UserDB.findUserById(1));

    }

}
    


