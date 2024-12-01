package com.example.nfcpro.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FirebaseInitializer {

    public static void initializeDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://nfctogo-f4da1-default-rtdb.firebaseio.com/");
        DatabaseReference rootRef = database.getReference().child("nfcpro");

        // 부스 정보 초기화
        DatabaseReference boothsRef = rootRef.child("booths");

        Map<String, Object> booth1 = new HashMap<>();
        booth1.put("id", "booth1");
        booth1.put("name", "푸드트럭 1");
        booth1.put("description", "수제버거와 감자튀김");
        booth1.put("location", "A구역");
        booth1.put("ownerUid", "owner1");

        Map<String, Object> booth2 = new HashMap<>();
        booth2.put("id", "booth2");
        booth2.put("name", "음료 부스");
        booth2.put("description", "시원한 음료와 디저트");
        booth2.put("location", "B구역");
        booth2.put("ownerUid", "owner2");

        boothsRef.child("booth1").setValue(booth1);
        boothsRef.child("booth2").setValue(booth2);

        // 사용자 데이터 초기화
        DatabaseReference usersRef = rootRef.child("users");

        Map<String, Object> user1 = new HashMap<>();
        user1.put("uid", "user1");
        user1.put("name", "홍길동");
        user1.put("email", "hong@example.com");
        user1.put("balance", 100000);
        user1.put("cardKey", "a1b2c3d4e5f6g7h8");
        user1.put("role", "customer"); // customer or booth_owner

        Map<String, Object> boothOwner1 = new HashMap<>();
        boothOwner1.put("uid", "owner1");
        boothOwner1.put("name", "김사장");
        boothOwner1.put("email", "kim@example.com");
        boothOwner1.put("boothId", "booth1");
        boothOwner1.put("role", "booth_owner");

        usersRef.child("user1").setValue(user1);
        usersRef.child("owner1").setValue(boothOwner1);

        // 상품 데이터 초기화
        DatabaseReference productsRef = rootRef.child("products");

        Map<String, Object> product1 = new HashMap<>();
        product1.put("id", "prod1");
        product1.put("boothId", "booth1");
        product1.put("name", "클래식 버거");
        product1.put("price", 8000);
        product1.put("imageUrl", "drawable/_060");
        product1.put("isAvailable", true);

        Map<String, Object> product2 = new HashMap<>();
        product2.put("id", "prod2");
        product2.put("boothId", "booth1");
        product2.put("name", "치즈 버거");
        product2.put("price", 9000);
        product2.put("imageUrl", "drawable/_060ti");
        product2.put("isAvailable", true);

        productsRef.child("prod1").setValue(product1);
        productsRef.child("prod2").setValue(product2);

        // 거래 이력 초기화
        DatabaseReference transactionsRef = rootRef.child("transactions");
        long currentTime = System.currentTimeMillis();

        // 부스별 거래 이력
        DatabaseReference boothTransactionsRef = rootRef.child("booth_transactions");

        // 샘플 거래 생성
        String transactionId = UUID.randomUUID().toString();

        Map<String, Object> transaction = new HashMap<>();
        transaction.put("transactionId", transactionId);
        transaction.put("userId", "user1");
        transaction.put("boothId", "booth1");
        transaction.put("amount", 17000);
        transaction.put("timestamp", currentTime);
        transaction.put("items", createSampleOrderItems());
        transaction.put("status", "completed");

        // 전체 거래 이력에 저장
        transactionsRef.child(transactionId).setValue(transaction);

        // 부스별 거래 이력에 저장
        boothTransactionsRef.child("booth1").child(transactionId).setValue(transaction);

        // 사용자별 거래 이력에 저장
        DatabaseReference userTransactionsRef = rootRef.child("user_transactions");
        userTransactionsRef.child("user1").child(transactionId).setValue(transaction);

        // 카드키 매핑 테이블
        DatabaseReference cardKeysRef = rootRef.child("card_keys");

        Map<String, Object> cardKey = new HashMap<>();
        cardKey.put("userId", "user1");
        cardKey.put("isActive", true);
        cardKey.put("lastUsed", currentTime);

        cardKeysRef.child("a1b2c3d4e5f6g7h8").setValue(cardKey);
    }

    private static Map<String, Object> createSampleOrderItems() {
        Map<String, Object> items = new HashMap<>();

        Map<String, Object> item1 = new HashMap<>();
        item1.put("productId", "prod1");
        item1.put("name", "클래식 버거");
        item1.put("quantity", 1);
        item1.put("price", 8000);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("productId", "prod2");
        item2.put("name", "치즈 버거");
        item2.put("quantity", 1);
        item2.put("price", 9000);

        items.put("item1", item1);
        items.put("item2", item2);

        return items;
    }
}

/*
데이터베이스 구조:

/nfcpro
    /booths
        /booth1
            id: "booth1"
            name: "푸드트럭 1"
            description: "수제버거와 감자튀김"
            location: "A구역"
            ownerUid: "owner1"

    /users
        /user1
            uid: "user1"
            name: "홍길동"
            balance: 100000
            cardKey: "a1b2c3d4e5f6g7h8"
            role: "customer"
        /owner1
            uid: "owner1"
            name: "김사장"
            boothId: "booth1"
            role: "booth_owner"

    /products
        /prod1
            id: "prod1"
            boothId: "booth1"
            name: "클래식 버거"
            price: 8000
            imageUrl: "drawable/_060"
            isAvailable: true

    /transactions
        /{transactionId}
            transactionId: "uuid"
            userId: "user1"
            boothId: "booth1"
            amount: 17000
            timestamp: 1234567890
            items: {
                item1: {
                    productId: "prod1"
                    name: "클래식 버거"
                    quantity: 1
                    price: 8000
                }
            }
            status: "completed"

    /booth_transactions
        /booth1
            /{transactionId}: {...}

    /user_transactions
        /user1
            /{transactionId}: {...}

    /card_keys
        /a1b2c3d4e5f6g7h8
            userId: "user1"
            isActive: true
            lastUsed: 1234567890
*/