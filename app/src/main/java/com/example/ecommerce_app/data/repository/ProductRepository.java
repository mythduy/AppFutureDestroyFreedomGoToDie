package com.example.ecommerce_app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.ecommerce_app.data.dao.ProductDao;
import com.example.ecommerce_app.data.database.AppDatabase;
import com.example.ecommerce_app.data.entities.Product;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * ProductRepository - Repository cho Product entity
 */
public class ProductRepository {
    
    private ProductDao productDao;
    
    public ProductRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        productDao = database.productDao();
    }
    
    // ==================== CRUD OPERATIONS ====================
    
    public LiveData<List<Product>> getAllActiveProducts() {
        return productDao.getAllActiveProducts();
    }
    
    public LiveData<Product> getProductById(long productId) {
        return productDao.getProductById(productId);
    }
    
    public Future<Product> getProductByIdSync(long productId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return productDao.getProductByIdSync(productId);
        });
    }
    
    public void insert(Product product) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            productDao.insert(product);
        });
    }
    
    public void update(Product product) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            product.setUpdatedAt(new Date());
            productDao.update(product);
        });
    }
    
    public void delete(Product product) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            productDao.delete(product);
        });
    }
    
    // ==================== SEARCH & FILTER ====================
    
    public LiveData<List<Product>> searchProducts(String keyword) {
        return productDao.searchProducts(keyword);
    }
    
    public LiveData<List<Product>> getProductsByCategory(long categoryId) {
        return productDao.getProductsByCategory(categoryId);
    }
    
    public LiveData<List<Product>> getProductsByPriceRange(double minPrice, double maxPrice) {
        return productDao.getProductsByPriceRange(minPrice, maxPrice);
    }
    
    public LiveData<List<Product>> getProductsByCategoryAndPrice(long categoryId, double minPrice, double maxPrice) {
        return productDao.getProductsByCategoryAndPrice(categoryId, minPrice, maxPrice);
    }
    
    public LiveData<List<Product>> getProductsByBrand(String brand) {
        return productDao.getProductsByBrand(brand);
    }
    
    // ==================== SPECIAL LISTS ====================
    
    public LiveData<List<Product>> getLatestProducts(int limit) {
        return productDao.getLatestProducts(limit);
    }
    
    public LiveData<List<Product>> getBestSellingProducts(int limit) {
        return productDao.getBestSellingProducts(limit);
    }
    
    public LiveData<List<Product>> getSaleProducts(int limit) {
        return productDao.getSaleProducts(limit);
    }
    
    // ==================== PAGING ====================
    
    public Future<List<Product>> getProductsPaged(int limit, int offset) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return productDao.getProductsPaged(limit, offset);
        });
    }
    
    public Future<Integer> getActiveProductCount() {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return productDao.getActiveProductCount();
        });
    }
    
    // ==================== STOCK MANAGEMENT ====================
    
    public Future<Boolean> decreaseStock(long productId, int quantity) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            int currentStock = productDao.getStock(productId);
            if (currentStock >= quantity) {
                productDao.decreaseStock(productId, quantity, new Date().getTime());
                return true;
            }
            return false;
        });
    }
    
    public void increaseStock(long productId, int quantity) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            productDao.increaseStock(productId, quantity, new Date().getTime());
        });
    }
    
    public void updateActiveStatus(long productId, boolean isActive) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            productDao.updateActiveStatus(productId, isActive, new Date().getTime());
        });
    }
}
