package com.example.ecommerce_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.data.entities.Review;
import com.example.ecommerce_app.data.repository.ProductRepository;
import com.example.ecommerce_app.data.repository.ReviewRepository;

import java.util.Date;

/**
 * AddReviewViewModel - ViewModel cho màn hình thêm review
 */
public class AddReviewViewModel extends AndroidViewModel {
    
    private ProductRepository productRepository;
    private ReviewRepository reviewRepository;
    
    private MutableLiveData<Product> product = new MutableLiveData<>();
    private MutableLiveData<Boolean> isReviewSubmitted = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();
    
    public AddReviewViewModel(@NonNull Application application) {
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
    
    // ==================== REVIEW ====================
    
    /**
     * Submit review mới
     */
    public void submitReview(long userId, long productId, int rating, String comment) {
        new Thread(() -> {
            try {
                Review review = new Review();
                review.setUserId(userId);
                review.setProductId(productId);
                review.setRating(rating);
                review.setComment(comment);
                review.setCreatedAt(new Date());
                review.setUpdatedAt(new Date());
                
                reviewRepository.insert(review);
                
                isReviewSubmitted.postValue(true);
                successMessage.postValue("Review submitted successfully!");
            } catch (Exception e) {
                errorMessage.postValue("Failed to submit review: " + e.getMessage());
            }
        }).start();
    }
    
    public LiveData<Boolean> getIsReviewSubmitted() {
        return isReviewSubmitted;
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
