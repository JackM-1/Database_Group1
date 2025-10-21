package com.example.lab3databases;

public class Product {
    private int id;
    private String productName;
    private double productPrice;

    public Product() {}

    public Product(String name, double price) {
        this.productName = name;
        this.productPrice = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    @Override
    public String toString() {
        return productName + " - $" + String.format("%.2f", productPrice);
    }
}
