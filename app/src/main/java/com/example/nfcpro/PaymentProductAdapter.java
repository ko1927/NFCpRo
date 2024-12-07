package com.example.nfcpro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
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
            // 이미지 URL 처리
            String imageUrl = product.getProductImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                if (imageUrl.startsWith("drawable/")) {
                    // drawable 리소스인 경우
                    String resourceName = imageUrl.replace("drawable/", "");
                    int resourceId = itemView.getContext().getResources()
                            .getIdentifier(resourceName, "drawable",
                                    itemView.getContext().getPackageName());
                    imageProduct.setImageResource(resourceId);
                } else {
                    // Firebase Storage URL인 경우
                    Glide.with(itemView.getContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.placehold)
                            .error(R.drawable.placehold)
                            .into(imageProduct);
                }
            } else {
                // 이미지 URL이 없는 경우 기본 이미지 표시
                imageProduct.setImageResource(R.drawable.placehold);
            }

            textProductName.setText(product.getProductName());
            textQuantity.setText("개수 " + product.getQuantity());
            textPrice.setText(product.getPrice());
        }
    }
}