package Objects;



public class Produit {
    private int id;
    private String name;
    private double rating;
    private int no_of_ratings;
    private int discount_price;
    private int actual_price;
    private int category;
    private int qteStocke;



    public Produit(int id, String name, double rating, int no_of_ratings, int discount_price, int actual_price,
                   int category, int qteStocke) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.no_of_ratings = no_of_ratings;
        this.discount_price = discount_price;
        this.actual_price = actual_price;
        this.category = category;
        this.qteStocke = qteStocke;
    }



    public int getId() {
        return id;
    }



    public void setId(int id) {
        this.id = id;
    }



    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }



    public double getRating() {
        return rating;
    }



    public void setRating(double rating) {
        this.rating = rating;
    }



    public int getNo_of_ratings() {
        return no_of_ratings;
    }



    public void setNo_of_ratings(int no_of_ratings) {
        this.no_of_ratings = no_of_ratings;
    }



    public int getDiscount_price() {
        return discount_price;
    }



    public void setDiscount_price(int discount_price) {
        this.discount_price = discount_price;
    }



    public int getActual_price() {
        return actual_price;
    }



    public void setActual_price(int actual_price) {
        this.actual_price = actual_price;
    }



    public int getCategory() {
        return category;
    }



    public void setCategory(int category) {
        this.category = category;
    }



    public int getQteStocke() {
        return qteStocke;
    }



    public void setQteStocke(int qteStocke) {
        this.qteStocke = qteStocke;
    }



    @Override
    public String toString() {
        return "Produit {" +
                "\n  id=" + id +
                ",\n  name='" + name + '\'' +
                ",\n  rating=" + rating +
                ",\n  no_of_ratings=" + no_of_ratings +
                ",\n  discount_price=" + discount_price +
                ",\n  actual_price=" + actual_price +
                ",\n  category=" + category +
                ",\n  qteStocke=" + qteStocke +
                "\n}";
    }
}