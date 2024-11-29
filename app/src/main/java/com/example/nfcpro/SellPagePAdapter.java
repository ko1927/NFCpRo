package com.example.nfcpro;


import android.content.Context;
import android.graphics.Color;
import android.os.Debug;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SellPagePAdapter extends RecyclerView.Adapter<SellPagePAdapter.ProductViewHolder> {
    private List<Product> products;
    private Context context;

    public SellPagePAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle;
        TextView productPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            productPrice = itemView.findViewById(R.id.productPrice);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productTitle.setText(product.getTitle());
        holder.productPrice.setText(product.getPrice());
        holder.productImage.setImageResource(product.getImageUrl());
//        // 이미지 로딩 (Glide 사용)
//        Glide.with(context)
//                .load(product.getImageUrl())
//                .placeholder(R.drawable.placeholder) // 플레이스홀더 이미지
//                .error(R.drawable.error) // 에러 이미지
//                .into(holder.productImage);

        // 아이템 클릭 리스너
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 상품 클릭 처리
                v.setBackgroundColor(Color.BLUE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}