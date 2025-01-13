import Managers.ProduitManager;
import Objects.*;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.*;


import static Objects.User.*;

public class Interact_Utilisateur {
    public static void main(String[] args) throws SQLException {
        Boolean estNew = newUserCheck();
        User me = login(estNew);
        print("Bonjour " + me.getName() + " " + me.getLastname());
        mainMenu(me);
    }

    // Unfini
    private static void mainMenu(User me) throws SQLException {
        print("Quest-ce que vous voulez faire ?");
        print("1. Afficher les produits dans une categorie");
        print("2. Rechercher un produit");
        print("3. Afficher mon panier");
        print("4. Regarde a mon previous panier");
        print("4. Quitter");
        String choice = readLine();
        switch (choice) {
            case "1" -> menuPanier(me);
            case "2" -> rechercherProduit(me);
            case "3" -> me.getPanier().affichier();
            case "4" -> historyPanier(me);
            case "5" -> System.exit(0);
            default -> mainMenu(me);
        }
    }

    private static void historyPanier(User me) throws SQLException {
        ArrayList<Panier> paniers = me.HistoryPanier();

        IntStream.range(0, paniers.size())
                .forEach(i -> {
                    Panier panier = paniers.get(i);
                    System.out.println("Panier " + (i + 1) + ":");
                    panier.getListProduit().forEach((produit, quantite) -> {
                        System.out.println("    Produit Id: " + produit.getId() +
                                ", Produit nom : " + produit.getName() +
                                ", qte : " + quantite);
                    });
                });

        int choice = demanderEntier("Voulez-vous répondre au panier? Entrez le numéro du panier et appuyez sur 0 pour revenir. ");
        while (choice != 0) {
            if (choice < 0 || choice > paniers.size()) {
                choice = demanderEntier("Wrong input, please try again ");
            } else {
                Panier selectedPanier = paniers.get(choice - 1);
                selectedPanier.getListProduit().entrySet().stream()
                        .forEach(entry -> {
                            try {
                                me.getPanier().addProduit(entry.getKey(), entry.getValue());
                            } catch (SQLException e) {
                                e.getMessage();
                            }
                        });
            }
        }
        mainMenu(me);

    }


    private static void rechercherProduit(User me) throws SQLException {
        String req = readLine("Quel-ce que vous voulez rechercher ?");
        ProduitManager.rechercherProduit(req);
        afficherProduit(me);
    }

    private static void afficherProduit(User me) throws SQLException {
        int id_prd = demanderEntier("Quel produit voulez-vous regard ? Entrez l'id du produit");
        ProduitManager.visualiserProduit(id_prd);
        addProduitAPanier(me, id_prd);
    }

    private static void addProduitAPanier(User me, int id_prd) throws SQLException {
        String choice = readLine("Voulez-vous l'ajouter dans votre panier ? (1.oui 2.non 3.retour au menu)");
        switch (choice) {
            case "1" -> {
                me.getPanier().addProduit(Produit.findProduit(id_prd));
                mainMenu(me);
            }
            case "2" -> afficherProduit(me);
            default -> mainMenu(me);
        }
    }


    private static void menuPanier(User me) throws SQLException {
        me.connectPanier();
        me.getPanier().affichier();
        print("Que voulez vous faire ?");
        print("1. Modifier le panier");
        print("2. Valider mon panier");
        print("3. Annuler mon panier");
        print("4. Retour au menu principal");
        print("5. Quitter");
        String choice = readLine();
        switch (choice) {
            case "1" -> modifierPanier(me);
            case "2" -> me.getPanier().validate();
            case "3" -> me.getPanier().annulerPanier();
            case "4" -> mainMenu(me);
            case "5" -> System.exit(0);
            default -> menuPanier(me);
        }

    }

    private static void modifierPanier(User me) throws SQLException {
        me.getPanier().affichier();
        print("1. Je veux supprimer un produit");
        print("2. Je souhaite modifier la quantité d'articles");
        print("3. Retour au menu Panier");
        String choice = readLine();
        switch (choice) {
            case "1" -> supprimePanier(me);
            case "2" -> modifierQuantite(me);
            case "3" -> menuPanier(me);
        }
    }


    private static void modifierQuantite(User me) throws SQLException {
        try {
            int id_prod = demanderEntier_Quantite("Entrez le id de produit que vous souhaitez modifier");
            int quantite = demanderEntier_Quantite("Entrez la quantité souhaitée");
            me.getPanier().modifierPanier(id_prod, quantite);
        } catch (NumberFormatException e) {
            System.out.println("Entrée invalide, veuillez réessayer.");
            modifierQuantite(me);
        }
    }

    private static int demanderEntier_Quantite(String message) {
        int result = demanderEntier(message);
        if (!(result == 1) && !(result == 2)) {
            return demanderEntier_Quantite("Input must be 1 or 2");
        }
        return result;
    }

    private static void supprimePanier(User me) throws SQLException {
        print("Quel produit voulez-vous supprimer ? Entrez l'id du produit");
        try {
            int id_sup = Integer.parseInt(readLine());
            me.getPanier().removeProduit(id_sup);
            modifierPanier(me);
        } catch (NumberFormatException | SQLException e) {
            System.out.println("Wrong input, please try again");
            supprimePanier(me);
        }
    }

    private static User login(Boolean estNew) throws SQLException {
        if (estNew) {
            print("Merci d'avoir fourni votre nom et prénom");
            String name = readLine("Veuillez entrer votre prénom :");
            String lastname = readLine("Veuillez entrer votre nom :");
            return createUtilisateur(name, lastname);
        } else {
            try {
                int id = Integer.parseInt(readLine("Type ton id_utilisateur pour continuer"));
                System.out.println("Enchanté! ");
                return findUtilisateur(id);
            } catch (NumberFormatException e) {
                System.out.println("Wrong input, please try again");
            }
            {
                return login(estNew);
            }
        }
    }

    private static boolean newUserCheck() {
        System.out.println("Bonjour !");
        System.out.println("Est-que vous etre nouveau?");
        System.out.println("1. Oui, je suis nouveau");
        System.out.println("2. Non, j'ai une account");
        String input = System.console().readLine();
        if (!input.equals("1") && !input.equals("2")) {
            System.out.println("Wrong input, please try again");
            newUserCheck();
        }
        return input.equals("1");
    }

    private static void print(String str) {
        System.out.println(str);
    }

    private static String readLine() {
        String res = System.console().readLine();
        if (res == null) {
            return readLine("Isnput is null, please try again");
        } else {
            return res;
        }
    }

    private static String readLine(String message) {
        print(message);
        String res = System.console().readLine();
        if (res == null) {
            return readLine("Isnput is null, please try again");
        } else {
            return res;
        }
    }

    private static int demanderEntier(String message) {
        print(message);
        int result = -1;
        try {
            result = Integer.parseInt(readLine());
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}


