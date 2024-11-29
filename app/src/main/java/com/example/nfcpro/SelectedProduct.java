package com.example.nfcpro;

import java.io.Serializable;

// 선택된 상품 정보를 담을 클래스
public class SelectedProduct implements Serializable {
    private String title;
    private String price;
    private int quantity;
    private int imageResource;

    public SelectedProduct(String title, String price, int quantity, int imageResource) {
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.imageResource = imageResource;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setQuantity(int Quantity) {
        this.quantity = Quantity;
    }
}
