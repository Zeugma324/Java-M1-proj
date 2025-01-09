package Requets;

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







    public static void main(String[] args) {
        // us0.1 Je veux rechercher un produit par mot-clé
        String motCle = "Air";
        rechercherProduitParMotCle(motCle);
        // us1.5 Je veux reprendre un panier en cours afin de finaliser mes achats.
        int userID = 1;
        PanierEnCours(userID);



    }
}


