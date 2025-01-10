package Requets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import connexion.Connect;
public class USpierre {
    // US 0.1 Je veux visualiser les détails d'un produit : prix unitaire, prix au kg, nutriscore, libellé article, poids, conditionnement, ...
    public static void visualiserProduit(int idProd) throws SQLException {
        String query = "SELECT produit.name, produit.actual_price, produit.discount_price, " +
                "categories.main_category, categories.sub_category, stock.quantity " +
                "FROM produit " +
                "JOIN categories ON produit.category = categories.id_cat " +
                "LEFT JOIN stock ON produit.id_produit = stock.id_produit " +
                "WHERE produit.id_produit = " + idProd;
        ResultSet result = Connect.executeQuery(query);
        if (result.next()) {
            String name = result.getString("name");
            double actualPrice = result.getDouble("actual_price");
            double discountPrice = result.getDouble("discount_price");
            String mainCategory = result.getString("main_category");
            String subCategory = result.getString("sub_category");
            int stockQuantity = result.getInt("quantity");
            System.out.println("Nom du produit : " + name);
            System.out.println("Prix réel : " + actualPrice);
            System.out.println("Prix remisé : " + discountPrice);
            System.out.println("Catégorie principale : " + mainCategory);
            System.out.println("Sous-catégorie : " + subCategory);
            System.out.println("Quantité en stock : " + stockQuantity);
        } else {
            System.out.println("Aucun produit trouvé avec cet ID.");
        }
    }
    //US.0.2
    public static void rechercherProduit(String keyword) throws SQLException {
        String query = "SELECT produit.id_produit, produit.name, produit.actual_price, produit.discount_price " +
                "FROM produit " +
                "WHERE produit.name LIKE '%" + keyword + "%'";
        ResultSet result = Connect.executeQuery(query);
        System.out.println("Produits trouvés pour le mot-clé : " + keyword);
        while (result.next()) {
            int id = result.getInt("id_produit");
            String name = result.getString("name");
            double actualPrice = result.getDouble("actual_price");
            double discountPrice = result.getDouble("discount_price");
            System.out.println("ID : " + id + ", Nom : " + name + ", Prix réel : " + actualPrice + ", Prix remisé : " + discountPrice);
        }
    }
    //US.03
    public static void consulterProduitsParCategorie(int idCat) throws SQLException {
        String query = "SELECT produit.id_produit, produit.name, produit.actual_price, produit.discount_price " +
                "FROM produit " +
                "WHERE produit.category = " + idCat;
        ResultSet result = Connect.executeQuery(query);
        System.out.println("Produits dans la catégorie ID : " + idCat);
        while (result.next()) {
            int id = result.getInt("id_produit");
            String name = result.getString("name");
            double actualPrice = result.getDouble("actual_price");
            double discountPrice = result.getDouble("discount_price");
            System.out.println("ID : " + id + ", Nom : " + name + ", Prix réel : " + actualPrice + ", Prix remisé : " + discountPrice);
        }
    }
    // US 0.4 Je veux trier une liste de produits
    public static void trierProduits(int idCat) throws SQLException {
        String where = "WHERE categories.Id_cat = " + idCat;
        String query = "SELECT produit.name, produit.actual_price, produit.ratings, produit.no_of_ratings, categories.main_category " +
                "FROM produit " +
                "JOIN categories ON produit.category = categories.Id_cat " +
                where +
                " ORDER BY produit.ratings DESC";
        ResultSet res_all = Connect.executeQuery(query);
        List<Produit> produits = new ArrayList<>();
        try {
            while (res_all.next()) {
                String name = res_all.getString("name");
                double actual_price = res_all.getDouble("actual_price");
                int ratings = res_all.getInt("ratings");
                int no_of_ratings = res_all.getInt("no_of_ratings");
                String category = res_all.getString("main_category");
                Produit produit = new Produit(name, actual_price, ratings, no_of_ratings, category);
                produits.add(produit);
            }
        } finally {
            res_all.close(); // Assurez-vous de fermer le ResultSet
        }
        produits.forEach(a -> System.out.println(a));
    }
    public static void main(String[] args) throws SQLException {
        // US 0.1 - Visualisation d'un produit
        //visualiserProduit(254);
        // US 0.2 - Recherche par mot-clé
        // rechercherProduit("Télé");
        // US 0.3 - Consultation par catégorie
        //consulterProduitsParCategorie(2);

        // exemple de utilisation de US 0.4
        //trierProduits(1);
    }
}