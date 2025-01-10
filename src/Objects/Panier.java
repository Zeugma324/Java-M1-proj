package Objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import connexion.Connect;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Panier {

    private List<Produit> produit = new ArrayList<>();
    private int id;
    private String time;
    private User user;

    public Panier(int id, User user) {
        this.id = id;
        this.time = new String(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        this.user = user;
    }

    public Produit getProduit(int i) {
        return this.produit.get(i);
    }

    public List<Produit> getListProduit(){
        return this.produit;
    }

    public void addProduit(Produit prd) throws SQLException {
        this.produit.add(prd);
        String query = "INSERT INTO panier VALUES (" + this.id + ", " + prd.getId() + ", " + prd.getQteStocke() + ", '" + this.time + "' , NULL, " + user.getId() + ")";
        Connect.executeUpdate(query);
    }

    public void removeProduit(Produit prd) throws SQLException{
        this.produit.remove(prd);
        String query = "DELETE FROM Panier WHERE Id_produit = " + prd.getId() + " AND Id_panier = " + this.id + " AND Id_user = " + user.getId() + " AND Date_debut = '" + this.time + "' )";
        Connect.executeUpdate(query);
    }

    public Produit getReplacment(Produit prd) throws SQLException {
        List<Produit> base_list = new ArrayList<>();
        String str = "SELECT p.*, pa.qte_produit FROM panier pa JOIN produit p ON pa.Id_produit = p.Id_produit WHERE pa.Id_user = " + user.getId() + " AND pa.Date_fin IS NOT NULL";
        ResultSet res = Connect.executeQuery(str);
        while(res.next()) {
            Produit prod = new Produit(res.getInt("Id_produit"),res.getString("name"),res.getDouble("ratings"), res.getInt("no_of_ratings"), res.getInt("discount_price"), res.getInt("actual_price"), res.getInt("category"), res.getInt("qte_produit"));
            base_list.add(prod);
        }
        int cat_prd = prd.getCategory();
        Optional<Integer> bestCat = base_list.stream()
                .collect(Collectors.groupingBy(p -> p.getCategory(), Collectors.counting()))
                .entrySet().stream()
                .max((e1, e2) -> Long.compare(e1.getValue(), e2.getValue()))
                .map(e -> e.getKey());

        String end_query = "SELECT * FROM produit WHERE category = " + bestCat + " ORDER BY RAND() LIMIT 1";
        ResultSet res_2 = Connect.executeQuery(end_query);
        while(res_2.next()) {
            Produit new_prod = new Produit(res_2.getInt("Id_produit"),res_2.getString("name"),res_2.getDouble("ratings"), res_2.getInt("no_of_ratings"), res_2.getInt("discount_price"), res_2.getInt("actual_price"), res_2.getInt("category"), res_2.getInt("qte_produit"));
            return new_prod;
        }
        return null;
    }

}