package com.example.ecommerce_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.R;
import com.example.ecommerce_app.activities.PaymentActivity;
import com.example.ecommerce_app.adapters.CartAdapter;
import com.example.ecommerce_app.data.entities.CartItem;
import com.example.ecommerce_app.utils.SessionManager;
import com.example.ecommerce_app.viewmodels.CartViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * CartFragment - Shopping cart screen
 */
public class CartFragment extends Fragment {

    private CartViewModel viewModel;
    private RecyclerView recyclerCart;
    private CartAdapter cartAdapter;
    private LinearLayout emptyState;
    private MaterialCardView bottomCard;
    private TextView tvSubtotal, tvShipping, tvTotal;
    private MaterialButton btnCheckout;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    private long currentUserId;
    private static final double SHIPPING_FEE = 6.00;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);

        // Initialize SessionManager
        sessionManager = new SessionManager(requireContext());

        // Get current user ID
        currentUserId = sessionManager.getUserId();

        // Initialize views
        initViews(view);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup listeners
        setupClickListeners(view);

        // Load cart data
        if (currentUserId > 0) {
            viewModel.setUserId(currentUserId);
            observeData();
        } else {
            showEmptyState(true);
            Toast.makeText(requireContext(), "Please login to view cart", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews(View view) {
        recyclerCart = view.findViewById(R.id.recycler_cart);
        emptyState = view.findViewById(R.id.empty_state);
        bottomCard = view.findViewById(R.id.bottom_card);
        tvSubtotal = view.findViewById(R.id.tv_subtotal);
        tvShipping = view.findViewById(R.id.tv_shipping);
        tvTotal = view.findViewById(R.id.tv_total);
        btnCheckout = view.findViewById(R.id.btn_checkout);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(requireContext());
        recyclerCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCart.setAdapter(cartAdapter);

        cartAdapter.setOnCartItemListener(new CartAdapter.OnCartItemListener() {
            @Override
            public void onItemChecked(CartItem item, boolean isChecked) {
                // Update total price when selection changes
                updateCheckoutCard();
            }

            @Override
            public void onIncreaseQuantity(CartItem item) {
                viewModel.updateQuantity(item.getId(), item.getQuantity() + 1);
                // Update checkout card after quantity change
                recyclerCart.postDelayed(() -> updateCheckoutCard(), 100);
            }

            @Override
            public void onDecreaseQuantity(CartItem item) {
                if (item.getQuantity() > 1) {
                    viewModel.updateQuantity(item.getId(), item.getQuantity() - 1);
                    // Update checkout card after quantity change
                    recyclerCart.postDelayed(() -> updateCheckoutCard(), 100);
                }
            }
        });
    }

    private void setupClickListeners(View view) {
        // Checkout button
        btnCheckout.setOnClickListener(v -> {
            // Get selected items
            List<CartItem> selectedItems = cartAdapter.getSelectedItems();
            
            if (selectedItems.isEmpty()) {
                Toast.makeText(requireContext(), "Please select items to checkout", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Navigate to Payment screen
            navigateToPayment(selectedItems);
        });
    }

    /**
     * Navigate to Payment screen with selected items
     */
    private void navigateToPayment(List<CartItem> selectedItems) {
        // Create intent extras with item IDs and quantities
        ArrayList<Long> itemIds = new ArrayList<>();
        ArrayList<Long> productIds = new ArrayList<>();
        ArrayList<Integer> quantities = new ArrayList<>();
        
        for (CartItem item : selectedItems) {
            itemIds.add(item.getId());
            productIds.add(item.getProductId());
            quantities.add(item.getQuantity());
        }
        
        Intent intent = new Intent(requireContext(), PaymentActivity.class);
        intent.putExtra("itemIds", itemIds);
        intent.putExtra("productIds", productIds);
        intent.putExtra("quantities", quantities);
        intent.putExtra("userId", currentUserId);
        
        startActivity(intent);
    }

    private void observeData() {
        // Observe cart items
        viewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {
            if (cartItems != null && !cartItems.isEmpty()) {
                cartAdapter.setCartItems(cartItems);
                showEmptyState(false);
                
                // Calculate total after adapter loads products
                recyclerCart.postDelayed(() -> {
                    double total = cartAdapter.getTotalPrice();
                    viewModel.calculateTotalPrice();
                    updateTotalPrice(total);
                }, 500); // Give time for products to load
            } else {
                showEmptyState(true);
            }
        });

        // Observe total price
        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), totalPrice -> {
            if (totalPrice != null) {
                updateTotalPrice(totalPrice);
            }
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmptyState(boolean show) {
        if (show) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerCart.setVisibility(View.GONE);
            bottomCard.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerCart.setVisibility(View.VISIBLE);
            // Hide bottom card by default - will show when items are checked
            bottomCard.setVisibility(View.GONE);
        }
    }

    private void updateTotalPrice(double total) {
        String formattedPrice = String.format(Locale.US, "$%.2f", total);
        tvSubtotal.setText(formattedPrice);
        tvTotal.setText(formattedPrice);
    }

    /**
     * Update checkout card visibility and calculations based on selected items
     */
    private void updateCheckoutCard() {
        int selectedCount = cartAdapter.getSelectedCount();
        
        if (selectedCount > 0) {
            // Show checkout card when items are selected
            bottomCard.setVisibility(View.VISIBLE);
            
            // Calculate subtotal from selected items
            double subtotal = cartAdapter.getTotalPrice();
            String formattedSubtotal = String.format(Locale.US, "$%.2f", subtotal);
            tvSubtotal.setText(formattedSubtotal);
            
            // Shipping fee
            String formattedShipping = String.format(Locale.US, "$%.2f", SHIPPING_FEE);
            tvShipping.setText(formattedShipping);
            
            // Total amount (subtotal + shipping)
            double totalAmount = subtotal + SHIPPING_FEE;
            String formattedTotal = String.format(Locale.US, "$%.2f", totalAmount);
            tvTotal.setText(formattedTotal);
        } else {
            // Hide checkout card when no items selected
            bottomCard.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh cart when fragment resumes
        if (currentUserId > 0) {
            viewModel.calculateTotalPrice();
        }
    }
}
