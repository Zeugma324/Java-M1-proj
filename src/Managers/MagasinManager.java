package Managers;

import Objects.User;
import connexion.Connect;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static BD_Connect.ProduitBD.catAndId_cat;

public class MagasinManager {
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
        produitList.remove(0);
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
                query.append( String.format(Locale.US,"('%s', %.2f, %d, %d, %d, %d), ",
                        libelle, rating, no_of_ratings, discount_price, actual_price, category));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        query.delete(query.length() - 2, query.length());
        query.append(';');
        Connect.executeUpdate(query.toString());
        Connect.closeConnexion();
    }

    // =========================
    // US 3.2 - Temps moyen de réalisation d'un panier
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

    public static void AVGTempRealiserPanier(int idmagasin) throws SQLException {
        String sql = "SELECT AVG(TIMESTAMPDIFF(DAY, Date_debut, Date_fin)) AS TempMoyenP " +
                "FROM panier WHERE Date_fin IS NOT NULL " +
                "AND Id_magasin = " + idmagasin;

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

    //us3.3 editer les statistique sur produits，category, client
    //3.affichier les QteStock Original de produit
    public static int QteStock(int produitID) throws SQLException{
        String sql = "SELECT SUM(quantity) AS total_qte FROM stock WHERE Id_produit = "  + produitID;
        ResultSet result = Connect.executeQuery(sql);
        if(result.next()){
            int totalQte = result.getInt("total_qte");
            return totalQte;
        }else{
            System.out.println("Trouvé pas");
            return 0;
        }
    }

    public static int QteStock(int produitID, int magasin_id) throws SQLException{
        String query = String.format("SELECT SUM(quantity) AS total_qte FROM stock WHERE Id_produit = %s AND id_magasin = %s", produitID, magasin_id);
        ResultSet result = Connect.executeQuery(query);
        if(result.next()){
            int totalQte = result.getInt("total_qte");
            return totalQte;
        }
        return 0;
    }
    //4.modifier QteStock de produit
    public static void AddStock(int produitID, int QteModifier, int id_magasin) throws SQLException{
        String sql = "UPDATE stock Set quantity = quantity + " + QteModifier + " WHERE Id_produit = " + produitID + " AND id_magasin = " + id_magasin;
        Connect.executeUpdate(sql);
        System.out.println("Stock change : " + QteModifier);
        Connect.closeConnexion();
    }
    public static void ModifierStock(int produitID, int QteModifier, int id_magasin) throws SQLException{
        String sql = "UPDATE stock Set quantity = " + QteModifier + " WHERE Id_produit = " + produitID + " AND id_magasin = " + id_magasin;
        Connect.executeUpdate(sql);
        System.out.println("Stock change : " + QteModifier);
        Connect.closeConnexion();
    }
    //5.Afficher le QteStock d'un produit
    public static void StockParSubCat(int catID) throws SQLException {
        String sql = "SELECT stock.Id_produit, SUM(stock.quantity) AS total_qte, category FROM stock JOIN produit on stock.Id_produit = produit.Id_produit WHERE category = " + catID + " GROUP BY stock.Id_produit ";

        ResultSet result = Connect.executeQuery(sql);

        System.out.println("produits dans la categorie " + catID + " : ");
        int totalQte = 0;
        while (result.next()){
            int Qte = result.getInt("total_qte");
            totalQte += Qte;
            System.out.println("Produit ID : " + result.getInt("Id_produit") + " QteStock : " + Qte);
        }

        System.out.println("Total stock dans la categorie " + catID + " : " + totalQte);

        Connect.closeConnexion();
    }

    //6.Modifier le Qtestock d'un produit
    public static void ModifierStockParSubCat(int catID, int StockChange) throws SQLException{

        String sql = "UPDATE stock JOIN produit on stock.Id_produit = produit.Id_produit Set quantity = quantity + "+ StockChange + " WHERE category = " + catID ;
        Connect.executeUpdate(sql);

        System.out.println("Stock change : " + StockChange);

        Connect.closeConnexion();
    }

    public static void ModifierCategory(int id_produit, String sub_category) throws SQLException {
        if(!catAndId_cat.containsKey(sub_category)){
            System.out.println("sub_category n'existe pas");
            return;
        }
        int id_cat = catAndId_cat.get(sub_category);
        String sql = String.format("UPDATE produit SET category = %s WHERE id_produit = %s", id_cat, id_produit);
        Connect.executeUpdate(sql);
        Connect.closeConnexion();
    }

    //9. Temps moyen de préparation des commandes(缺数据）
    public static void AVGTempPrepareCom() throws SQLException{

        String sql = " SELECT AVG(TIMESTAMPDIFF(Day, P.Date_finL, date_livrasion,)) AS TempMoyenC FROM panier P JOIN PanierCommande PC ON P.Id_panier = PC.panier_id JOIN livrasion L ON L.id_commande = PC.Id_commande WHERE P.Date_fin IS NOT NULL AND L.date_livrasion IS NOT NULL ";

        ResultSet result = Connect.executeQuery(sql);

        if (result.next()){
            double AVGTempDays = result.getDouble("TempMoyenC");
            System.out.println("temps moyen de préparation des commandes : " + AVGTempDays + "jours");
        }else {
            System.out.println("Trouvez pas les commandes preparés");
        }
        Connect.closeConnexion();
    }

    public static void AVGTempPrepareCom(int id_magasin) throws SQLException{

        String sql = "SELECT AVG(TIMESTAMPDIFF(DAY, l.date_livrasion, p.Date_fin)) AS TempMoyenC " +
                "FROM panier p " +
                "JOIN PanierCommande pc ON p.id_panier = pc.panier_id " +
                "JOIN commande c ON pc.id_commande = c.id_commande " +
                "JOIN livrasion l ON l.id_commande = c.id_commande " +
                "WHERE p.Date_fin IS NOT NULL " +
                "  AND l.date_livrasion IS NOT NULL " +
                "  AND p.id_magasin = l.id_magasin " +   // 如果表结构确保了同一订单只能对应同一个 magasin，可不加这行
                "  AND p.id_magasin = " + id_magasin;

        ResultSet result = Connect.executeQuery(sql);

        if (result.next()){
            double AVGTempDays = result.getDouble("TempMoyenC");
            System.out.println("temps moyen de préparation des commandes : " + AVGTempDays + "jours");
        }else {
            System.out.println("Trouvez pas les commandes preparés");
        }
        Connect.closeConnexion();
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

    public static ArrayList<Integer> commandAPreparer(int id_magasin) throws SQLException{
        String query = String.format(
                """
                    SELECT pc.id_commande, p.Date_fin
                    FROM panier p
                    JOIN PanierCommande pc ON p.id_panier = pc.panier_id
                    LEFT JOIN livrasion l on l.id_commande = pc.id_commande
                    WHERE p.id_magasin = %d
                    AND NOT EXISTS(
                        SELECT 1
                        FROM livrasion l2
                        WHERE l2.id_commande = pc.id_commande
                    )""",id_magasin);
        ResultSet result = Connect.executeQuery(query);
        HashMap<Integer, LocalDateTime> map = new HashMap<>();
        while(result.next()){
            int id_commande = result.getInt("id_commande");
            String date_fin = result.getString("Date_fin");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime date = LocalDateTime.parse(date_fin,formatter);
            map.put(id_commande, date);
        }
        ArrayList<Integer> list = new ArrayList<>();
        map.entrySet().stream()
                .sorted((o1,o2) -> o1.getValue().compareTo(o2.getValue()))
                .forEach(entry -> list.add(entry.getKey()));
        return list;
    }

    public static void finaliserCommand(int id_magasin, int id_commande, String moyen_livration) throws SQLException {
        if(!moyen_livration.equals("retrait en magasin") && !moyen_livration.equals("livrasion")){}
        String date_livrasion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String query = String.format(
                """
                        INSERT livrasion (moyen_livrasion, date_livrasion, id_commande,id_magasin)
                        VALUES ('%s', '%s', %d, '%d'); """, moyen_livration, date_livrasion, id_commande, id_magasin);
        Connect.executeUpdate(query);
        Connect.closeConnexion();
    }

    }
