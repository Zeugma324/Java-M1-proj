package BD_Connect;


import Objects.*;
import connexion.Connect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class PanierBD {

    public static Panier loadPanierByUser(User user) throws SQLException {
        String query = "SELECT * FROM Panier "
                + "WHERE id_user = " + user.getId() + " "
                + "AND Datefin IS NULL ;";
        if(Connect.recordExists(query)){
            ResultSet res = Connect.executeQuery(query);
            if(res.next()){
                int id = res.getInt("id_panier");
                String start_time = res.getString("Date_debut");
                TreeMap<Produit, Integer> produits = new TreeMap<>();
                while(res.next()){
                    produits.put(ProduitBD.loadProduit(res.getInt("id_produit")), res.getInt("quantity"));
                }
                Panier panier = new Panier(id, produits, user, start_time);
                return panier;
            }
        }
        return new Panier(calculateID(),user);
    }

    private static int calculateID () throws SQLException {
        String query = "SELECT MAX(Id_panier) FROM panier";
        ResultSet res = Connect.executeQuery(query);
        if(res.next()) {
            return res.getInt("MAX(Id_panier)") + 1;
        }
        return 1;
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
                    " AND Id_user = " + panier.getUser().getId();
            Connect.executeUpdate(query);
        } else {
            panier.getListProduit().put(prod, quantity);

            String query = "INSERT INTO Panier (Id_panier, Id_produit, qte_produit, Date_debut, Date_fin, Id_user) " +
                    "VALUES (" + panier.getId() + ", " + prod.getId() + ", " + quantity + ", '" + panier.getStartTime() + "', NULL, " + panier.getUser().getId() + ")";
            Connect.executeUpdate(query);
        }
    }

    public void removeProduitFromPanier(Panier panier, Produit prd) throws SQLException {
        panier.getListProduit().remove(prd);
        String query = "DELETE FROM Panier WHERE Id_produit = " + prd.getId() + " AND Id_panier = " + panier.getId() + " AND Id_user = " + panier.getUser().getId() + " AND Date_debut = '" + panier.getStartTime() + "' )";
        Connect.executeUpdate(query);
    }

    public void modifiereProduitPanier(Panier panier, Produit prd, int quantity) throws SQLException {
        panier.getListProduit().replace(prd, quantity);
        String query = "UPDATE Panier SET qte_produit = " + quantity +
                " WHERE Id_panier = " + panier.getId() +
                " AND Id_produit = " + prd.getId() +
                " AND Id_user = " + panier.getUser().getId();
    }

    public void annulerPanier(Panier panier) throws SQLException {
        String query = "DELETE FROM Panier WHERE Id_user = " + panier.getUser().getId() +
                " AND Date_debut = '" + panier.getStartTime() + "' )";
        Connect.executeUpdate(query);
        panier.getListProduit().clear();
    }

    //waiting to be connected with commandJava
    public void validerPanier(Panier panier) throws SQLException {
        panier.setEndTime(new String(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        panier.setActive(false);

        String str_panier = "UPDATE panier SET Date_fin = " + panier.getEndTime() + " WHERE Id_panier = " + panier.getId();
        Connect.executeUpdate(str_panier);
        String str_command_table = "INSERT INTO commande (Date_commande) VALUES (" + panier.getEndTime() + " )";
        String str_association_table = "INSERT INTO PanierCommande (panier_id, Id_commande) VALUES (" + panier.getId() +
                " , SELECT Id_commande FROM commande WHERE Date_commande = " + panier.getEndTime() + " )";
        Connect.executeUpdate(str_command_table);
        Connect.executeUpdate(str_association_table);
    }

    public void getReplacment(Panier panier,Produit prd) throws SQLException {
        removeProduitFromPanier(panier,prd);
        List<Produit> base_list = new ArrayList<>();
        TreeMap<String, Integer> cat_map = new TreeMap<>();
        String str = "SELECT * " +
                "FROM panier pa JOIN produit p ON pa.Id_produit = p.Id_produit " +
                "JOIN categories c ON p.category = c.Id_cat " +
                "WHERE pa.Id_user = " + panier.getUser().getId() + " AND pa.Date_fin IS NOT NULL";
        ResultSet res = Connect.executeQuery(str);
        while (res.next()) {
            Produit prod = new Produit(res.getInt("Id_produit"), res.getString("name"),
                    res.getDouble("ratings"), res.getInt("no_of_ratings"),
                    res.getInt("discount_price"), res.getInt("actual_price"),
                    res.getInt("category"));
            base_list.add(prod);
            cat_map.put(res.getString("sub_category"), res.getInt("category"));
        }
        String cat_prd = prd.getSub_category();
        Optional<Integer> bestCat = base_list.stream()
                .collect(Collectors.groupingBy(
                        p -> cat_map.getOrDefault(p.getSub_category(), -1),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .max((e1, e2) -> Long.compare(e1.getValue(), e2.getValue()))
                .map(e -> e.getKey());

        String end_query = "SELECT * FROM produit WHERE category = " + bestCat + " ORDER BY RAND() LIMIT 1";
        ResultSet res_2 = Connect.executeQuery(end_query);
        while(res_2.next()) {
            Produit new_prod = new Produit(
                    res_2.getInt("Id_produit"),
                    res_2.getString("name"),
                    res_2.getDouble("ratings"),
                    res_2.getInt("no_of_ratings"),
                    res_2.getInt("discount_price"),
                    res_2.getInt("actual_price"),
                    res_2.getInt("category"));
            addProduitToPanier(panier,new_prod,1);
            System.out.println(new_prod.toString());
        }
    }
}