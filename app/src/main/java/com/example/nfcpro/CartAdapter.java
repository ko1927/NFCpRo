package com.example.nfcpro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private ArrayList<SelectedProduct> selectedProducts;
    private OnCartUpdateListener onCartUpdateListener;
    private Context context;

    public interface OnCartUpdateListener {
        void onItemRemoved(SelectedProduct product, int position);
        void onQuantityChanged(int position, int newQuantity);
    }

    public CartAdapter(Context context, ArrayList<SelectedProduct> selectedProducts) {
        this.context = context;
        this.selectedProducts = selectedProducts;
    }

    public void setOnCartUpdateListener(OnCartUpdateListener listener) {
        this.onCartUpdateListener = listener;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle;
        TextView productPrice;
        TextView quantityText;
        ImageButton deleteButton;
        ImageButton decreaseButton;
        ImageButton increaseButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            productPrice = itemView.findViewById(R.id.productPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            increaseButton = itemView.findViewById(R.id.increaseButton);
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        SelectedProduct product = selectedProducts.get(position);

        // Glide를 사용하여 이미지 로드
        Glide.with(context)
                .load(product.getImageUrl())  // getImageUrl()로 변경
                .placeholder(R.drawable.placehold)  // 로딩 중 표시할 이미지
                .error(R.drawable.placehold)  // 로드 실패시 표시할 이미지
                .into(holder.productImage);

        holder.productTitle.setText(product.getTitle());
        holder.productPrice.setText(product.getPrice());
        holder.quantityText.setText(String.valueOf(product.getQuantity()));

        // 삭제 버튼
        holder.deleteButton.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                updateQuantity(pos, 0);
                removeItem(pos);
            }
        });

        // 수량 감소 버튼
        holder.decreaseButton.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                int currentQuantity = product.getQuantity();
                if (currentQuantity > 0) {
                    updateQuantity(pos, currentQuantity - 1);
                }
            }
        });

        // 수량 증가 버튼
        holder.increaseButton.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                int currentQuantity = product.getQuantity();
                updateQuantity(pos, currentQuantity + 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedProducts.size();
    }

    private void removeItem(int position) {
        if (onCartUpdateListener != null) {
            onCartUpdateListener.onItemRemoved(selectedProducts.get(position), position);
        }
        selectedProducts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, selectedProducts.size());
    }

    private void updateQuantity(int position, int newQuantity) {
        SelectedProduct product = selectedProducts.get(position);
        product.setQuantity(newQuantity);
        notifyItemChanged(position);

        if (onCartUpdateListener != null) {
            onCartUpdateListener.onQuantityChanged(position, newQuantity);
        }
    }

    public ArrayList<SelectedProduct> getSelectedProducts() {
        return selectedProducts;
    }
}