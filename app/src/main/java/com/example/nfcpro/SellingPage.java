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
import java.io.Serializable;

public class SellingPage extends AppCompatActivity {
    private RecyclerView recyclerView2;
    private SellPagePAdapter adapter;
    private List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sellingpage);

        recyclerView2 = findViewById(R.id.recyclerView);
        recyclerView2.setLayoutManager(new GridLayoutManager(this, 2));

        initializeProducts();

        adapter = new SellPagePAdapter(this, products);
        recyclerView2.setAdapter(adapter);

        Button priceButton = findViewById(R.id.priceButton);
        priceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processCheckout();
            }
        });
    }

    private void processCheckout() {
        Map<Integer, Integer> selections = adapter.getAllSelections();
        ArrayList<SelectedProduct> selectedProducts = new ArrayList<>();
        int totalAmount = 0;

        // 선택된 상품들의 정보 수집
        for (Map.Entry<Integer, Integer> entry : selections.entrySet()) {
            int position = entry.getKey();
            int quantity = entry.getValue();

            if (quantity > 0) {
                Product product = products.get(position);
                String priceString = product.getPrice().replaceAll("[^0-9]", "");
                int price = Integer.parseInt(priceString);
                totalAmount += price * quantity;

                selectedProducts.add(new SelectedProduct(
                        product.getTitle(),
                        product.getPrice(),
                        quantity,
                        product.getImageUrl()
                ));
            }
        }

        // 결제 페이지로 이동
        Intent intent = new Intent(SellingPage.this, sellandbuy.class);
        intent.putExtra("selectedProducts", selectedProducts);
        intent.putExtra("totalAmount", totalAmount);
        startActivity(intent);
    }

    private void initializeProducts() {
        products = new ArrayList<>();
        products.add(new Product("상품1", "10000원", R.drawable._060));
        products.add(new Product("상품2", "20000원", R.drawable._060ti));
        products.add(new Product("상품1", "10000원", R.drawable._060));
        products.add(new Product("상품2", "20000원", R.drawable._060ti));
        products.add(new Product("상품1", "10000원", R.drawable._060));
        products.add(new Product("상품2", "20000원", R.drawable._060ti));
        products.add(new Product("상품1", "10000원", R.drawable._060));
        products.add(new Product("상품2", "20000원", R.drawable._060ti));
    }
}