package com.example.nfcpro;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class sellpage extends Fragment {
    private RecyclerView recyclerView2;
    private SellPagePAdapter adapter;
    private List<Product> products;

    public sellpage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sellpage, container, false);

        recyclerView2 = view.findViewById(R.id.recyclerView);
        recyclerView2.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        initializeProducts();

        adapter = new SellPagePAdapter(getActivity(), products);
        recyclerView2.setAdapter(adapter);

        Button priceButton = view.findViewById(R.id.priceButton);
        priceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processCheckout();
            }
        });

        return view;
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
        Intent intent = new Intent(getActivity(), sellandbuy.class);
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