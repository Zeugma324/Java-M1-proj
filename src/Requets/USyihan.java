package Requets;

import java.sql.*;
import java.util.Scanner;
import connexion.Connect;


public class USyihan {

    // us0.2
    // 1.recherche produits par mot-cle
    public static void rechercherProduitParMotCle(String motCle) throws SQLException{
        String sql = "SELECT P.name FROM produit P WHERE P.name LIKE '%" + motCle + "%'";

        ResultSet result = Connect.executeQuery(sql);

        System.out.println("Produits trouvés :");
        while (result.next()) {
            String produit = result.getString("name");
            System.out.println(" " + produit);
        }

        Connect.closeConnexion();
    }


    // us1.5
    // 2.reprendre les paniers en cours
    public static void PanierEnCours(int userID) throws SQLException{

        String sql = "SELECT id_panier, Date_debut FROM panier WHERE Id_user = " + userID + " AND Date_fin IS NULL";

        ResultSet result = Connect.executeQuery(sql);

        if (result.next()) {
            int panierID = result.getInt("id_Panier");
            String DateDebut = result.getString("Date_debut");


            System.out.println("Panier en cours trouvé :");
            System.out.println("ID Panier: " + panierID);
            System.out.println("Date de début: " + DateDebut);

        }else{
            System.out.println("Aucun panier en cours trouvé pour User: " + userID);
        }

        Connect.closeConnexion();


    }

    //us3.3 editer les statistique sur produits，category, client
    //3.affichier les QteStock Original de produit
    public static void QteStock(int produitID) throws SQLException{

        String sql = "SELECT SUM(quantity) AS total_qte FROM stock WHERE Id_produit = "  + produitID;

        ResultSet result = Connect.executeQuery(sql);

            if(result.next()){
                int totalQte = result.getInt("total_qte");
                System.out.println("Quantity stock de produit " + produitID + " : " + totalQte);
            }else{
                System.out.println("Trouvé pas");
            }

            Connect.closeConnexion();

    }
    //4.modifier QteStock de produit
    public static void QteStockModifier(int produitID, int QteModifier) throws SQLException{

        String sql = "UPDATE stock Set quantity = quantity + " + QteModifier + " WHERE Id_produit = " + produitID;

        Connect.executeUpdate(sql);

        System.out.println("Stock change : " + QteModifier);

        Connect.closeConnexion();

    }
    //5.Afficher le QteStock d'un produit
    public static void StockParSubCat(int catID) throws SQLException {
        String sql = "SELECT stock.Id_produit, SUM(stock.quantity) AS total_qte, category FROM stock JOIN produit on stock.Id_produit = produit.Id_produit WHERE category = " + catID + " GROUP BY stock.Id_produit ";

        ResultSet result = Connect.executeQuery(sql);

        System.out.println("produits dans la categorie " + catID + " : ");
        int totalQte = 0;
        while (result.next()){
            int Qte = result.getInt("total_qte");
            totalQte += Qte;
            System.out.println("Produit ID : " + result.getInt("Id_produit") + " QteStock : " + Qte);
        }

        System.out.println("Total stock dans la categorie " + catID + " : " + totalQte);

        Connect.closeConnexion();
    }

    //6.Modifier le Qtestock d'un produit
    public static void ModifierStockParSubCat(int catID, int StockChange) throws SQLException{

        String sql = "UPDATE stock JOIN produit on stock.Id_produit = produit.Id_produit Set quantity = quantity + "+ StockChange + " WHERE category = " + catID ;
        Connect.executeUpdate(sql);

        System.out.println("Stock change : " + StockChange);

        Connect.closeConnexion();
    }


    //7.Définir les users VIP (qui commandent plus de cinq fois)
    public static void VIPusers() throws SQLException{
        String sql = "SELECT Id_user FROM panier JOIN PanierCommande ON panier.Id_panier = PanierCommande.panier_id GROUP by panier.Id_user HAVING COUNT(Id_commande) > 5";

        ResultSet result = Connect.executeQuery(sql);

        System.out.println(" VIP users :");

        while (result.next()){
            int userID = result.getInt("Id_user");
            System.out.println("User ID" + userID);

        }

        Connect.closeConnexion();
    }

    //US3.2
    // 8.Temps moyen de réalisation d'un panier par un client
    public static void AVGTempRealiserPanier() throws SQLException{
        String sql = "SELECT AVG(TIMESTAMPDIFF(Day, Date_debut, Date_fin)) AS TempMoyenP FROM panier WHERE Date_fin IS NOT NULL";

        ResultSet result = Connect.executeQuery(sql);

        if (result.next()){
            double AVGTempDays = result.getDouble("TempMoyenP");
            System.out.println("temps moyen de réalisation d'un panier par un client : " + AVGTempDays + "jours");
        } else {
            System.out.println("Trouvez pas les paniers réalisés");
        }
        Connect.closeConnexion();
    }

    //9.temps moyen de préparation des commandes(缺数据）
    public static void AVGTempPrepareCom() throws SQLException{

        String sql = " SELECT AVG(TIMESTAMPDIFF(Day, L.date_livrasion, P.Date_fin)) AS TempMoyenC FROM panier P JOIN PanierCommande PC ON P.Id_panier = PC.panier_id JOIN livrasion L ON L.id_commande = PC.Id_commande WHERE P.Date_fin IS NOT NULL AND L.date_livrasion IS NOT NULL ";

        ResultSet result = Connect.executeQuery(sql);

        if (result.next()){
            double AVGTempDays = result.getDouble("TempMoyenC");
            System.out.println("temps moyen de préparation des commandes : " + AVGTempDays + "jours");
        }else {
            System.out.println("Trouvez pas les commandes preparés");
        }
        Connect.closeConnexion();
    }

    //US3.5
    //consulter de consommateurs :
    // 10.Catégories de produits les plus sélectionnées dans le panier
    public static void CatPlusChoisirParUser() throws SQLException {

    }

    //US4.3
    //11.finaliser la préparation d'une commande
    public static void FinalPreparation(int commandeID) throws SQLException{
        String sql = "SELECT id_commande FROM livrasion WHERE date_livrasion IS NOT NULL AND id_commande = " + commandeID ;

        ResultSet result = Connect.executeQuery(sql);

        if (result.next()){
            System.out.println("Commande ID: " + commandeID +" Préparation terminée ");
        }else{
            System.out.println("La préparation n'est pas fini");

        }
        Connect.closeConnexion();
    }



    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("=== Menu ===");
            System.out.println("1. Rechercher les produit par mot-clé");
            System.out.println("2. Reprendre les paniers en cours");
            System.out.println("3. Afficher le QteStock d'un produit");
            System.out.println("4. Modifier le Qtestock d'un produit");
            System.out.println("5. Afficher le Qtestock des produits d'une catégorie");
            System.out.println("6. Modifier le Qtestock des produits d'une catégorie");
            System.out.println("7. Définir les users VIP");
            System.out.println("8. Temps moyen de réalisation d'un panier par un client");
            System.out.println("9.temps moyen de préparation des commandes");
            System.out.println("10.Catégories de produits les plus sélectionnées dans le panier");
            System.out.println("11.Finaliser la préparation d'une commande");
            System.out.println("12. Exit");
            System.out.print("Entrez votre choix : ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    System.out.print("Entrez le mot-clé : ");
                    String motCle = scanner.next();
                    rechercherProduitParMotCle(motCle);
                }
                case 2 -> {
                    System.out.print("Entrez l'ID user : ");
                    int userID = scanner.nextInt();
                    PanierEnCours(userID);
                }
                case 3 -> {
                    System.out.print("Entrez l'ID produit : ");
                    int produitID = scanner.nextInt();
                    QteStock(produitID);
                }
                case 4 -> {
                    System.out.print("Entrez l'ID produit : ");
                    int produitID = scanner.nextInt();
                    System.out.print("Entrez le changement de quantité : ");
                    int QteModifier = scanner.nextInt();
                    QteStockModifier(produitID, QteModifier);
                    QteStock(produitID); // 显示更新后的库存
                }
                case 5 -> {
                    System.out.print("Entrez l'ID catégorie : ");
                    int catID = scanner.nextInt();
                    StockParSubCat(catID);
                }
                case 6 -> {
                    System.out.print("Entrez l'ID catégorie : ");
                    int catID = scanner.nextInt();
                    System.out.print("Entrez le changement de quantité : ");
                    int StockChange = scanner.nextInt();
                    ModifierStockParSubCat(catID, StockChange);
                    StockParSubCat(catID);
                }
                case 7 -> {
                    VIPusers();
                }
                case 8 -> {
                    AVGTempRealiserPanier();
                }
                case 9 -> {
                    AVGTempPrepareCom();
                }
                case 10 -> {
                    CatPlusChoisirParUser();
                }
                case 11 -> {
                    System.out.print("Entrez l'ID commmande : ");
                    int commandeID = scanner.nextInt();
                    FinalPreparation(commandeID);
                }

                case 12 -> {
                    System.out.println("Exit l'application.");
                    return;
                }

                default -> System.out.println("Choix invalide !");
            }
        }


    }
}


