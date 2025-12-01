package com.example.ecommerce_app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.ecommerce_app.data.dao.CartItemDao;
import com.example.ecommerce_app.data.database.AppDatabase;
import com.example.ecommerce_app.data.entities.CartItem;

import java.util.List;
import java.util.concurrent.Future;

/**
 * CartRepository - Repository cho Cart operations
 */
public class CartRepository {
    
    private CartItemDao cartItemDao;
    
    public CartRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        cartItemDao = database.cartItemDao();
    }
    
    // ==================== CRUD OPERATIONS ====================
    
    public LiveData<List<CartItem>> getCartItems(long userId) {
        return cartItemDao.getCartItemsByUser(userId);
    }
    
    public Future<List<CartItem>> getCartItemsSync(long userId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return cartItemDao.getCartItemsByUserSync(userId);
        });
    }
    
    public LiveData<Integer> getCartItemCount(long userId) {
        return cartItemDao.getCartItemCount(userId);
    }
    
    public LiveData<Integer> getTotalQuantity(long userId) {
        return cartItemDao.getTotalQuantity(userId);
    }
    
    /**
     * Thêm sản phẩm vào giỏ hàng
     * Nếu sản phẩm đã có, tăng số lượng
     */
    public Future<Boolean> addToCart(long userId, long productId, int quantity) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            CartItem existingItem = cartItemDao.getCartItem(userId, productId);
            
            if (existingItem != null) {
                // Sản phẩm đã có trong giỏ, tăng số lượng
                cartItemDao.increaseQuantity(userId, productId, quantity);
            } else {
                // Sản phẩm chưa có, thêm mới
                CartItem newItem = new CartItem();
                newItem.setUserId(userId);
                newItem.setProductId(productId);
                newItem.setQuantity(quantity);
                cartItemDao.insert(newItem);
            }
            
            return true;
        });
    }
    
    /**
     * Cập nhật số lượng
     */
    public void updateQuantity(long cartItemId, int quantity) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            cartItemDao.updateQuantity(cartItemId, quantity);
        });
    }
    
    /**
     * Xóa sản phẩm khỏi giỏ
     */
    public void removeFromCart(long userId, long productId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            cartItemDao.removeFromCart(userId, productId);
        });
    }
    
    /**
     * Xóa toàn bộ giỏ hàng
     */
    public void clearCart(long userId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            cartItemDao.clearCart(userId);
        });
    }
    
    /**
     * Kiểm tra sản phẩm có trong giỏ không
     */
    public Future<Boolean> isInCart(long userId, long productId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return cartItemDao.checkProductInCart(userId, productId) > 0;
        });
    }
}
