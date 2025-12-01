package com.example.ecommerce_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.R;
import com.example.ecommerce_app.adapters.OrderAdapter;
import com.example.ecommerce_app.viewmodels.OrderViewModel;
import com.google.android.material.tabs.TabLayout;

/**
 * MyOrderFragment - Shows user's order history with status filtering
 */
public class MyOrderFragment extends Fragment {

    private RecyclerView recyclerOrders;
    private OrderAdapter orderAdapter;
    private OrderViewModel orderViewModel;
    private TabLayout tabLayout;
    private View emptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_order, container, false);

        // Initialize views
        recyclerOrders = view.findViewById(R.id.recycler_orders);
        tabLayout = view.findViewById(R.id.tab_layout);
        emptyState = view.findViewById(R.id.empty_state);

        // Setup RecyclerView
        recyclerOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(getContext());
        recyclerOrders.setAdapter(orderAdapter);

        // Setup ViewModel
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        // Set user ID (TODO: Get from session/SharedPreferences)
        orderViewModel.setUserId(1); // Temporary hardcoded user ID

        // Observe orders
        orderViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null && !orders.isEmpty()) {
                orderAdapter.setOrders(orders);
                recyclerOrders.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
            } else {
                recyclerOrders.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
            }
        });

        // Tab selection listener for filtering
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String status = getStatusFromTab(tab.getPosition());
                orderViewModel.filterByStatus(status);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    /**
     * Get order status from tab position
     */
    private String getStatusFromTab(int position) {
        switch (position) {
            case 0: return "ALL";
            case 1: return "PENDING";
            case 2: return "PROCESSING";
            case 3: return "COMPLETED";
            case 4: return "CANCELLED";
            default: return "ALL";
        }
    }
}
