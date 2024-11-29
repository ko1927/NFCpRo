package com.example.nfcpro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private ArrayList<SelectedProduct> selectedProducts;
    private OnItemRemovedListener onItemRemovedListener;

    public interface OnItemRemovedListener {
        void onItemRemoved(SelectedProduct product, int position);
    }

    public CartAdapter(ArrayList<SelectedProduct> selectedProducts) {
        this.selectedProducts = selectedProducts;
    }

    public void setOnItemRemovedListener(OnItemRemovedListener listener) {
        this.onItemRemovedListener = listener;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle;
        TextView productPrice;
        TextView quantityText;
        ImageButton deleteButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            productPrice = itemView.findViewById(R.id.productPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
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

        holder.productImage.setImageResource(product.getImageResource());
        holder.productTitle.setText(product.getTitle());
        holder.productPrice.setText(product.getPrice());
        holder.quantityText.setText(String.valueOf(product.getQuantity()));

        holder.deleteButton.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                removeItem(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedProducts.size();
    }

    private void removeItem(int position) {
        if (onItemRemovedListener != null) {
            onItemRemovedListener.onItemRemoved(selectedProducts.get(position), position);
        }
        selectedProducts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, selectedProducts.size());
    }

    public ArrayList<SelectedProduct> getSelectedProducts() {
        return selectedProducts;
    }
}