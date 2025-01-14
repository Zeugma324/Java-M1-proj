package Managers;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.*;

import BD_Connect.ProduitBD;
import Objects.*;
import connexion.Connect;

import static Managers.ProduitManager.*;


public class UserManager {
    // US2.1 Je veux consulter la liste des produits que je commande le plus fréquemment.
    public static ArrayList<Produit> AfficherProduitFrequents(User user, int limit) throws SQLException {
        String query = String.format("SELECT p.name, p.actual_price, COUNT(pa.id_produit) AS purchase_count " +
                "FROM panier pa " +
                "JOIN produit p ON pa.id_produit = p.id_produit " +
                "WHERE pa.id_user = '%s' GROUP BY pa.id_produit " +
                "ORDER BY purchase_count DESC ", user.getIdUser());

        ResultSet result = Connect.executeQuery(query);

        ArrayList<Produit> produits = new ArrayList<>();
        if (!result.next()) {
            System.out.println("No frequent products found for this user.");
        } else {
            while (result.next()) {
                produits.add(ProduitBD.loadProduit(result.getInt("id_produit")));
            }
        }
        produits.stream()
                .limit(limit)
                .forEach(produit -> System.out.println("ID : " + produit.getId() + " Libelle : " + produit.getLibelle()));
        return produits;
    }


    //US 2.2 Je veux consulter mes habitudes de consommation (bio, nutriscore, catégorie de produits, marques).
    // En fact nour consulter les habitudes par categorie/discountrate/popularity
    public static HashMap<Produit, Integer> historyPanier(User user) throws SQLException {
        String query = String.format("SELECT id_produit, qte_produit FROM panier WHERE id_user = '%s' ", user.getIdUser());
        ResultSet result = Connect.executeQuery(query);
        HashMap<Produit, Integer> produits = new HashMap<>();
        while (result.next()) {
            Produit produit = ProduitBD.loadProduit(result.getInt("id_produit"));
            if (produits.containsKey(produit)) {
                produits.put(produit, produits.get(produit) + result.getInt("qte_produit"));
            } else {
                produits.put(produit, result.getInt("qte_produit"));
            }
        }
        return produits;
    }

    public static int calculateMedian(List<Produit> produits) {
        List<Integer> ratings = produits.stream()
                .map(Produit::getNo_of_ratings)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
        int size = ratings.size();
        if (size == 0) return 0;
        return ratings.get(size / 2);
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

        System.out.println("Votre categorie préfréré est :");
        ArrayList<String> category_habits = new ArrayList<>();
        category_habitude.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue() - o1.getValue())
                .limit(3)
                .forEach(entry -> {
                    category_habits.add(entry.getKey());
                    System.out.println(entry.getKey());
                });
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

        System.out.println("Votre discount préfréré est : ");
        discount_habitude.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue() - o1.getValue())
                .limit(1)
                .forEach(entry -> System.out.println(entry.getKey()));
        return discount_habitude.keySet().iterator().next();
    }

    public static String calculatePopularityHabitude(HashMap<Produit, Integer> produits) throws SQLException {
        HashMap<String, List<Produit>> categoryCache = new HashMap<>();
        HashMap<String, Integer> popularity_habitude = new HashMap<>();

        produits.entrySet().forEach(entry -> {
            Produit produit = entry.getKey();
            int qte = entry.getValue();

            List<Produit> produits_in_cat = categoryCache.computeIfAbsent(
                    produit.getSub_category(),
                    key -> {
                        try {
                            return consulterProduitsParCategorieSansSort(ProduitBD.catAndId_cat.get(key));
                        } catch (SQLException e) {
                            e.printStackTrace();
                            return new ArrayList<>();
                        }
                    }
            );

            int median = calculateMedian(produits_in_cat);

            String key = (qte >= median) ? "populaire" : "rare";
            popularity_habitude.put(
                    key,
                    popularity_habitude.getOrDefault(key, 0) + qte
            );
        });

        System.out.println("Votre popularity préfréré est : ");
        popularity_habitude.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue() - o1.getValue())
                .limit(1)
                .forEach(entry -> System.out.println(entry.getKey()));

        return popularity_habitude.keySet().iterator().next();
    }

    public static void affichierHabitudes(User user) throws SQLException {
        System.out.println("Résumé des habitudes de consommation :");
        HashMap<Produit, Integer> produits = historyPanier(user);
        ArrayList<String> category_habits = calculateCategoryHabitude(produits);
        String discount_habit = calculateDiscountHabitude(produits);
        String popularity_habit = calculatePopularityHabitude(produits);

        System.out.println("Catégories préférées :");
        category_habits.forEach(System.out::println);

        System.out.println("Discount préféré :");
        System.out.println(discount_habit);
        System.out.println("Popularité préférée :");
        System.out.println(popularity_habit);
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
        affichierHabitudes(user);
        Scanner scanner = new Scanner(System.in);
        boolean[] preference = validerHabitudes(user);
        HashMap<Produit, Integer> produits_history= historyPanier(user);
        ArrayList<String> category = calculateCategoryHabitude(produits_history);
        String discount = calculateDiscountHabitude(produits_history);
        String popularity = calculatePopularityHabitude(produits_history);

        ArrayList<Produit> toutLesProduitsPossible = listDeProduitsDansCategory(user);
        toutLesProduitsPossible.stream()
                .sorted(customizedComparator(preference,category,discount,popularity))
                .limit(15)
                .forEach(System.out::println);

        return toutLesProduitsPossible;
    }

    public static ArrayList<Produit> listDeProduitsDansCategory(User user) throws SQLException {
        String query_mainCat = "SELECT DISTINCT c.main_category " +
                "FROM categories c JOIN produit p ON c.id_cat = p.category " +
                "JOIN panier pa ON p.id_produit = c.id_produit " +
                "WHERE pa.id_user = " + user.getIdUser();
        ResultSet res = Connect.executeQuery(query_mainCat);
        ArrayList<String> main_cat = new ArrayList<>();
        while (res.next()) {
            main_cat.add(res.getString("main_category"));
        }
        ArrayList<Produit> produits = new ArrayList<>();
        res.close();
        for (int i = 0; i < main_cat.size();i++){
            String query_produit = "SELECT name, ratings, no_of_ratings, discount_price, actual_price, category, id_produit " +
                    "FROM produit p JOIN categories c ON p.category = c.id_cat " +
                    "WHERE main_category = '" + main_cat.get(i) + "'";
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
        }
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

    public static int compairingScore(Produit prod, boolean[] preference, ArrayList<String> categories, String discount,String popularity) throws SQLException {
        Boolean cat = preference[0];
        Boolean disc = preference[1];
        Boolean pop = preference[2];
        Boolean toutnon = (!cat && !disc && !pop);
        // if user think all the preferences I analysed is wrong we use the defalt method

        int score = 0;

        if(cat||toutnon) {

            for( int i = categories.size(); i > 0; i--) {
                score += 2*(i+1);
            }
        }

        int max;
        int min;
        switch(discount){
            case "100%-90%" -> {
                max = 100;
                min = 90;
            }
            case "90%-80%" -> {
                max = 90;
                min = 80;
            }
            case "80%-70%" -> {
                max = 80;
                min = 70;
            }
            case "<70%" -> {
                max = 70;
                min = 0;
            }
            default -> {
                max = 70;
                min = 0;
            }
        }
        if(disc || toutnon) {
            if (prod.getDiscount_rate() > max) {
                score += (prod.getDiscount_rate() - max) / 3 + 5;
            } else if (prod.getDiscount_rate() < min) {
                score += (min - prod.getDiscount_rate()) / 3 + 5;
            } else{
                score += 5;
            }
        }
        if(pop || toutnon) {
            ArrayList<Produit> produits = ProduitManager.consulterProduitsParCategorie(ProduitBD.catAndId_cat.get(prod.getSub_category()));
            int midian = calculateMedian(produits);
            switch(popularity){
                case "populaire" -> {
                    if (prod.getNo_of_ratings() > midian) {
                        score += 3;
                    }else{
                        score -= 3;
                    }
                }
                case "rare" -> {
                    if (prod.getNo_of_ratings() > midian) {
                        score -= 3;
                    }else{
                        score += 3;
                    }
                }
            }
        }
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
