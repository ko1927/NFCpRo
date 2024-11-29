package com.example.nfcpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class SellingPage extends AppCompatActivity {

    private RecyclerView recyclerView2;
    private SellPagePAdapter adapter;
    private List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sellingpage);

        // RecyclerView 초기화
        recyclerView2 = findViewById(R.id.recyclerView);
        recyclerView2.setLayoutManager(new GridLayoutManager(this, 2));

        // 상품 데이터 초기화
        initializeProducts();

        // 어댑터 설정
        adapter = new SellPagePAdapter(this, products);
        recyclerView2.setAdapter(adapter);

        // 결제 버튼 설정
        Button priceButton = findViewById(R.id.priceButton);
        priceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 결제 처리
            }
        });
    }

    private void initializeProducts() {
        products = new ArrayList<>();
        products.add(new Product("상품1", "10,000원", R.drawable._060));
        products.add(new Product("상품2", "20,000원", R.drawable._060ti));
        products.add(new Product("상품1", "10,000원", R.drawable._060));
        products.add(new Product("상품2", "20,000원", R.drawable._060ti));
        products.add(new Product("상품1", "10,000원", R.drawable._060));
        products.add(new Product("상품2", "20,000원", R.drawable._060ti));
        products.add(new Product("상품1", "10,000원", R.drawable._060));
        products.add(new Product("상품2", "20,000원", R.drawable._060ti));
        // 더 많은 상품 추가
    }
}