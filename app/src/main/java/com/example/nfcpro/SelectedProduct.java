package com.example.nfcpro;

import java.io.Serializable;

public class SelectedProduct implements Serializable {
    private String title;
    private String price;
    private int quantity;
    private String imageUrl;  // 변수명 변경

    public SelectedProduct(String title, String price, int quantity, String imageUrl) {
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;  // Firebase Storage URL 저장
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

    public String getImageUrl() {  // 메소드명은 유지 (이미 이렇게 되어있네요)
        return imageUrl;
    }

    // Setter
    public void setQuantity(int quantity) {  // 파라미터명 수정 (일관성)
        this.quantity = quantity;
    }
}