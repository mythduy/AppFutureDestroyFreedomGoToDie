package com.example.ecommerce_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.R;
import com.example.ecommerce_app.data.entities.Order;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * OrderAdapter - RecyclerView adapter for displaying orders
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orders = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public OrderAdapter(Context context) {
        this.context = context;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
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

        holder.tvOrderNumber.setText("Order #" + order.getId());
        holder.tvOrderStatus.setText(order.getStatus());
        holder.tvOrderDate.setText(dateFormat.format(order.getCreatedAt()));
        holder.tvItemsCount.setText("0 items"); // TODO: Calculate from OrderItem
        holder.tvTotalAmount.setText(String.format(Locale.getDefault(), "$%.2f", order.getTotalAmount()));

        // Set status background color
        int statusColor = getStatusColor(order.getStatus());
        holder.tvOrderStatus.setBackgroundResource(statusColor);

        // Button click listeners
        holder.btnViewDetails.setOnClickListener(v -> {
            Toast.makeText(context, "View details for Order #" + order.getId(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to order details screen
        });

        holder.btnTrackOrder.setOnClickListener(v -> {
            Toast.makeText(context, "Track Order #" + order.getId(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to order tracking screen
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    /**
     * Get background resource based on status
     */
    private int getStatusColor(String status) {
        switch (status.toUpperCase()) {
            case "PENDING":
                return R.drawable.bg_status_pending;
            case "PROCESSING":
                return R.drawable.bg_status_processing;
            case "COMPLETED":
                return R.drawable.bg_status_completed;
            case "CANCELLED":
                return R.drawable.bg_status_cancelled;
            default:
                return R.drawable.bg_status_pending;
        }
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderNumber, tvOrderStatus, tvOrderDate, tvItemsCount, tvTotalAmount;
        MaterialButton btnViewDetails, btnTrackOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tv_order_number);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvItemsCount = itemView.findViewById(R.id.tv_items_count);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
            btnTrackOrder = itemView.findViewById(R.id.btn_track_order);
        }
    }
}
