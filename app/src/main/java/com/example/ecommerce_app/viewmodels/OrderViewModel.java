package com.example.ecommerce_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce_app.data.entities.Order;
import com.example.ecommerce_app.data.entities.OrderItem;
import com.example.ecommerce_app.data.repository.OrderRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * OrderViewModel - ViewModel cho quản lý đơn hàng
 */
public class OrderViewModel extends AndroidViewModel {
    
    private OrderRepository orderRepository;
    
    private MutableLiveData<Long> currentUserId = new MutableLiveData<>();
    private LiveData<List<Order>> orders;
    private MutableLiveData<String> selectedStatus = new MutableLiveData<>("ALL");
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    public OrderViewModel(@NonNull Application application) {
        super(application);
        orderRepository = new OrderRepository(application);
    }
    
    // ==================== GETTERS ====================
    
    public LiveData<List<Order>> getOrders() {
        return orders;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<String> getSelectedStatus() {
        return selectedStatus;
    }
    
    // ==================== SETUP ====================
    
    /**
     * Set user ID và load orders
     */
    public void setUserId(long userId) {
        currentUserId.setValue(userId);
        loadOrders();
    }
    
    /**
     * Load tất cả orders của user
     */
    public void loadOrders() {
        Long userId = currentUserId.getValue();
        if (userId == null || userId <= 0) {
            return;
        }
        
        orders = orderRepository.getOrdersByUser(userId);
    }
    
    /**
     * Filter orders theo status
     */
    public void filterByStatus(String status) {
        Long userId = currentUserId.getValue();
        if (userId == null || userId <= 0) {
            return;
        }
        
        selectedStatus.setValue(status);
        
        if (status.equals("ALL")) {
            orders = orderRepository.getOrdersByUser(userId);
        } else {
            orders = orderRepository.getOrdersByStatus(userId, status);
        }
    }
    
    // ==================== ORDER DETAIL ====================
    
    /**
     * Lấy order theo ID
     */
    public LiveData<Order> getOrderById(long orderId) {
        return orderRepository.getOrderById(orderId);
    }
    
    /**
     * Lấy order items
     */
    public LiveData<List<OrderItem>> getOrderItems(long orderId) {
        return orderRepository.getOrderItems(orderId);
    }
    
    // ==================== ORDER ACTIONS ====================
    
    /**
     * Hủy đơn hàng
     */
    public void cancelOrder(long orderId) {
        orderRepository.updateOrderStatus(orderId, "CANCELLED");
        errorMessage.setValue("Đã hủy đơn hàng");
    }
    
    /**
     * Xác nhận đã nhận hàng
     */
    public void confirmReceived(long orderId) {
        orderRepository.updateOrderStatus(orderId, "DELIVERED");
        errorMessage.setValue("Xác nhận đã nhận hàng");
    }
    
    // ==================== STATISTICS ====================
    
    /**
     * Đếm số đơn hàng
     */
    public void getOrderCount(OnCountCallback callback) {
        Long userId = currentUserId.getValue();
        if (userId == null || userId <= 0) {
            callback.onResult(0);
            return;
        }
        
        try {
            int count = orderRepository.getOrderCount(userId).get();
            callback.onResult(count);
        } catch (ExecutionException | InterruptedException e) {
            callback.onResult(0);
        }
    }
    
    /**
     * Tính tổng tiền đã chi tiêu
     */
    public void getTotalSpent(OnTotalCallback callback) {
        Long userId = currentUserId.getValue();
        if (userId == null || userId <= 0) {
            callback.onResult(0.0);
            return;
        }
        
        try {
            double total = orderRepository.getTotalSpent(userId).get();
            callback.onResult(total);
        } catch (ExecutionException | InterruptedException e) {
            callback.onResult(0.0);
        }
    }
    
    // Callback interfaces
    public interface OnCountCallback {
        void onResult(int count);
    }
    
    public interface OnTotalCallback {
        void onResult(double total);
    }
}
