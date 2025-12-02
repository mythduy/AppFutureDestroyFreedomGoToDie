package com.example.ecommerce_app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecommerce_app.data.entities.Product;

import java.util.List;

/**
 * ProductDao - Data Access Object cho Product entity
 * 
 * Bao gồm các query nâng cao: search, filter by category/price, paging
 */
@Dao
public interface ProductDao {
    
    // ==================== CREATE ====================
    
    @Insert
    long insert(Product product);
    
    @Insert
    void insertAll(List<Product> products);
    
    // ==================== READ ====================
    
    /**
     * Lấy tất cả products đang active
     */
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY createdAt DESC")
    LiveData<List<Product>> getAllActiveProducts();
    
    /**
     * Lấy tất cả products (kể cả inactive)
     */
    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    LiveData<List<Product>> getAllProducts();
    
    /**
     * Lấy tất cả products (sync - cho export)
     */
    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    List<Product> getAllProductsSync();
    
    /**
     * Lấy product theo ID
     */
    @Query("SELECT * FROM products WHERE id = :productId")
    LiveData<Product> getProductById(long productId);
    
    /**
     * Lấy product theo ID (sync)
     */
    @Query("SELECT * FROM products WHERE id = :productId")
    Product getProductByIdSync(long productId);
    
    /**
     * Tìm product theo SKU
     */
    @Query("SELECT * FROM products WHERE sku = :sku LIMIT 1")
    Product findBySku(String sku);
    
    /**
     * Tìm kiếm products theo tên
     */
    @Query("SELECT * FROM products WHERE isActive = 1 AND name LIKE '%' || :keyword || '%' ORDER BY name ASC")
    LiveData<List<Product>> searchByName(String keyword);
    
    /**
     * Tìm kiếm products theo tên hoặc mô tả
     */
    @Query("SELECT * FROM products WHERE isActive = 1 AND (name LIKE '%' || :keyword || '%' OR description LIKE '%' || :keyword || '%') ORDER BY name ASC")
    LiveData<List<Product>> searchProducts(String keyword);
    
    /**
     * Filter products theo category
     */
    @Query("SELECT * FROM products WHERE isActive = 1 AND categoryId = :categoryId ORDER BY name ASC")
    LiveData<List<Product>> getProductsByCategory(long categoryId);
    
    /**
     * Filter products theo khoảng giá
     */
    @Query("SELECT * FROM products WHERE isActive = 1 AND price BETWEEN :minPrice AND :maxPrice ORDER BY price ASC")
    LiveData<List<Product>> getProductsByPriceRange(double minPrice, double maxPrice);
    
    /**
     * Filter products theo category và khoảng giá
     */
    @Query("SELECT * FROM products WHERE isActive = 1 AND categoryId = :categoryId AND price BETWEEN :minPrice AND :maxPrice ORDER BY price ASC")
    LiveData<List<Product>> getProductsByCategoryAndPrice(long categoryId, double minPrice, double maxPrice);
    
    /**
     * Lấy products theo brand
     */
    @Query("SELECT * FROM products WHERE isActive = 1 AND brand = :brand ORDER BY name ASC")
    LiveData<List<Product>> getProductsByBrand(String brand);
    
    /**
     * Lấy các sản phẩm mới nhất (limited)
     */
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY createdAt DESC LIMIT :limit")
    LiveData<List<Product>> getLatestProducts(int limit);
    
    /**
     * Lấy các sản phẩm bán chạy (có thể sort theo số lượng order, tạm sort theo tên)
     */
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY name ASC LIMIT :limit")
    LiveData<List<Product>> getBestSellingProducts(int limit);
    
    /**
     * Lấy sản phẩm đang sale (giá < giá gốc, tạm thời sort theo giá thấp nhất)
     */
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY price ASC LIMIT :limit")
    LiveData<List<Product>> getSaleProducts(int limit);
    
    /**
     * Paging: Lấy products với offset và limit
     */
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    List<Product> getProductsPaged(int limit, int offset);
    
    /**
     * Đếm tổng số products active
     */
    @Query("SELECT COUNT(*) FROM products WHERE isActive = 1")
    int getActiveProductCount();
    
    /**
     * Kiểm tra còn hàng không
     */
    @Query("SELECT stock FROM products WHERE id = :productId")
    int getStock(long productId);
    
    // ==================== UPDATE ====================
    
    @Update
    void update(Product product);
    
    /**
     * Cập nhật stock khi có order
     */
    @Query("UPDATE products SET stock = stock - :quantity, updatedAt = :updatedAt WHERE id = :productId")
    void decreaseStock(long productId, int quantity, long updatedAt);
    
    /**
     * Tăng stock khi hủy order
     */
    @Query("UPDATE products SET stock = stock + :quantity, updatedAt = :updatedAt WHERE id = :productId")
    void increaseStock(long productId, int quantity, long updatedAt);
    
    /**
     * Cập nhật trạng thái active
     */
    @Query("UPDATE products SET isActive = :isActive, updatedAt = :updatedAt WHERE id = :productId")
    void updateActiveStatus(long productId, boolean isActive, long updatedAt);
    
    // ==================== DELETE ====================
    
    @Delete
    void delete(Product product);
    
    @Query("DELETE FROM products WHERE id = :productId")
    void deleteById(long productId);
    
    @Query("DELETE FROM products")
    void deleteAll();
}
