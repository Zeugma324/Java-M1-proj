package Console;

import java.sql.SQLException;
import java.util.Scanner;

public class main_console {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.println("===== TEST DES US =====");
            System.out.println("1. Visualiser un produit");
            System.out.println("2. Rechercher par mot-clé");
            System.out.println("3. Consulter par catégorie");
            System.out.println("4. Quitter");

            while (true) {
                System.out.print(" Votre choix : ");
                String choix = sc.nextLine();

                switch (choix) {
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
                        System.out.println("A bientôt !");
                        System.exit(0);
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
