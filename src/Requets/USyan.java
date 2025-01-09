package Requets;


import java.sql.*;


public class USyan {
    // US 0.1 Je veux visualiser les détails d'un produit : prix unitaire, prix au kg, nutriscore, libellé article, poids, conditionnement, ...
    static String url = "jdbc:mysql://sql7.freesqldatabase.com:3306/sql7756463";
    static String user = "sql7756463";
    static String mdp = "iFgwWVZFHW";

    public static void VisualiserProduit(int productId){


        // 产品 ID（输入参数）
        // int productId = 1; // 假设要查询的产品 ID 为 1
        // 数据库连接参数
        String query = "SELECT discount_price, actual_price, ratings, name FROM produit p" +
                ",categories ca WHERE ca.Id_cat=p.category AND id_produit =" + productId;




        // 数据库连接和查询
        try (Connection con = DriverManager.getConnection(url, user, mdp);
             Statement stm = con.createStatement()) {




            ResultSet resultSet = stm.executeQuery(query);




            // 处理查询结果
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                double discount_price = resultSet.getDouble("discount_price");
                double actual_price = resultSet.getDouble("actual_price");
                String ratings = resultSet.getString("ratings");

                System.out.println("Détails du produit :");
                System.out.println("Libellé_article : " + name);
                System.out.println("discount_price : " + discount_price + "$");
                System.out.println("actual_price : " + actual_price + "$");
                System.out.println("Ratings : " + ratings);
            } else {
                System.out.println("Aucun produit trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'exécution de la requête.");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        VisualiserProduit(3);
    }

}

