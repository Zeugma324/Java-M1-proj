package Console;

import BD_Connect.ProduitBD;
import BD_Connect.UserDB;
import Objects.Panier;
import Objects.Produit;
import Objects.User;
import connexion.Connect;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Scanner;

public class main_console {
    public static void main(String[] args) throws NoSuchAlgorithmException, SQLException {
        Scanner sc = new Scanner(System.in);
        User user = null;
        //Connect.closeConnexion();

        try {
        	System.out.println("Se connecter / nouvel utilisateur (sc/nu)");
            System.out.println("===== MENU =====");
            System.out.println("1. Visualiser un produit");
            System.out.println("2. Rechercher un produit par mot-clé");
            System.out.println("3. Consulter les produits par catégorie");

            System.out.println("4. Gérer mon panier");

            System.out.println("5. Définir les utilisateurs VIP");
            System.out.println("6. Temps moyen de réalisation d'un panier");
            System.out.println("7. Temps moyen de préparation des commandes");
            System.out.println("8. Afficher le prix moyen par catégorie");
            System.out.println("9. Comparer les notes d’une catégorie à l’autre");
            System.out.println("10. Produits fréquents");
            System.out.println("11. Habitudes de consommation");
            System.out.println("12. Recommandations");
            System.out.println("0. Quitter");

            while (true) {
                System.out.print("Votre choix : ");
                String choix = sc.nextLine();
                
                switch (choix) {
                	case "sc":
                		System.out.println("email : ");
                		String email = sc.nextLine();
                		System.out.println("mot de passe :");
                		String mdp = sc.nextLine();
                		user = UserDB.findUserBylogin(email, mdp);
                		if(user == null) {
                			System.out.println("Adresse mail ou mot de passe éroné");
                			System.exit(0);
                		}
                		break;
                		
                	case "nu":
                		System.out.println("Email : ");
                	    String emailNu = sc.nextLine();
                	    System.out.println("Mot de passe :");
                	    String mdpNu = sc.nextLine();
                	    System.out.println("Prénom : ");
                	    String prenomNu = sc.nextLine();
                	    System.out.println("Nom : ");
                	    String nomNu = sc.nextLine();
                	    System.out.println("Numéro de téléphone : ");
                	    String telNu = sc.nextLine();
                	    System.out.println("Adresse : ");
                	    String addressNu = sc.nextLine();
                	    System.out.println("Genre (M/F/Autre) : ");
                	    String genderNu = sc.nextLine();
                	    System.out.println("Date de naissance (yyyy-MM-dd) : ");
                	    String birthdayNu = sc.nextLine();
                	    user = UserDB.createUser(nomNu, prenomNu, emailNu, mdpNu, telNu, addressNu, genderNu, birthdayNu);
                	    break;
                		
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


                    case "4":
                        gererPanier(sc, user);
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
                        US.AfficherProduitFrequents(user, 10);
                        break;

                    case "11":
                        US.affichierHabitudes(user);
                        break;

                    case "12":
                        US.faireRecommandation(user);
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

    // SOUS MENU
    private static void gererPanier(Scanner sc, User user) throws SQLException {


        Panier panier = US.loadPanierByUser(user);
        System.out.println("Panier chargé (ID = " + panier.getId() + ") pour l'utilisateur " + user.getName());

        boolean continuer = true;
        while (continuer) {
            System.out.println("===== GESTION DU PANIER =====");
            System.out.println("1. Visualiser Panier");
            System.out.println("2. Ajouter un produit");
            System.out.println("3. Supprimer un produit");
            System.out.println("4. Valider le panier");
            System.out.println("5. Annuler (vider) le panier");
            System.out.println("0. Retour au menu principal");
            System.out.print("Votre choix : ");

            String sousChoix = sc.nextLine();

            switch (sousChoix) {
                case "0":
                    System.out.println("Retour au menu principal...");
                    continuer = false;
                    break;

                case "1" :
                    System.out.println("Panier chargé (ID = " + panier.getId() + ")");
                    panier.afficher();
                    break;
                case "2":
                    System.out.print("Entrez l'ID du produit à ajouter : ");
                    int idProd = Integer.parseInt(sc.nextLine());

                    System.out.print("Entrez la quantité : ");
                    int qty = Integer.parseInt(sc.nextLine());

                    Produit p = US.visualiserProduitEtRetour(idProd);
                    if (p != null) {
                        US.addProduitToPanier(panier, p, qty);
                        System.out.println("Produit ajouté avec succès !");
                    } else {
                        System.out.println("Produit introuvable !");
                    }
                    break;

                case "3":
                    System.out.print("Entrez l'ID du produit à supprimer : ");
                    int idProdToRemove = Integer.parseInt(sc.nextLine());

                    Produit pr = US.visualiserProduitEtRetour(idProdToRemove);
                    if (pr != null) {
                        US.removeProduitFromPanier(panier, pr);
                        System.out.println("Produit supprimé du panier !");
                    } else {
                        System.out.println("Produit introuvable !");
                    }
                    break;

                case "4":
                    US us1 = new US();
                    us1.validerPanier(panier);
                    System.out.println("Panier validé avec succès !");
                    continuer = false;
                    break;

                case "5":
                    US us = new US();
                    us.annulerPanier(panier);
                    System.out.println("Panier annulé/vidé !");
                    continuer = false;
                    break;
                case "6" :
                    US us2 = new US();
                    us2.modifiereProduitPanier(user.getPanier(),ProduitBD.loadProduit(1),4);



                default:
                    System.out.println("Choix invalide (sous-menu) !");
            }
        }
    }
}