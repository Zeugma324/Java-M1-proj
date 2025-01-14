package Console;

import BD_Connect.UserDB;
import Objects.User;

import java.sql.SQLException;
import java.util.Scanner;

public class main_console {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            System.out.println("===== MENU FUSIONNÉ : US & Statistiques =====");
            System.out.println("1. Visualiser un produit");
            System.out.println("2. Rechercher un produit par mot-clé");
            System.out.println("3. Consulter les produits par catégorie");
            System.out.println("5. Définir les utilisateurs VIP");
            System.out.println("6. Temps moyen de réalisation d'un panier");
            System.out.println("7. Temps moyen de préparation des commandes");
            System.out.println("8. Afficher le prix moyen par catégorie");
            System.out.println("9. Comparer les notes d’une catégorie à l’autre");

            System.out.println("0. Quitter");

            while (true) {
                System.out.print("\nVotre choix : ");
                String choix = sc.nextLine();

                switch (choix) {
                    case "0":
                        System.out.println("A bientôt !");
                        System.exit(0);
                        break;

                    case "1":
                        System.out.print("Entrez l'ID du produit : ");
                        int idProd = Integer.parseInt(sc.nextLine());
                        US.visualiserProduit(idProd);
                        break;

                    case "2":
                        System.out.print("Mot-clé : ");
                        String keyword = sc.nextLine();
                        US.rechercherProduit(keyword);
                        break;

                    case "3":
                        System.out.print("ID de la catégorie : ");
                        int idCat = Integer.parseInt(sc.nextLine());
                        US.consulterProduitsParCategorie(idCat);
                        break;

                    case "5":
                        US.VIPusers();
                        break;

                    case "6":
                        US.AVGTempRealiserPanier();
                        break;

                    case "7":
                        US.AVGTempPrepareCom();
                        break;
                        
                    case "8":
                        US.averagePriceByCategory();
                        break;

                    case "9":
                        US.compareRatingAcrossCategories();
                        break;


                    case "10":
                        US.AfficherProduitFrequents(UserDB.findUserById(1), 10);
                        break;

                    case "11":
                        US.affichierHabitudes(UserDB.findUserById(1));
                        break;

                    case "12" :
                        US.faireRecommandation(UserDB.findUserById(1));
                        break;
                    default:
                        System.out.println("Choix invalide !");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }
}
