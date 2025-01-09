package Requets;

import java.sql.*;

public class Panier {

    static String url = "jdbc:mysql://sql7.freesqldatabase.com:3306/sql7756463";
    static String user = "sql7756463";
    static String mdp = "iFgwWVZFHW";

    /*
    US 1.6
    Je veux choisir un produit de remplacement parmi une liste
    qui m'est proposée correspondant à mes habitudes de consommation
    afin de pallier à l'indisponibilité du produit que j'ai choisi.
     */
    static void recommandProduit(int idProd){
        String query = "SELECT id_produit, name, ratings, discount_price FROM produit JOIN categories ON produit.category = categories.Id_cat";
        String where = "WHERE category = (SELECT category FROM produit WHERE id_produit = " + idProd + ")";
        String add = "ORDER BY ratings DESC limit 15";
        try (Connection con = DriverManager.getConnection(url, user, mdp);
             Statement stm = con.createStatement();)
        {
            ResultSet res = stm.executeQuery(query + " " + where + " " + add);
            int nb_colonnes = res.getMetaData().getColumnCount();

            for(int i = 1; i <= nb_colonnes; i++) {
                System.out.print(res.getMetaData().getColumnName(i) + "\t");
            }
            System.out.println();
            while(res.next()){
                for(int i = 1; i <= nb_colonnes; i++) {
                    System.out.print(res.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Requete/Syntaxe incorrect");
            e.printStackTrace();
        }
    }



    static void selectProduit(){}
    public static void main(String[] args) {
        recommandProduit(2);
    }
}
