package com.example.nfcpro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class INBOOTHDETAILADAPTER extends RecyclerView.Adapter<INBOOTHDETAILADAPTER.ProductViewHolder> {
    private List<Product> products;
    private Context context;
    private Map<Integer, Integer> selectionCount;

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

        // Glide를 사용하여 이미지 로드


        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placehold)
                .error(R.drawable._060)
                .into(holder.productImage);

//        // 선택 개수 표시
//        int count = selectionCount.getOrDefault(position, 0);
//        if (count > 0) {
//            holder.selectionCountView.setVisibility(View.VISIBLE);
//            holder.selectionCountView.setText(String.valueOf(count));
//        } else {
//            holder.selectionCountView.setVisibility(View.GONE);
//        }

        // 클릭 리스너 추가
        holder.itemView.setOnClickListener(v -> {
            incrementSelection(position);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void incrementSelection(int position) {
        int currentCount = selectionCount.getOrDefault(position, 0);
        selectionCount.put(position, currentCount + 1);
        notifyItemChanged(position);
    }

    public int getSelectionCount(int position) {
        return selectionCount.getOrDefault(position, 0);
    }

    public Map<Integer, Integer> getAllSelections() {
        return new HashMap<>(selectionCount);
    }

    public void clearSelections() {
        selectionCount.clear();
        notifyDataSetChanged();
    }
}