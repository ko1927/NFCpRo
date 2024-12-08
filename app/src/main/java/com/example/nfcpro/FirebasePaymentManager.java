package com.example.nfcpro;

import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FirebasePaymentManager {
    private static final String DATABASE_URL = "https://nfctogo-f4da1-default-rtdb.firebaseio.com/";
    private static final String ROOT_PATH = "nfcpro";
    private static final String TAG = "FirebasePaymentManager";
    private DatabaseReference rootRef;

    public FirebasePaymentManager() {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
            this.rootRef = database.getReference().child(ROOT_PATH);
        } catch (Exception e) {
            Log.e(TAG, "Firebase 초기화 실패: " + e.getMessage());
        }
    }

    public void processPayment(ArrayList<SelectedProduct> products, int totalAmount, String boothId,
                               String userId, PaymentCallback callback) {
        if (products == null || products.isEmpty()) {
            callback.onFailure("상품 목록이 비어있습니다.");
            return;
        }

        if (rootRef == null) {
            callback.onFailure("Firebase 연결에 실패했습니다.");
            return;
        }

        Log.d(TAG, "결제 처리 시작 - 부스ID: " + boothId + ", 사용자ID: " + userId + ", 총액: " + totalAmount);

        String transactionId = UUID.randomUUID().toString();

        Map<String, Object> transaction = new HashMap<>();
        transaction.put("userId", userId);
        transaction.put("boothId", boothId);
        transaction.put("amount", totalAmount);
        transaction.put("timestamp", System.currentTimeMillis());
        transaction.put("status", "completed");

        Map<String, Object> transactionItems = new HashMap<>();
        for (SelectedProduct product : products) {
            try {
                Map<String, Object> item = new HashMap<>();
                item.put("productId", product.getTitle());
                item.put("quantity", product.getQuantity());
                item.put("price", product.getPrice());
                item.put("name", product.getTitle());
                item.put("imageUrl", product.getImageUrl());

                transactionItems.put(UUID.randomUUID().toString(), item);
            } catch (Exception e) {
                Log.e(TAG, "상품 정보 처리 실패: " + e.getMessage());
                callback.onFailure("상품 정보 처리 중 오류가 발생했습니다.");
                return;
            }
        }

        rootRef.child("users").child(userId).child("balance").get()
                .addOnSuccessListener(dataSnapshot -> {
                    try {
                        Integer currentBalance = dataSnapshot.getValue(Integer.class);
                        if (currentBalance == null) {
                            Log.e(TAG, "사용자 잔액이 null입니다.");
                            callback.onFailure("사용자 잔액 정보를 찾을 수 없습니다.");
                            return;
                        }

                        int newBalance = currentBalance - totalAmount;
                        if (newBalance < 0) {
                            Log.e(TAG, "잔액 부족 - 현재: " + currentBalance + ", 필요: " + totalAmount);
                            callback.onFailure("잔액이 부족합니다");
                            return;
                        }

                        updateBoothRevenue(boothId, totalAmount, new BoothRevenueCallback() {
                            @Override
                            public void onSuccess(Map<String, Object> updates) {
                                updates.put("/transactions/" + transactionId, transaction);
                                updates.put("/transaction_items/" + transactionId, transactionItems);
                                updates.put("/users/" + userId + "/balance", newBalance);
                                updates.put("/booth_transactions/" + boothId + "/" + transactionId, true);
                                updates.put("/user_transactions/" + userId + "/" + transactionId, true);

                                rootRef.updateChildren(updates)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "결제 성공 - 거래ID: " + transactionId);
                                            callback.onSuccess();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "결제 업데이트 실패: " + e.getMessage());
                                            callback.onFailure("결제 처리 중 오류가 발생했습니다.");
                                        });
                            }

                            @Override
                            public void onFailure(String error) {
                                callback.onFailure(error);
                            }
                        });

                    } catch (Exception e) {
                        Log.e(TAG, "잔액 처리 중 오류: " + e.getMessage());
                        callback.onFailure("잔액 처리 중 오류가 발생했습니다.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "잔액 조회 실패: " + e.getMessage());
                    callback.onFailure("잔액 조회에 실패했습니다.");
                });
    }

    private void updateBoothRevenue(String boothId, int amount, BoothRevenueCallback callback) {
        rootRef.child("rank").child(boothId).get()
                .addOnSuccessListener(dataSnapshot -> {
                    Integer currentRevenue = dataSnapshot.getValue(Integer.class);
                    if (currentRevenue == null) currentRevenue = 0;

                    int newRevenue = currentRevenue + amount;
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("/rank/" + boothId, newRevenue);

                    Log.d(TAG, "부스 매출액 업데이트 - 부스ID: " + boothId +
                            ", 현재: " + currentRevenue + ", 신규: " + newRevenue);

                    callback.onSuccess(updates);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "부스 매출액 조회 실패: " + e.getMessage());
                    callback.onFailure("부스 매출액 업데이트에 실패했습니다.");
                });
    }

    private interface BoothRevenueCallback {
        void onSuccess(Map<String, Object> updates);
        void onFailure(String error);
    }

    public interface PaymentCallback {
        void onSuccess();
        void onFailure(String error);
    }
}