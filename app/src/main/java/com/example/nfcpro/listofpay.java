package com.example.nfcpro;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class listofpay extends Fragment {
    private RecyclerView recyclerView;
    private PaymentHistoryAdapter adapter;
    private DatabaseReference databaseRef;
    private SessionManager sessionManager;
    private String currentBoothId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listofpay, container, false);

        // 세션 매니저 초기화
        sessionManager = new SessionManager(requireContext());

        // 현재 로그인된 부스 정보 확인
        SessionManager.SessionData sessionData = sessionManager.getSession();
        if (sessionData == null) {
            Toast.makeText(requireContext(), "세션이 만료되었습니다. 다시 로그인해주세요.", Toast.LENGTH_LONG).show();
            // TODO: 로그인 화면으로 이동
            return view;
        }

        currentBoothId = sessionData.getBoothId();
        initializeFirebase();
        initializeViews(view);
        loadBoothTransactions();
        return view;
    }

    private void initializeFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://nfctogo-f4da1-default-rtdb.firebaseio.com/");
        databaseRef = database.getReference().child("nfcpro");
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewPayments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PaymentHistoryAdapter(payment -> {
            Intent intent = new Intent(getActivity(), PaymentDetailActivity.class);
            intent.putExtra("orderId", payment.getOrderId());
            intent.putExtra("orderName", payment.getOrderName());
            intent.putExtra("paymentTime", payment.getPaymentTime());
            intent.putExtra("amount", payment.getAmount());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadBoothTransactions() {
        DatabaseReference boothTransactionsRef = databaseRef.child("booth_transactions").child(currentBoothId);
        boothTransactionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot boothTransactionsSnapshot) {
                List<PaymentData> paymentList = new ArrayList<>();

                for (DataSnapshot transactionSnapshot : boothTransactionsSnapshot.getChildren()) {
                    String transactionId = transactionSnapshot.getKey();

                    // 거래 상세 정보 조회
                    databaseRef.child("transactions").child(transactionId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot transactionDetailSnapshot) {
                                    if (transactionDetailSnapshot.exists()) {
                                        String userId = transactionDetailSnapshot.child("userId").getValue(String.class);
                                        Long timestamp = transactionDetailSnapshot.child("timestamp").getValue(Long.class);
                                        Integer amount = transactionDetailSnapshot.child("amount").getValue(Integer.class);

                                        // 사용자 정보 조회
                                        databaseRef.child("users").child(userId)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot userSnapshot) {
                                                        String userName = userSnapshot.child("name").getValue(String.class);

                                                        String formattedTime = formatTimestamp(timestamp);
                                                        String formattedAmount = formatAmount(amount);

                                                        PaymentData paymentData = new PaymentData(
                                                                transactionId,
                                                                userName != null ? userName + "님의 주문" : "알 수 없는 사용자",
                                                                formattedTime,
                                                                formattedAmount
                                                        );

                                                        paymentList.add(paymentData);

                                                        // 최신 순으로 정렬
                                                        Collections.sort(paymentList, (p1, p2) ->
                                                                p2.getPaymentTime().compareTo(p1.getPaymentTime()));
                                                        adapter.setPayments(paymentList);
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError error) {
                                                        handleDatabaseError(error);
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    handleDatabaseError(error);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                handleDatabaseError(error);
            }
        });
    }

    private void handleDatabaseError(DatabaseError error) {
        Toast.makeText(requireContext(),
                "데이터를 불러오는 중 오류가 발생했습니다: " + error.getMessage(),
                Toast.LENGTH_SHORT).show();
    }

    private String formatTimestamp(Long timestamp) {
        if (timestamp == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA);
        return sdf.format(new Date(timestamp));
    }

    private String formatAmount(Integer amount) {
        if (amount == null) return "0원";
        return String.format(Locale.KOREA, "%,d원", amount);
    }
}