package com.example.ecommerce_app.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.R;
import com.example.ecommerce_app.adapters.FavoriteProductAdapter;
import com.example.ecommerce_app.data.database.AppDatabase;
import com.example.ecommerce_app.data.dao.ProductDao;
import com.example.ecommerce_app.data.entities.Favorite;
import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.utils.SessionManager;
import com.example.ecommerce_app.viewmodels.FavoriteViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * FavoriteFragment - Favorite products screen with search and filtering
 */
public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerFavorites;
    private FavoriteProductAdapter favoriteAdapter;
    private FavoriteViewModel favoriteViewModel;
    private View emptyState;
    private EditText etSearch;
    private ChipGroup chipGroup;
    
    private AppDatabase database;
    private ProductDao productDao;
    private ExecutorService executorService;
    private SessionManager sessionManager;
    private long userId;
    
    private List<Product> allFavoriteProducts = new ArrayList<>();
    private String currentFilter = "ALL";
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        // Initialize database
        database = AppDatabase.getInstance(requireContext());
        productDao = database.productDao();
        executorService = Executors.newSingleThreadExecutor();
        
        // Get user ID from session
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();

        // Initialize views
        recyclerFavorites = view.findViewById(R.id.recycler_favorites);
        emptyState = view.findViewById(R.id.empty_state);
        etSearch = view.findViewById(R.id.et_search);
        chipGroup = view.findViewById(R.id.chip_group);

        // Setup RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerFavorites.setLayoutManager(gridLayoutManager);
        favoriteAdapter = new FavoriteProductAdapter(getContext());
        recyclerFavorites.setAdapter(favoriteAdapter);

        // Set adapter click listeners
        favoriteAdapter.setOnProductClickListener(new FavoriteProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                // TODO: Navigate to product detail
                Toast.makeText(getContext(), "View details: " + product.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFavoriteClick(Product product) {
                // Remove from favorites
                favoriteViewModel.removeFromFavorites(product.getId());
                favoriteAdapter.removeProduct(product);
                allFavoriteProducts.remove(product);
                
                // Check if empty
                if (allFavoriteProducts.isEmpty()) {
                    showEmptyState();
                }
            }
        });

        // Setup ViewModel
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        
        // Set user ID from session
        if (userId > 0) {
            favoriteViewModel.setUserId(userId);
        }

        // Observe favorites
        favoriteViewModel.getFavorites().observe(getViewLifecycleOwner(), favorites -> {
            if (favorites != null && !favorites.isEmpty()) {
                loadFavoriteProducts(favorites);
            } else {
                showEmptyState();
            }
        });

        // Setup search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().toLowerCase().trim();
                filterProducts();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup filter chips
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                return;
            }
            
            int selectedId = checkedIds.get(0);
            if (selectedId == R.id.chip_all) {
                currentFilter = "ALL";
            } else if (selectedId == R.id.chip_latest) {
                currentFilter = "LATEST";
            } else if (selectedId == R.id.chip_most_popular) {
                currentFilter = "POPULAR";
            } else if (selectedId == R.id.chip_cheapest) {
                currentFilter = "CHEAPEST";
            }
            
            filterProducts();
        });

        // Set default filter
        chipGroup.check(R.id.chip_all);

        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Reload favorites when fragment becomes visible again
        refreshFavorites();
    }
    
    /**
     * Refresh favorites list - public method for external refresh
     */
    public void refreshFavorites() {
        if (favoriteViewModel != null && userId > 0) {
            favoriteViewModel.setUserId(userId);
        }
    }

    /**
     * Load favorite products from database
     */
    private void loadFavoriteProducts(List<Favorite> favorites) {
        executorService.execute(() -> {
            List<Product> products = new ArrayList<>();
            
            for (Favorite favorite : favorites) {
                Product product = productDao.getProductByIdSync(favorite.getProductId());
                if (product != null) {
                    products.add(product);
                }
            }

            // Update UI on main thread
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    allFavoriteProducts = products;
                    filterProducts();
                    
                    if (products.isEmpty()) {
                        showEmptyState();
                    } else {
                        hideEmptyState();
                    }
                });
            }
        });
    }

    /**
     * Filter products based on search query and selected filter
     */
    private void filterProducts() {
        List<Product> filteredProducts = new ArrayList<>(allFavoriteProducts);

        // Apply search filter
        if (!searchQuery.isEmpty()) {
            List<Product> searchResults = new ArrayList<>();
            for (Product product : filteredProducts) {
                if (product.getName().toLowerCase().contains(searchQuery) ||
                    (product.getBrand() != null && product.getBrand().toLowerCase().contains(searchQuery))) {
                    searchResults.add(product);
                }
            }
            filteredProducts = searchResults;
        }

        // Apply sort filter
        switch (currentFilter) {
            case "LATEST":
                // Sort by created date (newest first)
                Collections.sort(filteredProducts, (p1, p2) -> 
                    p2.getCreatedAt().compareTo(p1.getCreatedAt()));
                break;
            case "POPULAR":
                // Sort by name (as we don't have popularity metric)
                // In a real app, you'd sort by number of purchases or views
                Collections.sort(filteredProducts, (p1, p2) -> 
                    p1.getName().compareTo(p2.getName()));
                break;
            case "CHEAPEST":
                // Sort by price (lowest first)
                Collections.sort(filteredProducts, (p1, p2) -> 
                    Double.compare(p1.getPrice(), p2.getPrice()));
                break;
            case "ALL":
            default:
                // No sorting, keep original order
                break;
        }

        favoriteAdapter.setProducts(filteredProducts);

        // Show/hide empty state
        if (filteredProducts.isEmpty() && !allFavoriteProducts.isEmpty()) {
            // Show "no results" message
            recyclerFavorites.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            hideEmptyState();
        }
    }

    /**
     * Show empty state
     */
    private void showEmptyState() {
        recyclerFavorites.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
    }

    /**
     * Hide empty state
     */
    private void hideEmptyState() {
        recyclerFavorites.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
