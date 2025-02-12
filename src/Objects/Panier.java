package Objects;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

public class Panier {
    private int id;
    private Map<Produit, Integer> produits = new TreeMap<>();
    private Map<Produit, Integer> produitAndMagasin = new TreeMap<>();
    private String start_time;
    private User user;
    private String end_time;

    private boolean isactive = true;


    public Panier(int id, Map<Produit, Integer> produits, User user, String start_time, boolean isactive) {
        this.id = id;
        this.produits = produits;
        this.user = user;
        this.start_time = start_time;
    }


    public Panier(int id, User user) {
        this.id = id;
        this.user = user;
        this.start_time = new String(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    public Panier(int id, Map<Produit, Integer> produits, String start_time, String end_time, User user) {
        this.id = id;
        this.produits = produits;
        this.start_time = start_time;
        this.end_time = end_time;
        this.user = user;
        this.isactive = false;
    }

    public int getId() {
        return this.id;
    }

    public Map<Produit, Integer> getListProduit() {
        return this.produits;
    }

    public User getUser() {
        return this.user;
    }

    public String getStartTime() {
        return this.start_time;
    }

    public String getEndTime() {
        return this.end_time;
    }

    public boolean isActive() {
        return this.isactive;
    }

    public Map<Produit, Integer> getListProduitAndMagasin() {  return this.produitAndMagasin; }

    public void setId(int id) {
        this.id = id;
    }

    public void setProduits(Map<Produit, Integer> produits) {
        this.produits = produits;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStartTime(String start_time) {
        this.start_time = start_time;
    }

    public void setEndTime(String end_time) {
        this.end_time = end_time;
    }

    public void setActive(boolean isactive) {
        this.isactive = isactive;
    }

    public void setProduitAndMagasin(Map<Produit, Integer> produitAndMagasin) { this.produitAndMagasin = produitAndMagasin; }

    public void afficher(){
        System.out.println("User: "+ this.getUser().getLastname() + " " + this.getUser().getLastname());
        System.out.println("Temp_Depart: " + this.start_time);
        this.produits.entrySet().stream()
                .forEach(entry ->{
                    Produit prod = entry.getKey();
                    int qte = entry.getValue();
                    System.out.println(prod.getId() + ". Produit libelle : " + prod.getLibelle() + " Qte: " + qte);
                });
    }

    @Override
    public String toString() {
        return "Panier{" +
                "id=" + id +
                ", produits=" + produits +
                ", start_time='" + start_time + '\'' +
                ", user=" + user +
                ", end_time='" + end_time + '\'' +
                ", isactive=" + isactive +
                '}';
    }
}
