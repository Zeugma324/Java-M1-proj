package Requets;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class USyan {

    static String url = "jdbc:mysql://sql7.freesqldatabase.com:3306/sql7756463";
    static String user = "sql7756463";
    static String mdp = "iFgwWVZFHW";

    //US01 Je veux visualiser les détails d'un produit : prix unitaire, prix au kg, nutriscore, libellé article, poids, conditionnement, ...
    public static void VisualiserProduit(int productId) {
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

    // US21 sans steam
    public static void CommandeFrePro(int IdUser) {
        String query = "SELECT p.Id_produit, p.name, count(Id_commande) as nombreCO\n" +
                "FROM produit p, panier pa, utilisateur u, PanierCommande PC\n" +
                "WHERE p.Id_produit=pa.Id_produit\n" +
                "AND pa.Id_user=u.Id_user\n" +
                "AND pa.Id_panier=PC.panier_id\n" +
                "GROUP BY p.Id_produit, p.name\n" +
                "ORDER BY count(Id_commande) DESC\n";

        try (Connection con = DriverManager.getConnection(url, user, mdp);
             Statement stm = con.createStatement()) {
            ResultSet resultSet = stm.executeQuery(query);

            System.out.println("Liste des produits commandés le plus fréquemment :");
            boolean hasResults = false;

            while (resultSet.next()) {
                hasResults = true;
                int idProduit = resultSet.getInt("Id_produit");
                String name = resultSet.getString("name");
                int nombreCommandes = resultSet.getInt("nombreCO");

                System.out.println("- Produit ID : " + idProduit +
                        ", Nom : " + name +
                        ", Nombre de commandes : " + nombreCommandes);
            }
            if (!hasResults) {
                System.out.println("Aucun produit trouvé pour cet utilisateur.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'exécution de la requête.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        VisualiserProduit(3);
        CommandeFrePro(34);
    }

}

