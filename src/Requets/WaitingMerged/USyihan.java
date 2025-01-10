package Requets.WaitingMerged;

import java.sql.*;

public class USyihan {
    static String url = "jdbc:mysql://sql7.freesqldatabase.com:3306/sql7756463";
    static String user = "sql7756463";
    static String mdp = "iFgwWVZFHW";
    // us0.2 recherche produits par mot-cle
    public static void rechercherProduitParMotCle(String motCle) {
        // SQL查询
        String sql02 = "SELECT P.name FROM produit P WHERE P.name LIKE ?";

        // 使用 Connect 类来获取连接
        try (Connection con = DriverManager.getConnection(url, user, mdp);
             PreparedStatement statement = con.prepareStatement(sql02)) {

            // 设置参数 (模糊匹配)
            statement.setString(1, "%" + motCle + "%");

            // 执行查询
            ResultSet resultSet = statement.executeQuery();

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

    //us3.3 修改产品库存、类别、客户的数据
    //显示产品原始库存数量
    public static void QteStock(int produitID){
        String sql = "SELECT Id_produit, quantity FROM stock WHERE Id_produit = ?";

        try (Connection con = DriverManager.getConnection(url, user, mdp);
             PreparedStatement statement = con.prepareStatement(sql)) {
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
        try (Connection con = DriverManager.getConnection(url, user, mdp);
             PreparedStatement statement = con.prepareStatement(sql)) {
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

    //修改某类别产品的销量










    public static void main(String[] args) {
        // us0.1 Je veux rechercher un produit par mot-clé
        String motCle = "Air";
        rechercherProduitParMotCle(motCle);
        // us1.5 Je veux reprendre un panier en cours afin de finaliser mes achats.
        int userID = 1;
        PanierEnCours(userID);

        //us3.3
            //显示原始库存
        int produitID = 1;
        System.out.println("Original information : ");
        QteStock(produitID);
            //显示修改更新后的数据
        int QteModifier = 10;
        System.out.println("Apres modifier: ");
        QteStockModifier(produitID, QteModifier);
        QteStock(produitID);








    }
}


