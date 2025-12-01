package com.example.ecommerce_app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ecommerce_app.data.entities.Favorite;

import java.util.List;

/**
 * FavoriteDao - Data Access Object cho Favorite entity
 */
@Dao
public interface FavoriteDao {
    
    // ==================== CREATE ====================
    
    @Insert
    long insert(Favorite favorite);
    
    @Insert
    void insertAll(List<Favorite> favorites);
    
    // ==================== READ ====================
    
    /**
     * Lấy tất cả sản phẩm yêu thích của user
     */
    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY addedAt DESC")
    LiveData<List<Favorite>> getFavoritesByUser(long userId);
    
    /**
     * Lấy tất cả sản phẩm yêu thích của user (sync)
     */
    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY addedAt DESC")
    List<Favorite> getFavoritesByUserSync(long userId);
    
    /**
     * Kiểm tra sản phẩm đã được yêu thích chưa
     */
    @Query("SELECT * FROM favorites WHERE userId = :userId AND productId = :productId LIMIT 1")
    Favorite getFavorite(long userId, long productId);
    
    /**
     * Kiểm tra sản phẩm đã được yêu thích chưa (boolean)
     */
    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId AND productId = :productId")
    int isFavorite(long userId, long productId);
    
    /**
     * Đếm số sản phẩm yêu thích
     */
    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId")
    int getFavoriteCount(long userId);
    
    /**
     * Lấy tất cả users đã yêu thích một sản phẩm
     */
    @Query("SELECT * FROM favorites WHERE productId = :productId")
    LiveData<List<Favorite>> getUsersFavoritedProduct(long productId);
    
    // ==================== DELETE ====================
    
    @Delete
    void delete(Favorite favorite);
    
    /**
     * Xóa khỏi danh sách yêu thích
     */
    @Query("DELETE FROM favorites WHERE userId = :userId AND productId = :productId")
    void removeFromFavorites(long userId, long productId);
    
    /**
     * Xóa tất cả favorites của user
     */
    @Query("DELETE FROM favorites WHERE userId = :userId")
    void clearFavorites(long userId);
    
    @Query("DELETE FROM favorites")
    void deleteAll();
}
