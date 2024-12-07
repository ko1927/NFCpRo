package com.example.nfcpro;

import java.io.Serializable;

public class SelectedProduct implements Serializable {
    private String id;        // 추가: Firebase 제품 ID
    private String title;
    private String price;
    private int quantity;
    private String imageUrl;

    public SelectedProduct(String id, String title, String price, int quantity, String imageUrl) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    // 기존 생성자 유지 (이전 코드와의 호환성)
    public SelectedProduct(String title, String price, int quantity, String imageUrl) {
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}