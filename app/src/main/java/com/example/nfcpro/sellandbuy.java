package com.example.nfcpro;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class sellandbuy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellandbuy);

        // 전달받은 데이터 가져오기
        ArrayList<SelectedProduct> selectedProducts =
                (ArrayList<SelectedProduct>) getIntent().getSerializableExtra("selectedProducts");
        int totalAmount = getIntent().getIntExtra("totalAmount", 0);

        // 총액 표시
        TextView totalAmountView = findViewById(R.id.totalAmountTextView);
        totalAmountView.setText(String.format("%,d원", totalAmount));

        // 선택된 상품 목록 표시
        RecyclerView cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CartAdapter cartAdapter = new CartAdapter(selectedProducts);
        cartRecyclerView.setAdapter(cartAdapter);
    }
}