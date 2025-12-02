package com.example.ecommerce_app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecommerce_app.data.entities.OrderItem;

import java.util.List;

/**
 * OrderItemDao - Data Access Object cho OrderItem entity
 */
@Dao
public interface OrderItemDao {
    
    // ==================== CREATE ====================
    
    @Insert
    long insert(OrderItem orderItem);
    
    @Insert
    void insertAll(List<OrderItem> orderItems);
    
    // ==================== READ ====================
    
    /**
     * Lấy tất cả items của một order
     */
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    LiveData<List<OrderItem>> getOrderItemsByOrder(long orderId);
    
    /**
     * Lấy tất cả items của một order (sync)
     */
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    List<OrderItem> getOrderItemsByOrderSync(long orderId);
    
    /**
     * Lấy tất cả items của một order (sync) - Alternative method name
     */
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    List<OrderItem> getOrderItemsSync(long orderId);
    
    /**
     * Lấy order item theo ID
     */
    @Query("SELECT * FROM order_items WHERE id = :orderItemId")
    LiveData<OrderItem> getOrderItemById(long orderItemId);
    
    /**
     * Lấy order item cụ thể (order + product)
     */
    @Query("SELECT * FROM order_items WHERE orderId = :orderId AND productId = :productId LIMIT 1")
    OrderItem getOrderItem(long orderId, long productId);
    
    /**
     * Đếm số items trong order
     */
    @Query("SELECT COUNT(*) FROM order_items WHERE orderId = :orderId")
    int getItemCount(long orderId);
    
    /**
     * Tính tổng số lượng sản phẩm trong order
     */
    @Query("SELECT SUM(quantity) FROM order_items WHERE orderId = :orderId")
    int getTotalQuantity(long orderId);
    
    /**
     * Lấy tất cả orders có chứa một sản phẩm cụ thể
     */
    @Query("SELECT * FROM order_items WHERE productId = :productId")
    LiveData<List<OrderItem>> getOrderItemsByProduct(long productId);
    
    // ==================== UPDATE ====================
    
    @Update
    void update(OrderItem orderItem);
    
    // ==================== DELETE ====================
    
    @Delete
    void delete(OrderItem orderItem);
    
    @Query("DELETE FROM order_items WHERE id = :orderItemId")
    void deleteById(long orderItemId);
    
    /**
     * Xóa tất cả items của một order
     */
    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    void deleteByOrder(long orderId);
    
    @Query("DELETE FROM order_items")
    void deleteAll();
}
