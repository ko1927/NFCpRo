package com.example.nfcpro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class BoothListAdapter extends RecyclerView.Adapter<BoothListAdapter.ViewHolder> {
    private List<BoothData> booths = new ArrayList<>();
    private OnBoothClickListener listener;

    public interface OnBoothClickListener {
        void onBoothClick(BoothData booth);
    }

    public BoothListAdapter(OnBoothClickListener listener) {
        this.listener = listener;
    }

    public void setBooths(List<BoothData> booths) {
        this.booths = booths;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booth, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BoothData booth = booths.get(position);
        holder.boothName.setText(booth.getName());
        holder.boothDescription.setText(booth.getDescription());
        holder.boothLocation.setText(booth.getLocation());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBoothClick(booth);
            }
        });
    }

    @Override
    public int getItemCount() {
        return booths.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView boothName;
        TextView boothDescription;
        TextView boothLocation;

        ViewHolder(View view) {
            super(view);
            boothName = view.findViewById(R.id.textBoothName);
            boothDescription = view.findViewById(R.id.textBoothDescription);
            boothLocation = view.findViewById(R.id.textBoothLocation);
        }
    }
}