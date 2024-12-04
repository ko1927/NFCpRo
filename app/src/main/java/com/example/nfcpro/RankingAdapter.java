package com.example.nfcpro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private List<RankingItem> rankingList;

    public RankingAdapter() {
        this.rankingList = new ArrayList<>();
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder {
        private final TextView rankingNumber;
        private final TextView boothName;
        private final TextView salesAmount;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            rankingNumber = itemView.findViewById(R.id.ranking_number);
            boothName = itemView.findViewById(R.id.booth_name);
            salesAmount = itemView.findViewById(R.id.sales_amount);
        }

        public void bind(RankingItem item) {
            rankingNumber.setText(String.valueOf(item.getRank()));
            boothName.setText(item.getBoothName());
            salesAmount.setText(item.getSalesAmount());
        }
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_lamkimg, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        holder.bind(rankingList.get(position));
    }

    @Override
    public int getItemCount() {
        return rankingList.size();
    }

    public void submitList(List<RankingItem> newList) {
        rankingList = new ArrayList<>();
        // 4등 이후의 데이터만 필터링
        for (RankingItem item : newList) {
            if (item.getRank() > 3) {
                rankingList.add(item);
            }
        }
        notifyDataSetChanged();
    }
}
