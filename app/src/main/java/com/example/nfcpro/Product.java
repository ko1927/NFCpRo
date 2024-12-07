package com.example.nfcpro;

public class Product {
    private String title;
    private String price;
    private String imageUrl;

    public Product(String title, String price, String imageUrl) {
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}