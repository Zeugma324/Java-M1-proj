package Requets.WaitingMerged;
import java.sql.ResultSet;
import java.sql.SQLException;

import connexion.Connect;

public class Test_Stock {

    public static void afficherQuantiteEnStock(int idProd) throws SQLException {
        String query = "SELECT s.quantity " +
                       "FROM stock s " +
                       "JOIN produit p ON s.id_produit = p.id_produit " +
                       "WHERE p.id_produit = " + idProd;

        ResultSet result = Connect.executeQuery(query);

        System.out.println("Quantité en stock pour le produit ID " + idProd + ":");
        if (result.next()) {
            int quantity = result.getInt("quantity");
            System.out.println("Quantité : " + quantity);
        } else {
            System.out.println("Aucun produit trouvé avec cet ID.");
        }}
        
        public static void main(String[] args) throws SQLException {
        	
        afficherQuantiteEnStock(1);
}}
