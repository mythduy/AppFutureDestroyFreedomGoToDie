package com.example.ecommerce_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.adapters.ProductImagePagerAdapter;
import com.example.ecommerce_app.adapters.ReviewAdapter;
import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.utils.ImageHelper;
import com.example.ecommerce_app.utils.SessionManager;
import com.example.ecommerce_app.viewmodels.ProductDetailViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.viewpager2.widget.ViewPager2;

/**
 * ProductDetailActivity - Màn hình chi tiết sản phẩm
 * Design từ Figma: Product Details Screen (node-id=270-4656)
 */
public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "product_id";
    
    // UI Components
    private ImageButton btnBack;
    private ViewPager2 viewPagerImages;
    private TabLayout tabIndicator;
    private MaterialButton btnPrevImage, btnNextImage;
    private TextView tvProductName, tvQuantity, tvTotalPrice;
    private TextView tvProductDescription, tvSpecification;
    private CardView btnMinus, btnPlus;
    private MaterialButton btnAddToCart;
    private FloatingActionButton btnFavorite;
    private TextView tvViewAll;
    private RecyclerView rvReviews;
    
    // ViewModel & Adapter
    private ProductDetailViewModel viewModel;
    private ProductImagePagerAdapter imagePagerAdapter;
    private ReviewAdapter reviewAdapter;
    private SessionManager sessionManager;
    
    // Data
    private long productId;
    private long userId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        
        // Get product ID from intent
        productId = getIntent().getLongExtra(EXTRA_PRODUCT_ID, -1);
        if (productId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Get user ID from session
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);
        
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
        viewModel.loadReviews(productId);
        viewModel.checkFavorite(userId, productId);
    }
    
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        viewPagerImages = findViewById(R.id.viewPagerImages);
        tabIndicator = findViewById(R.id.tabIndicator);
        btnPrevImage = findViewById(R.id.btnPrevImage);
        btnNextImage = findViewById(R.id.btnNextImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvSpecification = findViewById(R.id.tvSpecification);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnFavorite = findViewById(R.id.btnFavorite);
        tvViewAll = findViewById(R.id.tvViewAll);
        rvReviews = findViewById(R.id.rvReviews);
    }
    
    private void setupRecyclerView() {
        // Setup image pager
        imagePagerAdapter = new ProductImagePagerAdapter(this, productId);
        viewPagerImages.setAdapter(imagePagerAdapter);
        
        // Setup reviews
        reviewAdapter = new ReviewAdapter(this);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);
        rvReviews.setNestedScrollingEnabled(false);
    }
    
    private void setupObservers() {
        // Observe product data
        viewModel.getProduct().observe(this, product -> {
            if (product != null) {
                displayProduct(product);
            }
        });
        
        // Observe reviews
        viewModel.getReviews().observe(this, reviews -> {
            if (reviews != null) {
                reviewAdapter.setReviews(reviews);
            }
        });
        
        // Observe quantity
        viewModel.getQuantity().observe(this, quantity -> {
            if (quantity != null) {
                tvQuantity.setText(String.valueOf(quantity));
                updateTotalPrice();
            }
        });
        
        // Observe favorite status
        viewModel.getIsFavorite().observe(this, isFavorite -> {
            if (isFavorite != null && isFavorite) {
                btnFavorite.setImageResource(R.drawable.ic_favorite);
            } else {
                btnFavorite.setImageResource(R.drawable.ic_favorite_border);
            }
        });
        
        // Observe messages
        viewModel.getSuccessMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                viewModel.clearMessages();
            }
        });
        
        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                viewModel.clearMessages();
            }
        });
    }
    
    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());
        
        // Image navigation buttons
        btnPrevImage.setOnClickListener(v -> {
            int currentItem = viewPagerImages.getCurrentItem();
            if (currentItem > 0) {
                viewPagerImages.setCurrentItem(currentItem - 1, true);
            }
        });
        
        btnNextImage.setOnClickListener(v -> {
            int currentItem = viewPagerImages.getCurrentItem();
            if (currentItem < imagePagerAdapter.getItemCount() - 1) {
                viewPagerImages.setCurrentItem(currentItem + 1, true);
            }
        });
        
        // Update navigation buttons visibility on page change
        viewPagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateNavigationButtons(position);
            }
        });
        
        // Quantity controls
        btnMinus.setOnClickListener(v -> viewModel.decreaseQuantity());
        btnPlus.setOnClickListener(v -> viewModel.increaseQuantity());
        
        // Add to Cart
        btnAddToCart.setOnClickListener(v -> {
            if (userId > 0) {
                viewModel.addToCart(userId, productId);
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Favorite
        btnFavorite.setOnClickListener(v -> {
            if (userId > 0) {
                viewModel.toggleFavorite(userId, productId);
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập để thêm yêu thích", Toast.LENGTH_SHORT).show();
            }
        });
        
        // View All Reviews
        tvViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewsActivity.class);
            intent.putExtra(ReviewsActivity.EXTRA_PRODUCT_ID, productId);
            startActivity(intent);
        });
    }
    
    /**
     * Update navigation buttons visibility based on current position
     */
    private void updateNavigationButtons(int position) {
        int itemCount = imagePagerAdapter.getItemCount();
        
        // Hide buttons if only one image
        if (itemCount <= 1) {
            btnPrevImage.setVisibility(android.view.View.GONE);
            btnNextImage.setVisibility(android.view.View.GONE);
            return;
        }
        
        // Show/hide previous button
        btnPrevImage.setVisibility(position > 0 ? android.view.View.VISIBLE : android.view.View.INVISIBLE);
        
        // Show/hide next button
        btnNextImage.setVisibility(position < itemCount - 1 ? android.view.View.VISIBLE : android.view.View.INVISIBLE);
    }
    
    private void displayProduct(Product product) {
        // Set product name
        tvProductName.setText(product.getName());
        
        // Load product images into ViewPager
        List<String> imageNames = getProductImageNames(product);
        imagePagerAdapter.setImageNames(imageNames);
        
        // Setup tab indicator with ViewPager2 only if there are multiple images
        if (imageNames.size() > 1) {
            tabIndicator.setVisibility(android.view.View.VISIBLE);
            new TabLayoutMediator(tabIndicator, viewPagerImages,
                    (tab, position) -> {
                        // Don't need to set any text, just create tabs
                    }
            ).attach();
        } else {
            // Hide indicator if only 1 image
            tabIndicator.setVisibility(android.view.View.GONE);
        }
        
        // Update navigation buttons for initial state
        updateNavigationButtons(0);
        
        // Set description
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            tvProductDescription.setText(formatDescription(product.getDescription()));
        }
        
        // Set specifications
        if (product.getSpecifications() != null && !product.getSpecifications().isEmpty()) {
            tvSpecification.setText(product.getSpecifications());
        }
        
        // Update total price
        updateTotalPrice();
    }
    
    /**
     * Get list of image names for the product
     * If product has imageFilenames, use them. Otherwise, check for default images.
     */
    private List<String> getProductImageNames(Product product) {
        List<String> imageNames = new ArrayList<>();
        
        // Check if product has image filenames defined
        if (product.getImageFilenames() != null && !product.getImageFilenames().isEmpty()) {
            imageNames.addAll(product.getImageFilenames());
        } else {
            // Try to get images from assets folder dynamically
            String[] availableImages = ImageHelper.getProductImageList(this, product.getId());
            if (availableImages != null && availableImages.length > 0) {
                // Sort to ensure main.jpg comes first
                java.util.Arrays.sort(availableImages, (a, b) -> {
                    if (a.equals("main.jpg")) return -1;
                    if (b.equals("main.jpg")) return 1;
                    return a.compareTo(b);
                });
                
                for (String imageName : availableImages) {
                    imageNames.add(imageName);
                }
            }
        }
        
        // Fallback: if no images found, use main.jpg
        if (imageNames.isEmpty()) {
            imageNames.add(ImageHelper.MAIN_IMAGE);
        }
        
        return imageNames;
    }
    
    /**
     * Format description thành bullet list
     */
    private String formatDescription(String description) {
        // Nếu description đã có bullet points, return luôn
        if (description.contains("•")) {
            return description;
        }
        
        // Nếu không, thêm bullet points cho mỗi dòng
        String[] lines = description.split("\n");
        StringBuilder formatted = new StringBuilder();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                formatted.append("• ").append(line.trim()).append("\n");
            }
        }
        return formatted.toString().trim();
    }
    
    /**
     * Update total price based on quantity
     */
    private void updateTotalPrice() {
        Product product = viewModel.getProduct().getValue();
        Integer quantity = viewModel.getQuantity().getValue();
        
        if (product != null && quantity != null) {
            double totalPrice = product.getPrice() * quantity;
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvTotalPrice.setText(formatter.format(totalPrice));
        }
    }
}
