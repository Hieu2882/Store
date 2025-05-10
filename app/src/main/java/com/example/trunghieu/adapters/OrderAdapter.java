package com.example.trunghieu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trunghieu.R;
import com.example.trunghieu.models.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orders;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
        if (context instanceof OnOrderClickListener) {
            this.listener = (OnOrderClickListener) context;
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView orderIdTextView;
        private TextView orderDateTextView;
        private TextView orderStatusTextView;
        private TextView orderTotalTextView;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
            orderStatusTextView = itemView.findViewById(R.id.orderStatusTextView);
            orderTotalTextView = itemView.findViewById(R.id.orderTotalTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onOrderClick(orders.get(position));
                }
            });
        }

        void bind(Order order) {
            orderIdTextView.setText(String.format("Order #%d", order.getId()));
            orderDateTextView.setText(formatDate(order.getOrderDate()));
            orderStatusTextView.setText(order.getStatus());
            orderTotalTextView.setText(String.format("$%.2f", order.getTotalAmount()));

            // Set status color based on order status
            int statusColor;
            switch (order.getStatus().toUpperCase()) {
                case "PENDING":
                    statusColor = context.getResources().getColor(android.R.color.holo_orange_light);
                    break;
                case "CONFIRMED":
                    statusColor = context.getResources().getColor(android.R.color.holo_blue_light);
                    break;
                case "SHIPPED":
                    statusColor = context.getResources().getColor(android.R.color.holo_purple);
                    break;
                case "DELIVERED":
                    statusColor = context.getResources().getColor(android.R.color.holo_green_light);
                    break;
                case "CANCELLED":
                    statusColor = context.getResources().getColor(android.R.color.holo_red_light);
                    break;
                default:
                    statusColor = context.getResources().getColor(android.R.color.darker_gray);
            }
            orderStatusTextView.setTextColor(statusColor);
        }

        private String formatDate(String dateStr) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                Date date = inputFormat.parse(dateStr);
                return outputFormat.format(date);
            } catch (Exception e) {
                return dateStr;
            }
        }
    }
} 