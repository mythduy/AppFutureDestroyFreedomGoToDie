package com.example.ecommerce_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.data.models.ReviewWithUser;
import com.example.ecommerce_app.data.repository.CartRepository;
import com.example.ecommerce_app.data.repository.FavoriteRepository;
import com.example.ecommerce_app.data.repository.ProductRepository;
import com.example.ecommerce_app.data.repository.ReviewRepository;

import java.util.List;

/**
 * ProductDetailViewModel - ViewModel cho chi tiết sản phẩm
 * 
 * Quản lý: thông tin sản phẩm, reviews, cart, favorite, quantity
 */
public class ProductDetailViewModel extends AndroidViewModel {
    
    private ProductRepository productRepository;
    private ReviewRepository reviewRepository;
    private CartRepository cartRepository;
    private FavoriteRepository favoriteRepository;
    
    private MutableLiveData<Long> productId = new MutableLiveData<>();
    private MutableLiveData<Product> product = new MutableLiveData<>();
    private MutableLiveData<List<ReviewWithUser>> reviews = new MutableLiveData<>();
    private MutableLiveData<Integer> reviewCount = new MutableLiveData<>();
    private MutableLiveData<Double> averageRating = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFavorite = new MutableLiveData<>();
    private MutableLiveData<Integer> quantity = new MutableLiveData<>(1);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    public ProductDetailViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        reviewRepository = new ReviewRepository(application);
        cartRepository = new CartRepository(application);
        favoriteRepository = new FavoriteRepository(application);
    }
    
    // ==================== PRODUCT ====================
    
    public LiveData<Product> getProduct() {
        return product;
    }
    
    public LiveData<Long> getProductId() {
        return productId;
    }
    
    /**
     * Load product theo ID
     */
    public void loadProduct(long id) {
        productId.setValue(id);
        isLoading.setValue(true);
        
        // Observe product from repository and post to our MutableLiveData
        LiveData<Product> productLiveData = productRepository.getProductById(id);
        productLiveData.observeForever(loadedProduct -> {
            product.postValue(loadedProduct);
            isLoading.postValue(false);
        });
    }
    
    /**
     * Refresh product data
     */
    public void refresh() {
        Long id = productId.getValue();
        if (id != null && id > 0) {
            loadProduct(id);
        }
    }
    
    // ==================== REVIEWS ====================
    
    /**
     * Load reviews của sản phẩm (top 3 mới nhất)
     */
    public void loadReviews(long productId) {
        new Thread(() -> {
            try {
                List<ReviewWithUser> reviewList = reviewRepository.getReviewsWithUserLimit(productId, 3).get();
                reviews.postValue(reviewList);
            } catch (Exception e) {
                errorMessage.postValue("Lỗi khi tải reviews: " + e.getMessage());
            }
        }).start();
        
        loadReviewStats(productId);
    }
    
    /**
     * Load tổng số reviews và rating trung bình
     */
    private void loadReviewStats(long productId) {
        new Thread(() -> {
            try {
                Integer count = reviewRepository.getReviewCount(productId).get();
                reviewCount.postValue(count);
                
                Double rating = reviewRepository.getAverageRating(productId).get();
                averageRating.postValue(rating);
            } catch (Exception e) {
                errorMessage.postValue("Lỗi khi tải thống kê reviews: " + e.getMessage());
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
    
    // ==================== CART ====================
    
    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    public void addToCart(long userId, long productId) {
        int qty = quantity.getValue() != null ? quantity.getValue() : 1;
        
        new Thread(() -> {
            try {
                Boolean result = cartRepository.addToCart(userId, productId, qty).get();
                if (result != null && result) {
                    successMessage.postValue("Đã thêm vào giỏ hàng");
                } else {
                    errorMessage.postValue("Không thể thêm vào giỏ hàng");
                }
            } catch (Exception e) {
                errorMessage.postValue("Lỗi khi thêm vào giỏ hàng: " + e.getMessage());
            }
        }).start();
    }
    
    // ==================== FAVORITE ====================
    
    /**
     * Kiểm tra sản phẩm đã được yêu thích chưa
     */
    public void checkFavorite(long userId, long productId) {
        new Thread(() -> {
            try {
                Boolean isFav = favoriteRepository.isFavorite(userId, productId).get();
                isFavorite.postValue(isFav);
            } catch (Exception e) {
                errorMessage.postValue("Lỗi khi kiểm tra favorite: " + e.getMessage());
            }
        }).start();
    }
    
    /**
     * Toggle favorite
     */
    public void toggleFavorite(long userId, long productId) {
        Boolean currentState = isFavorite.getValue();
        if (currentState != null && currentState) {
            // Xóa khỏi favorite
            favoriteRepository.removeFromFavorites(userId, productId);
            isFavorite.postValue(false);
            successMessage.postValue("Đã xóa khỏi yêu thích");
        } else {
            // Thêm vào favorite
            new Thread(() -> {
                try {
                    Boolean result = favoriteRepository.addToFavorites(userId, productId).get();
                    if (result != null && result) {
                        isFavorite.postValue(true);
                        successMessage.postValue("Đã thêm vào yêu thích");
                    } else {
                        errorMessage.postValue("Không thể thêm vào yêu thích");
                    }
                } catch (Exception e) {
                    errorMessage.postValue("Lỗi khi thêm vào yêu thích: " + e.getMessage());
                }
            }).start();
        }
    }
    
    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }
    
    // ==================== QUANTITY ====================
    
    /**
     * Tăng số lượng
     */
    public void increaseQuantity() {
        int currentQty = quantity.getValue() != null ? quantity.getValue() : 1;
        Product currentProduct = product.getValue();
        
        if (currentProduct != null) {
            if (currentQty < currentProduct.getStock()) {
                quantity.setValue(currentQty + 1);
            } else {
                errorMessage.setValue("Đã đạt số lượng tối đa trong kho");
            }
        }
    }
    
    /**
     * Giảm số lượng
     */
    public void decreaseQuantity() {
        int currentQty = quantity.getValue() != null ? quantity.getValue() : 1;
        if (currentQty > 1) {
            quantity.setValue(currentQty - 1);
        }
    }
    
    public LiveData<Integer> getQuantity() {
        return quantity;
    }
    
    // ==================== MESSAGES ====================
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public void clearMessages() {
        errorMessage.setValue(null);
        successMessage.setValue(null);
    }
}
