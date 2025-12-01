package com.example.ecommerce_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.data.repository.ProductRepository;

import java.util.List;

/**
 * ProductListViewModel - ViewModel cho danh sách sản phẩm
 * 
 * Hỗ trợ search, filter theo category, price range
 */
public class ProductListViewModel extends AndroidViewModel {
    
    private ProductRepository productRepository;
    
    // LiveData cho danh sách sản phẩm
    private LiveData<List<Product>> products;
    
    // Filter parameters
    private MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private MutableLiveData<Long> selectedCategoryId = new MutableLiveData<>(-1L);
    private MutableLiveData<Double> minPrice = new MutableLiveData<>(0.0);
    private MutableLiveData<Double> maxPrice = new MutableLiveData<>(Double.MAX_VALUE);
    
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    public ProductListViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        
        // Mặc định load tất cả products
        products = productRepository.getAllActiveProducts();
    }
    
    // ==================== GETTERS ====================
    
    public LiveData<List<Product>> getProducts() {
        return products;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }
    
    public LiveData<Long> getSelectedCategoryId() {
        return selectedCategoryId;
    }
    
    // ==================== SEARCH & FILTER ====================
    
    /**
     * Tìm kiếm sản phẩm
     */
    public void searchProducts(String keyword) {
        searchQuery.setValue(keyword);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            // Nếu không có keyword, load tất cả
            loadAllProducts();
        } else {
            products = productRepository.searchProducts(keyword);
        }
    }
    
    /**
     * Filter theo category
     */
    public void filterByCategory(long categoryId) {
        selectedCategoryId.setValue(categoryId);
        
        if (categoryId <= 0) {
            loadAllProducts();
        } else {
            products = productRepository.getProductsByCategory(categoryId);
        }
    }
    
    /**
     * Filter theo khoảng giá
     */
    public void filterByPrice(double min, double max) {
        minPrice.setValue(min);
        maxPrice.setValue(max);
        
        long categoryId = selectedCategoryId.getValue() != null ? selectedCategoryId.getValue() : -1;
        
        if (categoryId > 0) {
            products = productRepository.getProductsByCategoryAndPrice(categoryId, min, max);
        } else {
            products = productRepository.getProductsByPriceRange(min, max);
        }
    }
    
    /**
     * Filter theo brand
     */
    public void filterByBrand(String brand) {
        products = productRepository.getProductsByBrand(brand);
    }
    
    /**
     * Load tất cả sản phẩm
     */
    public void loadAllProducts() {
        selectedCategoryId.setValue(-1L);
        searchQuery.setValue("");
        products = productRepository.getAllActiveProducts();
    }
    
    // ==================== SPECIAL LISTS ====================
    
    /**
     * Lấy sản phẩm mới nhất
     */
    public LiveData<List<Product>> getLatestProducts(int limit) {
        return productRepository.getLatestProducts(limit);
    }
    
    /**
     * Lấy sản phẩm bán chạy
     */
    public LiveData<List<Product>> getBestSellingProducts(int limit) {
        return productRepository.getBestSellingProducts(limit);
    }
    
    /**
     * Lấy sản phẩm đang sale
     */
    public LiveData<List<Product>> getSaleProducts(int limit) {
        return productRepository.getSaleProducts(limit);
    }
    
    /**
     * Reset tất cả filters
     */
    public void resetFilters() {
        loadAllProducts();
    }
}
