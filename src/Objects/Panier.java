package Objects;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import connexion.Connect;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Panier {

    private int id;
    private Map<Produit, Integer> produits = new TreeMap<>();
    private String start_time;
    private User user;
    private String end_time;
    private boolean isactive = true;

    //create un nouvean panier pour un utilisateur quand le vielle panier est deja validee
    private Panier( User user, int idproduit ) throws SQLException {
        this.id = calculateID();
        this.start_time = new String(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        this.user = user;
        produits.put(Produit.findProduit(idproduit), 1);
        String query = "INSERT INTO panier VALUES (" + this.id + ", " + idproduit + ", 1, '" + this.start_time + "' , NULL, " + user.getId() + ")";
    }

    Panier(int id_panier) throws SQLException, NoSuchAlgorithmException {
        String query = "SELECT * FROM panier WHERE Id_panier = " + id_panier + " AND Date_fin IS NULL";
        try(Connection conn = Connect.getConnexion();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query) ){
            if(res.next()) {
                this.id = res.getInt("Id_panier");
                this.start_time = res.getString("Date_debut");
                this.end_time = res.getString("Date_fin");
                this.user = User.findUtilisateur(res.getString("email"), res.getString("mot_de_passe"));
                isactive = false;
            }
            while(res.next()) {
                Produit prod = Produit.findProduit(res.getInt("Id_produit"));
                int qte = res.getInt("qte_produit");
                produits.put(prod, qte);
            }
        }
    }
    public void addPreviousPanier(Panier oldPanier) throws SQLException {
        for(Map.Entry<Produit, Integer> entry : oldPanier.getListProduit().entrySet()) {
            Produit prod = entry.getKey();
            int qte = entry.getValue();
            if(this.produits.containsKey(prod)) {
                int qte_old = this.produits.get(prod);
                this.produits.put(prod, qte_old + qte);
            }else{
                this.produits.put(prod, qte);
            }
        }

    }
    //put an already existed panier into java
    Panier(User user, Boolean haveValidPanier) throws SQLException {
        if(haveValidPanier) {
            String query = "SELECT * FROM panier WHERE Id_user = " + user.getId() + " AND Date_fin IS NULL";
            try (Connection conn = Connect.getConnexion();
                 Statement stmt = conn.createStatement();
                 ResultSet res = stmt.executeQuery(query)) {
                if (res.next()) {
                    this.id = res.getInt("Id_panier");
                    this.start_time = res.getString("Date_debut");
                    this.end_time = res.getString("Date_fin");
                    this.user = user;
                }
                while (res.next()) {
                    Produit prod = Produit.findProduit(res.getInt("Id_produit"));
                    int qte = res.getInt("qte_produit");
                    produits.put(prod, qte);
                }
            }
        }else{
            System.out.println("Votre panier est vide ! ");
            this.id = calculateID();
            this.user = user;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Panier panier = (Panier) o;
        return id == panier.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    private static int calculateID () throws SQLException {
        String query = "SELECT MAX(Id_panier) FROM panier";
        ResultSet res = Connect.executeQuery(query);
        if(res.next()) {
            return res.getInt("MAX(Id_panier)") + 1;
        }
        return 1;
    }

    public Map<Produit, Integer> getListProduit(){
        return this.produits;
    }

    public void addProduit(Produit prd) throws SQLException {
//        Optional<Produit> search_prod = this.produit.stream()
//                .filter(p -> p.getId() == prd.getId())
//                .findFirst();
//        if(search_prod.isPresent()) {
//            search_prod.get().setQteStocke(search_prod.get().getQteStocke()+1);
//            Connect.executeUpdate("UPDATE panier SET qte_produit = qte_produit + 1 WHERE Id_produit = " + prd.getId() + " AND Id_panier = " + this.id + " AND Id_user = " + user.getId() + " AND Date_debut = '" + this.start_time + "' )");
//        }
//        else {
//            this.produit.add(prd);
//            String query = "INSERT INTO panier VALUES (" + this.id + ", " + prd.getId() + ", " + prd.getQteStocke() + ", '" + this.start_time + "' , NULL, " + user.getId() + ")";
//            Connect.executeUpdate(query);
//        }
        if (produits.isEmpty()){
            this.start_time = new String(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if(this.produits.containsKey(prd)) {
            int qte = this.produits.get(prd);
            this.produits.put(prd, qte + 1);
            String query = "INSERT INTO panier VALUES (" + this.id + ", " + prd.getId() + ", "+ qte +", '" + this.start_time + "' , NULL, " + user.getId() + ")";
            Connect.executeUpdate(query);
        }
        else {
            this.produits.put(prd, 1);
            String query = "INSERT INTO panier VALUES (" + this.id + ", " + prd.getId() + ", 1, '" + this.start_time + "' , NULL, " + user.getId() + ")";
            Connect.executeUpdate(query);
        }
    }

    public void addProduit(Produit prd, int qte_new) throws SQLException {
        if (produits.isEmpty()) {
            this.start_time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        if (produits.containsKey(prd)) {
            int currentQuantity = produits.get(prd);
            int updatedQuantity = currentQuantity + qte_new;
            produits.put(prd, updatedQuantity);

            String query = "UPDATE Panier SET qte_produit = " + updatedQuantity +
                    " WHERE Id_panier = " + this.id +
                    " AND Id_produit = " + prd.getId() +
                    " AND Id_user = " + user.getId();
            Connect.executeUpdate(query);
        } else {
            produits.put(prd, qte_new);

            String query = "INSERT INTO Panier (Id_panier, Id_produit, qte_produit, Date_debut, Date_fin, Id_user) " +
                    "VALUES (" + this.id + ", " + prd.getId() + ", " + qte_new + ", '" + this.start_time + "', NULL, " + user.getId() + ")";
            Connect.executeUpdate(query);
        }
    }


    public void minusProduit(int id_prd) throws SQLException {
        Produit prd = Produit.findProduit(id_prd);
        if (this.produits.containsKey(prd)) {
            int qte = this.produits.get(prd);

            if (qte == 1) {
                removeProduit(id_prd);
            } else {
                this.produits.put(prd, qte - 1);
                String query = "UPDATE Panier SET qte_produit = qte_produit - 1 WHERE Id_produit = " + prd.getId() +
                        " AND Id_panier = " + this.id +
                        " AND Id_user = " + user.getId();
                Connect.executeUpdate(query);
            }
        } else {
            System.out.println("Ce produit n'est pas dans le panier !");
        }
    }

    public void removeProduit(int id_prd) throws SQLException{
        this.produits.remove(Produit.findProduit(id_prd));
        String query = "DELETE FROM Panier WHERE Id_produit = " + id_prd + " AND Id_panier = " + this.id + " AND Id_user = " + user.getId() + " AND Date_debut = '" + this.start_time + "' )";
        Connect.executeUpdate(query);
    }

    public void getReplacment(int id_prd) throws SQLException {
        removeProduit(id_prd);
        Produit prd = Produit.findProduit(id_prd);
        List<Produit> base_list = new ArrayList<>();
        TreeMap<String, Integer> cat_map = new TreeMap<>();
        String str = "SELECT * " +
                "FROM panier pa JOIN produit p ON pa.Id_produit = p.Id_produit " +
                "JOIN categories c ON p.category = c.Id_cat " +
                "WHERE pa.Id_user = " + user.getId() + " AND pa.Date_fin IS NOT NULL";
        ResultSet res = Connect.executeQuery(str);
        while (res.next()) {
            Produit prod = new Produit(
                    res.getInt("Id_produit"),
                    res.getString("name"),
                    res.getDouble("ratings"),
                    res.getInt("no_of_ratings"),
                    res.getInt("discount_price"),
                    res.getInt("actual_price"),
                    res.getInt("category"),
                    res.getInt("qte_produit")
            );
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

    // il faut ajouter le check on si le stockage est suffi
    public void affichier(){
        produits.entrySet().stream()
                .forEach(entry ->
                        System.out.println("Produit Id: " + entry.getKey().getId() + " Produit nom : " + entry.getKey().getName() + ", qte : " + entry.getValue()));

    }

    public void modifierPanier(int id_prd, int value) throws SQLException {
        Produit prd = Produit.findProduit(id_prd);
        if (this.produits.containsKey(prd)) {
            this.produits.put(prd, value);
            String query = "UPDATE Panier SET qte_produit = " + value +
                    " WHERE Id_produit = " + prd.getId() +
                    " AND Id_panier = " + this.id +
                    " AND Id_user = " + user.getId();
            Connect.executeUpdate(query);
        } else {
            System.out.println("Ce produit n'est pas dans le panier !");
        }
    }

    public void annulerPanier() throws SQLException {
        String query = "DELETE FROM Panier WHERE Id_user = " + user.getId() + " AND Date_debut = '" + this.start_time + "' )";
        Connect.executeUpdate(query);
        produits.clear();
    }
}