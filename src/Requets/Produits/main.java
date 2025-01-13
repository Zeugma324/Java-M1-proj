package Requets.Produits;

import java.sql.SQLException;
import java.util.Scanner;

import static Managers.ProduitManager.*;

public class main {
    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // Identification client
        System.out.println("=================================");
        System.out.println("        Identification Client     ");
        System.out.println("=================================");

        System.out.print("Êtes-vous un nouveau client ? (y/n) : ");
        String nouveauClient = scanner.nextLine().trim().toLowerCase();

        String nomClient;

        if (nouveauClient.equals("y")) {
            System.out.print("Bienvenue ! Veuillez entrer votre nom : ");
            nomClient = scanner.nextLine();
            System.out.println("Merci, " + nomClient + ", votre compte a été créé avec succès !\n");
        } else if (nouveauClient.equals("n")) {
            System.out.print("Entrez votre nom pour continuer : ");
            nomClient = scanner.nextLine();
            System.out.println("Bienvenue de retour, " + nomClient + " !\n");
        } else {
            System.out.println("Entrée invalide. Redémarrez le programme.");
            scanner.close();
            return;
        }

        boolean tomito = true;

        while (tomito) {
            System.out.println("=================================");
            System.out.println("        Bienvenue au Menu        ");
            System.out.println("=================================");

            // Options
            System.out.println("1. Visualiser un produit");
            System.out.println("2. Recherche par mot-clé");
            System.out.println("3. Consultation par catégorie");
            System.out.println("4. Trier les produits d'une catégorie");
            System.out.println("5. Ajouter un produit au panier");
            System.out.println("6. Visualiser le panier");
            System.out.println("7. Valider le panier");
            System.out.println("8. Annuler le panier");
            System.out.println("9. Reprendre un panier en cours");
            System.out.println("10. Choisir un produit de remplacement");
            System.out.println("11. Choisir un mode de réception de commande");
            System.out.println("12. Consulter les produits les plus commandés");
            System.out.println("13. Consulter mes habitudes de consommation");
            System.out.println("14. Valider les préférences pour les remplacements");
            System.out.println("15. Importer des produits pour mise à jour");
            System.out.println("16. Vérifier l'efficacité des systèmes");
            System.out.println("17. Éditer des statistiques");
            System.out.println("18. Paramétrer l'algorithme de recommandation");
            System.out.println("19. Consulter les profils de consommateurs");
            System.out.println("20. Détecter les nouvelles habitudes de consommation");
            System.out.println("21. Vérifier l'efficacité des algorithmes");
            System.out.println("22. Consulter les commandes à préparer");
            System.out.println("23. Marquer une commande en préparation");
            System.out.println("24. Finaliser la préparation d'une commande");
            System.out.println("25. Quitter");
            System.out.println("=================================");

            System.out.print("Choisissez une option : ");

            int choix;

            try {
                choix = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide. Veuillez entrer un nombre.");
                continue;
            }

            switch (choix) {
                case 1:
                    System.out.println("\n=== Visualisation d'un produit ===");
                    System.out.print("Entrez l'ID du produit : ");
                    int produitId = Integer.parseInt(scanner.nextLine());
                    visualiserProduit(produitId);
                    break;
                case 2:
                    System.out.println("\n=== Recherche par mot-clé ===");
                    System.out.print("Entrez un mot-clé : ");
                    String motCle = scanner.nextLine();
                    rechercherProduit(motCle);
                    break;
                case 3:
                    System.out.println("\n=== Consultation par catégorie ===");
                    System.out.print("Entrez l'ID de la catégorie : ");
                    int categorieId = Integer.parseInt(scanner.nextLine());
                    consulterProduitsParCategorie(categorieId);
                    break;
                case 4:
                    System.out.println("\n=== Trier les produits d'une catégorie ===");
                    System.out.print("Entrez l'ID de la catégorie : ");
                    int categorieTriId = Integer.parseInt(scanner.nextLine());
                    trierProduits(categorieTriId);
                    break;
                case 5:
                    System.out.println("\n=== Ajouter un produit au panier ===");
                    // Code pour ajouter un produit au panier
                    break;
                case 6:
                    System.out.println("\n=== Visualiser le panier ===");
                    // Code pour visualiser le panier
                    break;
                case 7:
                    System.out.println("\n=== Valider le panier ===");
                    // Code pour valider le panier
                    break;
                case 8:
                    System.out.println("\n=== Annuler le panier ===");
                    // Code pour annuler le panier
                    break;
                case 9:
                    System.out.println("\n=== Reprendre un panier en cours ===");
                    // Code pour reprendre un panier en cours
                    break;
                case 10:
                    System.out.println("\n=== Choisir un produit de remplacement ===");
                    // Code pour choisir un produit de remplacement
                    break;
                case 11:
                    System.out.println("\n=== Choisir un mode de réception de commande ===");
                    // Code pour choisir un mode de réception
                    break;
                case 12:
                    System.out.println("\n=== Consulter les produits les plus commandés ===");
                    // Code pour consulter les produits les plus commandés
                    break;
                case 13:
                    System.out.println("\n=== Consulter mes habitudes de consommation ===");
                    // Code pour consulter les habitudes de consommation
                    break;
                case 14:
                    System.out.println("\n=== Valider les préférences pour les remplacements ===");
                    // Code pour valider les préférences
                    break;
                case 15:
                    System.out.println("\n=== Importer des produits pour mise à jour ===");
                    // Code pour importer des produits
                    break;
                case 16:
                    System.out.println("\n=== Vérifier l'efficacité des systèmes ===");
                    // Code pour vérifier l'efficacité des systèmes
                    break;
                case 17:
                    System.out.println("\n=== Éditer des statistiques ===");
                    // Code pour éditer des statistiques
                    break;
                case 18:
                    System.out.println("\n=== Paramétrer l'algorithme de recommandation ===");
                    // Code pour paramétrer l'algorithme
                    break;
                case 19:
                    System.out.println("\n=== Consulter les profils de consommateurs ===");
                    // Code pour consulter les profils
                    break;
                case 20:
                    System.out.println("\n=== Détecter les nouvelles habitudes de consommation ===");
                    // Code pour détecter les nouvelles habitudes
                    break;
                case 21:
                    System.out.println("\n=== Vérifier l'efficacité des algorithmes ===");
                    // Code pour vérifier l'efficacité des algorithmes
                    break;
                case 22:
                    System.out.println("\n=== Consulter les commandes à préparer ===");
                    // Code pour consulter les commandes
                    break;
                case 23:
                    System.out.println("\n=== Marquer une commande en préparation ===");
                    // Code pour marquer une commande
                    break;
                case 24:
                    System.out.println("\n=== Finaliser la préparation d'une commande ===");
                    // Code pour finaliser la commande
                    break;
                case 25:
                    System.out.println("Merci et au revoir !");
                    running = false;
                    break;
                default:
                    System.out.println("Option invalide. Veuillez choisir entre 1 et 25.");
            }
        }

        scanner.close();
    }
}
