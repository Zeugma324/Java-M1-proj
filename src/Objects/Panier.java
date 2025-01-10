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
    private String start_time;
    private User user;
    private String end_time;
    private boolean isactive = true;

    public Panier(int id, User user) {
        this.id = id;
        this.start_time = new String(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        this.user = user;
    }

    public Produit getProduit(int i) {
        return this.produit.get(i);
    }

    public List<Produit> getListProduit(){
        return this.produit;
    }

    public void addProduit(Produit prd) throws SQLException {
        Optional<Produit> search_prod = this.produit.stream()
                .filter(p -> p.getId() == prd.getId())
                .findFirst();
        if(search_prod.isPresent()) {
            search_prod.get().setQteStocke(search_prod.get().getQteStocke()+1);
            Connect.executeUpdate("UPDATE panier SET qte_produit = qte_produit + 1 WHERE Id_produit = " + prd.getId() + " AND Id_panier = " + this.id + " AND Id_user = " + user.getId() + " AND Date_debut = '" + this.start_time + "' )");
        }
        else {
            this.produit.add(prd);
            String query = "INSERT INTO panier VALUES (" + this.id + ", " + prd.getId() + ", " + prd.getQteStocke() + ", '" + this.start_time + "' , NULL, " + user.getId() + ")";
            Connect.executeUpdate(query);
        }
    }

    public void removeProduit(Produit prd) throws SQLException{
        this.produit.remove(prd);
        String query = "DELETE FROM Panier WHERE Id_produit = " + prd.getId() + " AND Id_panier = " + this.id + " AND Id_user = " + user.getId() + " AND Date_debut = '" + this.start_time + "' )";
        Connect.executeUpdate(query);
    }

    public void getReplacment(Produit prd) throws SQLException {
        removeProduit(prd);
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
            addProduit(new_prod);
            System.out.println(new_prod.toString());
        }
    }

    public void validate() throws SQLException {
        this.end_time = new String(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        isactive = false;
        String str_panier = "UPDATE panier SET Date_fin = " + this.end_time + " WHERE Id_panier = " + this.id;
        Connect.executeUpdate(str_panier);
        String str_command_table = "INSERT INTO commande (Date_commande) VALUES (" + this.end_time + " )";
        String str_association_table = "INSERT INTO PanierCommande (panier_id, Id_commande) VALUES (" + this.id +
                " , SELECT Id_commande FROM commande WHERE Date_commande = " + this.end_time + " )";
        Connect.executeUpdate(str_command_table);
        Connect.executeUpdate(str_association_table);
    }

    public void delete() throws SQLException{
        this.produit.clear();
        Connect.executeUpdate("DELETE FROM panier WHERE Id_panier = " + this.id);
    }
}