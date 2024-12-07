package com.example.nfcpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InBoothDetail extends AppCompatActivity {
    private RecyclerView recyclerView;
    private INBOOTHDETAILADAPTER adapter;
    private List<Product> products;
    private DatabaseReference databaseRef;
    private String selectedBoothId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_booth_detail);

        // Intent에서 선택된 부스 ID 가져오기
        selectedBoothId = getIntent().getStringExtra("boothId");
        if (selectedBoothId == null) {
            Toast.makeText(this, "부스 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeFirebase();
        initializeViews();
        loadBoothProducts();
    }

    private void initializeFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://nfctogo-f4da1-default-rtdb.firebaseio.com/");
        databaseRef = database.getReference().child("nfcpro");
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewS);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        products = new ArrayList<>();
        adapter = new INBOOTHDETAILADAPTER(this, products);
        recyclerView.setAdapter(adapter);
    }

    private void loadBoothProducts() {
        DatabaseReference boothProductsRef = databaseRef.child("booth_products").child(selectedBoothId);
        boothProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot boothProductsSnapshot) {
                products.clear();

                for (DataSnapshot productIdSnapshot : boothProductsSnapshot.getChildren()) {
                    String productId = productIdSnapshot.getKey();
                    Boolean isAvailable = productIdSnapshot.getValue(Boolean.class);

                    if (Boolean.TRUE.equals(isAvailable)) {
                        loadProductDetails(productId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                handleDatabaseError(error);
            }
        });
    }

    private void loadProductDetails(String productId) {
        databaseRef.child("products").child(productId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot productSnapshot) {
                        if (productSnapshot.exists()) {
                            String boothId = productSnapshot.child("boothId").getValue(String.class);

                            // 해당 부스의 상품인지 확인
                            if (selectedBoothId.equals(boothId)) {
                                String name = productSnapshot.child("name").getValue(String.class);
                                Integer price = productSnapshot.child("price").getValue(Integer.class);
                                String imageUrl = productSnapshot.child("imageUrl").getValue(String.class);
                                Boolean isAvailable = productSnapshot.child("isAvailable").getValue(Boolean.class);

                                if (Boolean.TRUE.equals(isAvailable)) {
                                    String formattedPrice = String.format(Locale.KOREA, "%,d원", price != null ? price : 0);

                                    Product product = new Product(
                                            name != null ? name : "알 수 없는 상품",
                                            formattedPrice,
                                            imageUrl != null ? imageUrl : ""
                                    );

                                    products.add(product);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        handleDatabaseError(error);
                    }
                });
    }

    private void handleDatabaseError(DatabaseError error) {
        Toast.makeText(this,
                "데이터를 불러오는 중 오류가 발생했습니다: " + error.getMessage(),
                Toast.LENGTH_SHORT).show();
    }
}