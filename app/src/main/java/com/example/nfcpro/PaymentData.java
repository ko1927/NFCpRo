package com.example.nfcpro;

public class PaymentData {
    private String orderId;
    private String orderName;
    private String paymentTime;
    private String amount;

    public PaymentData(String orderId, String orderName, String paymentTime, String amount) {
        this.orderId = orderId;
        this.orderName = orderName;
        this.paymentTime = paymentTime;
        this.amount = amount;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getOrderName() { return orderName; }
    public String getPaymentTime() { return paymentTime; }
    public String getAmount() { return amount; }
}
