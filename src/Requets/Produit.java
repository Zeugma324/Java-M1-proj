package Requets;
public class Produit {
    private String name;
    private double actual_price;
    private int ratings;
    private int no_of_ratings;
    private String category;
    
    public Produit(String name, double actual_price, int ratings, int no_of_ratings, String category) {
        this.name = name;
        this.actual_price = actual_price;
        this.ratings = ratings;
        this.no_of_ratings = no_of_ratings;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public double getActualPrice() {
        return actual_price;
    }

    public int getRatings() {
        return ratings;
    }

    public int getNoOfRatings() {
        return no_of_ratings;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "Produit{" +
                "name='" + name + '\'' +
                ", actual_price=" + actual_price +
                ", ratings=" + ratings +
                ", no_of_ratings=" + no_of_ratings +
                ", category='" + category + '\'' +
                '}';
    }
}
