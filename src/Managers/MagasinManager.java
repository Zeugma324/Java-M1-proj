package Managers;

import connexion.Connect;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
    public static void main(String[] args) throws SQLException, IOException {
        AVGTempPrepareCom(1);
    }

    }
