package com.example.ecommerce_app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.utils.ImageHelper;
import com.example.ecommerce_app.utils.SessionManager;
import com.example.ecommerce_app.viewmodels.AddReviewViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * AddReviewActivity - Màn hình thêm review mới
 */
public class AddReviewActivity extends AppCompatActivity {
    
    public static final String EXTRA_PRODUCT_ID = "product_id";
    
    private AddReviewViewModel viewModel;
    private SessionManager sessionManager;
    
    // UI Components
    private ImageButton btnBack;
    private ImageView ivProductImage;
    private TextView tvProductName;
    private ImageView[] starViews;
    private TextView tvRatingText;
    private TextInputEditText etReviewTitle;
    private TextInputEditText etReviewComment;
    private MaterialButton btnSubmit;
    
    private long productId;
    private long userId;
    private int selectedRating = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        
        // Get product ID from intent
        productId = getIntent().getLongExtra(EXTRA_PRODUCT_ID, -1);
        if (productId == -1) {
            Toast.makeText(this, "Invalid product", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Get user ID from session
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
        
        if (userId <= 0) {
            Toast.makeText(this, "Please login to add review", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AddReviewViewModel.class);
        
        // Initialize views
        initViews();
        
        // Setup observers
        setupObservers();
        
        // Setup click listeners
        setupClickListeners();
        
        // Load product data
        viewModel.loadProduct(productId);
    }
    
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvRatingText = findViewById(R.id.tvRatingText);
        etReviewTitle = findViewById(R.id.etReviewTitle);
        etReviewComment = findViewById(R.id.etReviewComment);
        btnSubmit = findViewById(R.id.btnSubmit);
        
        // Initialize star views
        starViews = new ImageView[5];
        starViews[0] = findViewById(R.id.ivStar1);
        starViews[1] = findViewById(R.id.ivStar2);
        starViews[2] = findViewById(R.id.ivStar3);
        starViews[3] = findViewById(R.id.ivStar4);
        starViews[4] = findViewById(R.id.ivStar5);
    }
    
    private void setupObservers() {
        // Observe product
        viewModel.getProduct().observe(this, product -> {
            if (product != null) {
                displayProduct(product);
            }
        });
        
        // Observe review submit result
        viewModel.getIsReviewSubmitted().observe(this, isSubmitted -> {
            if (isSubmitted != null && isSubmitted) {
                Toast.makeText(this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
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
        
        // Star rating click listeners
        for (int i = 0; i < starViews.length; i++) {
            final int rating = i + 1;
            starViews[i].setOnClickListener(v -> {
                selectedRating = rating;
                updateStarDisplay();
                updateRatingText();
            });
        }
        
        // Submit button
        btnSubmit.setOnClickListener(v -> submitReview());
    }
    
    private void displayProduct(Product product) {
        // Set product name
        tvProductName.setText(product.getName());
        
        // Load product image
        String imagePath = ImageHelper.getProductMainImagePath(product.getId());
        ImageHelper.loadImageFromAssets(this, imagePath, ivProductImage);
    }
    
    private void updateStarDisplay() {
        for (int i = 0; i < starViews.length; i++) {
            if (i < selectedRating) {
                starViews[i].setImageResource(R.drawable.ic_star_filled);
            } else {
                starViews[i].setImageResource(R.drawable.ic_star_outline);
            }
        }
    }
    
    private void updateRatingText() {
        String[] ratingTexts = {
            "Tap to rate",
            "Poor",
            "Fair", 
            "Good",
            "Very Good",
            "Excellent"
        };
        
        if (selectedRating >= 0 && selectedRating < ratingTexts.length) {
            tvRatingText.setText(ratingTexts[selectedRating]);
        }
    }
    
    private void submitReview() {
        // Validate rating
        if (selectedRating == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get review text
        String title = etReviewTitle.getText() != null ? etReviewTitle.getText().toString().trim() : "";
        String comment = etReviewComment.getText() != null ? etReviewComment.getText().toString().trim() : "";
        
        // Validate comment
        if (comment.isEmpty()) {
            Toast.makeText(this, "Please write your review", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Combine title and comment
        String fullComment = title.isEmpty() ? comment : title + "\n\n" + comment;
        
        // Submit review
        viewModel.submitReview(userId, productId, selectedRating, fullComment);
    }
}
