package Requets;

import java.sql.*;
import java.util.Scanner;
import connexion.Connect;


public class USyihan {

    // us0.2 recherche produits par mot-cle
    public static void rechercherProduitParMotCle(String motCle) throws SQLException{
        String sql = "SELECT P.name FROM produit P WHERE P.name LIKE '%" + motCle + "%'";

        ResultSet result = Connect.executeQuery(sql);

        System.out.println("Produits trouvés :");
        while (result.next()) {
            String produit = result.getString("name");
            System.out.println(" " + produit);
        }

        Connect.closeConnexion();
    }


    // us1.5 reprendre panier en cours
    public static void PanierEnCours(int userID) throws SQLException{

        String sql = "SELECT id_panier, Date_debut FROM panier WHERE Id_user = " + userID + " AND Date_fin IS NULL";

        ResultSet result = Connect.executeQuery(sql);

        if (result.next()) {
            int panierID = result.getInt("id_Panier");//获取购物车id
            String DateDebut = result.getString("Date_debut"); //获取该购物车开始时间

            // 打印购物车信息
            System.out.println("Panier en cours trouvé :");
            System.out.println("ID Panier: " + panierID);
            System.out.println("Date de début: " + DateDebut);

        }else{
            System.out.println("Aucun panier en cours trouvé pour User: " + userID);
        }

        Connect.closeConnexion();


    }

    //us3.3 修改产品库存、类别、客户的数据 editer les statistique sur produits，category...
    //显示产品原始库存数量 affichier les QteStock Original
    public static void QteStock(int produitID) throws SQLException{

        String sql = "SELECT Id_produit, quantity FROM stock WHERE Id_produit = "  + produitID;

        ResultSet result = Connect.executeQuery(sql);

            if(result.next()){
                System.out.println("Quantity stock de produit " + produitID + " : ");
                System.out.println(result.getInt("quantity"));
            }else{
                System.out.println("Aucun produit trouvé");
            }

            Connect.closeConnexion();

    }
    //修改产品库存数量
    public static void QteStockModifier(int produitID, int QteModifier) throws SQLException{

        String sql = "UPDATE stock Set quantity = quantity + " + QteModifier + " WHERE Id_produit = " + produitID;

        // 执行更新 update
        Connect.executeUpdate(sql);

        System.out.println("Stock change : " + QteModifier);

        Connect.closeConnexion();

    }

    //显示某类别产品的库存数量
    public static void StockParSubCat(int catID) throws SQLException {
        String sql = "SELECT stock.Id_produit, SUM(stock.quantity) AS total_quantity, category FROM stock JOIN produit on stock.Id_produit = produit.Id_produit WHERE category = " + catID + " GROUP BY stock.Id_produit ";

        ResultSet result = Connect.executeQuery(sql);

        System.out.println("produits dans la categorie " + catID + " : ");
        int totalStock = 0;
        while (result.next()){
            int Qte = result.getInt("total_quantite");
            totalStock += Qte;
            System.out.println("Produit ID : " + result.getInt("Id_produit") + " QteStock : " + Qte);
        }
        // 显示加总数量
        System.out.println("Total stock dans la categorie " + catID + " : " + totalStock);

        Connect.closeConnexion();
    }

    //修改某类别产品的库存数量
    public static void ModifierStockParSubCat(int catID, int StockChange) throws SQLException{

        String sql = "UPDATE stock JOIN produit on stock.Id_produit = produit.Id_produit Set quantity = quantity + ? WHERE category = ?";
        Connect.executeUpdate(sql);

        System.out.println("Stock change : " + StockChange);

        Connect.closeConnexion();
    }

//将下单次数超过五次的客户设置为VIP客户
//Définir les clients qui commandent plus de cinq fois comme des clients VIP
    public static void VIPCLients() throws SQLException{
        String sql = "SELECT Id_user FROM panier JOIN PanierCommande ON panier.Id_panier = PanierCommande.panier_id GROUP by panier.Id_user HAVING COUNT(Id_commande) > 5";

        ResultSet result = Connect.executeQuery(sql);

        System.out.println("Les clients suivants sont des VIP :");

        while (result.next()){
            int userID = result.getInt("Id_user");
            System.out.println("User ID" + userID);

        }

        Connect.closeConnexion();
    }



    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("=== Menu ===");
            System.out.println("1. Rechercher un produit par mot-clé");
            System.out.println("2. Reprendre un panier en cours");
            System.out.println("3. Afficher le stock d'un produit");
            System.out.println("4. Modifier le stock d'un produit");
            System.out.println("5. Afficher le stock d'une catégorie");
            System.out.println("6. Modifier le stock d'une catégorie");
            System.out.println("7. Afficher les clients VIP");
            System.out.println("8. Exit");
            System.out.print("Entrez votre choix : ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    System.out.print("Entrez le mot-clé : ");
                    String motCle = scanner.next();
                    rechercherProduitParMotCle(motCle);
                }
                case 2 -> {
                    System.out.print("Entrez l'ID user : ");
                    int userID = scanner.nextInt();
                    PanierEnCours(userID);
                }
                case 3 -> {
                    System.out.print("Entrez l'ID produit : ");
                    int produitID = scanner.nextInt();
                    QteStock(produitID);
                }
                case 4 -> {
                    System.out.print("Entrez l'ID produit : ");
                    int produitID = scanner.nextInt();
                    System.out.print("Entrez le changement de quantité : ");
                    int QteModifier = scanner.nextInt();
                    QteStockModifier(produitID, QteModifier);
                    QteStock(produitID); // 显示更新后的库存
                }
                case 5 -> {
                    System.out.print("Entrez l'ID catégorie : ");
                    int catID = scanner.nextInt();
                    StockParSubCat(catID);
                }
                case 6 -> {
                    System.out.print("Entrez l'ID catégorie : ");
                    int catID = scanner.nextInt();
                    System.out.print("Entrez le changement de quantité : ");
                    int StockChange = scanner.nextInt();
                    ModifierStockParSubCat(catID, StockChange);
                    StockParSubCat(catID); // 显示更新后的库存
                }
                case 7 -> {
                    VIPCLients();
                }
                case 8 -> {
                    System.out.println("Exit l'application.");
                    return;
                }
                default -> System.out.println("Choix invalide !");
            }
        }


    }
}


