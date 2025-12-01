package com.example.ecommerce_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce_app.data.entities.Review;
import com.example.ecommerce_app.data.repository.ReviewRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * ReviewViewModel - ViewModel cho quản lý đánh giá sản phẩm
 */
public class ReviewViewModel extends AndroidViewModel {
    
    private ReviewRepository reviewRepository;
    
    private MutableLiveData<Long> currentUserId = new MutableLiveData<>();
    private MutableLiveData<Long> currentProductId = new MutableLiveData<>();
    private LiveData<List<Review>> reviews;
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> reviewSubmitted = new MutableLiveData<>();
    
    // Form fields
    private MutableLiveData<Integer> rating = new MutableLiveData<>(5);
    private MutableLiveData<String> comment = new MutableLiveData<>("");
    
    public ReviewViewModel(@NonNull Application application) {
        super(application);
        reviewRepository = new ReviewRepository(application);
    }
    
    // ==================== GETTERS ====================
    
    public LiveData<List<Review>> getReviews() {
        return reviews;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<Boolean> getReviewSubmitted() {
        return reviewSubmitted;
    }
    
    public LiveData<Integer> getRating() {
        return rating;
    }
    
    public LiveData<String> getComment() {
        return comment;
    }
    
    // ==================== SETTERS ====================
    
    public void setUserId(long userId) {
        currentUserId.setValue(userId);
    }
    
    public void setProductId(long productId) {
        currentProductId.setValue(productId);
        loadReviews();
    }
    
    public void setRating(int rating) {
        this.rating.setValue(rating);
    }
    
    public void setComment(String comment) {
        this.comment.setValue(comment);
    }
    
    // ==================== LOAD REVIEWS ====================
    
    /**
     * Load tất cả reviews của sản phẩm
     */
    public void loadReviews() {
        Long productId = currentProductId.getValue();
        if (productId == null || productId <= 0) {
            return;
        }
        
        reviews = reviewRepository.getReviewsByProduct(productId);
    }
    
    /**
     * Load reviews của user
     */
    public void loadUserReviews() {
        Long userId = currentUserId.getValue();
        if (userId == null || userId <= 0) {
            return;
        }
        
        reviews = reviewRepository.getReviewsByUser(userId);
    }
    
    /**
     * Load top reviews (rating cao nhất)
     */
    public void loadTopReviews(int limit) {
        Long productId = currentProductId.getValue();
        if (productId == null || productId <= 0) {
            return;
        }
        
        reviews = reviewRepository.getTopReviews(productId, limit);
    }
    
    /**
     * Filter reviews theo rating
     */
    public void filterByRating(int rating) {
        Long productId = currentProductId.getValue();
        if (productId == null || productId <= 0) {
            return;
        }
        
        reviews = reviewRepository.getReviewsByRating(productId, rating);
    }
    
    // ==================== SUBMIT REVIEW ====================
    
    /**
     * Submit review mới
     */
    public void submitReview() {
        Long userId = currentUserId.getValue();
        Long productId = currentProductId.getValue();
        Integer ratingValue = rating.getValue();
        String commentText = comment.getValue();
        
        // Validation
        if (userId == null || userId <= 0) {
            errorMessage.setValue("Vui lòng đăng nhập để đánh giá");
            return;
        }
        
        if (productId == null || productId <= 0) {
            errorMessage.setValue("Không tìm thấy sản phẩm");
            return;
        }
        
        if (ratingValue == null || ratingValue < 1 || ratingValue > 5) {
            errorMessage.setValue("Vui lòng chọn số sao từ 1-5");
            return;
        }
        
        if (commentText == null || commentText.trim().isEmpty()) {
            errorMessage.setValue("Vui lòng nhập nhận xét");
            return;
        }
        
        try {
            long reviewId = reviewRepository.addReview(userId, productId, ratingValue, commentText).get();
            
            if (reviewId == -1) {
                errorMessage.setValue("Bạn đã đánh giá sản phẩm này rồi");
                reviewSubmitted.setValue(false);
            } else if (reviewId > 0) {
                errorMessage.setValue("Đánh giá thành công");
                reviewSubmitted.setValue(true);
                resetForm();
                loadReviews(); // Reload để hiển thị review mới
            } else {
                errorMessage.setValue("Lỗi khi gửi đánh giá");
                reviewSubmitted.setValue(false);
            }
        } catch (ExecutionException | InterruptedException e) {
            errorMessage.setValue("Lỗi: " + e.getMessage());
            reviewSubmitted.setValue(false);
        }
    }
    
    /**
     * Update review đã có
     */
    public void updateReview(long reviewId) {
        Integer ratingValue = rating.getValue();
        String commentText = comment.getValue();
        
        if (ratingValue == null || ratingValue < 1 || ratingValue > 5) {
            errorMessage.setValue("Vui lòng chọn số sao từ 1-5");
            return;
        }
        
        if (commentText == null || commentText.trim().isEmpty()) {
            errorMessage.setValue("Vui lòng nhập nhận xét");
            return;
        }
        
        reviewRepository.updateReview(reviewId, ratingValue, commentText);
        errorMessage.setValue("Cập nhật đánh giá thành công");
        reviewSubmitted.setValue(true);
        resetForm();
        loadReviews();
    }
    
    /**
     * Xóa review
     */
    public void deleteReview(long reviewId) {
        Long userId = currentUserId.getValue();
        Long productId = currentProductId.getValue();
        
        if (userId == null || userId <= 0 || productId == null || productId <= 0) {
            errorMessage.setValue("Không thể xóa đánh giá");
            return;
        }
        
        reviewRepository.deleteReview(userId, productId);
        errorMessage.setValue("Đã xóa đánh giá");
        loadReviews();
    }
    
    // ==================== CHECK STATUS ====================
    
    /**
     * Kiểm tra user đã review sản phẩm chưa
     */
    public void hasUserReviewed(OnCheckCallback callback) {
        Long userId = currentUserId.getValue();
        Long productId = currentProductId.getValue();
        
        if (userId == null || userId <= 0 || productId == null || productId <= 0) {
            callback.onResult(false);
            return;
        }
        
        try {
            boolean hasReviewed = reviewRepository.hasUserReviewed(userId, productId).get();
            callback.onResult(hasReviewed);
        } catch (ExecutionException | InterruptedException e) {
            callback.onResult(false);
        }
    }
    
    // ==================== STATISTICS ====================
    
    /**
     * Lấy số lượng reviews
     */
    public void getReviewCount(OnCountCallback callback) {
        Long productId = currentProductId.getValue();
        if (productId == null || productId <= 0) {
            callback.onResult(0);
            return;
        }
        
        try {
            int count = reviewRepository.getReviewCount(productId).get();
            callback.onResult(count);
        } catch (ExecutionException | InterruptedException e) {
            callback.onResult(0);
        }
    }
    
    /**
     * Lấy rating trung bình
     */
    public void getAverageRating(OnRatingCallback callback) {
        Long productId = currentProductId.getValue();
        if (productId == null || productId <= 0) {
            callback.onResult(0.0);
            return;
        }
        
        try {
            double avgRating = reviewRepository.getAverageRating(productId).get();
            callback.onResult(avgRating);
        } catch (ExecutionException | InterruptedException e) {
            callback.onResult(0.0);
        }
    }
    
    // ==================== HELPERS ====================
    
    /**
     * Reset form về giá trị mặc định
     */
    public void resetForm() {
        rating.setValue(5);
        comment.setValue("");
        reviewSubmitted.setValue(false);
    }
    
    // Callback interfaces
    public interface OnCheckCallback {
        void onResult(boolean hasReviewed);
    }
    
    public interface OnCountCallback {
        void onResult(int count);
    }
    
    public interface OnRatingCallback {
        void onResult(double rating);
    }
}
