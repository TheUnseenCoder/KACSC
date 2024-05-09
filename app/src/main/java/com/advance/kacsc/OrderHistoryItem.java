package com.advance.kacsc;
public class OrderHistoryItem {
    private int id;
    private String email;
    private int productId;
    private String size;
    private int quantity;
    private String productName;
    private String imagePath;
    private double base_price;
    private String order_status;
    private String product_description;
    private double amount_paid;
    private String first_transaction_id;
    private String dedication;
    private String delivery_date;
    private String delivery_time;




    public OrderHistoryItem(int id, String email, int productId, String size, int quantity, String productName, String imagePath, double base_price, String delivery_date, String delivery_time, String status_order, String productDescription, Double amountPaid, String firstTransactionId, String dedication) {
        this.id = id;
        this.email = email;
        this.productId = productId;
        this.size = size;
        this.delivery_date = delivery_date;
        this.quantity = quantity;
        this.productName = productName;
        this.imagePath = imagePath;
        this.base_price = base_price;
        this.delivery_time = delivery_time;
        this.order_status = status_order;
        this.dedication = dedication;
        this.product_description = productDescription;
        this.amount_paid = amountPaid;
        this.first_transaction_id = firstTransactionId;

    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public int getProductId() {
        return productId;
    }

    public String getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getProductName() {
        return productName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public double getBasePrice() {
        return base_price;
    }

    public String getOrderStatus() {
        return order_status;
    }

    public String getDeliverydate() {
        return delivery_date;
    }
    public String getDeliverytime() {
        return delivery_time;
    }


    public String getProductDescription() {
        return product_description;
    }

    public double getAmount_paid() {
        return amount_paid;
    }
    public String getFirstTransactionId() {
        return first_transaction_id;
    }

    public String getDedication() {
        return dedication;
    }


}
