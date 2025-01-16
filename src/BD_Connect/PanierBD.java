package BD_Connect;

import Objects.*;
import connexion.Connect;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static BD_Connect.ProduitBD.catAndId_cat;

public class PanierBD {

//    public static Panier loadPanierByUser(User user) throws SQLException {
//        String query = "SELECT pa.*, p.* " +
//                "FROM panier pa JOIN produit p ON pa.id_produit = p.id_produit " +
//                "WHERE id_user = " + user.getIdUser() + " " +
//                "AND Date_fin IS NULL ;";
//        ResultSet res = Connect.executeQuery(query);
//        if (res.next()) {
//            int id = res.getInt("id_panier");
//            String start_time = res.getString("Date_debut");
//            int id_magasin = res.getInt("id_magasin");
//        }
//        Map<Produit, Integer> produits = new TreeMap<>();
//        Map<Produit, Integer> produitMagasins = new TreeMap<>();
//        while (res.next()) {
//            int id = res.getInt("id_produit");
//            String libelle = res.getString("name");
//            double ratings = res.getDouble("ratings");
//            int no_of_ratings = res.getInt("no_of_ratings");
//            int discount_price = res.getInt("discount_price");
//            int actual_price = res.getInt("actual_price");
//            int categorie = res.getInt("category");
//            int qte = res.getInt("qte_produit");
//            int id_magasin = res.getInt("id_magasin");
//            Produit produit = new Produit(id,libelle,ratings,no_of_ratings,discount_price,actual_price,categorie);
//            produits.put(produit,produits.getOrDefault(produit,0) + qte);
//        }
//        Panier panier = new Panier(calculateID(),user);
//        panier.setProduits(produits);
//        panier.setProduitAndMagasin(produitMagasins);
//        return panier;
//    }

    public static Panier loadPanierByUser(User user) throws SQLException {

        String query = "SELECT pa.Id_panier, pa.Id_user, pa.Id_produit, pa.qte_produit, "
                + "       pa.Date_debut, pa.Date_fin, pa.id_magasin, "
                + "       p.id_produit, p.name, p.ratings, p.no_of_ratings, "
                + "       p.discount_price, p.actual_price, p.category "
                + "FROM panier pa "
                + "JOIN produit p ON pa.id_produit = p.id_produit "
                + "WHERE pa.Id_user = " + user.getIdUser() + " "
                + "  AND pa.Date_fin IS NULL;";
        ResultSet res = Connect.executeQuery(query);

        Map<Produit, Integer> produits = new HashMap<>();
        int panierId = -1;
        String start_time = null;
        Integer id_magasin = null;
        boolean foundAnyRow = false;

        while (res.next()) {
            foundAnyRow = true;

            if (panierId < 0) {
                panierId    = res.getInt("Id_panier");
                start_time  = res.getString("Date_debut");
                id_magasin  = res.getInt("id_magasin");
            }

            Produit produit = new Produit(
                    res.getInt("id_produit"),
                    res.getString("name"),
                    res.getDouble("ratings"),
                    res.getInt("no_of_ratings"),
                    res.getInt("discount_price"),
                    res.getInt("actual_price"),
                    res.getInt("category")
            );

            int qte = res.getInt("qte_produit");
            produits.put(produit, produits.getOrDefault(produit, 0) + qte);
        }
        res.close();

        if (!foundAnyRow) {
            int newId = calculateID();
            System.out.println("No existing Panier found. Creating new one: " + newId);
            return new Panier(newId, user);
        } else {
            Panier panier = new Panier(panierId, user);
            panier.setProduits(produits);

            panier.setStartTime(start_time);
            panier.setActive(true);

            System.out.println("Loaded existing Panier from DB, id=" + panierId);
            return panier;
        }
    }




    private static int calculateID () throws SQLException {
        String query = "SELECT MAX(Id_panier) FROM panier";
        ResultSet res = Connect.executeQuery(query);
        if(res.next()) {
            return res.getInt("MAX(Id_panier)") + 1;
        }
        return 1;
    }

    public static void addProduitToPanier(Panier panier, Produit prod, int quantity, int id_magasin) throws SQLException {
        if (panier.getListProduit().isEmpty()) {
            panier.setStartTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        if (panier.getListProduit().containsKey(prod)) {
            int currentQuantity = panier.getListProduit().get(prod);
            int updatedQuantity = currentQuantity + quantity;
            panier.getListProduit().put(prod, updatedQuantity);

            String query = "UPDATE panier SET qte_produit = " + updatedQuantity + ", id_magasin = " + id_magasin +
                    " WHERE Id_panier = " + panier.getId() +
                    " AND Id_produit = " + prod.getId() +
                    " AND Id_user = " + panier.getUser().getIdUser();
            Connect.executeUpdate(query);
        } else {
            panier.getListProduit().put(prod, quantity);
            String query = "INSERT INTO panier (Id_panier, Id_produit, qte_produit, Date_debut, Date_fin, Id_user, id_magasin) " +
                    "VALUES (" + panier.getId() + ", " + prod.getId() + ", " + quantity + ", '" + panier.getStartTime() + "', NULL, " + panier.getUser().getIdUser() +"," + id_magasin+ ")";
            Connect.executeUpdate(query);
        }
    }



    public static void removeProduitFromPanier(Panier panier, Produit prd) throws SQLException {
        panier.getListProduit().remove(prd);
        panier.getListProduitAndMagasin().remove(prd);
        String query = "DELETE FROM Panier WHERE Id_produit = " + prd.getId() + " AND Id_panier = " + panier.getId() + " AND Id_user = " + panier.getUser().getIdUser() + " AND Date_debut = '" + panier.getStartTime() + "' )";
        Connect.executeUpdate(query);
    }

    public void modifiereProduitPanier(Panier panier, Produit prd, int quantity) throws SQLException {
        panier.getListProduit().replace(prd, quantity);
        String query = "UPDATE Panier SET qte_produit = " + quantity +
                " WHERE Id_panier = " + panier.getId() +
                " AND Id_produit = " + prd.getId() +
                " AND Id_user = " + panier.getUser().getIdUser();
    }

    public static void annulerPanier(Panier panier) throws SQLException {
        String query = "DELETE FROM panier WHERE id_panier = " + panier.getId();
        Connect.executeUpdate(query);
        panier.getListProduit().clear();
        panier.getListProduitAndMagasin().clear();
    }

    public static void annulerPanier(User user) throws SQLException {
        String query = "DELETE FROM panier WHERE id_panier = " + user.getPanier().getId();
        Connect.executeUpdate(query);
        user.getPanier().getListProduit().clear();
        user.getPanier().getListProduitAndMagasin().clear();
    }

    public static void validerPanier(Panier panier) throws SQLException {
        panier.setEndTime("'" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'");
        panier.setActive(false);

        String str_panier = "UPDATE panier SET Date_fin = " + panier.getEndTime() + " WHERE Id_panier = " + panier.getId();
        Connect.executeUpdate(str_panier);
        String str_command_table = "INSERT INTO commande (Date_commande) VALUES (" + panier.getEndTime() + " )";
        int id_commande = Connect.creationWithAutoIncrement(str_command_table);
        String str_association_table = "INSERT INTO PanierCommande VALUES (" + panier.getId() +
                " , "+ id_commande +" );";
        Connect.executeUpdate(str_association_table);
    }

    public static void importerProduit(String name_csv) throws SQLException, IOException {
        String csvFile = "src/data/" + name_csv + ".csv";
        char separator = ',';
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO produit(name, ratings, no_of_ratings, discount_price, actual_price, category) VALUES ");
        ArrayList<String[]> produitList = new ArrayList<>();

        boolean isFirstLine = true;
        for (String line : Files.readAllLines(Paths.get(csvFile))) {
            if (isFirstLine) {
                isFirstLine = false;
                continue;
            }
            String[] data = line.split(String.valueOf(separator));
            if (data.length != 7) {
                System.err.println("data pas correct : " + Arrays.toString(data));
                continue;
            }
            produitList.add(data);
        }

        produitList.forEach(produit -> {
            try {
                String libelle = produit[0];
                String main_category = produit[1];
                String sub_category = produit[2];
                double rating = Double.parseDouble(produit[3]);
                int no_of_ratings = Integer.parseInt(produit[4]);
                int discount_price = Integer.parseInt(produit[5]);
                int actual_price = Integer.parseInt(produit[6]);

                int category;
                if (catAndId_cat.containsKey(sub_category)) {
                    category = catAndId_cat.get(sub_category);
                } else {
                    category = catAndId_cat.size() + 1;
                    catAndId_cat.put(sub_category, category);

                    String insertCategoryQuery = String.format(
                            "INSERT INTO categories (Id_cat, main_category, sub_category) VALUES (%d, '%s', '%s')",
                            category, main_category, sub_category);
                    Connect.executeUpdate(insertCategoryQuery);
                }

                query.append(String.format("('%s', %.2f, %d, %d, %d, %d), ",
                        libelle, rating, no_of_ratings, discount_price, actual_price, category));
            } catch (Exception e) {
                System.err.println("Error parce que : " + Arrays.toString(produit));
                e.printStackTrace();
            }
        });

        query.delete(query.length() - 2, query.length());

        query.append(';');

        System.out.println("Final SQL Query: " + query.toString());
        Connect.executeUpdate(query.toString());
        Connect.closeConnexion();
    }

    public static Panier addPaniertoPanier(User user, Panier panier) throws SQLException {
        user.setPanier(loadPanierByUser(user));
        Map<Produit, Integer> produits = panier.getListProduit();
        produits.entrySet().stream()
                .forEach(entry -> {
                    user.getPanier().getListProduit().put(entry.getKey(), user.getPanier().getListProduit().getOrDefault(entry.getKey(), 0) + entry.getValue());
                });
        updatePanierInDB(user.getPanier());
        return user.getPanier();
    }

    public static void updatePanierInDB(Panier panier) throws SQLException {
        String deleteSQL = String.format("DELETE FROM panier WHERE id_panier = %s AND Date_fin IS NULL", panier.getId());
        Connect.executeUpdate(deleteSQL);

        int id_panier = panier.getId();
        String date_debut = panier.getStartTime().toString();
        int id_user = panier.getUser().getIdUser();
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO panier(Id_panier, Id_produit, qte_produit, Date_debut,id_user) VALUES (");
        panier.getListProduit().entrySet().stream()
                .forEach(entry -> {
                    query.append(id_panier + "," + entry.getKey().getId() + "," + entry.getValue() + ", '" + date_debut + "'," + id_user + "),");
                });
        query.deleteCharAt(query.length() - 1);
        Connect.executeUpdate(query.toString());
    }

    //afficher une liste de Panier que le user choisir
    public static ArrayList<Panier> historyPanierByUser(User user) throws SQLException {
        String query = String.format(
                "SELECT pa.id_panier, pa.id_user, pa.id_produit, pa.qte_produit, pa.Date_debut, pa.Date_fin, " +
                        " p.name, p.ratings, p.no_of_ratings, p.discount_price, p.actual_price, p.category " +
                        "FROM panier pa " +
                        "JOIN produit p ON pa.id_produit = p.id_produit " +
                        "WHERE pa.id_user = %d " +
                        "ORDER BY pa.id_panier",
                user.getIdUser()
        );
        Map<Integer, Panier> panierMap = new HashMap<>();
        try (ResultSet result = Connect.executeQuery(query)) {
            while (result.next()) {
                int idPanier = result.getInt("id_panier");
                Panier panier = panierMap.get(idPanier);
                if (panier == null) {
                    String start_time = result.getString("Date_debut");
                    String end_time = result.getString("Date_fin");
                    Map<Produit, Integer> produitsInThisPanier = new HashMap<>();

                    panier = new Panier(idPanier, produitsInThisPanier, start_time, end_time, user);
                    panier.setActive(true);

                    panierMap.put(idPanier, panier);
                }

                Produit produit = new Produit(
                        result.getInt("id_produit"),
                        result.getString("name"),
                        result.getDouble("ratings"),
                        result.getInt("no_of_ratings"),
                        result.getInt("discount_price"),
                        result.getInt("actual_price"),
                        result.getInt("category")
                );

                int qte = result.getInt("qte_produit");

                panier.getListProduit().put(
                        produit,
                        panier.getListProduit().getOrDefault(produit, 0) + qte
                );
            }
        }
        return new ArrayList<>(panierMap.values());
    }


    public static void main(String[] args) throws SQLException {
        User user = UserDB.findUserById(1);
        Produit produit = ProduitBD.loadProduit(1);
        addProduitToPanier(user.getPanier(),produit,4,2);

        validerPanier(user.getPanier());
        System.out.println(historyPanierByUser(user));
        Panier panier = historyPanierByUser(user).getFirst();
        addPaniertoPanier(user,panier);
        System.out.println(user.getPanier());
        annulerPanier(user.getPanier());
        System.out.println("Panier apres annuler" + user.getPanier());
    }
}