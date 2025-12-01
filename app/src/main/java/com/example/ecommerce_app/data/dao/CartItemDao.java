package com.example.ecommerce_app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecommerce_app.data.entities.CartItem;

import java.util.List;

/**
 * CartItemDao - Data Access Object cho CartItem entity
 */
@Dao
public interface CartItemDao {
    
    // ==================== CREATE ====================
    
    @Insert
    long insert(CartItem cartItem);
    
    @Insert
    void insertAll(List<CartItem> cartItems);
    
    // ==================== READ ====================
    
    /**
     * Lấy tất cả items trong giỏ hàng của user
     */
    @Query("SELECT * FROM cart_items WHERE userId = :userId ORDER BY addedAt DESC")
    LiveData<List<CartItem>> getCartItemsByUser(long userId);
    
    /**
     * Lấy tất cả items trong giỏ hàng của user (sync)
     */
    @Query("SELECT * FROM cart_items WHERE userId = :userId ORDER BY addedAt DESC")
    List<CartItem> getCartItemsByUserSync(long userId);
    
    /**
     * Lấy cart item cụ thể (user + product)
     */
    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId LIMIT 1")
    CartItem getCartItem(long userId, long productId);
    
    /**
     * Lấy cart item theo ID
     */
    @Query("SELECT * FROM cart_items WHERE id = :cartItemId")
    LiveData<CartItem> getCartItemById(long cartItemId);
    
    /**
     * Đếm số items trong giỏ hàng
     */
    @Query("SELECT COUNT(*) FROM cart_items WHERE userId = :userId")
    LiveData<Integer> getCartItemCount(long userId);
    
    /**
     * Tính tổng số lượng sản phẩm trong giỏ (sum of quantities)
     */
    @Query("SELECT SUM(quantity) FROM cart_items WHERE userId = :userId")
    LiveData<Integer> getTotalQuantity(long userId);
    
    /**
     * Kiểm tra sản phẩm đã có trong giỏ chưa
     */
    @Query("SELECT COUNT(*) FROM cart_items WHERE userId = :userId AND productId = :productId")
    int checkProductInCart(long userId, long productId);
    
    // ==================== UPDATE ====================
    
    @Update
    void update(CartItem cartItem);
    
    /**
     * Cập nhật số lượng
     */
    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :cartItemId")
    void updateQuantity(long cartItemId, int quantity);
    
    /**
     * Tăng số lượng
     */
    @Query("UPDATE cart_items SET quantity = quantity + :amount WHERE userId = :userId AND productId = :productId")
    void increaseQuantity(long userId, long productId, int amount);
    
    // ==================== DELETE ====================
    
    @Delete
    void delete(CartItem cartItem);
    
    @Query("DELETE FROM cart_items WHERE id = :cartItemId")
    void deleteById(long cartItemId);
    
    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    @Query("DELETE FROM cart_items WHERE userId = :userId AND productId = :productId")
    void removeFromCart(long userId, long productId);
    
    /**
     * Xóa toàn bộ giỏ hàng của user
     */
    @Query("DELETE FROM cart_items WHERE userId = :userId")
    void clearCart(long userId);
    
    @Query("DELETE FROM cart_items")
    void deleteAll();
}
