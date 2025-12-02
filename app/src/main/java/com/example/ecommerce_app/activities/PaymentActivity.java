package com.example.ecommerce_app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.R;
import com.example.ecommerce_app.adapters.PaymentProductAdapter;
import com.example.ecommerce_app.data.entities.CartItem;
import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.data.repository.ProductRepository;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class PaymentActivity extends AppCompatActivity {
    
    private static final double SHIPPING_FEE = 6.00;
    
    // Views
    private ImageView btnBack;
    private TextView btnEditAddress;
    private TextView tvAddressType;
    private TextView tvAddressLine1;
    private TextView tvAddressLine2;
    private TextView tvProductsTitle;
    private RecyclerView rvProducts;
    private TextView tvTotalAmount;
    private MaterialButton btnCheckoutNow;
    
    // Data
    private List<CartItem> selectedItems;
    private Map<Long, Product> productMap;
    private PaymentProductAdapter adapter;
    private ProductRepository productRepository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        
        productRepository = new ProductRepository(getApplication());
        
        initViews();
        receiveData();
        setupRecyclerView();
        setupListeners();
        loadProducts();
    }
    
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnEditAddress = findViewById(R.id.btnEditAddress);
        tvAddressType = findViewById(R.id.tvAddressType);
        tvAddressLine1 = findViewById(R.id.tvAddressLine1);
        tvAddressLine2 = findViewById(R.id.tvAddressLine2);
        tvProductsTitle = findViewById(R.id.tvProductsTitle);
        rvProducts = findViewById(R.id.rvProducts);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnCheckoutNow = findViewById(R.id.btnCheckoutNow);
    }
    
    private void receiveData() {
        // Receive data from intent
        ArrayList<Long> itemIds = (ArrayList<Long>) getIntent().getSerializableExtra("itemIds");
        ArrayList<Long> productIds = (ArrayList<Long>) getIntent().getSerializableExtra("productIds");
        ArrayList<Integer> quantities = (ArrayList<Integer>) getIntent().getSerializableExtra("quantities");
        
        selectedItems = new ArrayList<>();
        productMap = new HashMap<>();
        
        if (itemIds != null && productIds != null && quantities != null && 
            itemIds.size() == productIds.size() && itemIds.size() == quantities.size()) {
            
            // Reconstruct CartItem objects
            for (int i = 0; i < itemIds.size(); i++) {
                CartItem item = new CartItem();
                item.setId(itemIds.get(i));
                item.setProductId(productIds.get(i));
                item.setQuantity(quantities.get(i));
                selectedItems.add(item);
            }
        }
    }
    
    private void setupRecyclerView() {
        adapter = new PaymentProductAdapter();
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(adapter);
        
        adapter.setItems(selectedItems, productMap);
    }
    
    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnEditAddress.setOnClickListener(v -> {
            // TODO: Navigate to address selection/edit screen
            Toast.makeText(this, "Address edit feature coming soon", Toast.LENGTH_SHORT).show();
        });
        
        findViewById(R.id.cvPaymentMethod).setOnClickListener(v -> {
            // TODO: Navigate to payment method selection screen
            Toast.makeText(this, "Payment method selection coming soon", Toast.LENGTH_SHORT).show();
        });
        
        btnCheckoutNow.setOnClickListener(v -> {
            // TODO: Process payment and create order
            processCheckout();
        });
    }
    
    /**
     * Load products from database in background thread
     */
    private void loadProducts() {
        new Thread(() -> {
            for (CartItem item : selectedItems) {
                try {
                    Product product = productRepository.getProductByIdSync(item.getProductId()).get();
                    if (product != null) {
                        productMap.put(item.getProductId(), product);
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            // Update UI on main thread
            runOnUiThread(() -> {
                adapter.setItems(selectedItems, productMap);
                updateUI();
            });
        }).start();
    }

    private void updateUI() {
        // Update products title
        int itemCount = selectedItems.size();
        tvProductsTitle.setText(String.format(Locale.US, "Products (%d)", itemCount));
        
        // Calculate total amount
        double subtotal = calculateSubtotal();
        double totalAmount = subtotal + SHIPPING_FEE;
        
        // Update total amount
        tvTotalAmount.setText(String.format(Locale.US, "$ %.2f", totalAmount));
        
        // Set default address (can be replaced with user's actual address)
        tvAddressType.setText("House");
        tvAddressLine1.setText("5482 Adobe Falls Rd #155San");
        tvAddressLine2.setText("Diego, California(CA), 92120");
    }
    
    private double calculateSubtotal() {
        double total = 0.0;
        
        for (CartItem item : selectedItems) {
            Product product = productMap.get(item.getProductId());
            if (product != null) {
                total += product.getPrice() * item.getQuantity();
            }
        }
        
        return total;
    }
    
    private void processCheckout() {
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "No items to checkout", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // TODO: Implement payment processing logic
        // 1. Validate payment method
        // 2. Create order in database
        // 3. Process payment
        // 4. Clear cart items
        // 5. Navigate to order confirmation screen
        
        Toast.makeText(this, "Payment processing feature coming soon", Toast.LENGTH_SHORT).show();
    }
}
