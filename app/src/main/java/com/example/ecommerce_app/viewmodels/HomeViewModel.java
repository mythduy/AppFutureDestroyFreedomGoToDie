package com.example.ecommerce_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce_app.data.entities.Category;
import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.data.repository.CategoryRepository;
import com.example.ecommerce_app.data.repository.ProductRepository;

import java.util.List;

/**
 * HomeViewModel - ViewModel for Home Screen
 */
public class HomeViewModel extends AndroidViewModel {
    
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    
    private LiveData<List<Product>> newArrivals;
    private LiveData<List<Product>> bestSelling;
    private LiveData<List<Category>> categories;
    
    private MutableLiveData<String> userName = new MutableLiveData<>("Jonathan");
    
    public HomeViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        categoryRepository = new CategoryRepository(application);
        
        loadData();
    }
    
    /**
     * Load initial data
     */
    private void loadData() {
        // Load latest products (New Arrivals)
        newArrivals = productRepository.getLatestProducts(10);
        
        // Load best selling products
        bestSelling = productRepository.getBestSellingProducts(10);
        
        // Load all categories
        categories = categoryRepository.getAllCategories();
    }
    
    /**
     * Get new arrivals products
     */
    public LiveData<List<Product>> getNewArrivals() {
        return newArrivals;
    }
    
    /**
     * Get best selling products
     */
    public LiveData<List<Product>> getBestSelling() {
        return bestSelling;
    }
    
    /**
     * Get all categories
     */
    public LiveData<List<Category>> getCategories() {
        return categories;
    }
    
    /**
     * Get user name for greeting
     */
    public LiveData<String> getUserName() {
        return userName;
    }
    
    /**
     * Set user name
     */
    public void setUserName(String name) {
        userName.setValue(name);
    }
    
    /**
     * Search products
     */
    public LiveData<List<Product>> searchProducts(String query) {
        return productRepository.searchProducts(query);
    }
}
