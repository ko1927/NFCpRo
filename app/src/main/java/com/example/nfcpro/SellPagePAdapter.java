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

public class SellPagePAdapter extends RecyclerView.Adapter<SellPagePAdapter.ProductViewHolder> {
    private List<Product> products;
    private Context context;
    private Map<Integer, Integer> selectionCount; // Maps product position to selection count

    public SellPagePAdapter(Context context, List<Product> products) {
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
//        holder.productImage.setImageResource(product.getImageUrl());
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placehold) // 로딩 중 표시할 이미지
                .error(R.drawable._060) // 로드 실패시 표시할 이미지
                .into(holder.productImage);

        // Item click listener
        holder.itemView.setOnClickListener(v -> {
            incrementSelection(position);
            notifyItemChanged(position);

            // Show toast with updated count
            int newCount = selectionCount.get(position);
            Toast.makeText(context,
                    product.getTitle() + " selected: " + newCount + " times",
                    Toast.LENGTH_SHORT).show();
        });
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



    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    private OnItemLongClickListener onItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // 기존 ViewHolder 코드에 아래 내용 추가

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 기존 초기화 코드

            itemView.setOnLongClickListener(v -> {
                if (onItemLongClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemLongClickListener.onItemLongClick(position);
                        return true;
                    }
                }
                return false;
            });
        }
    }
}