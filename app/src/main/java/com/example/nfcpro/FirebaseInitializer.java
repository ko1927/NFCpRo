package com.example.nfcpro;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FirebaseInitializer {
    private static final String DATABASE_URL = "https://nfctogo-f4da1-default-rtdb.firebaseio.com/";
    private static final String ROOT_PATH = "nfcpro";
    private DatabaseReference rootRef;

    public FirebaseInitializer() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
        this.rootRef = database.getReference().child(ROOT_PATH);
    }

    public void initializeDatabase() {
        initializeBooths();
        initializeUsers();
        initializeProducts();
        initializeSampleTransaction();
        initializeCardKeys();
    }

    private void initializeBooths() {
        DatabaseReference boothsRef = rootRef.child("booths");

        // ownerUid와 boothId를 연결하는 맵 생성
        Map<String, Object> boothOwners = new HashMap<>();
        boothOwners.put("owner1", "booth1");
        boothOwners.put("owner2", "booth2");
        rootRef.child("booth_owners").setValue(boothOwners);

        // 부스 데이터 생성 및 저장
        Map<String, Object> booth1 = createBooth("푸드트럭 1", "수제버거와 감자튀김", "A구역", hashPassword("booth1pass"));
        Map<String, Object> booth2 = createBooth("음료 부스", "시원한 음료와 디저트", "B구역", hashPassword("booth2pass"));

        boothsRef.child("booth1").setValue(booth1);
        boothsRef.child("booth2").setValue(booth2);
    }

    private void initializeUsers() {
        DatabaseReference usersRef = rootRef.child("users");

        Map<String, Object> customerData = createUser("홍길동", "hong@example.com", 100000, "customer");
        Map<String, Object> ownerData = createUser("김사장", "kim@example.com", 0, "booth_owner");

        usersRef.child("user1").setValue(customerData);
        usersRef.child("owner1").setValue(ownerData);

        // 카드키-사용자 매핑
        Map<String, String> cardUserMap = new HashMap<>();
        cardUserMap.put("a1b2c3d4e5f6g7h8", "user1");
        rootRef.child("card_user_map").setValue(cardUserMap);
    }

    private void initializeProducts() {
        DatabaseReference productsRef = rootRef.child("products");

        // 상품 카테고리 정보
        Map<String, Object> categories = new HashMap<>();
        categories.put("food", "음식");
        categories.put("beverage", "음료");
        rootRef.child("product_categories").setValue(categories);

        // 상품 정보
        Map<String, Object> product1 = createProduct("클래식 버거", 8000, "food", "drawable/_060", true, "booth1");
        Map<String, Object> product2 = createProduct("치즈 버거", 9000, "food", "drawable/_060ti", true, "booth1");

        String prod1Id = UUID.randomUUID().toString();
        String prod2Id = UUID.randomUUID().toString();

        productsRef.child(prod1Id).setValue(product1);
        productsRef.child(prod2Id).setValue(product2);

        // 부스-상품 매핑
        Map<String, Object> boothProducts = new HashMap<>();
        boothProducts.put(prod1Id, true);
        boothProducts.put(prod2Id, true);
        rootRef.child("booth_products").child("booth1").setValue(boothProducts);
    }

    private void initializeSampleTransaction() {
        String transactionId = UUID.randomUUID().toString();
        DatabaseReference transactionsRef = rootRef.child("transactions");

        // 거래 기본 정보
        Map<String, Object> transaction = createTransaction("user1", "booth1", 8000);
        transactionsRef.child(transactionId).setValue(transaction);

        // 거래-상품 매핑
        Map<String, Object> transactionItems = new HashMap<>();
        transactionItems.put("productId", "prod1");
        transactionItems.put("quantity", 1);
        transactionItems.put("price", 8000);
        rootRef.child("transaction_items").child(transactionId).setValue(transactionItems);

        // 인덱스 업데이트
        updateTransactionIndexes(transactionId, "user1", "booth1");
    }

    private void initializeCardKeys() {
        DatabaseReference cardStatusRef = rootRef.child("card_status");
        cardStatusRef.child("a1b2c3d4e5f6g7h8").setValue(createCardStatus(true, System.currentTimeMillis()));
    }

    // Helper Methods
    private Map<String, Object> createBooth(String name, String description, String location, String password) {
        Map<String, Object> booth = new HashMap<>();
        booth.put("name", name);
        booth.put("description", description);
        booth.put("location", location);
        booth.put("password", password);
        return booth;
    }

    private Map<String, Object> createUser(String name, String email, int balance, String role) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        if (balance > 0) user.put("balance", balance);
        user.put("role", role);
        return user;
    }

    private Map<String, Object> createProduct(String name, int price, String category,
                                              String imageUrl, boolean isAvailable, String boothId) {
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("price", price);
        product.put("category", category);
        product.put("imageUrl", imageUrl);
        product.put("isAvailable", isAvailable);
        product.put("boothId", boothId);
        return product;
    }

    private Map<String, Object> createTransaction(String userId, String boothId, int amount) {
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("userId", userId);
        transaction.put("boothId", boothId);
        transaction.put("amount", amount);
        transaction.put("timestamp", System.currentTimeMillis());
        transaction.put("status", "completed");
        return transaction;
    }

    private Map<String, Object> createCardStatus(boolean isActive, long lastUsed) {
        Map<String, Object> status = new HashMap<>();
        status.put("isActive", isActive);
        status.put("lastUsed", lastUsed);
        return status;
    }

    private void updateTransactionIndexes(String transactionId, String userId, String boothId) {
        Map<String, Object> indexUpdates = new HashMap<>();
        indexUpdates.put("/user_transactions/" + userId + "/" + transactionId, true);
        indexUpdates.put("/booth_transactions/" + boothId + "/" + transactionId, true);
        rootRef.updateChildren(indexUpdates);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}