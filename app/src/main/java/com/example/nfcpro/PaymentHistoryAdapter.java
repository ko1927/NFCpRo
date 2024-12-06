package com.example.nfcpro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder> {
    private List<PaymentData> payments = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PaymentData payment);
    }

    public PaymentHistoryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setPayments(List<PaymentData> payments) {
        this.payments = payments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentData payment = payments.get(position);
        holder.bind(payment, listener);
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textPaymentDate;
        private TextView textPaymentAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textPaymentDate = itemView.findViewById(R.id.textPaymentDate);
            textPaymentAmount = itemView.findViewById(R.id.textPaymentAmount);
        }

        public void bind(final PaymentData payment, final OnItemClickListener listener) {
            textPaymentDate.setText(payment.getPaymentTime());
            textPaymentAmount.setText(payment.getAmount());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(payment);
                }
            });
        }
    }
}