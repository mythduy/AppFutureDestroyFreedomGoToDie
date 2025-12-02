package com.example.ecommerce_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.adapters.ReviewAdapter;
import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.utils.ImageHelper;
import com.example.ecommerce_app.viewmodels.ReviewsViewModel;
import com.google.android.material.button.MaterialButton;

/**
 * ReviewsActivity - Màn hình hiển thị tất cả reviews của sản phẩm
 */
public class ReviewsActivity extends AppCompatActivity {
    
    public static final String EXTRA_PRODUCT_ID = "product_id";
    private static final int REQUEST_ADD_REVIEW = 100;
    
    private ReviewsViewModel viewModel;
    private ReviewAdapter reviewAdapter;
    
    // UI Components
    private ImageButton btnBack;
    private TextView tvTitle;
    private ImageView ivProductImage;
    private TextView tvProductName;
    private TextView tvAverageRating;
    private TextView tvReviewCount;
    private RecyclerView rvReviews;
    private LinearLayout layoutEmptyState;
    private MaterialButton btnAddReview;
    
    private long productId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        
        // Get product ID from intent
        productId = getIntent().getLongExtra(EXTRA_PRODUCT_ID, -1);
        if (productId == -1) {
            Toast.makeText(this, "Invalid product", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ReviewsViewModel.class);
        
        // Initialize views
        initViews();
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup observers
        setupObservers();
        
        // Setup click listeners
        setupClickListeners();
        
        // Load data
        viewModel.loadProduct(productId);
        viewModel.loadAllReviews(productId);
    }
    
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        tvReviewCount = findViewById(R.id.tvReviewCount);
        rvReviews = findViewById(R.id.rvReviews);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        btnAddReview = findViewById(R.id.btnAddReview);
    }
    
    private void setupRecyclerView() {
        reviewAdapter = new ReviewAdapter(this);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);
    }
    
    private void setupObservers() {
        // Observe product
        viewModel.getProduct().observe(this, product -> {
            if (product != null) {
                displayProduct(product);
            }
        });
        
        // Observe reviews
        viewModel.getReviews().observe(this, reviews -> {
            if (reviews != null && !reviews.isEmpty()) {
                reviewAdapter.setReviews(reviews);
                rvReviews.setVisibility(View.VISIBLE);
                layoutEmptyState.setVisibility(View.GONE);
            } else {
                rvReviews.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.VISIBLE);
            }
        });
        
        // Observe review count
        viewModel.getReviewCount().observe(this, count -> {
            if (count != null) {
                tvReviewCount.setText("(" + count + " reviews)");
            }
        });
        
        // Observe average rating
        viewModel.getAverageRating().observe(this, rating -> {
            if (rating != null && rating > 0) {
                tvAverageRating.setText(String.format("%.1f", rating));
            } else {
                tvAverageRating.setText("0.0");
            }
        });
        
        // Observe messages
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                viewModel.clearMessages();
            }
        });
        
        viewModel.getSuccessMessage().observe(this, success -> {
            if (success != null && !success.isEmpty()) {
                Toast.makeText(this, success, Toast.LENGTH_SHORT).show();
                viewModel.clearMessages();
            }
        });
    }
    
    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());
        
        // Add Review button
        btnAddReview.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddReviewActivity.class);
            intent.putExtra(AddReviewActivity.EXTRA_PRODUCT_ID, productId);
            startActivityForResult(intent, REQUEST_ADD_REVIEW);
        });
    }
    
    private void displayProduct(Product product) {
        // Set product name
        tvProductName.setText(product.getName());
        
        // Load product image
        String imagePath = ImageHelper.getProductMainImagePath(product.getId());
        ImageHelper.loadImageFromAssets(this, imagePath, ivProductImage);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_ADD_REVIEW && resultCode == RESULT_OK) {
            // Reload reviews after adding new review
            viewModel.loadAllReviews(productId);
            Toast.makeText(this, "Review added successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}
