package Requets.WaitingMerged;

public class ProduitInfo {

    int idProduit;
    String name;
    int nombreCommandes;

    public ProduitInfo(int idProduit, String name, int nombreCommandes)
    {
        this.idProduit = idProduit;
        this.name = name;
        this.nombreCommandes = nombreCommandes;
    }

    @Override
    public String toString() {
        return "Objects.Produit ID: " + idProduit +
                ", Nom: " + name +
                ", Nombre de commandes: " + nombreCommandes;
    }




}


