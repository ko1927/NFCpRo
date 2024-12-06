package com.example.nfcpro;

public class PaymentProductData {
    private String productImage;
    private String productName;
    private int quantity;
    private String price;

    public PaymentProductData(String productImage, String productName, int quantity, String price) {
        this.productImage = productImage;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters
    public String getProductImage() { return productImage; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public String getPrice() { return price; }
}
