public class USpierre {
    public static void main(String[] args) {
    	System.out.println("------------------------US.04  ----------------------------------");
       //US.04 trier les produits 
        Produits p1 = new Produit("Tomato", "Harrys", 2.54, 54, 142);
        Produits p2 = new Produit("pomme", "BioFarm", 1.99, 90, 50);
        Produits p3 = new Produit("Lait", "Lactel", 0.89, 70, 200);
        Produits p4 = new Produit("Chocolat", "Nestlé", 3.25, 85, 120);
        Produits p5 = new Produit("Pain", "Baguette", 1.50, 60, 80);

     
        List<Produit> produits = new ArrayList<>();
        produits.add(p1);
        produits.add(p2);
        produits.add(p3);
        produits.add(p4);
        produits.add(p5);

      
        List<Produit> produitsParPrix = produits.stream()
        		.sorted((a, b) -> Double.compare(a.getPrixUnitP(), b.getPrixUnitP()))// trier par prix croissant
                .collect(Collectors.toList());// retourne une new liste trié
        
        produitsParPrix.forEach(a-> System.out.println(a));
        
        System.out.println("------------------------US.1.1  ----------------------------------");

        Panier pa1 = new Panier(1, new Date(0), null);
        Panier pa2 = new Panier(2, new Date(0), null);

        Utilisateurs utilisateur1 = new Utilisateurs(1, "Dupont", "Alice", "0623456789", "Rue de la Paix", pa1);
        Utilisateurs utilisateur2 = new Utilisateurs(2, "Martin", "Bob", "0787654321", "Avenue des Champs", pa2);

        List<Produit> produitsAAjouter1 = new ArrayList<>();
        produitsAAjouter1.add(p1);
        produitsAAjouter1.add(p2);

        utilisateur1.getPanier().ajouterProduits(produitsAAjouter1);

        List<Produit> produitsAAjouter2 = new ArrayList<>();
        produitsAAjouter2.add(p3);
        produitsAAjouter2.add(p4);

        utilisateur2.getPanier().ajouterProduits(produitsAAjouter2);

        System.out.println(utilisateur1);
        System.out.println(utilisateur2);
    }
        
}
