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

    public static Panier loadPanierByUser(User user) throws SQLException {
        String query = "SELECT * FROM panier "
                + "WHERE id_user = " + user.getIdUser() + " "
                + "AND Date_fin IS NULL ;";
        if(Connect.recordExists(query)){
            ResultSet res = Connect.executeQuery(query);
            if(res.next()){
                int id = res.getInt("id_panier");
                String start_time = res.getString("Date_debut");
                int id_magasin = res.getInt("id_magasin");
                TreeMap<Produit, Integer> produits = new TreeMap<>();
                while(res.next()){
                    produits.put(ProduitBD.loadProduit(res.getInt("id_produit")), 1);
                }
                Panier panier = new Panier(id, produits, user, start_time,false);
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

    public static void addProduitToPanier(Panier panier, Produit prod, int quantity) throws SQLException {
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



    public static void removeProduitFromPanier(Panier panier, Produit prd) throws SQLException {
        panier.getListProduit().remove(prd);
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

    public void annulerPanier(Panier panier) throws SQLException {
        String query = "DELETE FROM Panier WHERE Id_user = " + panier.getUser().getIdUser() +
                " AND Date_debut = '" + panier.getStartTime() + "' )";
        Connect.executeUpdate(query);
        panier.getListProduit().clear();
    }

    //waiting to be connected with commandJava
    public static void validerPanier(Panier panier) throws SQLException {
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

    public static void importerProduit(String name_csv) throws SQLException, IOException {
        String csvFile = "src/data/" + name_csv + ".csv";
        char separator = ',';
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO produit(name, ratings, no_of_ratings, discount_price, actual_price, category) VALUES ");
        ArrayList<String[]> produitList = new ArrayList<>();

        boolean isFirstLine = true;
        for (String line : Files.readAllLines(Paths.get(csvFile))) {
            if (isFirstLine) {
                isFirstLine = false; // 跳过标题行
                continue;
            }
            String[] data = line.split(String.valueOf(separator));
            if (data.length != 7) { // 确保每行数据有 7 列
                System.err.println("Invalid data: " + Arrays.toString(data));
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
                System.err.println("Error processing produit: " + Arrays.toString(produit));
                e.printStackTrace();
            }
        });

        if (query.charAt(query.length() - 2) == ',') {
            query.delete(query.length() - 2, query.length()); // 移除最后的逗号
        }
        query.append(';');

        System.out.println("Final SQL Query: " + query.toString()); // 打印最终 SQL 查询
        Connect.executeUpdate(query.toString());
        Connect.closeConnexion();
    }

    public static void main(String[] args) throws SQLException {
        User user = UserDB.findUserById(1);
        Produit produit = ProduitBD.loadProduit(1);
        addProduitToPanier(user.getPanier(),produit,4);
        user.getPanier();
    }


    public static Panier addPaniertoPanier(User user, Panier panier) throws SQLException {
        Map<Produit, Integer> produits = panier.getListProduit();
        produits.entrySet().stream()
                .forEach(entry -> {
                    user.getPanier().getListProduit().put(entry.getKey(), user.getPanier().getListProduit().getOrDefault(entry.getKey(), 0) + entry.getValue());
                        });
        updatePanierInDB(user.getPanier());
        return panier;
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
                "SELECT pa.id_panier, pa.id_user, pa.id_produit, pa.qte_produit, pa.start_time, pa.end_time, pa.isactive, " +
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
                    String start_time = result.getString("start_time");
                    String end_time = result.getString("end_time");
                    boolean isActive = result.getBoolean("isactive");
                    Map<Produit, Integer> produitsInThisPanier = new HashMap<>();

                    panier = new Panier(idPanier, produitsInThisPanier, start_time, end_time, user);
                    panier.setActive(isActive);

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


}