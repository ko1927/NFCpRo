package com.example.nfcpro;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nfcpro.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class sellpage extends Fragment {
    private RecyclerView recyclerView2;
    private SellPagePAdapter adapter;
    private List<Product> products;
    private SessionManager sessionManager;
    private DatabaseReference databaseRef;
    private String boothId;

    public sellpage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireContext());

        // Firebase 초기화
        databaseRef = FirebaseDatabase.getInstance().getReference().child("nfcpro");

        // 세션에서 부스 정보 가져오기
        SessionManager.SessionData sessionData = sessionManager.getSession();
        if (sessionData != null) {
            boothId = sessionData.getBoothId();
        } else {
            // 세션 정보가 없으면 로그인 페이지로 이동
            redirectToLogin();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sellpage, container, false);

        recyclerView2 = view.findViewById(R.id.recyclerView);
        recyclerView2.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        products = new ArrayList<>();
        adapter = new SellPagePAdapter(getActivity(), products);
        recyclerView2.setAdapter(adapter);

        Button priceButton = view.findViewById(R.id.priceButton);
        priceButton.setOnClickListener(v -> processCheckout());

        // Firebase에서 상품 정보 로드
        loadProducts();

        return view;
    }

    private void loadProducts() {
        if (boothId == null) return;

        // 부스의 상품 목록 가져오기
        databaseRef.child("booth_products").child(boothId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot boothProductsSnapshot) {
                products.clear();

                for (DataSnapshot productIdSnapshot : boothProductsSnapshot.getChildren()) {
                    String productId = productIdSnapshot.getKey();

                    // 각 상품의 상세 정보 가져오기
                    databaseRef.child("products").child(productId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot productSnapshot) {
                                    if (productSnapshot.exists() &&
                                            Boolean.TRUE.equals(productSnapshot.child("isAvailable").getValue(Boolean.class))) {

                                        String name = productSnapshot.child("name").getValue(String.class);
                                        Long price = productSnapshot.child("price").getValue(Long.class);
                                        String imageUrl = productSnapshot.child("imageUrl").getValue(String.class);

                                        // 이미지 리소스 ID 가져오기
                                        int imageResId = getResources().getIdentifier(
                                                imageUrl,
                                                "drawable",
                                                requireContext().getPackageName()
                                        );

                                        products.add(new Product(
                                                name,
                                                price + "원",
                                                imageResId
                                        ));

                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    showError("상품 정보를 불러오는데 실패했습니다");
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showError("상품 목록을 불러오는데 실패했습니다");
            }
        });
    }

    private void processCheckout() {
        Map<Integer, Integer> selections = adapter.getAllSelections();
        ArrayList<SelectedProduct> selectedProducts = new ArrayList<>();
        int totalAmount = 0;

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

        if (selectedProducts.isEmpty()) {
            showError("선택된 상품이 없습니다");
            return;
        }

        Intent intent = new Intent(getActivity(), sellandbuy.class);
        intent.putExtra("selectedProducts", selectedProducts);
        intent.putExtra("totalAmount", totalAmount);
        intent.putExtra("BOOTH_ID", boothId);
        startActivity(intent);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LoginAdmin.class);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}