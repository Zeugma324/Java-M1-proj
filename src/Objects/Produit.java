package Objects;


import connexion.Connect;

import java.sql.*;
import java.util.*;
import BD_Connect.ProduitBD;

import static BD_Connect.ProduitBD.id_catAndCat;
import static BD_Connect.ProduitBD.subCatAndMainCat;

public class Produit implements Comparable<Produit> {
    private int id;
    private String libelle;
    private Double rating;
    private Integer no_of_ratings;
    private Integer discount_price;
    private Integer actual_price;
    private String sub_category;
    private String main_category;
    private int discount_rate = 100 * (discount_price/actual_price);



    public Produit(){ }

    public Produit(int id, String libelle, Double rating, Integer no_of_ratings, Integer discount_price, Integer actual_price, int category) {
        this.id = id;
        this.libelle = libelle;
        this.rating = rating;
        this.no_of_ratings = no_of_ratings;
        this.discount_price = discount_price;
        this.actual_price = actual_price;
        this.sub_category = id_catAndCat.get(category);
        this.main_category = subCatAndMainCat.get(this.sub_category);
    }

    public int getId() { return this.id; }
    public String getLibelle() { return this.libelle; }
    public Double getRating() { return this.rating; }
    public Integer  getNo_of_ratings() { return this.no_of_ratings; }
    public Integer  getDiscount_price() { return this.discount_price; }
    public Integer  getActual_price() { return this.actual_price; }
    public String getMain_category() { return this.main_category; }
    public String getSub_category() { return this.sub_category; }
    public int getDiscount_rate() { return this.discount_rate; }

    public void setId(int id) { this.id = id; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public void setRating(double rating) { this.rating = rating; }
    public void setNo_of_ratings(int no_of_ratings) { this.no_of_ratings = no_of_ratings; }
    public void setDiscount_price(int discount_price) { this.discount_price = discount_price; }
    public void setActual_price(int actual_price) { this.actual_price = actual_price; }
    public void setMain_category(String main_category) { this.main_category = main_category; }
    public void setSub_category(String sub_category) { this.sub_category = sub_category; }

    public static void afficherCategories(){
        id_catAndCat.entrySet().stream()
                .forEach(entry ->{
                    int id = entry.getKey();
                    String sub_category = entry.getValue();
                    String main_category = subCatAndMainCat.get(sub_category);
                    System.out.println("Id : " + id + " - Sub-category : " + sub_category + " - Main-category : " + main_category);
                });
    }


    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", lebelle='" + libelle + '\'' +
                ", rating=" + rating +
                ", no_of_ratings=" + no_of_ratings +
                ", discount_price=" + discount_price +
                ", actual_price=" + actual_price +
                ", main_category='" + main_category + '\'' +
                ", sub_category='" + sub_category + '\'' +
                '}';
    }

    @Override
    public int compareTo(Produit o) {
        return Integer.compare(this.id, o.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Produit produit = (Produit) obj;
        return id == produit.id; // 两个 Produit 相等的条件是 id 相等
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}




