package com.example.ecommerce_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.data.repository.ProductRepository;

/**
 * ProductDetailViewModel - ViewModel cho chi tiết sản phẩm
 * 
 * Hiển thị thông tin chi tiết, gallery ảnh, reviews
 */
public class ProductDetailViewModel extends AndroidViewModel {
    
    private ProductRepository productRepository;
    
    private MutableLiveData<Long> productId = new MutableLiveData<>();
    private LiveData<Product> product;
    
    public ProductDetailViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
    }
    
    // ==================== GETTERS ====================
    
    public LiveData<Product> getProduct() {
        return product;
    }
    
    public LiveData<Long> getProductId() {
        return productId;
    }
    
    // ==================== OPERATIONS ====================
    
    /**
     * Load product theo ID
     */
    public void loadProduct(long id) {
        productId.setValue(id);
        product = productRepository.getProductById(id);
    }
    
    /**
     * Refresh product data
     */
    public void refresh() {
        Long id = productId.getValue();
        if (id != null && id > 0) {
            loadProduct(id);
        }
    }
}
