package com.example.ecommerce_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.R;
import com.example.ecommerce_app.data.entities.CartItem;
import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.utils.ImageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PaymentProductAdapter extends RecyclerView.Adapter<PaymentProductAdapter.ViewHolder> {
    
    private List<CartItem> cartItems;
    private Map<Long, Product> productMap;
    
    public PaymentProductAdapter() {
        this.cartItems = new ArrayList<>();
        this.productMap = new HashMap<>();
    }
    
    public void setItems(List<CartItem> cartItems, Map<Long, Product> productMap) {
        this.cartItems = cartItems;
        this.productMap = productMap;
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
        CartItem cartItem = cartItems.get(position);
        Product product = productMap.get(cartItem.getProductId());
        
        if (product != null) {
            holder.bind(cartItem, product);
        }
    }
    
    @Override
    public int getItemCount() {
        return cartItems.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        
        private ImageView ivProductImage;
        private TextView tvProductName;
        private TextView tvProductColor;
        private TextView tvProductPrice;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductColor = itemView.findViewById(R.id.tvProductColor);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
        }
        
        public void bind(CartItem cartItem, Product product) {
            // Set product name
            tvProductName.setText(product.getName());
            
            // Set product color (using brand as color for now)
            tvProductColor.setText("Color: " + (product.getBrand() != null ? product.getBrand() : "N/A"));
            
            // Set product price (price * quantity)
            double totalPrice = product.getPrice() * cartItem.getQuantity();
            tvProductPrice.setText(String.format(Locale.US, "$ %.2f", totalPrice));
            
            // Load product image
            String imagePath = ImageHelper.getProductMainImagePath(product.getId());
            ImageHelper.loadImageFromAssets(itemView.getContext(), imagePath, ivProductImage);
        }
    }
}
