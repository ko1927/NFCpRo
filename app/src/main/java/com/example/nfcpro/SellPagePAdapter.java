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

public class SellPagePAdapter extends RecyclerView.Adapter<SellPagePAdapter.ViewHolder> {
    private final Context context;
    private final List<Product> products;
    private final Map<Integer, Integer> selections = new HashMap<>();
    private OnItemLongClickListener longClickListener;
    private OnSelectionChangedListener selectionChangedListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public interface OnSelectionChangedListener {
        void onSelectionChanged();
    }

    public SellPagePAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionChangedListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.titleText.setText(product.getTitle());
        holder.priceText.setText(product.getPrice());

        // 이미지 로딩
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.placehold)
                    .error(R.drawable.placehold)
                    .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.placehold);
        }

        // 수량 표시
        int quantity = selections.getOrDefault(position, 0);
        holder.selectionCount.setText(quantity > 0 ? quantity + "개" : "0개");

        // 클릭 리스너 설정
        holder.itemView.setOnClickListener(v -> {
            int newQuantity = quantity + 1;
            updateQuantity(position, newQuantity);
        });

        // 길게 누르기 리스너 설정
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateQuantity(int position, int quantity) {
        if (quantity == 0) {
            selections.remove(position);
        } else {
            selections.put(position, quantity);
        }

        if (selectionChangedListener != null) {
            selectionChangedListener.onSelectionChanged();
        }

        notifyItemChanged(position);
    }

    public Map<Integer, Integer> getAllSelections() {
        return new HashMap<>(selections);
    }

    public void clearAllSelections() {
        selections.clear();
        if (selectionChangedListener != null) {
            selectionChangedListener.onSelectionChanged();
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView titleText;
        TextView priceText;
        TextView selectionCount;

        ViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            titleText = itemView.findViewById(R.id.productTitle);
            priceText = itemView.findViewById(R.id.productPrice);
            selectionCount = itemView.findViewById(R.id.selectionCount);
        }
    }
}