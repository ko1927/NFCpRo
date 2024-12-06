package com.example.nfcpro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PaymentProductAdapter extends RecyclerView.Adapter<PaymentProductAdapter.ViewHolder> {
    private List<PaymentProductData> products = new ArrayList<>();

    public void setProducts(List<PaymentProductData> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentProductData product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageProduct;
        private TextView textProductName;
        private TextView textQuantity;
        private TextView textPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textProductName = itemView.findViewById(R.id.textProductName);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            textPrice = itemView.findViewById(R.id.textPrice);
        }

        public void bind(PaymentProductData product) {
            // 이미지 로딩 (실제 구현 시 Glide 등의 라이브러리 사용 권장)
            if (product.getProductImage() != null) {
                imageProduct.setImageResource(R.drawable.placehold);
            }
            textProductName.setText(product.getProductName());
            textQuantity.setText("개수 " + product.getQuantity());
            textPrice.setText(product.getPrice());
        }
    }
}
