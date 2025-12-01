package com.example.ecommerce_app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.ecommerce_app.data.dao.FavoriteDao;
import com.example.ecommerce_app.data.database.AppDatabase;
import com.example.ecommerce_app.data.entities.Favorite;

import java.util.List;
import java.util.concurrent.Future;

/**
 * FavoriteRepository - Repository cho Favorite entity
 */
public class FavoriteRepository {
    
    private FavoriteDao favoriteDao;
    
    public FavoriteRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        favoriteDao = database.favoriteDao();
    }
    
    // ==================== OPERATIONS ====================
    
    /**
     * Lấy danh sách yêu thích của user
     */
    public LiveData<List<Favorite>> getFavoritesByUser(long userId) {
        return favoriteDao.getFavoritesByUser(userId);
    }
    
    public Future<List<Favorite>> getFavoritesByUserSync(long userId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return favoriteDao.getFavoritesByUserSync(userId);
        });
    }
    
    /**
     * Thêm vào danh sách yêu thích
     */
    public Future<Boolean> addToFavorites(long userId, long productId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            // Kiểm tra đã tồn tại chưa
            if (favoriteDao.isFavorite(userId, productId) > 0) {
                return false; // Đã có trong favorites
            }
            
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setProductId(productId);
            favoriteDao.insert(favorite);
            return true;
        });
    }
    
    /**
     * Xóa khỏi danh sách yêu thích
     */
    public void removeFromFavorites(long userId, long productId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            favoriteDao.removeFromFavorites(userId, productId);
        });
    }
    
    /**
     * Kiểm tra sản phẩm đã được yêu thích chưa
     */
    public Future<Boolean> isFavorite(long userId, long productId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return favoriteDao.isFavorite(userId, productId) > 0;
        });
    }
    
    /**
     * Đếm số lượng favorites
     */
    public Future<Integer> getFavoriteCount(long userId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return favoriteDao.getFavoriteCount(userId);
        });
    }
    
    /**
     * Xóa tất cả favorites của user
     */
    public void clearFavorites(long userId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            favoriteDao.clearFavorites(userId);
        });
    }
}
