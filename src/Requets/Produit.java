package Requets;



class Produit {
    private String libelle;
    private String marque;
    private double prixUnit;
    private int nutriscore;
    private int qteStocke;

    public Produit(String libelle, String marque, double prixUnit, int nutriscore, int qteStocke) {
        this.libelle = libelle;
        this.marque = marque;
        this.prixUnit = prixUnit;
        this.nutriscore = nutriscore;
        this.qteStocke = qteStocke;
    }

    public double getPrixUnitP() {
        return prixUnit;
    }

    @Override
    public String toString() {
        return libelle + " (" + marque + ") - Prix: " + prixUnit + "€, Nutriscore: " + nutriscore;
    }
}
