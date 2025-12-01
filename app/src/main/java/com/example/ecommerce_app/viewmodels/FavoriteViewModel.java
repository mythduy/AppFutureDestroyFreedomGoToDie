package com.example.ecommerce_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce_app.data.entities.Favorite;
import com.example.ecommerce_app.data.repository.FavoriteRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * FavoriteViewModel - ViewModel cho quản lý danh sách yêu thích
 */
public class FavoriteViewModel extends AndroidViewModel {
    
    private FavoriteRepository favoriteRepository;
    
    private MutableLiveData<Long> currentUserId = new MutableLiveData<>();
    private LiveData<List<Favorite>> favorites;
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> actionSuccess = new MutableLiveData<>();
    
    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        favoriteRepository = new FavoriteRepository(application);
    }
    
    // ==================== GETTERS ====================
    
    public LiveData<List<Favorite>> getFavorites() {
        return favorites;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<Boolean> getActionSuccess() {
        return actionSuccess;
    }
    
    // ==================== SETUP ====================
    
    /**
     * Set user ID và load favorites
     */
    public void setUserId(long userId) {
        currentUserId.setValue(userId);
        loadFavorites();
    }
    
    /**
     * Load tất cả favorites của user
     */
    public void loadFavorites() {
        Long userId = currentUserId.getValue();
        if (userId == null || userId <= 0) {
            return;
        }
        
        favorites = favoriteRepository.getFavoritesByUser(userId);
    }
    
    // ==================== FAVORITE ACTIONS ====================
    
    /**
     * Thêm sản phẩm vào favorites
     */
    public void addToFavorites(long productId) {
        Long userId = currentUserId.getValue();
        if (userId == null || userId <= 0) {
            errorMessage.setValue("Vui lòng đăng nhập");
            return;
        }
        
        try {
            boolean success = favoriteRepository.addToFavorites(userId, productId).get();
            if (success) {
                errorMessage.setValue("Đã thêm vào yêu thích");
                actionSuccess.setValue(true);
            } else {
                errorMessage.setValue("Sản phẩm đã có trong danh sách yêu thích");
                actionSuccess.setValue(false);
            }
        } catch (ExecutionException | InterruptedException e) {
            errorMessage.setValue("Lỗi khi thêm vào yêu thích");
            actionSuccess.setValue(false);
        }
    }
    
    /**
     * Xóa sản phẩm khỏi favorites
     */
    public void removeFromFavorites(long productId) {
        Long userId = currentUserId.getValue();
        if (userId == null || userId <= 0) {
            return;
        }
        
        favoriteRepository.removeFromFavorites(userId, productId);
        errorMessage.setValue("Đã xóa khỏi yêu thích");
        actionSuccess.setValue(true);
    }
    
    /**
     * Xóa tất cả favorites
     */
    public void clearFavorites() {
        Long userId = currentUserId.getValue();
        if (userId == null || userId <= 0) {
            return;
        }
        
        favoriteRepository.clearFavorites(userId);
        errorMessage.setValue("Đã xóa tất cả yêu thích");
    }
    
    // ==================== CHECK STATUS ====================
    
    /**
     * Kiểm tra sản phẩm có trong favorites không
     */
    public void isFavorite(long productId, OnCheckCallback callback) {
        Long userId = currentUserId.getValue();
        if (userId == null || userId <= 0) {
            callback.onResult(false);
            return;
        }
        
        try {
            boolean isFav = favoriteRepository.isFavorite(userId, productId).get();
            callback.onResult(isFav);
        } catch (ExecutionException | InterruptedException e) {
            callback.onResult(false);
        }
    }
    
    /**
     * Đếm số lượng favorites
     */
    public void getFavoriteCount(OnCountCallback callback) {
        Long userId = currentUserId.getValue();
        if (userId == null || userId <= 0) {
            callback.onResult(0);
            return;
        }
        
        try {
            int count = favoriteRepository.getFavoriteCount(userId).get();
            callback.onResult(count);
        } catch (ExecutionException | InterruptedException e) {
            callback.onResult(0);
        }
    }
    
    // ==================== TOGGLE FAVORITE ====================
    
    /**
     * Toggle favorite status (thêm nếu chưa có, xóa nếu đã có)
     */
    public void toggleFavorite(long productId) {
        isFavorite(productId, isFav -> {
            if (isFav) {
                removeFromFavorites(productId);
            } else {
                addToFavorites(productId);
            }
        });
    }
    
    // Callback interfaces
    public interface OnCheckCallback {
        void onResult(boolean isFavorite);
    }
    
    public interface OnCountCallback {
        void onResult(int count);
    }
}
