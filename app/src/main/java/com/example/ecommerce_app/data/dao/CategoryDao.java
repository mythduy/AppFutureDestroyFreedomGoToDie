package com.example.ecommerce_app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecommerce_app.data.entities.Category;

import java.util.List;

/**
 * CategoryDao - Data Access Object cho Category entity
 */
@Dao
public interface CategoryDao {
    
    // ==================== CREATE ====================
    
    @Insert
    long insert(Category category);
    
    @Insert
    void insertAll(List<Category> categories);
    
    // ==================== READ ====================
    
    /**
     * Lấy tất cả categories
     */
    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAllCategories();
    
    /**
     * Lấy tất cả categories (sync)
     */
    @Query("SELECT * FROM categories ORDER BY name ASC")
    List<Category> getAllCategoriesSync();
    
    /**
     * Lấy category theo ID
     */
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    LiveData<Category> getCategoryById(long categoryId);
    
    /**
     * Lấy category theo ID (sync)
     */
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    Category getCategoryByIdSync(long categoryId);
    
    /**
     * Tìm category theo tên
     */
    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    Category findByName(String name);
    
    /**
     * Tìm kiếm categories theo keyword
     */
    @Query("SELECT * FROM categories WHERE name LIKE '%' || :keyword || '%' OR description LIKE '%' || :keyword || '%' ORDER BY name ASC")
    LiveData<List<Category>> searchCategories(String keyword);
    
    /**
     * Đếm số lượng categories
     */
    @Query("SELECT COUNT(*) FROM categories")
    int getCount();
    
    // ==================== UPDATE ====================
    
    @Update
    void update(Category category);
    
    // ==================== DELETE ====================
    
    @Delete
    void delete(Category category);
    
    @Query("DELETE FROM categories WHERE id = :categoryId")
    void deleteById(long categoryId);
    
    @Query("DELETE FROM categories")
    void deleteAll();
}
