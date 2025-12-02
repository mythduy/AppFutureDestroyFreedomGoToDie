package com.example.ecommerce_app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.ecommerce_app.data.dao.ReviewDao;
import com.example.ecommerce_app.data.database.AppDatabase;
import com.example.ecommerce_app.data.entities.Review;
import com.example.ecommerce_app.data.entities.User;
import com.example.ecommerce_app.data.models.ReviewWithUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * ReviewRepository - Repository cho Review entity
 */
public class ReviewRepository {
    
    private ReviewDao reviewDao;
    
    public ReviewRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        reviewDao = database.reviewDao();
    }
    
    // ==================== OPERATIONS ====================
    
    /**
     * Lấy reviews của sản phẩm
     */
    public LiveData<List<Review>> getReviewsByProduct(long productId) {
        return reviewDao.getReviewsByProduct(productId);
    }
    
    public Future<List<Review>> getReviewsByProductSync(long productId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return reviewDao.getReviewsByProductSync(productId);
        });
    }
    
    /**
     * Lấy reviews của user
     */
    public LiveData<List<Review>> getReviewsByUser(long userId) {
        return reviewDao.getReviewsByUser(userId);
    }
    
    /**
     * Thêm review mới
     */
    public Future<Long> addReview(long userId, long productId, int rating, String comment) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            // Kiểm tra đã review chưa
            if (reviewDao.hasUserReviewed(userId, productId) > 0) {
                return -1L; // Đã review rồi
            }
            
            Review review = new Review();
            review.setUserId(userId);
            review.setProductId(productId);
            review.setRating(rating);
            review.setComment(comment);
            
            return reviewDao.insert(review);
        });
    }
    
    /**
     * Insert review trực tiếp
     */
    public void insert(Review review) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            reviewDao.insert(review);
        });
    }
    
    /**
     * Cập nhật review
     */
    public void updateReview(long reviewId, int rating, String comment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            reviewDao.updateReview(reviewId, rating, comment, new Date().getTime());
        });
    }
    
    /**
     * Xóa review
     */
    public void deleteReview(long userId, long productId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            reviewDao.deleteReview(userId, productId);
        });
    }
    
    /**
     * Kiểm tra user đã review chưa
     */
    public Future<Boolean> hasUserReviewed(long userId, long productId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return reviewDao.hasUserReviewed(userId, productId) > 0;
        });
    }
    
    /**
     * Đếm số reviews
     */
    public Future<Integer> getReviewCount(long productId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return reviewDao.getReviewCount(productId);
        });
    }
    
    /**
     * Tính rating trung bình
     */
    public Future<Double> getAverageRating(long productId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return reviewDao.getAverageRating(productId);
        });
    }
    
    /**
     * Lấy top reviews
     */
    public LiveData<List<Review>> getTopReviews(long productId, int limit) {
        return reviewDao.getTopReviews(productId, limit);
    }
    
    /**
     * Lấy reviews theo rating
     */
    public LiveData<List<Review>> getReviewsByRating(long productId, int rating) {
        return reviewDao.getReviewsByRating(productId, rating);
    }
    
    /**
     * Lấy reviews với user info
     * Map manually vì Room không tự động map ReviewWithUser
     */
    public Future<List<ReviewWithUser>> getReviewsWithUser(long productId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            List<Review> reviews = reviewDao.getReviewsWithUserSync(productId);
            List<ReviewWithUser> result = new ArrayList<>();
            
            for (Review review : reviews) {
                User user = AppDatabase.getInstance(null).userDao().getUserByIdSync(review.getUserId());
                ReviewWithUser reviewWithUser = new ReviewWithUser(review, user);
                result.add(reviewWithUser);
            }
            
            return result;
        });
    }
    
    /**
     * Lấy reviews với user info (giới hạn số lượng)
     */
    public Future<List<ReviewWithUser>> getReviewsWithUserLimit(long productId, int limit) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            List<Review> reviews = reviewDao.getReviewsWithUserLimitSync(productId, limit);
            List<ReviewWithUser> result = new ArrayList<>();
            
            for (Review review : reviews) {
                User user = AppDatabase.getInstance(null).userDao().getUserByIdSync(review.getUserId());
                ReviewWithUser reviewWithUser = new ReviewWithUser(review, user);
                result.add(reviewWithUser);
            }
            
            return result;
        });
    }
}
