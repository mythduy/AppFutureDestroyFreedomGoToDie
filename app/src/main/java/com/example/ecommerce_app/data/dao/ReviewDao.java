package com.example.ecommerce_app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.ecommerce_app.data.entities.Review;
import com.example.ecommerce_app.data.models.ReviewWithUser;

import java.util.List;

/**
 * ReviewDao - Data Access Object cho Review entity
 */
@Dao
public interface ReviewDao {
    
    // ==================== CREATE ====================
    
    @Insert
    long insert(Review review);
    
    @Insert
    void insertAll(List<Review> reviews);
    
    // ==================== READ ====================
    
    /**
     * Lấy tất cả reviews của một sản phẩm
     */
    @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY createdAt DESC")
    LiveData<List<Review>> getReviewsByProduct(long productId);
    
    /**
     * Lấy tất cả reviews của một sản phẩm (sync)
     */
    @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY createdAt DESC")
    List<Review> getReviewsByProductSync(long productId);
    
    /**
     * Lấy tất cả reviews của một user
     */
    @Query("SELECT * FROM reviews WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<Review>> getReviewsByUser(long userId);
    
    /**
     * Lấy review cụ thể (user + product)
     */
    @Query("SELECT * FROM reviews WHERE userId = :userId AND productId = :productId LIMIT 1")
    Review getReview(long userId, long productId);
    
    /**
     * Kiểm tra user đã review sản phẩm chưa
     */
    @Query("SELECT COUNT(*) FROM reviews WHERE userId = :userId AND productId = :productId")
    int hasUserReviewed(long userId, long productId);
    
    /**
     * Đếm số reviews của sản phẩm
     */
    @Query("SELECT COUNT(*) FROM reviews WHERE productId = :productId")
    int getReviewCount(long productId);
    
    /**
     * Tính rating trung bình của sản phẩm
     */
    @Query("SELECT AVG(rating) FROM reviews WHERE productId = :productId")
    double getAverageRating(long productId);
    
    /**
     * Lấy reviews theo rating
     */
    @Query("SELECT * FROM reviews WHERE productId = :productId AND rating = :rating ORDER BY createdAt DESC")
    LiveData<List<Review>> getReviewsByRating(long productId, int rating);
    
    /**
     * Lấy top reviews (rating cao nhất)
     */
    @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY rating DESC, createdAt DESC LIMIT :limit")
    LiveData<List<Review>> getTopReviews(long productId, int limit);
    
    /**
     * Lấy reviews với user info (JOIN)
     * Note: Room không tự động map ReviewWithUser, cần xử lý trong Repository
     */
    @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY createdAt DESC")
    List<Review> getReviewsWithUserSync(long productId);
    
    @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY createdAt DESC LIMIT :limit")
    List<Review> getReviewsWithUserLimitSync(long productId, int limit);
    
    // ==================== UPDATE ====================
    
    @Update
    void update(Review review);
    
    /**
     * Cập nhật review
     */
    @Query("UPDATE reviews SET rating = :rating, comment = :comment, updatedAt = :updatedAt WHERE id = :reviewId")
    void updateReview(long reviewId, int rating, String comment, long updatedAt);
    
    // ==================== DELETE ====================
    
    @Delete
    void delete(Review review);
    
    @Query("DELETE FROM reviews WHERE id = :reviewId")
    void deleteById(long reviewId);
    
    /**
     * Xóa review của user cho sản phẩm
     */
    @Query("DELETE FROM reviews WHERE userId = :userId AND productId = :productId")
    void deleteReview(long userId, long productId);
    
    @Query("DELETE FROM reviews")
    void deleteAll();
}
