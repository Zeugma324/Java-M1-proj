package Requets;

import java.sql.*;
import javax.swing.*;
import connexion.Connect;

public class Produits {


    // US 0.1 Visualiser les détails d'un produit
    public static void visualiser(int idProd) {
        String libelle = "";
        String main_category = "";
        String sub_category = "";
        double rating = 0.0;
        int nb_reviews = 0;
        int discounted_price = 0;
        int actual_price = 0;
        int discount_percentage;

        String where = "WHERE id_produit = " + idProd;
        String query_all = "SELECT * FROM produit JOIN categories ON produit.category = categories.Id_cat";

        try (Connection con = Connect.getConnection();
             Statement stm = con.createStatement()) {

            ResultSet res_all = stm.executeQuery(query_all + " " + where);

            if (res_all.next()) {
                libelle = res_all.getString("name");
                main_category = res_all.getString("main_category");
                sub_category = res_all.getString("sub_category");
                rating = res_all.getDouble("ratings");
                nb_reviews = res_all.getInt("no_of_ratings");
                discounted_price = res_all.getInt("discount_price");
                actual_price = res_all.getInt("actual_price");
            }

            if (actual_price != 0) {
                discount_percentage = discounted_price * 100 / actual_price;
            } else {
                discount_percentage = 0;
            }

            System.out.println("Produit libelle : " + libelle);
            System.out.println("Produit main_category : " + main_category);
            System.out.println("Produit sub_category : " + sub_category);
            System.out.println("Produit rating : " + rating);
            System.out.println("Produit nb_reviews : " + nb_reviews);
            System.out.println("Produit discounted_price : " + discounted_price);
            System.out.println("Produit actual_price : " + actual_price);
            System.out.println("Produit discount_percentage : " + discount_percentage + "%");

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des détails du produit.");
            e.printStackTrace();
        }
    }

<<<<<<< Updated upstream
    // US 0.4 Trier une liste de produits
    public static void trierProduits(int idCat, String trier_par, String trier_ord) {
        String where = "WHERE category = " + idCat;
        String query = "SELECT name FROM produit JOIN categories ON produit.category = categories.Id_cat";
        String trier = "ORDER BY " + trier_par + " " + trier_ord;

        try (Connection con = Connect.getConnection();
             Statement stm = con.createStatement()) {

            ResultSet res_all = stm.executeQuery(query + " " + where + " " + trier);
=======
    //US 0.4 Je veux trier une liste de produits
    public static void trierProduits(int idCat) throws SQLException{
    	 String where = "WHERE categories.Id_cat = " + idCat;
         String query = "SELECT produit.name, produit.brand, produit.prixUnitP, produit.ratings, produit.quantity " +
                        "FROM produit " +
                        "JOIN categories ON produit.category = categories.Id_cat " +
                        where;
>>>>>>> Stashed changes

            int count = 1;
            while (res_all.next()) {
                System.out.print(count + ". ");
                System.out.println(res_all.getString("name"));
                count++;
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors du tri des produits.");
            e.printStackTrace();
        }
    }
<<<<<<< Updated upstream
=======
    public static void main(String[] args) throws SQLException {
        // exemple de utilisation de US 0.1
    	//visualiser(3);
>>>>>>> Stashed changes

    public static void trier(int idCat) {
        JPanel panel = new JPanel();

        // Options de tri
        String[] options = {"libelle", "rating", "price"};
        JComboBox<String> comboBox = new JComboBox<>(options);
        panel.add(new JLabel("Trier par :"));
        panel.add(comboBox);

        // Ordres de tri
        String[] ordre = {"descending","ascending"};
        JComboBox<String> comboBox2 = new JComboBox<>(ordre);
        panel.add(new JLabel("Ordre :"));
        panel.add(comboBox2);

        int result = JOptionPane.showConfirmDialog(null, panel, "Comment trier ?", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String trier = comboBox.getSelectedItem().toString();
            String order = comboBox2.getSelectedItem().toString();

            String trier_par = switch (trier) {
                case "libelle" -> "name";
                case "rating" -> "ratings";
                case "price" -> "discount_price";
                default -> "ratings";
            };

            String trier_ord = switch (order) {
                case "ascending" -> "ASC";
                case "descending" -> "DESC";
                default -> "";
            };

            trierProduits(idCat, trier_par, trier_ord);
        }
    }

    public static void main(String[] args) {
        // Exemple d'utilisation de US 0.1
        visualiser(3);

        // Exemple d'utilisation de US 0.4
        trier(1);
    }
}
