package com.example.ecommerce_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.HomeActivity;
import com.example.ecommerce_app.R;
import com.example.ecommerce_app.adapters.CategoryAdapter;
import com.example.ecommerce_app.data.entities.Category;
import com.example.ecommerce_app.viewmodels.CategoryViewModel;
import com.example.ecommerce_app.viewmodels.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * CategoryFragment - Category screen showing all product categories
 */
public class CategoryFragment extends Fragment {

    private CategoryViewModel categoryViewModel;
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerCategories;
    private CategoryAdapter categoryAdapter;
    private TextView textGreeting;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModels
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Initialize views
        initViews(view);

        // Setup RecyclerView
        setupRecyclerView();

        // Observe data
        observeData();

        // Setup click listeners
        setupClickListeners(view);
    }

    private void initViews(View view) {
        textGreeting = view.findViewById(R.id.text_greeting);
        recyclerCategories = view.findViewById(R.id.recycler_categories);
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter(requireContext());
        recyclerCategories.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCategories.setAdapter(categoryAdapter);

        categoryAdapter.setOnCategoryClickListener(new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category) {
                // TODO: Navigate to products by category
            }
        });
    }

    private void observeData() {
        // Observe user name
        homeViewModel.getUserName().observe(getViewLifecycleOwner(), name -> {
            textGreeting.setText(getString(R.string.greeting, name));
        });

        // Observe categories
        categoryViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                categoryAdapter.setCategories(categories);
                
                // Set dummy product counts (in real app, get from repository)
                List<Integer> counts = new ArrayList<>();
                for (Category category : categories) {
                    counts.add((int) (Math.random() * 300) + 50); // Random count for demo
                }
                categoryAdapter.setProductCounts(counts);
            }
        });
    }

    private void setupClickListeners(View view) {
        view.findViewById(R.id.btn_search).setOnClickListener(v -> {
            // TODO: Open search
        });

        view.findViewById(R.id.btn_notification).setOnClickListener(v -> {
            // TODO: Open notifications
        });

        view.findViewById(R.id.tab_home).setOnClickListener(v -> {
            // Switch to home tab
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
            }
        });
    }
}
