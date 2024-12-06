package com.example.nfcpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PaymentDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProducts;
    private PaymentProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);

        initializeViews();
        loadPaymentDetails();
        loadProductList();
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
        String orderId = intent.getStringExtra("orderId");
        String orderName = intent.getStringExtra("orderName");
        String paymentTime = intent.getStringExtra("paymentTime");
        String amount = intent.getStringExtra("amount");

        TextView textOrderNumber = findViewById(R.id.textOrderNumber);
        TextView textOrderer = findViewById(R.id.textOrderer);
        TextView textPaymentTime = findViewById(R.id.textPaymentTime);
        TextView textAmount = findViewById(R.id.textAmount);

        textOrderNumber.setText(orderId);
        textOrderer.setText(orderName);
        textPaymentTime.setText(paymentTime);
        textAmount.setText(amount);
    }

    private void loadProductList() {
        // 더미 데이터 생성
        List<PaymentProductData> dummyProducts = new ArrayList<>();
        dummyProducts.add(new PaymentProductData("image_url", "물품이름", 1, "가격"));
        dummyProducts.add(new PaymentProductData("image_url", "물품이름", 2, "가격"));
        dummyProducts.add(new PaymentProductData("image_url", "물품이름", 1, "가격"));

        productAdapter.setProducts(dummyProducts);
    }


}