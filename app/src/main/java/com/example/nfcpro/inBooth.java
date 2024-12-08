package com.example.nfcpro;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class inBooth extends Fragment {

    private RecyclerView recyclerView;
    private PaymentHistoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_in_booth, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewPayments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PaymentHistoryAdapter(new PaymentHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PaymentData payment) {
                Intent intent = new Intent(getActivity(), InBoothDetail.class);
                intent.putExtra("orderId", payment.getOrderId());
                intent.putExtra("orderName", payment.getOrderName());
                intent.putExtra("paymentTime", payment.getPaymentTime());
                intent.putExtra("amount", payment.getAmount());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        // 더미 데이터 로드
        loadDummyData();
    }

    private void loadDummyData() {
        List<PaymentData> dummyData = new ArrayList<>();
        dummyData.add(new PaymentData("000000000001", "XXX", "2024.03.04 24:00", "99,999원"));
        adapter.setPayments(dummyData);
    }
}