package com.example.nfcpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PaymentDetailActivity extends AppCompatActivity {
    private static final String TAG = "PaymentDetailActivity";
    private RecyclerView recyclerViewProducts;
    private PaymentProductAdapter productAdapter;
    private DatabaseReference databaseRef;
    private String transactionId;
    private SessionManager sessionManager;
    private String currentBoothId;
    private List<PaymentProductData> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);

        sessionManager = new SessionManager(this);
        SessionManager.SessionData sessionData = sessionManager.getSession();
        if (sessionData != null) {
            currentBoothId = sessionData.getBoothId();
        }

        productList = new ArrayList<>();
        initializeFirebase();
        initializeViews();
        loadPaymentDetails();
    }

    private void initializeFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://nfctogo-f4da1-default-rtdb.firebaseio.com/");
        databaseRef = database.getReference().child("nfcpro");
    }

    private void initializeViews() {
        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());

        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new PaymentProductAdapter();
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void loadPaymentDetails() {
        Intent intent = getIntent();
        transactionId = intent.getStringExtra("orderId");
        String orderName = intent.getStringExtra("orderName");
        String paymentTime = intent.getStringExtra("paymentTime");
        String amount = intent.getStringExtra("amount");

        TextView textOrderNumber = findViewById(R.id.textOrderNumber);
        TextView textOrderer = findViewById(R.id.textOrderer);
        TextView textPaymentTime = findViewById(R.id.textPaymentTime);
        TextView textAmount = findViewById(R.id.textAmount);

        textOrderNumber.setText(transactionId);
        textOrderer.setText(orderName);
        textPaymentTime.setText(paymentTime);
        textAmount.setText(amount);

        loadTransactionItems();
    }

    private void loadTransactionItems() {
        DatabaseReference transactionItemsRef = databaseRef.child("transaction_items").child(transactionId);
        transactionItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot transactionSnapshot) {
                productList.clear();

                for (DataSnapshot itemSnapshot : transactionSnapshot.getChildren()) {
                    try {
                        // 직접 transaction_items에서 모든 정보 가져오기
                        String name = itemSnapshot.child("name").getValue(String.class);
                        Integer quantity = itemSnapshot.child("quantity").getValue(Integer.class);
                        Integer price = itemSnapshot.child("price").getValue(Integer.class);
                        String productId = itemSnapshot.child("productId").getValue(String.class);

                        if (name != null && quantity != null && price != null) {
                            String formattedPrice = String.format(Locale.KOREA, "%,d원", price);

                            PaymentProductData productData = new PaymentProductData(
                                    "drawable/_060", // 기본 이미지 URL
                                    name,
                                    quantity,
                                    formattedPrice
                            );

                            productList.add(productData);
                            Log.d(TAG, "상품 추가: " + name + ", 수량: " + quantity + ", 가격: " + price);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "상품 데이터 처리 실패: " + e.getMessage());
                    }
                }

                // 모든 상품 정보를 한번에 업데이트
                if (!productList.isEmpty()) {
                    productAdapter.setProducts(productList);
                } else {
                    Toast.makeText(PaymentDetailActivity.this,
                            "주문 상품이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                handleDatabaseError(error);
            }
        });
    }

    private void handleDatabaseError(DatabaseError error) {
        Log.e(TAG, "데이터베이스 오류: " + error.getMessage());
        Toast.makeText(this,
                "데이터를 불러오는 중 오류가 발생했습니다: " + error.getMessage(),
                Toast.LENGTH_SHORT).show();
    }
}