package Managers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.*;

import BD_Connect.ProduitBD;
import Objects.*;
import connexion.Connect;

import static BD_Connect.ProduitBD.*;



public class UserManager {

    // US2.1. Je veux consulter la liste des produits que je commande le plus fréquemment.
    public static ArrayList<Produit> AfficherProduitFrequents(User user, int limit) throws SQLException {
        HashMap<Produit, Integer> produits = historyPanier(user);
        ArrayList<Produit> produitsFrequents = new ArrayList<>();
        produits.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .limit(limit)
                .forEach(o -> produitsFrequents.add(o.getKey()));
        return produitsFrequents;
    }


    //US 2.2 Je veux consulter mes habitudes de consommation (bio, nutriscore, catégorie de produits, marques).
    // En fact nour consulter les habitudes par categorie/discountrate/popularity
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

    // US2.3 Je veux valider les préférences que me propose le système afin d'avoir des produits de remplacements
    // qui correspondent mieux à mes habitudes.
    public static boolean[] validerHabitudes(User user) throws SQLException {
        affichierHabitudes(user);

        Scanner scanner = new Scanner(System.in);

        System.out.println("La catégorie correspond-elle à vos préférences ? (1. Oui, 2. Non)");
        Boolean category = getBooleanPreference(scanner);

        System.out.println("Le discount correspond-il à vos préférences ? (1. Oui, 2. Non)");
        Boolean discount = getBooleanPreference(scanner);

        System.out.println("La popularité correspond-elle à vos préférences ? (1. Oui, 2. Non)");
        Boolean popular = getBooleanPreference(scanner);

        return new boolean[]{category, discount, popular};
    }

    //US 3.5
    //Je veux consulter les différents profils de consommateurs.
    public static void afficherUtilisateur (User user) throws SQLException {
        System.out.println("====== Information du Utilisateur ======");
        System.out.println("ID            : " + user.getIdUser());
        System.out.println("Nom           : " + user.getLastname() + "·" + user.getName());
        System.out.println("Tel           : " + user.getTel());
        System.out.println("Adress        : " + user.getAddress());
        System.out.println("E_mail        : " + user.getEmail());
        System.out.println("========================================");
        affichierHabitudes(user);
    }

    public static HashMap<Produit, Integer> historyPanier(User user) throws SQLException {
        String query = String.format("SELECT p.id_produit, p.name,p.ratings,p.no_of_ratings,p.discount_price, p.actual_price, p.category, qte_produit " +
                "FROM panier pa JOIN produit p on pa.id_produit = p.id_produit " +
                "WHERE id_user = '%s' ", user.getIdUser());
        ResultSet result = Connect.executeQuery(query);
        HashMap<Produit, Integer> produits = new HashMap<>();
        while (result.next()) {
            Produit produit = new Produit(
                    result.getInt("id_produit"),
                    result.getString("name"),
                    result.getDouble("ratings"),
                    result.getInt("no_of_ratings"),
                    result.getInt("discount_price"),
                    result.getInt("actual_price"),
                    result.getInt("category")
            );
            int qte = result.getInt("qte_produit");
            produits.put(produit, produits.getOrDefault(produit,0) + qte);
        }
        return produits;
    }


    public static ArrayList<String> calculateCategoryHabitude(HashMap<Produit, Integer> produits) {
        HashMap<String, Integer> category_habitude = new HashMap<>();
        produits.entrySet().forEach(produit -> {
            int qte = produit.getValue();
            category_habitude.put(
                    produit.getKey().getMain_category(),
                    category_habitude.getOrDefault(produit.getKey().getMain_category(), 0) + qte
            );
        });


        ArrayList<String> category_habits = new ArrayList<>();
        category_habitude.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue() - o1.getValue())
                .limit(3)
                .forEach(entry -> category_habits.add(entry.getKey()));
        return category_habits;
    }


    public static String calculateDiscountHabitude(HashMap<Produit, Integer> produits) {
        HashMap<String, Integer> discount_habitude = new HashMap<>();
        produits.entrySet().stream()
                .filter(produit -> produit.getKey().getDiscount_price() != null && produit.getKey().getActual_price() != null)
                .collect(Collectors.groupingBy(
                        produit -> {
                            int discount = 100 * produit.getKey().getDiscount_price() / produit.getKey().getActual_price();
                            if (discount >= 90) {
                                return "100%-90%";
                            } else if (discount >= 80) {
                                return "90%-80%";
                            } else if (discount >= 70) {
                                return "80%-70%";
                            } else {
                                return "<70%";
                            }
                        },
                        Collectors.summingInt(produit -> 1)
                ))
                .forEach((group, count) -> discount_habitude.put(group, discount_habitude.getOrDefault(group, 0) + count));

        discount_habitude.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue() - o1.getValue())
                .limit(1);
        return discount_habitude.keySet().iterator().next();
    }


    public static String calculatePopularityHabitude(HashMap<Produit, Integer> produits) throws SQLException {
        ArrayList<String> category = new ArrayList<>();
        produits.keySet().stream()
                .forEach(produit -> {
                    category.add(produit.getMain_category());
                });
        category.stream()
                .distinct()
                .collect(Collectors.toList());

        HashMap<String, Integer> popularity_habitude = new HashMap<>();
        produits.entrySet().stream()
                .forEach(produit -> {
                    if (produit.getKey().getDiscount_price() > mainCatAndAvgNoRating.get(produit.getKey().getMain_category())) {
                        popularity_habitude.put("populaire", popularity_habitude.getOrDefault("populaire" , 0)+produit.getValue());
                    }else{
                        popularity_habitude.put("rare", popularity_habitude.getOrDefault("populaire" , 0)+produit.getValue());
                    }
                });
        popularity_habitude.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue() - o1.getValue());
        return popularity_habitude.keySet().iterator().next();
    }


    private static boolean getBooleanPreference(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine();
            if ("1".equals(input)) {
                return true;
            } else if ("2".equals(input)) {
                return false;
            } else {
                System.out.println("Entrée invalide, veuillez réessayer. (1. Oui, 2. Non)");
            }
        }
    }

    public static ArrayList<Produit> faireRecommandation(User user) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean[] preference = validerHabitudes(user);
        HashMap<Produit, Integer> produits_history= historyPanier(user);
        ArrayList<String> category = calculateCategoryHabitude(produits_history);
        String discount = calculateDiscountHabitude(produits_history);
        String popularity = calculatePopularityHabitude(produits_history);

        ArrayList<Produit> toutLesProduitsPossible = listDeProduitsDansCategory(produits_history,user);
        Map<Produit, Integer> scoreMap = toutLesProduitsPossible.stream()
                .collect(Collectors.toMap(prod -> prod, prod -> {
                    try {
                        return compairingScore(prod, preference, category, discount, popularity);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }));

        toutLesProduitsPossible.stream()
                .sorted((p1, p2) -> scoreMap.get(p2) - scoreMap.get(p1))
                .limit(15)
                .forEach(System.out::println);

        return toutLesProduitsPossible;
    }

    public static ArrayList<Produit> listDeProduitsDansCategory(HashMap<Produit, Integer> produitsHistoire, User user) throws SQLException {
    ArrayList<Produit> produits = new ArrayList<>();
        String query_produit = "SELECT DISTINCT p.id_produit, p.name,p.ratings,p.no_of_ratings,p.discount_price, p.actual_price, p.category " +
                "FROM produit p JOIN categories c ON p.category = c.id_cat " +
                "WHERE c.main_category IN ( " +
                "SELECT DISTINCT c2.main_category " +
                "FROM categories c2 " +
                "JOIN produit p2 ON c2.id_cat = p2.category " +
                "JOIN panier pa ON p2.id_produit = pa.id_produit " +
                "WHERE pa.id_user = " + user.getIdUser() + " )";
        ResultSet res1 = Connect.executeQuery(query_produit);

        while (res1.next()) {
            produits.add(new Produit(
                            res1.getInt("id_produit"),
                            res1.getString("name"),
                            res1.getDouble("ratings"),
                            res1.getInt("no_of_ratings"),
                            res1.getInt("discount_price"),
                            res1.getInt("actual_price"),
                            res1.getInt("category")
                    )
            );
        }
        res1.close();

        return produits;
    }

    public static Comparator<Produit> customizedComparator(boolean[] preference, ArrayList<String> categories, String discount,String popularity) throws SQLException {
        return(p1,p2) ->
        {
            try {
                int score1 = compairingScore(p1, preference, categories, discount, popularity);
                int score2 = compairingScore(p2, preference, categories, discount, popularity);
                return score2 - score1;
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        };
    }

    public static int compairingScore(Produit prod, boolean[] preference, ArrayList<String> categories, String discount, String popularity) throws SQLException {
        boolean cat = preference[0];
        boolean disc = preference[1];
        boolean pop = preference[2];
        boolean toutnon = (!cat && !disc && !pop);

        int score = 0;

        // 1. 类别分数（加权差异化）
        if (cat || toutnon) {
            if (categories.contains(prod.getMain_category())){
                score += 50;
            }
        }

        int max, min;
        switch (discount) {
            case "100%-90%" -> { max = 100; min = 90; }
            case "90%-80%" -> { max = 90; min = 80; }
            case "80%-70%" -> { max = 80; min = 70; }
            default -> { max = 70; min = 0; }
        }

        if (disc || toutnon) {
            int discountRate = prod.getDiscount_rate();
            if (discountRate > max) {
                score -= (discountRate - max) * 25; // 超出范围按差值加分
            } else if (discountRate < min) {
                score += (min - discountRate) *10; // 低于范围按差值加分
            } else {
                score += 300; // 在范围内固定加分
            }
        }

        if (pop || toutnon) {
            int avgNoOfRatings = ProduitBD.mainCatAndAvgNoRating.getOrDefault(prod.getMain_category(), 0);

            // 计算百分比差异
            double percentageDifference;
            if (avgNoOfRatings > 0) {
                percentageDifference = ((double) (prod.getNo_of_ratings() - avgNoOfRatings) / avgNoOfRatings) * 300;
            } else {
                percentageDifference = 0; // 如果平均值为 0，则设置差异为 0
            }

            // 根据百分比差异动态调整分数
            switch (popularity) {
                case "populaire" -> {
                    if (percentageDifference > 0) {
                        score += Math.min((int) percentageDifference, 200); // 上限为 50 分
                    } else {
                        score += Math.max((int) percentageDifference, -200); // 下限为 -50 分
                    }
                }
                case "rare" -> {
                    if (percentageDifference > 0) {
                        score += Math.max(-(int) percentageDifference, -200); // 下限为 -50 分
                    } else {
                        score += Math.min(-(int) percentageDifference, 200); // 上限为 50 分
                    }
                }
            }
        }


        int avgNoOfRatings = ProduitBD.mainCatAndAvgNoRating.getOrDefault(prod.getMain_category(), 0);
        score += (int) (10 * (prod.getRating() - avgNoOfRatings));
        score += Math.random()*500;

        return Math.max(0, score);
    }

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
