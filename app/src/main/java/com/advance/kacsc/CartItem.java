package com.advance.kacsc;

public class CartItem {
    private int id;
    private String email;
    private int productId;
    private String productName;
    private String imagePath;
    private double base_price;

    private String description;
    public CartItem(int id, String email, int productId, String productName, String imagePath, double base_price, String description) {
        this.id = id;
        this.email = email;
        this.productId = productId;;
        this.productName = productName;
        this.imagePath = imagePath;
        this.base_price = base_price;
        this.description = description;
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

    public String getProductName() {
        return productName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public double base_price() {
        return base_price;
    }
    public String getDescription() {
        return description;
    }

}

