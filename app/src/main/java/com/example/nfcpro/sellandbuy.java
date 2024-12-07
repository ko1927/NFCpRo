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
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellandbuy);

        // SessionManager 초기화
        sessionManager = new SessionManager(this);

        // 전달받은 데이터 가져오기
        selectedProducts = (ArrayList<SelectedProduct>) getIntent().getSerializableExtra("selectedProducts");
        totalAmount = getIntent().getIntExtra("totalAmount", 0);

        // 뒤로가기 버튼 설정
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // RecyclerView 설정
        RecyclerView cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // CartAdapter 초기화
        cartAdapter = new CartAdapter(this, selectedProducts);
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
            findViewById(R.id.paymentButton).setEnabled(true);
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
            // 세션 확인
            SessionManager.SessionData sessionData = sessionManager.getSession();
            if (sessionData == null || sessionData.getBoothId() == null) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("오류")
                        .setMessage("부스 정보를 찾을 수 없습니다. 다시 로그인해주세요.")
                        .setPositiveButton("확인", (dialog, which) -> finish())
                        .show();
                return;
            }

            // 새로운 PaymentHandler 인스턴스 생성 및 결제 프로세스 시작
            PaymentHandler paymentHandler = new PaymentHandler(this, selectedProducts, totalAmount);
            paymentHandler.startPaymentProcess();
        }
    }
}