package com.example.ecommerce_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.data.models.ReviewWithUser;
import com.example.ecommerce_app.data.repository.ProductRepository;
import com.example.ecommerce_app.data.repository.ReviewRepository;

import java.util.List;

/**
 * ReviewsViewModel - ViewModel cho màn hình danh sách reviews
 */
public class ReviewsViewModel extends AndroidViewModel {
    
    private ProductRepository productRepository;
    private ReviewRepository reviewRepository;
    
    private MutableLiveData<Product> product = new MutableLiveData<>();
    private MutableLiveData<List<ReviewWithUser>> reviews = new MutableLiveData<>();
    private MutableLiveData<Integer> reviewCount = new MutableLiveData<>();
    private MutableLiveData<Double> averageRating = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();
    
    public ReviewsViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        reviewRepository = new ReviewRepository(application);
    }
    
    // ==================== PRODUCT ====================
    
    public void loadProduct(long productId) {
        // Observe product from repository and post to our MutableLiveData
        LiveData<Product> productLiveData = productRepository.getProductById(productId);
        productLiveData.observeForever(loadedProduct -> {
            product.postValue(loadedProduct);
        });
    }
    
    public LiveData<Product> getProduct() {
        return product;
    }
    
    // ==================== REVIEWS ====================
    
    /**
     * Load tất cả reviews của sản phẩm
     */
    public void loadAllReviews(long productId) {
        new Thread(() -> {
            try {
                List<ReviewWithUser> reviewList = reviewRepository.getReviewsWithUser(productId).get();
                reviews.postValue(reviewList);
                
                // Load stats
                Integer count = reviewRepository.getReviewCount(productId).get();
                reviewCount.postValue(count);
                
                Double rating = reviewRepository.getAverageRating(productId).get();
                averageRating.postValue(rating);
            } catch (Exception e) {
                errorMessage.postValue("Lỗi khi tải reviews: " + e.getMessage());
            }
        }).start();
    }
    
    public LiveData<List<ReviewWithUser>> getReviews() {
        return reviews;
    }
    
    public LiveData<Integer> getReviewCount() {
        return reviewCount;
    }
    
    public LiveData<Double> getAverageRating() {
        return averageRating;
    }
    
    // ==================== MESSAGES ====================
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }
    
    public void clearMessages() {
        errorMessage.setValue(null);
        successMessage.setValue(null);
    }
}
