package com.example.ecommerce_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.R;
import com.example.ecommerce_app.data.entities.CartItem;
import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.data.repository.ProductRepository;
import com.example.ecommerce_app.utils.ImageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * CartAdapter - Adapter for displaying cart items with product details
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private Map<Long, Product> productMap; // Cache products by ID
    private Map<Long, Boolean> selectedItems; // Track selected items by CartItem ID
    private OnCartItemListener listener;
    private ProductRepository productRepository;

    public CartAdapter(Context context) {
        this.context = context;
        this.cartItems = new ArrayList<>();
        this.productMap = new HashMap<>();
        this.selectedItems = new HashMap<>();
        this.productRepository = new ProductRepository(
            (android.app.Application) context.getApplicationContext());
    }

    public void setCartItems(List<CartItem> items) {
        this.cartItems = items != null ? items : new ArrayList<>();
        loadProducts();
    }

    /**
     * Load all products for cart items
     */
    private void loadProducts() {
        new Thread(() -> {
            for (CartItem item : cartItems) {
                if (!productMap.containsKey(item.getProductId())) {
                    try {
                        Product product = productRepository.getProductByIdSync(item.getProductId()).get();
                        if (product != null) {
                            productMap.put(item.getProductId(), product);
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            // Notify on main thread
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).runOnUiThread(() -> notifyDataSetChanged());
            }
        }).start();
    }

    public void setOnCartItemListener(OnCartItemListener listener) {
        this.listener = listener;
    }

    /**
     * Get total price of selected items only
     */
    public double getTotalPrice() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            Boolean isSelected = selectedItems.get(item.getId());
            if (isSelected != null && isSelected) {
                Product product = productMap.get(item.getProductId());
                if (product != null) {
                    total += product.getPrice() * item.getQuantity();
                }
            }
        }
        return total;
    }

    /**
     * Get count of selected items
     */
    public int getSelectedCount() {
        int count = 0;
        for (Boolean selected : selectedItems.values()) {
            if (selected != null && selected) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get list of selected cart items
     */
    public List<CartItem> getSelectedItems() {
        List<CartItem> selected = new ArrayList<>();
        for (CartItem item : cartItems) {
            Boolean isSelected = selectedItems.get(item.getId());
            if (isSelected != null && isSelected) {
                selected.add(item);
            }
        }
        return selected;
    }

    /**
     * Get product map for passing to other activities
     */
    public Map<Long, Product> getProductMap() {
        return new HashMap<>(productMap);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = productMap.get(cartItem.getProductId());
        holder.bind(cartItem, product);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkboxItem;
        ImageView ivProduct, btnDecrease, btnIncrease;
        TextView tvProductName, tvColor, tvQuantity, tvPrice;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            checkboxItem = itemView.findViewById(R.id.checkbox_item);
            ivProduct = itemView.findViewById(R.id.iv_product);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvColor = itemView.findViewById(R.id.tv_color);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvPrice = itemView.findViewById(R.id.tv_price);

            // Checkbox listener
            checkboxItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    CartItem item = cartItems.get(position);
                    selectedItems.put(item.getId(), isChecked);
                    if (listener != null) {
                        listener.onItemChecked(item, isChecked);
                    }
                }
            });

            // Decrease quantity
            btnDecrease.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDecreaseQuantity(cartItems.get(position));
                    }
                }
            });

            // Increase quantity
            btnIncrease.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onIncreaseQuantity(cartItems.get(position));
                    }
                }
            });
        }

        public void bind(CartItem cartItem, Product product) {
            // Set quantity
            tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

            if (product != null) {
                // Product name
                tvProductName.setText(product.getName());

                // Color (use brand as color for now, or product category)
                String color = product.getBrand() != null ? product.getBrand() : "Default";
                tvColor.setText(String.format("Color: %s", color));

                // Price (total for this item)
                double totalPrice = product.getPrice() * cartItem.getQuantity();
                tvPrice.setText(String.format(Locale.US, "$%.2f", totalPrice));

                // Load product image
                String imagePath = ImageHelper.getProductMainImagePath(product.getId());
                ImageHelper.loadImageFromAssets(context, imagePath, ivProduct);
            } else {
                // Loading state
                tvProductName.setText("Loading...");
                tvColor.setText("");
                tvPrice.setText("$0.00");
            }

            // Restore checkbox state
            Boolean isSelected = selectedItems.get(cartItem.getId());
            checkboxItem.setChecked(isSelected != null && isSelected);
        }
    }

    /**
     * Listener interface for cart item actions
     */
    public interface OnCartItemListener {
        void onItemChecked(CartItem item, boolean isChecked);
        void onIncreaseQuantity(CartItem item);
        void onDecreaseQuantity(CartItem item);
    }
}
