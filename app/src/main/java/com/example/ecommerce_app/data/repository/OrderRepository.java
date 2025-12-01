package com.example.ecommerce_app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.ecommerce_app.data.dao.OrderDao;
import com.example.ecommerce_app.data.dao.OrderItemDao;
import com.example.ecommerce_app.data.database.AppDatabase;
import com.example.ecommerce_app.data.entities.Order;
import com.example.ecommerce_app.data.entities.OrderItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;

/**
 * OrderRepository - Repository cho Order và OrderItem
 */
public class OrderRepository {
    
    private OrderDao orderDao;
    private OrderItemDao orderItemDao;
    
    public OrderRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        orderDao = database.orderDao();
        orderItemDao = database.orderItemDao();
    }
    
    // ==================== ORDER OPERATIONS ====================
    
    public LiveData<List<Order>> getOrdersByUser(long userId) {
        return orderDao.getOrdersByUser(userId);
    }
    
    public LiveData<Order> getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }
    
    public Future<Order> getOrderByIdSync(long orderId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return orderDao.getOrderByIdSync(orderId);
        });
    }
    
    public LiveData<List<Order>> getOrdersByStatus(long userId, String status) {
        return orderDao.getOrdersByStatus(userId, status);
    }
    
    public LiveData<List<Order>> getAllOrders() {
        return orderDao.getAllOrders();
    }
    
    /**
     * Tạo order mới
     * @return Order ID
     */
    public Future<Long> createOrder(long userId, double totalAmount, String shippingAddress, 
                                    String shippingPhone, String note) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            Order order = new Order();
            order.setUserId(userId);
            order.setOrderNumber(generateOrderNumber());
            order.setTotalAmount(totalAmount);
            order.setShippingAddress(shippingAddress);
            order.setShippingPhone(shippingPhone);
            order.setNote(note);
            
            return orderDao.insert(order);
        });
    }
    
    /**
     * Cập nhật trạng thái order
     */
    public void updateOrderStatus(long orderId, String status) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            orderDao.updateStatus(orderId, status, new Date().getTime());
        });
    }
    
    /**
     * Cập nhật thông tin giao hàng
     */
    public void updateShippingInfo(long orderId, String address, String phone) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            orderDao.updateShippingInfo(orderId, address, phone, new Date().getTime());
        });
    }
    
    public void deleteOrder(long orderId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            orderDao.deleteById(orderId);
        });
    }
    
    // ==================== ORDER ITEM OPERATIONS ====================
    
    public LiveData<List<OrderItem>> getOrderItems(long orderId) {
        return orderItemDao.getOrderItemsByOrder(orderId);
    }
    
    public Future<List<OrderItem>> getOrderItemsSync(long orderId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return orderItemDao.getOrderItemsByOrderSync(orderId);
        });
    }
    
    /**
     * Thêm item vào order
     */
    public Future<Long> addOrderItem(long orderId, long productId, int quantity, double price) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            OrderItem item = new OrderItem();
            item.setOrderId(orderId);
            item.setProductId(productId);
            item.setQuantity(quantity);
            item.setPrice(price);
            item.setSubtotal(quantity * price);
            
            return orderItemDao.insert(item);
        });
    }
    
    /**
     * Thêm nhiều items vào order
     */
    public void addOrderItems(List<OrderItem> orderItems) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            orderItemDao.insertAll(orderItems);
        });
    }
    
    // ==================== STATISTICS ====================
    
    public Future<Integer> getOrderCount(long userId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return orderDao.getOrderCount(userId);
        });
    }
    
    public Future<Double> getTotalSpent(long userId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return orderDao.getTotalSpent(userId);
        });
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Tạo mã order number unique
     * Format: ORD-YYYYMMDD-HHMMSS
     */
    private String generateOrderNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault());
        return "ORD-" + sdf.format(new Date());
    }
}
