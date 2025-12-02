package com.example.ecommerce_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.R;
import com.example.ecommerce_app.data.entities.Order;
import com.example.ecommerce_app.data.entities.OrderItem;
import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.utils.ImageHelper;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * OrderAdapter - RecyclerView adapter for displaying orders with product details
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<OrderWithDetails> ordersWithDetails = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onDetailClick(Order order);
        void onActionClick(Order order);
    }

    public OrderAdapter(Context context) {
        this.context = context;
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<Order> orders) {
        this.ordersWithDetails.clear();
        for (Order order : orders) {
            ordersWithDetails.add(new OrderWithDetails(order, null, null));
        }
        notifyDataSetChanged();
    }

    public void setOrdersWithDetails(List<OrderWithDetails> ordersWithDetails) {
        this.ordersWithDetails = ordersWithDetails;
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
        OrderWithDetails orderWithDetails = ordersWithDetails.get(position);
        Order order = orderWithDetails.order;
        OrderItem firstItem = orderWithDetails.firstItem;
        Product product = orderWithDetails.product;

        // Set product image
        if (product != null && product.getImageFilenames() != null && !product.getImageFilenames().isEmpty()) {
            String imageFilename = product.getImageFilenames().get(0);
            String imagePath = ImageHelper.getProductImagePath(imageFilename);
            ImageHelper.loadImageFromAssets(context, imagePath, holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.ic_launcher_background);
        }

        // Set product name
        if (product != null) {
            holder.tvProductName.setText(product.getName());
            
            // Set color if available (from product description or attributes)
            // For now, we'll show a generic color text
            holder.tvProductColor.setText("Color: " + getProductColor(product));
        } else {
            holder.tvProductName.setText("Order #" + order.getOrderNumber());
            holder.tvProductColor.setText("Multiple items");
        }

        // Set quantity
        if (firstItem != null) {
            holder.tvProductQuantity.setText("Qty: " + firstItem.getQuantity());
        } else {
            holder.tvProductQuantity.setText("Qty: -");
        }

        // Set status
        String statusText = getStatusText(order.getStatus());
        holder.tvOrderStatus.setText(statusText);
        
        // Set status background
        int statusBg = getStatusBackground(order.getStatus());
        holder.tvOrderStatus.setBackgroundResource(statusBg);
        
        // Set status text color
        int statusTextColor = getStatusTextColor(order.getStatus());
        holder.tvOrderStatus.setTextColor(context.getResources().getColor(statusTextColor, null));

        // Set price
        holder.tvProductPrice.setText(String.format(Locale.getDefault(), "$ %.2f", order.getTotalAmount()));

        // Set action button text based on status
        String actionText = getActionButtonText(order.getStatus());
        holder.btnAction.setText(actionText);

        // Button click listeners
        holder.btnDetail.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetailClick(order);
            } else {
                Toast.makeText(context, "View details for Order #" + order.getOrderNumber(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnAction.setOnClickListener(v -> {
            if (listener != null) {
                listener.onActionClick(order);
            } else {
                Toast.makeText(context, actionText + " for Order #" + order.getOrderNumber(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersWithDetails.size();
    }

    /**
     * Get product color from product data
     */
    private String getProductColor(Product product) {
        // This is a simple implementation
        // In a real app, you might have a separate color field or parse from description
        if (product.getDescription() != null && product.getDescription().contains("Brown")) {
            return "Brown";
        } else if (product.getDescription() != null && product.getDescription().contains("Black")) {
            return "Black";
        } else if (product.getDescription() != null && product.getDescription().contains("Pink")) {
            return "Pink";
        }
        return "Default";
    }

    /**
     * Get status display text
     */
    private String getStatusText(String status) {
        switch (status.toUpperCase()) {
            case "PENDING":
                return "Pending";
            case "PROCESSING":
                return "On Progress";
            case "SHIPPED":
                return "Shipped";
            case "DELIVERED":
                return "Completed";
            case "COMPLETED":
                return "Completed";
            case "CANCELLED":
                return "Cancelled";
            default:
                return status;
        }
    }

    /**
     * Get background resource based on status
     */
    private int getStatusBackground(String status) {
        switch (status.toUpperCase()) {
            case "PROCESSING":
            case "SHIPPED":
                return R.drawable.bg_status_progress;
            case "DELIVERED":
            case "COMPLETED":
                return R.drawable.bg_status_completed;
            case "CANCELLED":
                return R.drawable.bg_status_cancelled;
            case "PENDING":
            default:
                return R.drawable.bg_status_pending;
        }
    }

    /**
     * Get status text color
     */
    private int getStatusTextColor(String status) {
        switch (status.toUpperCase()) {
            case "PROCESSING":
            case "SHIPPED":
                return R.color.status_progress;
            case "DELIVERED":
            case "COMPLETED":
                return R.color.status_completed;
            case "CANCELLED":
                return R.color.status_cancelled;
            case "PENDING":
            default:
                return R.color.status_pending;
        }
    }

    /**
     * Get action button text based on status
     */
    private String getActionButtonText(String status) {
        switch (status.toUpperCase()) {
            case "PROCESSING":
            case "SHIPPED":
                return "Tracking";
            case "DELIVERED":
            case "COMPLETED":
                return "Received Order";
            case "PENDING":
                return "Pay Now";
            case "CANCELLED":
                return "Reorder";
            default:
                return "View";
        }
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductColor, tvProductQuantity;
        TextView tvOrderStatus, tvProductPrice;
        MaterialButton btnDetail, btnAction;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductColor = itemView.findViewById(R.id.tv_product_color);
            tvProductQuantity = itemView.findViewById(R.id.tv_product_quantity);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            btnDetail = itemView.findViewById(R.id.btn_detail);
            btnAction = itemView.findViewById(R.id.btn_action);
        }
    }

    /**
     * Helper class to hold order with its first item and product details
     */
    public static class OrderWithDetails {
        public Order order;
        public OrderItem firstItem;
        public Product product;

        public OrderWithDetails(Order order, OrderItem firstItem, Product product) {
            this.order = order;
            this.firstItem = firstItem;
            this.product = product;
        }
    }
}
