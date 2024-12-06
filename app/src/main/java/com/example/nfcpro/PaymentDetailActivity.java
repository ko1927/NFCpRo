package com.example.nfcpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PaymentDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);

        initializeViews();
        loadPaymentDetails();
    }

    private void initializeViews() {
        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
}