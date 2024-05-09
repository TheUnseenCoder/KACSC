package com.advance.kacsc;
public class OrderItem {
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
    private double total;
    private String dedication;
    private String date;
    private String time;



    public OrderItem(int id, String email, int productId, String size, int quantity, String productName, String imagePath, double base_price, String status_order, String productDescription, Double total, String dedication, String date, String time) {
        this.id = id;
        this.email = email;
        this.productId = productId;
        this.size = size;
        this.quantity = quantity;
        this.productName = productName;
        this.imagePath = imagePath;
        this.base_price = base_price;
        this.order_status = status_order;
        this.product_description = productDescription;
        this.total = total;
        this.dedication = dedication;
        this.date = date;
        this.time = time;
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

    public String getProductDescription() {
        return product_description;
    }

    public double getTotal() {
        return total;
    }

    public String getDedication() {
        return dedication;
    }
    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }




}
