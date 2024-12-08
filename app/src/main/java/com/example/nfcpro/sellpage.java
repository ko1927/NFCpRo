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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class sellpage extends Fragment implements SellPagePAdapter.OnItemLongClickListener {
    private RecyclerView recyclerView2;
    private SellPagePAdapter adapter;
    private List<Product> products;
    private List<String> productIds;
    private SessionManager sessionManager;
    private DatabaseReference databaseRef;
    private String boothId;
    private FloatingActionButton addProductButton;
    private Button priceButton;
    private ValueEventListener productsListener;
    private boolean isDataLoaded = false;

    public sellpage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        databaseRef = FirebaseDatabase.getInstance().getReference().child("nfcpro");

        SessionManager.SessionData sessionData = sessionManager.getSession();
        if (sessionData != null) {
            boothId = sessionData.getBoothId();
        } else {
            redirectToLogin();
        }

        products = new ArrayList<>();
        productIds = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sellpage, container, false);

        recyclerView2 = view.findViewById(R.id.recyclerView);
        recyclerView2.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        adapter = new SellPagePAdapter(getActivity(), products);
        adapter.setOnItemLongClickListener(this);
        adapter.setOnSelectionChangedListener(this::updatePriceButton);
        recyclerView2.setAdapter(adapter);

        addProductButton = view.findViewById(R.id.addProductButton);
        addProductButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), ProductManageActivity.class);
                startActivity(intent);
            }
        });

        priceButton = view.findViewById(R.id.priceButton);
        priceButton.setOnClickListener(v -> processCheckout());
        updatePriceButton();

        if (!isDataLoaded) {
            loadProducts();
        }

        return view;
    }

    private void updatePriceButton() {
        if (!isAdded() || priceButton == null) return;

        Map<Integer, Integer> selections = adapter.getAllSelections();
        int totalAmount = 0;

        for (Map.Entry<Integer, Integer> entry : selections.entrySet()) {
            int position = entry.getKey();
            int quantity = entry.getValue();

            if (position < products.size() && quantity > 0) {
                Product product = products.get(position);
                String priceString = product.getPrice().replaceAll("[^0-9]", "");
                int price = Integer.parseInt(priceString);
                totalAmount += price * quantity;
            }
        }

        if (totalAmount > 0) {
            priceButton.setText(String.format("%,d원 결제하기", totalAmount));
        } else {
            priceButton.setText("결제하기");
        }
    }

    private void loadProducts() {
        if (boothId == null || !isAdded()) return;

        if (productsListener != null) {
            databaseRef.child("booth_products").child(boothId).removeEventListener(productsListener);
        }

        productsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot boothProductsSnapshot) {
                if (!isAdded()) return;

                products.clear();
                productIds.clear();

                for (DataSnapshot productIdSnapshot : boothProductsSnapshot.getChildren()) {
                    String productId = productIdSnapshot.getKey();
                    loadProductDetails(productId);
                }
                isDataLoaded = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded()) {
                    showError("상품 목록을 불러오는데 실패했습니다");
                }
            }
        };

        databaseRef.child("booth_products").child(boothId).addValueEventListener(productsListener);
    }

    private void loadProductDetails(String productId) {
        if (!isAdded()) return;

        databaseRef.child("products").child(productId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot productSnapshot) {
                        if (!isAdded()) return;

                        try {
                            if (productSnapshot.exists() &&
                                    Boolean.TRUE.equals(productSnapshot.child("isAvailable").getValue(Boolean.class))) {

                                String name = productSnapshot.child("name").getValue(String.class);
                                Long price = productSnapshot.child("price").getValue(Long.class);
                                String imageUrl = productSnapshot.child("imageUrl").getValue(String.class);

                                if (getContext() != null) {
                                    Product newProduct = new Product(
                                            name,
                                            price + "원",
                                            imageUrl
                                    );
                                    products.add(newProduct);
                                    productIds.add(productId);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        } catch (Exception e) {
                            if (isAdded()) {
                                showError("상품 정보 처리 중 오류가 발생했습니다");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (isAdded()) {
                            showError("상품 정보를 불러오는데 실패했습니다");
                        }
                    }
                });
    }

    private void processCheckout() {
        if (!isAdded()) return;

        Map<Integer, Integer> selections = adapter.getAllSelections();
        ArrayList<SelectedProduct> selectedProducts = new ArrayList<>();
        int totalAmount = 0;

        for (Map.Entry<Integer, Integer> entry : selections.entrySet()) {
            int position = entry.getKey();
            int quantity = entry.getValue();

            if (quantity > 0 && position < products.size()) {
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

    @Override
    public void onItemLongClick(int position) {
        if (!isAdded() || getActivity() == null) return;

        if (position < productIds.size()) {
            String productId = productIds.get(position);
            Intent intent = new Intent(getActivity(), ProductManageActivity.class);
            intent.putExtra("PRODUCT_ID", productId);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (productsListener != null && boothId != null) {
            databaseRef.child("booth_products").child(boothId).removeEventListener(productsListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            loadProducts();
            if (adapter != null) {
                adapter.clearAllSelections();
                updatePriceButton();
            }
        }
    }

    private void redirectToLogin() {
        if (!isAdded() || getActivity() == null) return;
        Intent intent = new Intent(getActivity(), LoginAdmin.class);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void showError(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}