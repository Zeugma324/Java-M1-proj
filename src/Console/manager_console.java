package Console;

import Managers.MagasinManager;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import static Managers.MagasinManager.*;
import static Managers.ProduitManager.consulterMagasin;

public class manager_console {
    public static void main(String[] args) throws SQLException, IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Veuillez saisir l'id du magasin ");
        int id_magasin = sc.nextInt();
        sc.nextLine();
        System.out.println("======================================");
        System.out.println("Vous être Magasin " + id_magasin);
        System.out.println("======================================");
        boolean continuerMenu = true;
        while (continuerMenu) {

            System.out.println("======= VOUS VOULEX FAIRE ? =======");
            System.out.println("1. importer des produits");
            System.out.println("2. Voir temp moyen de réalisation de panier");
            System.out.println("3. Voir temp moyen de préparation des commandes ");
            System.out.println("4. éditer des statistiques de produits");
            System.out.println("5. consulter les profils de consumers");
            System.out.println("6. consulter la liste des commandes à preparer");
            System.out.println("7. finaliser le préparation des produits");
            System.out.println("0. Quitter");
            String choix = sc.nextLine();
            switch (choix) {
                case "1":
                    System.out.println("Veuillez mettre le fichier csv dans le dossier de données et indiquer le nom");
                    String csv = sc.nextLine();
                    MagasinManager.importerProduit(csv);
                    break;
                case "2":
                    AVGTempRealiserPanier(id_magasin);
                    break;
                case "3":
                    AVGTempPrepareCom(id_magasin);
                    break;
                case "4":
                    System.out.println("Que souhaiteriez-vous changer ? ");
                    System.out.println("1. Modifier le quantity du stock; 2. Modifier le category du stock");
                    int statistiques = Integer.parseInt(sc.nextLine());
                    switch (statistiques) {
                        case 1:
                            System.out.println("Veuillez saisir l'id du produit ");
                            int id_p1 = Integer.parseInt(sc.nextLine());
                            System.out.println("Que-ce que le nouveau QTE ? ");
                            int qte = Integer.parseInt(sc.nextLine());
                            ModifierStock(id_p1, qte, id_magasin);
                            break;
                        case 2:
                            System.out.println("Veuillez saisir l'id du produit ");
                            int id_p2 = Integer.parseInt(sc.nextLine());
                            System.out.println("Que-ce que le nom de category ? ");
                            String category = sc.nextLine();
                            ModifierCategory(id_p2, category);
                            break;
                    }
                    break;
                case "5":
                    consulterMagasin(id_magasin);
                    break;
                case "6":
                    ArrayList<Integer> commande = commandAPreparer(id_magasin);
                    System.out.println("Je vous recommandé de préparer dans cet ordre ");
                    commande.forEach(System.out::println);
                    break;
                case "7":
                    System.out.println("Pour quelle commande êtes-vous prêt ? (Entrez le id de commande) ");
                    int id_commande = Integer.parseInt(sc.nextLine());
                    System.out.println("Quelle est le moyen de commande? ");
                    String moyen_commande = sc.nextLine();
                    finaliserCommand(id_magasin, id_commande, moyen_commande);
                    break;
                case "0":
                    System.out.println("A bientôt !");
                    continuerMenu = false;
                    break;
                default:
                    System.out.println("Choix invalide !");
                    break;
            }
        }
    }
}
