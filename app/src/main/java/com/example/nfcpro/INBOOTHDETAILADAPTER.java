
package com.example.nfcpro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class INBOOTHDETAILADAPTER extends RecyclerView.Adapter<com.example.nfcpro.SellPagePAdapter.ProductViewHolder> {
    private List<Product> products;
    private Context context;
    private Map<Integer, Integer> selectionCount; // Maps product position to selection count

    public INBOOTHDETAILADAPTER(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
        this.selectionCount = new HashMap<>();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle;
        TextView productPrice;
        TextView selectionCountView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            productPrice = itemView.findViewById(R.id.productPrice);
            selectionCountView = itemView.findViewById(R.id.selectionCount);
        }
    }

    @NonNull
    @Override
    public com.example.nfcpro.SellPagePAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new com.example.nfcpro.SellPagePAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.nfcpro.SellPagePAdapter.ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productTitle.setText(product.getTitle());
        holder.productPrice.setText(product.getPrice());
//        holder.productImage.setImageResource(product.getImageUrl());

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placehold) // 로딩 중 표시할 이미지
                .error(R.drawable._060) // 로드 실패시 표시할 이미지
                .into(holder.productImage);
//            // Item click listener
//            holder.itemView.setOnClickListener(v -> {
//                incrementSelection(position);
//                notifyItemChanged(position);
//
//                // Show toast with updated count
//                int newCount = selectionCount.get(position);
//                Toast.makeText(context,
//                        product.getTitle() + " selected: " + newCount + " times",
//                        Toast.LENGTH_SHORT).show();
//            });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    // Method to increment selection count
    private void incrementSelection(int position) {
        int currentCount = selectionCount.getOrDefault(position, 0);
        selectionCount.put(position, currentCount + 1);
    }

    // Method to get total selections for a product
    public int getSelectionCount(int position) {
        return selectionCount.getOrDefault(position, 0);
    }

    // Method to get all selections
    public Map<Integer, Integer> getAllSelections() {
        return new HashMap<>(selectionCount);
    }

    // Method to clear selections
    public void clearSelections() {
        selectionCount.clear();
        notifyDataSetChanged();
    }
}