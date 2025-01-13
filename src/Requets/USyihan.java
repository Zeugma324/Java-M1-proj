package Requets;

import java.sql.*;
import java.util.Scanner;

public class USyihan {
    static String url = "jdbc:mysql://sql7.freesqldatabase.com:3306/sql7756463";
    static String user = "sql7756463";
    static String mdp = "iFgwWVZFHW";
    // us0.2 recherche produits par mot-cle
    public static void rechercherProduitParMotCle(String motCle) {

        // SQL查询
        String sql02 = "SELECT P.name FROM produit P WHERE P.name LIKE '%' + motCle + '%'";

        // 使用 Connect 类来获取连接
        try (Connection con = DriverManager.getConnection(url, user, mdp);
             Statement stm = con.createStatement();){

            // 执行查询
            ResultSet resultSet = stm.executeQuery(sql02);

            // 输出结果
            System.out.println("Produits trouvés :");
            while (resultSet.next()) { // 遍历结果集
                String produit = resultSet.getString("name"); // 获取查询结果中的产品名称
                System.out.println(" " + produit); //打印每个产品名称
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // us1.5 reprendre panier en cours
    public static void PanierEnCours(int userID){

        String sql15 = "SELECT id_panier, Date_debut FROM panier WHERE Id_user = ? AND Date_fin IS NULL";

        try (Connection con = DriverManager.getConnection(url, user, mdp);
             PreparedStatement statement = con.prepareStatement(sql15)) {

            statement.setInt(1, userID); //设置查询参数

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int panierID = resultSet.getInt("id_Panier");//获取购物车id
                String DateDebut = resultSet.getString("Date_debut"); //获取该购物车开始时间

                // 打印购物车信息
                System.out.println("Panier en cours trouvé :");
                System.out.println("ID Panier: " + panierID);
                System.out.println("Date de début: " + DateDebut);

            }else{
                System.out.println("Aucun panier en cours trouvé pour User: " + userID);
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //us3.3 修改产品库存、类别、客户的数据 editer les statistique sur produits，category...
    //显示产品原始库存数量 affichier les QteStock Original
    public static void QteStock(int produitID){

        String sql = "SELECT Id_produit, quantity FROM stock WHERE Id_produit = ?";

        try (Connection connection = DriverManager.getConnection(url, user, mdp);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, produitID);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                System.out.println("Quantity stock de produit " + produitID + " : ");
                System.out.println(resultSet.getInt("quantity"));
            }else{
                System.out.println("Aucun produit trouvé");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    //修改产品库存数量
    public static void QteStockModifier(int produitID, int QteModifier){

        String sql = "UPDATE stock Set quantity = quantity + ? WHERE Id_produit = ?";
        try (Connection connection = DriverManager.getConnection(url, user, mdp);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, QteModifier);
            statement.setInt(2, produitID);

            int rowsUpdated = statement.executeUpdate();
            if(rowsUpdated > 0){
                System.out.println("Stock change : " + QteModifier);
            }else {
                System.out.println("Error! Verifiez produitID.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //显示某类别产品的库存数量
    public static void StockParSubCat(int catID){
        String sql = "SELECT stock.Id_produit, SUM(stock.quantity) AS total_quantity, category FROM stock JOIN produit on stock.Id_produit = produit.Id_produit WHERE category = ? GROUP BY stock.Id_produit ";

        try (Connection connection = DriverManager.getConnection(url, user, mdp);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, catID);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("produits dans la categorie " + catID + " : ");
            int totalStock = 0;
            while (resultSet.next()){
                int Qte = resultSet.getInt("total_quantity");
                totalStock += Qte;
                System.out.println("Produit ID : " + resultSet.getInt("Id_produit") + " QteStock : " + Qte);
            }
            // 显示加总数量
            System.out.println("Total stock dans la categorie " + catID + " : " + totalStock);

        }catch (Exception e) {
            e.printStackTrace();
        }

        }
    //修改某类别产品的库存数量
    public static void ModifierStockParSubCat(int catID, int StockChange){


        String sql = "UPDATE stock JOIN produit on stock.Id_produit = produit.Id_produit Set quantity = quantity + ? WHERE category = ?";
        try (Connection connection = DriverManager.getConnection(url, user, mdp);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, StockChange);
            statement.setInt(2, catID);

            int rowsUpdated = statement.executeUpdate();
            if(rowsUpdated > 0){
                System.out.println("Stock change : " + StockChange);
            }else {
                System.out.println("Error! Verifiez ID categorie.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//将下单次数超过五次的客户设置为VIP客户
//Définir les clients qui commandent plus de cinq fois comme des clients VIP
    public static void VIPCLients(){
        String sql = "SELECT Id_user FROM panier JOIN PanierCommande ON panier.Id_panier = PanierCommande.panier_id GROUP by panier.Id_user HAVING COUNT(Id_commande) > 5";

        try (Connection connection = DriverManager.getConnection(url, user, mdp);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet resultSet = statement.executeQuery();
            System.out.println("Les clients suivants sont des VIP :");

            while (resultSet.next()){
                int userID = resultSet.getInt("Id_user");
                System.out.println("userID");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Menu ===");
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
                    System.out.print("Entrez l'ID utilisateur : ");
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


