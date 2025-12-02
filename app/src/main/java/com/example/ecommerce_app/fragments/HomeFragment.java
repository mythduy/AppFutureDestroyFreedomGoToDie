package com.example.ecommerce_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerce_app.HomeActivity;
import com.example.ecommerce_app.R;
import com.example.ecommerce_app.adapters.BannerAdapter;
import com.example.ecommerce_app.adapters.ProductAdapter;
import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.viewmodels.HomeViewModel;

/**
 * HomeFragment - Home screen with products and banners
 */
public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;
    private ViewPager2 bannerViewPager;
    private RecyclerView recyclerProducts;
    private ProductAdapter productAdapter;
    private BannerAdapter bannerAdapter;
    private TextView textGreeting;
    private LinearLayout bannerIndicator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Initialize views
        initViews(view);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup Banner ViewPager
        setupBannerViewPager();

        // Observe data
        observeData();

        // Setup click listeners
        setupClickListeners(view);
    }

    private void initViews(View view) {
        textGreeting = view.findViewById(R.id.text_greeting);
        bannerViewPager = view.findViewById(R.id.banner_viewpager);
        bannerIndicator = view.findViewById(R.id.banner_indicator);
        recyclerProducts = view.findViewById(R.id.recycler_products);
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(requireContext());
        recyclerProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerProducts.setAdapter(productAdapter);

        productAdapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                // Navigate to product detail
                android.content.Intent intent = new android.content.Intent(getActivity(), 
                    com.example.ecommerce_app.ProductDetailActivity.class);
                intent.putExtra(com.example.ecommerce_app.ProductDetailActivity.EXTRA_PRODUCT_ID, 
                    product.getId());
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Product product) {
                // TODO: Add to favorites
            }
        });
    }

    private void setupBannerViewPager() {
        bannerAdapter = new BannerAdapter(requireContext());
        bannerViewPager.setAdapter(bannerAdapter);

        // Setup banner indicator
        setupBannerIndicator();

        // Auto scroll banner
        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateBannerIndicator(position);
            }
        });
    }

    private void setupBannerIndicator() {
        int bannerCount = 3; // Number of banners
        for (int i = 0; i < bannerCount; i++) {
            View indicator = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) (8 * getResources().getDisplayMetrics().density),
                    (int) (8 * getResources().getDisplayMetrics().density)
            );
            params.setMargins(4, 0, 4, 0);
            indicator.setLayoutParams(params);
            indicator.setBackgroundResource(R.drawable.indicator_inactive);
            bannerIndicator.addView(indicator);
        }
        if (bannerCount > 0) {
            bannerIndicator.getChildAt(0).setBackgroundResource(R.drawable.indicator_active);
        }
    }

    private void updateBannerIndicator(int position) {
        int childCount = bannerIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View indicator = bannerIndicator.getChildAt(i);
            if (i == position) {
                indicator.setBackgroundResource(R.drawable.indicator_active);
            } else {
                indicator.setBackgroundResource(R.drawable.indicator_inactive);
            }
        }
    }

    private void observeData() {
        // Observe user name
        viewModel.getUserName().observe(getViewLifecycleOwner(), name -> {
            textGreeting.setText(getString(R.string.greeting, name));
        });

        // Observe new arrivals
        viewModel.getNewArrivals().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                productAdapter.setProducts(products);
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

        view.findViewById(R.id.btn_see_all).setOnClickListener(v -> {
            // TODO: Show all products
        });

        view.findViewById(R.id.tab_category).setOnClickListener(v -> {
            // Switch to category tab
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new CategoryFragment())
                        .commit();
            }
        });
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh username khi quay lại fragment (sau login/logout)
        if (viewModel != null) {
            viewModel.refreshUserName();
        }
    }
}
