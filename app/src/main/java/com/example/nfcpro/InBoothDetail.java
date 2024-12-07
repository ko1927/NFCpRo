package com.example.nfcpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InBoothDetail extends AppCompatActivity {
    private RecyclerView recyclerView;
    private INBOOTHDETAILADAPTER adapter;
    private List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_booth_detail);

        recyclerView = findViewById(R.id.recyclerViewS);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        initializeProducts();

        adapter = new INBOOTHDETAILADAPTER(this, products);
        recyclerView.setAdapter(adapter);
    }

    private void initializeProducts() {
        products = new ArrayList<>();
//        products.add(new Product("상품1", "10000원", R.drawable._060));
//        products.add(new Product("상품2", "20000원", R.drawable._060ti));
//        products.add(new Product("상품1", "10000원", R.drawable._060));
//        products.add(new Product("상품2", "20000원", R.drawable._060ti));
//        products.add(new Product("상품1", "10000원", R.drawable._060));
//        products.add(new Product("상품2", "20000원", R.drawable._060ti));
//        products.add(new Product("상품1", "10000원", R.drawable._060));
//        products.add(new Product("상품2", "20000원", R.drawable._060ti));
    }
}