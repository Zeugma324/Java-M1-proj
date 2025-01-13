package BD_Connect;


import Objects.*;
import connexion.Connect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PanierBD {
    public Panier loadPanierByUser(User user) throws SQLException {
        String query = "SELECT * FROM Panier "
                + "WHERE id_user = " + user.getIdUser() + " "
                + "AND Datefin IS NULL ;";
        if(Connect.recordExists(query)){
            ResultSet res = Connect.executeQuery(query);
            if(res.next()){
                int id = res.getInt("id_panier");
                String start_time = res.getString("Date_debut");
                TreeMap<Produit, Integer> produits = new TreeMap<>();
                while(res.next()){
                    produits.put(Produit.findProduit(res.getInt("id_produit")), res.getInt("quantity"));
                }
                Panier panier = new Panier(id, produits, user, start_time);
                return panier;
            }
        }
        return null;
    }

    public void addProduitToPanier(Panier panier, Produit prod, int quantity) throws SQLException {
        if (panier.getListProduit().isEmpty()) {
            panier.setStartTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        if (panier.getListProduit().containsKey(prod)) {
            int currentQuantity = panier.getListProduit().get(prod);
            int updatedQuantity = currentQuantity + quantity;
            panier.getListProduit().put(prod, updatedQuantity);

            String query = "UPDATE Panier SET qte_produit = " + updatedQuantity +
                    " WHERE Id_panier = " + panier.getId() +
                    " AND Id_produit = " + prod.getId() +
                    " AND Id_user = " + panier.getUser().getIdUser();
            Connect.executeUpdate(query);
        } else {
            panier.getListProduit().put(prod, quantity);

            String query = "INSERT INTO Panier (Id_panier, Id_produit, qte_produit, Date_debut, Date_fin, Id_user) " +
                    "VALUES (" + panier.getId() + ", " + prod.getId() + ", " + quantity + ", '" + panier.getStartTime() + "', NULL, " + panier.getUser().getIdUser() + ")";
            Connect.executeUpdate(query);
        }
    }



}