package Managers;

import BD_Connect.PanierBD;
import BD_Connect.ProduitBD;
import Objects.Panier;
import Objects.Produit;
import Objects.User;
import connexion.Connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PanierManager {

    public static void getReplacment(Panier panier,Produit prd) throws SQLException {
        PanierBD.removeProduitFromPanier(panier,prd);
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
            PanierBD.addProduitToPanier(panier,new_prod,1);
            System.out.println(new_prod.toString());
        }
    }
}
