package com.example.ecommerce_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.R;
import com.example.ecommerce_app.adapters.OrderAdapter;
import com.example.ecommerce_app.data.database.AppDatabase;
import com.example.ecommerce_app.data.dao.OrderItemDao;
import com.example.ecommerce_app.data.dao.ProductDao;
import com.example.ecommerce_app.data.entities.Order;
import com.example.ecommerce_app.data.entities.OrderItem;
import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.viewmodels.OrderViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MyOrderFragment - Shows user's order history with tab filtering
 * Tabs: My Order (in progress orders) and History (completed/cancelled orders)
 */
public class MyOrderFragment extends Fragment {

    private RecyclerView recyclerOrders;
    private OrderAdapter orderAdapter;
    private OrderViewModel orderViewModel;
    private TabLayout tabLayout;
    private View emptyState;
    
    private AppDatabase database;
    private OrderItemDao orderItemDao;
    private ProductDao productDao;
    private ExecutorService executorService;
    
    private int currentTab = 0; // 0 = My Order, 1 = History

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_order, container, false);

        // Initialize database
        database = AppDatabase.getInstance(requireContext());
        orderItemDao = database.orderItemDao();
        productDao = database.productDao();
        executorService = Executors.newSingleThreadExecutor();

        // Initialize views
        recyclerOrders = view.findViewById(R.id.recycler_orders);
        tabLayout = view.findViewById(R.id.tab_layout);
        emptyState = view.findViewById(R.id.empty_state);

        // Setup RecyclerView
        recyclerOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(getContext());
        recyclerOrders.setAdapter(orderAdapter);
        
        // Set order click listener
        orderAdapter.setOnOrderClickListener(new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onDetailClick(Order order) {
                // TODO: Navigate to order detail activity
                Toast.makeText(getContext(), "View details for Order #" + order.getOrderNumber(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onActionClick(Order order) {
                handleOrderAction(order);
            }
        });

        // Setup ViewModel
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        // Set user ID (TODO: Get from session/SharedPreferences)
        orderViewModel.setUserId(1); // Temporary hardcoded user ID

        // Observe orders based on current tab
        loadOrdersForCurrentTab();

        // Tab selection listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                loadOrdersForCurrentTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    /**
     * Load orders based on current tab selection
     */
    private void loadOrdersForCurrentTab() {
        if (currentTab == 0) {
            // My Order tab - show in progress orders (PENDING, PROCESSING, SHIPPED)
            loadInProgressOrders();
        } else {
            // History tab - show completed/cancelled orders (DELIVERED, COMPLETED, CANCELLED)
            loadHistoryOrders();
        }
    }

    /**
     * Load in-progress orders
     */
    private void loadInProgressOrders() {
        orderViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) {
                // Filter for in-progress orders
                List<Order> inProgressOrders = new ArrayList<>();
                for (Order order : orders) {
                    String status = order.getStatus().toUpperCase();
                    if (status.equals("PENDING") || status.equals("PROCESSING") || status.equals("SHIPPED")) {
                        inProgressOrders.add(order);
                    }
                }
                
                loadOrdersWithDetails(inProgressOrders);
            }
        });
    }

    /**
     * Load history orders (completed/cancelled)
     */
    private void loadHistoryOrders() {
        orderViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) {
                // Filter for history orders
                List<Order> historyOrders = new ArrayList<>();
                for (Order order : orders) {
                    String status = order.getStatus().toUpperCase();
                    if (status.equals("DELIVERED") || status.equals("COMPLETED") || status.equals("CANCELLED")) {
                        historyOrders.add(order);
                    }
                }
                
                loadOrdersWithDetails(historyOrders);
            }
        });
    }

    /**
     * Load orders with product details
     */
    private void loadOrdersWithDetails(List<Order> orders) {
        if (orders.isEmpty()) {
            recyclerOrders.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            return;
        }

        executorService.execute(() -> {
            List<OrderAdapter.OrderWithDetails> ordersWithDetails = new ArrayList<>();
            
            for (Order order : orders) {
                // Get first order item
                List<OrderItem> items = orderItemDao.getOrderItemsSync(order.getId());
                if (!items.isEmpty()) {
                    OrderItem firstItem = items.get(0);
                    Product product = productDao.getProductByIdSync(firstItem.getProductId());
                    ordersWithDetails.add(new OrderAdapter.OrderWithDetails(order, firstItem, product));
                } else {
                    ordersWithDetails.add(new OrderAdapter.OrderWithDetails(order, null, null));
                }
            }

            // Update UI on main thread
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    orderAdapter.setOrdersWithDetails(ordersWithDetails);
                    recyclerOrders.setVisibility(View.VISIBLE);
                    emptyState.setVisibility(View.GONE);
                });
            }
        });
    }

    /**
     * Handle action button click based on order status
     */
    private void handleOrderAction(Order order) {
        String status = order.getStatus().toUpperCase();
        switch (status) {
            case "PENDING":
                // TODO: Navigate to payment
                Toast.makeText(getContext(), "Navigate to payment", Toast.LENGTH_SHORT).show();
                break;
            case "PROCESSING":
            case "SHIPPED":
                // TODO: Navigate to tracking
                Toast.makeText(getContext(), "Track order #" + order.getOrderNumber(), Toast.LENGTH_SHORT).show();
                break;
            case "DELIVERED":
            case "COMPLETED":
                // Confirm received
                orderViewModel.confirmReceived(order.getId());
                Toast.makeText(getContext(), "Thank you for confirming!", Toast.LENGTH_SHORT).show();
                loadOrdersForCurrentTab();
                break;
            case "CANCELLED":
                // TODO: Reorder
                Toast.makeText(getContext(), "Reorder functionality", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
