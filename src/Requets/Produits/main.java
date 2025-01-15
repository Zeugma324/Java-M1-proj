package Requets.Produits;

import Managers.ProduitManager;

import java.io.IOException;
import java.sql.SQLException;

import static Managers.ProduitManager.*;



public class main {
    public static void main(String[] args) throws SQLException, IOException {
//         Exemple d'utilisation des fonctionnalités
//         US 0.1 - Visualisation d'un produit
//      visualiserProduit(254);
//         US 0.2 - Recherche par mot-clé
//        rechercherProduit("Dishwasher");
//         US 0.3 - Consultation par catégorie
//       consulterProduitsParCategorie(2);
//         US 0.4 - Trier les produits d'une catégorie
//        trierProduits(1);
//        - Afficher la quantité en stock
//        afficherQuantiteEnStock(1);

        ProduitManager.importerProduit("new_produits_list");
    }
}
