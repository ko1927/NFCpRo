package com.example.nfcpro;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class sellandbuy extends AppCompatActivity implements CartAdapter.OnCartUpdateListener {

    private CartAdapter cartAdapter;
    private TextView totalAmountView;
    private ArrayList<SelectedProduct> selectedProducts;
    private int totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellandbuy);

        // 전달받은 데이터 가져오기
        selectedProducts = (ArrayList<SelectedProduct>) getIntent().getSerializableExtra("selectedProducts");
        totalAmount = getIntent().getIntExtra("totalAmount", 0);

        // 뒤로가기 버튼 설정
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // RecyclerView 설정
        RecyclerView cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // CartAdapter 초기화 시 Context 전달
        cartAdapter = new CartAdapter(this, selectedProducts);  // 여기만 수정
        cartAdapter.setOnCartUpdateListener(this);
        cartRecyclerView.setAdapter(cartAdapter);

        // 총액 표시 뷰 초기화
        totalAmountView = findViewById(R.id.totalAmountTextView);
        updateTotalAmount();

        // 결제 버튼 설정
        findViewById(R.id.paymentButton).setOnClickListener(v -> processPayment());
    }

    private void updateTotalAmount() {
        totalAmount = 0;
        for (SelectedProduct product : selectedProducts) {
            String priceString = product.getPrice().replaceAll("[^0-9]", "");
            int price = Integer.parseInt(priceString);
            totalAmount += price * product.getQuantity();
        }
        if (totalAmount > 0) {
            totalAmountView.setText(String.format("%,d원 결제하기", totalAmount));
        } else {
            totalAmountView.setText("장바구니가 비었습니다");
            findViewById(R.id.paymentButton).setEnabled(false);
        }
    }

    @Override
    public void onItemRemoved(SelectedProduct product, int position) {
        // 삭제된 상품의 가격 계산
        String priceString = product.getPrice().replaceAll("[^0-9]", "");
        int price = Integer.parseInt(priceString);
        totalAmount -= price * product.getQuantity();

        // UI 업데이트
        updateTotalAmount();

        // 모든 상품이 삭제된 경우
        if (selectedProducts.isEmpty()) {
            finish(); // 또는 다른 처리 (예: 장바구니가 비었다는 메시지 표시)
        }
    }

    @Override
    public void onQuantityChanged(int position, int newQuantity) {
        updateTotalAmount();
    }

    private void processPayment() {
        if (totalAmount > 0) {
            PaymentHandler paymentHandler = new PaymentHandler(this);
            paymentHandler.startPaymentProcess();
        }
    }
}