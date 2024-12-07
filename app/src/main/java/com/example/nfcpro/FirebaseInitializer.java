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
        initializeTransactions();
        initializeCardKeys();
    }

    private void initializeTransactions() {
        DatabaseReference transactionsRef = rootRef.child("transactions");

        // 각 사용자별로 샘플 트랜잭션 생성
        for (int i = 1; i <= 5; i++) {
            String userId = "user" + i;
            createSampleTransactionsForUser(userId, transactionsRef);
        }
    }

    private void createSampleTransactionsForUser(String userId, DatabaseReference transactionsRef) {
        // 현재 시간을 기준으로 최근 일주일치 트랜잭션 생성
        long currentTime = System.currentTimeMillis();
        long oneDay = 24 * 60 * 60 * 1000; // 밀리초 단위의 하루

        for (int day = 0; day < 7; day++) {
            // 각 날짜별 1-3개의 랜덤 트랜잭션 생성
            int transactionsPerDay = 1 + new Random().nextInt(3);

            for (int t = 0; t < transactionsPerDay; t++) {
                String transactionId = UUID.randomUUID().toString();
                createRandomTransaction(userId, transactionId, currentTime - (day * oneDay), transactionsRef);
            }
        }
    }

    private void createRandomTransaction(String userId, String transactionId, long timestamp,
                                         DatabaseReference transactionsRef) {
        // 랜덤한 부스 선택
        int boothNum = 1 + new Random().nextInt(10);
        String boothId = "booth" + boothNum;

        // 트랜잭션 금액 (3000원 ~ 50000원 사이)
        int amount = 3000 + new Random().nextInt(47001);

        // 기본 트랜잭션 정보 생성
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("userId", userId);
        transaction.put("boothId", boothId);
        transaction.put("amount", amount);
        transaction.put("timestamp", timestamp);
        transaction.put("status", "completed");

        // 트랜잭션 저장
        transactionsRef.child(transactionId).setValue(transaction);

        // 트랜잭션 아이템 생성
        createRandomTransactionItems(transactionId, amount, boothId);

        // 인덱스 업데이트
        updateTransactionIndexes(transactionId, userId, boothId);

        // 부스 매출 순위 업데이트
        updateBoothRank(boothId, amount);
    }

    private void createRandomTransactionItems(String transactionId, int totalAmount, String boothId) {
        DatabaseReference transactionItemsRef = rootRef.child("transaction_items").child(transactionId);

        // 1-3개의 랜덤한 아이템 생성
        int itemCount = 1 + new Random().nextInt(3);
        int remainingAmount = totalAmount;

        for (int i = 0; i < itemCount; i++) {
            String itemId = UUID.randomUUID().toString();

            Map<String, Object> item = new HashMap<>();
            int itemPrice;
            if (i == itemCount - 1) {
                itemPrice = remainingAmount;
            } else {
                itemPrice = remainingAmount / (itemCount - i);
                remainingAmount -= itemPrice;
            }

            item.put("productId", "product" + (1 + new Random().nextInt(3))); // 임의의 상품 ID
            item.put("name", getRandomProductName());
            item.put("price", itemPrice);
            item.put("quantity", 1 + new Random().nextInt(3));
            item.put("imageUrl", "drawable/product" + (1 + new Random().nextInt(5)));

            transactionItemsRef.child(itemId).setValue(item);
        }
    }

    private String getRandomProductName() {
        String[] productNames = {
                "클래식 버거", "치즈 버거", "불고기 버거", "아메리카노", "카페라떼",
                "스무디", "샐러드", "파스타", "피자", "치킨"
        };
        return productNames[new Random().nextInt(productNames.length)];
    }

    private void updateTransactionIndexes(String transactionId, String userId, String boothId) {
        Map<String, Object> indexUpdates = new HashMap<>();
        indexUpdates.put("/user_transactions/" + userId + "/" + transactionId, true);
        indexUpdates.put("/booth_transactions/" + boothId + "/" + transactionId, true);
        rootRef.updateChildren(indexUpdates);
    }

    private void updateBoothRank(String boothId, int amount) {
        DatabaseReference rankRef = rootRef.child("rank").child(boothId);
        rankRef.get().addOnSuccessListener(dataSnapshot -> {
            int currentRank = dataSnapshot.exists() ? dataSnapshot.getValue(Integer.class) : 0;
            rankRef.setValue(currentRank + amount);
        });
    }

    private void initializeBooths() {
        DatabaseReference boothsRef = rootRef.child("booths");
        Map<String, Object> boothOwners = new HashMap<>();

        // 10개의 다양한 부스 생성
        String[][] boothData = {
                {"booth1", "푸드트럭 1", "수제버거와 감자튀김", "A구역", "owner1"},
                {"booth2", "음료 부스", "시원한 음료와 디저트", "B구역", "owner2"},
                {"booth3", "한식 부스", "든든한 한식 도시락", "A구역", "owner3"},
                {"booth4", "타코 부스", "멕시칸 타코와 부리또", "C구역", "owner4"},
                {"booth5", "디저트 카페", "달콤한 디저트와 커피", "B구역", "owner5"},
                {"booth6", "분식 부스", "추억의 분식메뉴", "D구역", "owner6"},
                {"booth7", "샐러드 바", "신선한 샐러드와 건강식", "C구역", "owner7"},
                {"booth8", "일식 부스", "정통 일식과 덮밥", "D구역", "owner8"},
                {"booth9", "피자 부스", "화덕피자와 파스타", "E구역", "owner9"},
                {"booth10", "치킨 부스", "바삭한 치킨과 사이드", "E구역", "owner10"}
        };

        for (String[] booth : boothData) {
            String boothId = booth[0];
            Map<String, Object> boothInfo = createBooth(booth[1], booth[2], booth[3], hashPassword(boothId + "pass"));
            boothsRef.child(boothId).setValue(boothInfo);
            boothOwners.put(booth[4], boothId);
        }

        rootRef.child("booth_owners").setValue(boothOwners);
        initializeBoothRanks();
    }

    private void initializeUsers() {
        DatabaseReference usersRef = rootRef.child("users");
        Map<String, String> cardUserMap = new HashMap<>();

        // 20명의 다양한 사용자 생성
        String[][] userData = {
                {"user1", "김고객", "kim1@example.com", "100000"},
                {"user2", "이용자", "lee2@example.com", "150000"},
                {"user3", "박손님", "park3@example.com", "200000"},
                {"user4", "정방문", "jung4@example.com", "80000"},
                {"user5", "최구매", "choi5@example.com", "120000"}
        };

        String[][] ownerData = {
                {"owner1", "김사장", "owner1@example.com"},
                {"owner2", "이운영", "owner2@example.com"},
                {"owner3", "박대표", "owner3@example.com"},
                {"owner4", "정창업", "owner4@example.com"},
                {"owner5", "최경영", "owner5@example.com"}
        };

        // 일반 사용자 생성
        for (String[] user : userData) {
            Map<String, Object> customerData = createUser(user[1], user[2], Integer.parseInt(user[3]), "customer");
            usersRef.child(user[0]).setValue(customerData);
            // 각 사용자별 NFC 카드키 매핑
            String cardKey = generateCardKey(user[0]);
            cardUserMap.put(cardKey, user[0]);
        }

        // 부스 운영자 생성
        for (String[] owner : ownerData) {
            Map<String, Object> ownerData2 = createUser(owner[1], owner[2], 0, "booth_owner");
            usersRef.child(owner[0]).setValue(ownerData2);
        }

        rootRef.child("card_user_map").setValue(cardUserMap);
    }

    private void initializeProducts() {
        DatabaseReference productsRef = rootRef.child("products");
        Map<String, String> categories = new HashMap<>();
        categories.put("food", "음식");
        categories.put("beverage", "음료");
        categories.put("dessert", "디저트");
        categories.put("snack", "분식");
        rootRef.child("product_categories").setValue(categories);

        // 각 부스별 대표 메뉴들 생성
        for (int i = 1; i <= 10; i++) {
            String boothId = "booth" + i;
            List<Map<String, Object>> boothProducts = generateProductsForBooth(boothId);
            Map<String, Object> boothProductMap = new HashMap<>();

            for (Map<String, Object> product : boothProducts) {
                String productId = UUID.randomUUID().toString();
                productsRef.child(productId).setValue(product);
                boothProductMap.put(productId, true);
            }

            rootRef.child("booth_products").child(boothId).setValue(boothProductMap);
        }
    }

    private List<Map<String, Object>> generateProductsForBooth(String boothId) {
        List<Map<String, Object>> products = new ArrayList<>();
        switch(boothId) {
            case "booth1":
                products.add(createProduct("클래식 버거", 8000, "food", "drawable/burger1", true, boothId));
                products.add(createProduct("치즈 버거", 9000, "food", "drawable/burger2", true, boothId));
                products.add(createProduct("감자튀김", 4000, "food", "drawable/fries", true, boothId));
                break;
            case "booth2":
                products.add(createProduct("아메리카노", 4500, "beverage", "drawable/coffee1", true, boothId));
                products.add(createProduct("카페라떼", 5000, "beverage", "drawable/coffee2", true, boothId));
                products.add(createProduct("스무디", 6000, "beverage", "drawable/smoothie", true, boothId));
                break;
            // 나머지 부스들의 제품도 비슷한 방식으로 추가
        }
        return products;
    }

    private void initializeCardKeys() {
        DatabaseReference cardStatusRef = rootRef.child("card_status");
        long currentTime = System.currentTimeMillis();

        // 사용자별 카드 상태 초기화
        for (int i = 1; i <= 5; i++) {
            String cardKey = generateCardKey("user" + i);
            cardStatusRef.child(cardKey).setValue(createCardStatus(true, currentTime));
        }
    }

    private void initializeBoothRanks() {
        Map<String, Integer> ranks = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            ranks.put("booth" + i, 0);
        }
        rootRef.child("rank").setValue(ranks);
    }

    // Helper methods
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
        product.put("stock", 100);
        return product;
    }

    private Map<String, Object> createCardStatus(boolean isActive, long lastUsed) {
        Map<String, Object> status = new HashMap<>();
        status.put("isActive", isActive);
        status.put("lastUsed", lastUsed);
        return status;
    }

    private String generateCardKey(String userId) {
        // 간단한 카드키 생성 로직 (실제 구현시에는 더 복잡한 암호화 사용 필요)
        return userId + "_" + UUID.randomUUID().toString().substring(0, 8);
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