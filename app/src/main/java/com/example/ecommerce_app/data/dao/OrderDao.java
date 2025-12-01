package com.example.ecommerce_app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecommerce_app.data.entities.Order;

import java.util.List;

/**
 * OrderDao - Data Access Object cho Order entity
 */
@Dao
public interface OrderDao {
    
    // ==================== CREATE ====================
    
    @Insert
    long insert(Order order);
    
    @Insert
    void insertAll(List<Order> orders);
    
    // ==================== READ ====================
    
    /**
     * Lấy tất cả orders của user
     */
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<Order>> getOrdersByUser(long userId);
    
    /**
     * Lấy tất cả orders của user (sync)
     */
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    List<Order> getOrdersByUserSync(long userId);
    
    /**
     * Lấy order theo ID
     */
    @Query("SELECT * FROM orders WHERE id = :orderId")
    LiveData<Order> getOrderById(long orderId);
    
    /**
     * Lấy order theo ID (sync)
     */
    @Query("SELECT * FROM orders WHERE id = :orderId")
    Order getOrderByIdSync(long orderId);
    
    /**
     * Tìm order theo orderNumber
     */
    @Query("SELECT * FROM orders WHERE orderNumber = :orderNumber LIMIT 1")
    Order findByOrderNumber(String orderNumber);
    
    /**
     * Lấy orders theo trạng thái
     */
    @Query("SELECT * FROM orders WHERE userId = :userId AND status = :status ORDER BY createdAt DESC")
    LiveData<List<Order>> getOrdersByStatus(long userId, String status);
    
    /**
     * Lấy tất cả orders (admin view)
     */
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    LiveData<List<Order>> getAllOrders();
    
    /**
     * Lấy orders theo trạng thái (admin view)
     */
    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAt DESC")
    LiveData<List<Order>> getAllOrdersByStatus(String status);
    
    /**
     * Đếm số orders của user
     */
    @Query("SELECT COUNT(*) FROM orders WHERE userId = :userId")
    int getOrderCount(long userId);
    
    /**
     * Đếm orders theo trạng thái
     */
    @Query("SELECT COUNT(*) FROM orders WHERE userId = :userId AND status = :status")
    int getOrderCountByStatus(long userId, String status);
    
    /**
     * Tính tổng tiền đã mua của user
     */
    @Query("SELECT SUM(totalAmount) FROM orders WHERE userId = :userId AND status IN ('DELIVERED', 'SHIPPED')")
    double getTotalSpent(long userId);
    
    // ==================== UPDATE ====================
    
    @Update
    void update(Order order);
    
    /**
     * Cập nhật trạng thái order
     */
    @Query("UPDATE orders SET status = :status, updatedAt = :updatedAt WHERE id = :orderId")
    void updateStatus(long orderId, String status, long updatedAt);
    
    /**
     * Cập nhật địa chỉ giao hàng
     */
    @Query("UPDATE orders SET shippingAddress = :address, shippingPhone = :phone, updatedAt = :updatedAt WHERE id = :orderId")
    void updateShippingInfo(long orderId, String address, String phone, long updatedAt);
    
    // ==================== DELETE ====================
    
    @Delete
    void delete(Order order);
    
    @Query("DELETE FROM orders WHERE id = :orderId")
    void deleteById(long orderId);
    
    @Query("DELETE FROM orders")
    void deleteAll();
}
