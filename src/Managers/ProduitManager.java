package Managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import BD_Connect.PanierBD;
import BD_Connect.ProduitBD;
import Objects.*;
import connexion.Connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static BD_Connect.ProduitBD.catAndId_cat;


public class ProduitManager {
    // requets
    // US 0.1 Je veux visualiser les détails d'un produit
    // US 0.1 : Visualiser les détails d'un produit
    public static void visualiserProduit(int idProd) throws SQLException {
        Produit prod = ProduitBD.loadProduit(idProd);
        System.out.println(prod);
    }

    // US 0.2 Je veux rechercher un produit par mot-clé.
    public static void rechercherProduit(String keyword) throws SQLException {
        String query = "SELECT p.id_produit, p.name, p.ratings, p.no_of_ratings, "
                + "p.discount_price, p.actual_price, p.category "
                + "FROM produit p "
                + "WHERE p.name LIKE '%" + keyword + "%'";

        ResultSet rs = Connect.executeQuery(query);
        ArrayList<Produit> produits = new ArrayList<>();
        while (rs.next()) {
            Produit produit = new Produit(
                    rs.getInt("id_produit"),
                    rs.getString("name"),
                    rs.getDouble("ratings"),
                    rs.getInt("no_of_ratings"),
                    rs.getInt("discount_price"),
                    rs.getInt("actual_price"),
                    rs.getInt("category")
            );
            produits.add(produit);
        }
        produits.stream()
                .sorted(selectComparator())
                .forEach(produit -> System.out.println(produit));
        Connect.closeConnexion();
    }

    // US 0.3 : Consulter les produits par catégorie
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
            return liste;
        }
        liste.stream()
                .sorted(selectComparator())
                .limit(10);
        return liste;
    }

    public static ArrayList<Produit> consulterProduitsParCategorieSansSort(int idCat) throws SQLException {
        String query = "SELECT * " +
                "FROM produit " +
                "WHERE produit.category = " + idCat;
        ResultSet result = Connect.executeQuery(query);
        ArrayList<Produit> produits = new ArrayList<>();
        while (result.next()) {
            produits.add(new Produit(
                    result.getInt("id_produit"),
                    result.getString("name"),
                    result.getDouble("ratings"),
                    result.getInt("no_of_ratings"),
                    result.getInt("discount_price"),
                    result.getInt("actual_price"),
                    result.getInt("category")
            ));
        }
        return produits;
    }

    public static Comparator<Produit> selectComparator() {

        System.out.println("Choisissez un champ de tri (1.libelle, 2.rating, 3.price) :");

        String trier = System.console().readLine();
        if (!trier.equals("1") & !trier.equals("2") & !trier.equals("3")) {
            System.out.println("Champ invalide");
            selectComparator();
        }

        System.out.println("Choisissez l'ordre de tri (1.ascending, 2.descending) :");
        String order = System.console().readLine();
        if (!order.equals("1") & !order.equals("2")) {
            System.out.println("Champ invalide");
            selectComparator();
        }
        System.out.println("------- il faut attenuate pour quelque seconds --------");
        Comparator<Produit> comparator = switch (trier) {
            case "1" -> Comparator.comparing(Produit::getLibelle);
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

    //US 3.1. Je veux importer automatiquement des produits afin de mettre à jour mon catalogue produit.
    //Le csv est dans le dossier data.
    public static void importerProduit(String name_csv) throws SQLException, IOException {
        String csvFile = "src/data/" + name_csv + ".csv";
        char separator = ',';
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO produit(name, ratings, no_of_ratings, discount_price, actual_price, category) VALUES ");
        ArrayList<String[]> produitList = new ArrayList<>();

        boolean isFirstLine = true;
        for (String line : Files.readAllLines(Paths.get(csvFile))) {
            if (isFirstLine) {
                isFirstLine = false; // 跳过标题行
                continue;
            }
            String[] data = line.split(String.valueOf(separator));
            if (data.length != 7) { // 确保列数为 7
                System.err.println("Invalid data: " + Arrays.toString(data));
                continue;
            }
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

                int category;
                if (catAndId_cat.containsKey(sub_category)) {
                    category = catAndId_cat.get(sub_category);
                } else {
                    category = catAndId_cat.size() + 1;
                    catAndId_cat.put(sub_category, category);

                    String insertCategoryQuery = String.format(
                            "INSERT INTO categories (Id_cat, main_category, sub_category) VALUES (%d, '%s', '%s')",
                            category, main_category, sub_category);
                    Connect.executeUpdate(insertCategoryQuery);
                }

                query.append(String.format(Locale.US, "('%s', %.1f, %d, %d, %d, %d), ",
                        libelle, rating, no_of_ratings, discount_price, actual_price, category));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (query.charAt(query.length() - 2) == ',') {
            query.delete(query.length() - 2, query.length()); // 移除最后的逗号
        }
        query.append(';');
        Connect.executeUpdate(query.toString());
//        System.out.println(query);
        Connect.closeConnexion();
    }


    //Je veux consulter les différents profils de consommateurs.
    public static void consulterProduit(Produit produit) throws SQLException {
        HashMap<User, Integer> consulterUserParProduit = consulterUserParProduit(produit);
        HashMap<String, Integer> agePercentage = groupUserParAge(consulterUserParProduit);
        HashMap<String, Integer> genderPercentage = groupUserParGender(consulterUserParProduit);
        HashMap<String, Integer> zodiaquePercentage = groupUserParZodiaque(consulterUserParProduit);
        System.out.println("========================================");
        System.out.println("Consulter produit : " + produit.getLibelle());
        System.out.println(" Répartition selon les groupes d'âge :");
        agePercentage.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue() - o1.getValue())
                .forEach(entry -> System.out.printf("Âge " + entry.getKey() + " : %" + entry.getValue()));

        System.out.println("Répartition hommes/femmes :");
        genderPercentage.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue() - o1.getValue())
                .forEach(entry -> System.out.printf(entry.getKey() + " : %" + entry.getValue()));

        System.out.println("Répartition selon les signes du zodiaque :");
        zodiaquePercentage.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue() - o1.getValue())
                .forEach(entry -> System.out.printf(entry.getKey() + " : %" + entry.getValue()));
    }


//    public static HashMap<User, Integer> consulterUserParProduit(Produit produit) throws SQLException {
//        String query = "SELECT u.id_user, u.lastname, u.name, u.tel, u.adress, u.email, u.mot_de_passe, u.gender, u.date_de_naissance, p.qte_produit " +
//                "FROM utilisateur u JOIN panier p on u.id_user = p.id_user " +
//                "WHERE p.id_produit = " + produit.getId() + " "
//                + "AND Date_fin IS NOT NULL ; ";
////        System.out.println(query);
//        ResultSet rs = Connect.executeQuery(query);
//        HashMap<User, Integer> usersAndQteAchat = new HashMap<>();
//        while (rs.next()) {
//            User user = new User(
//                    rs.getInt("id_user"),
//                    rs.getString("lastname"),
//                    rs.getString("name"),
//                    rs.getString("tel"),
//                    rs.getString("adress"),
//                    rs.getString("email"),
//                    rs.getString("mot_de_passe"),
//                    rs.getString("gender"),
//                    rs.getString("date_de_naissance"));
//            user.setPanier(PanierBD.loadPanierByUser(user));
//            int qteAchat = rs.getInt("qte_produit");
//            usersAndQteAchat.put(user, usersAndQteAchat.getOrDefault(user, 0) + qteAchat);
//        }
//        return usersAndQteAchat;
//    }

public static HashMap<User, Integer> consulterUserParMagasin(int id_magasin) throws SQLException {
    String query = """
        SELECT u.*,
        pa.*,
        p.id_produit, p.name AS produit_name, p.ratings, p.no_of_ratings, p.discount_price, p.actual_price, p.category
        FROM utilisateur u
        JOIN panier pa ON u.id_user = pa.id_user
        JOIN produit p ON pa.id_produit = p.id_produit
        WHERE pa.id_magasin = %d
        AND pa.date_fin IS NOT NULL;
    """.formatted(id_magasin);

    ResultSet rs = Connect.executeQuery(query);

    // 1) 用于缓存已构造的 User 对象，避免重复创建
    HashMap<Integer, User> userMap = new HashMap<>();
    // 2) 用于记录结果 (User -> 累计购买数量)
    HashMap<User, Integer> result = new HashMap<>();

    while (rs.next()) {
        int userId = rs.getInt("id_user");

        // 如果该 user 不在缓存中，就新建一个
        User user = userMap.get(userId);
        if (user == null) {
            user = new User(
                    userId,
                    rs.getString("lastname"),
                    rs.getString("name"),
                    rs.getString("tel"),
                    rs.getString("adress"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe"),
                    rs.getString("gender"),
                    rs.getString("date_de_naissance")
            );
            userMap.put(userId, user);
        }

        // 取出 qte_produit 并累加到 result
        int qte = rs.getInt("qte_produit");
        result.put(user, result.getOrDefault(user, 0) + qte);
    }

    rs.close();
    Connect.closeConnexion();
    return result;
}

    public static HashMap<String, Integer> groupUserParAge(HashMap<User, Integer> usersAndQteAchat) throws SQLException {
        HashMap<String, Integer> userAgeAndQte = new HashMap<>();
        usersAndQteAchat.entrySet().stream()
                .forEach(entry -> {
                    int age = entry.getKey().getAge();
                    int qte = entry.getValue();
                    String ageGroup = "";
                    if (age <= 18 && age > 0) {
                        ageGroup = "<18";
                    } else if (age <= 25) {
                        ageGroup = "18-25";
                    } else if (age <= 35) {
                        ageGroup = "25-35";
                    } else if (age <= 50) {
                        ageGroup = "35-50";
                    } else if (age <= 75) {
                        ageGroup = "50-75";
                    } else {
                        ageGroup = ">75";
                    }
                    userAgeAndQte.put(ageGroup, userAgeAndQte.getOrDefault(ageGroup, 0) + qte);
                });
        int amount_total = userAgeAndQte.values().stream().mapToInt(Integer::intValue).sum();
        HashMap<String, Integer> percentageMap = new HashMap<>();
        userAgeAndQte.forEach((key, value) -> {
            int percentage = value * 100 / amount_total;
            percentageMap.put(key, percentage);
        });
        return percentageMap;
    }

    public static HashMap<String, Integer> groupUserParGender(HashMap<User, Integer> usersAndQteAchat) throws SQLException {
        HashMap<String, Integer> userGenderAndQte = new HashMap<>();
        usersAndQteAchat.entrySet().stream()
                .forEach(entry -> {
                    String gender = entry.getKey().getGender();
                    int qte = entry.getValue();
                    userGenderAndQte.put(entry.getKey().getGender(), userGenderAndQte.getOrDefault(entry.getKey().getGender(), 0) + qte);
                });
        int amount_total = userGenderAndQte.values().stream().mapToInt(Integer::intValue).sum();
        HashMap<String, Integer> percentageMap = new HashMap<>();
        userGenderAndQte.forEach((key, value) -> {
            int percentage = value * 100 / amount_total;
            percentageMap.put(key, percentage);
        });
        return percentageMap;
    }


    public static HashMap<User, Integer> consulterUserParProduit(Produit produit) throws SQLException {
        String query = """
        SELECT u.id_user, u.lastname, u.name, u.tel, u.adress, u.email, u.mot_de_passe, u.gender, u.date_de_naissance,
        pa.id_panier, pa.qte_produit, pa.id_magasin, pa.Date_debut, pa.Date_fin,
        p.id_produit, p.name AS produit_name, p.ratings, p.no_of_ratings, p.discount_price, p.actual_price, p.category
        FROM utilisateur u
        JOIN panier pa ON u.id_user = pa.id_user
        JOIN produit p ON pa.id_produit = p.id_produit
        WHERE p.id_produit = %d
        AND pa.date_fin IS NOT NULL;
    """.formatted(produit.getId());

        ResultSet rs = Connect.executeQuery(query);
        HashMap<Integer, User> userMap = new HashMap<>();
        HashMap<User, Integer> result = new HashMap<>();

        while (rs.next()) {
            int userId = rs.getInt("id_user");
            User user = userMap.get(userId);
            if (user == null) {
                user = new User(
                        userId,
                        rs.getString("lastname"),
                        rs.getString("name"),
                        rs.getString("tel"),
                        rs.getString("adress"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("gender"),
                        rs.getString("date_de_naissance")
                );
                userMap.put(userId, user);
            }
            int qte = rs.getInt("qte_produit");

            result.put(user, result.getOrDefault(user, 0) + qte);
        }
        rs.close();
        Connect.closeConnexion();
        return result;
    }


    public static HashMap<String, Integer> groupUserParZodiaque(HashMap<User, Integer> usersAndQteAchat) throws SQLException {
        HashMap<String, Integer> userZodiaqueAndQte = new HashMap<>();
        usersAndQteAchat.entrySet().stream()
                .forEach(entry -> {
                    String zodiaque = entry.getKey().getZodiaque();
                    int qte = entry.getValue();
                    userZodiaqueAndQte.put(entry.getKey().getZodiaque(), userZodiaqueAndQte.getOrDefault(entry.getKey().getZodiaque(), 0) + qte);
                });
        int amount_total = userZodiaqueAndQte.values().stream().mapToInt(Integer::intValue).sum();
        HashMap<String, Integer> percentageMap = new HashMap<>();
        userZodiaqueAndQte.forEach((key, value) -> {
            int percentage = value * 100 / amount_total;
            percentageMap.put(key, percentage);
        });
        return percentageMap;
    }

    public static void main(String[] args) throws SQLException, IOException {

    }

}
