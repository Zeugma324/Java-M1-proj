package Objects;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

public class Panier {
    private int id;
    private Map<Produit, Integer> produits = new TreeMap<>();
    private String start_time;
    private User user;
    private String end_time;
    private boolean isactive = true;


    public Panier(int id, Map<Produit, Integer> produits, User user, String start_time) {
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
}
