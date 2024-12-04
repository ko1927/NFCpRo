package com.example.nfcpro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private List<RankingItem> rankingList = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankingNumber, boothName, salesAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankingNumber = itemView.findViewById(R.id.ranking_number);
            boothName = itemView.findViewById(R.id.booth_name);
            salesAmount = itemView.findViewById(R.id.sales_amount);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ranking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RankingItem item = rankingList.get(position);
        holder.rankingNumber.setText(String.valueOf(item.getRank()));
        holder.boothName.setText(item.getBoothName());
        holder.salesAmount.setText(item.getSalesAmount());
    }

    @Override
    public int getItemCount() {
        return rankingList.size();
    }

    public void setRankingList(List<RankingItem> list) {
        this.rankingList = list;
        notifyDataSetChanged();
    }
}
