package Requets;

public class OrderDetail {
    private final int orderId;
    private final String category;
    private final int productCount;

    public OrderDetail(int orderId, String category, int productCount) {
        this.orderId = orderId;
        this.category = category;
        this.productCount = productCount;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCategory() {
        return category;
    }

    public int getProductCount() {
        return productCount;
    }
}
