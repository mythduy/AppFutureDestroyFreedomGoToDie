package com.example.ecommerce_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce_app.data.entities.CartItem;
import com.example.ecommerce_app.data.entities.Order;
import com.example.ecommerce_app.data.entities.OrderItem;
import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.data.repository.CartRepository;
import com.example.ecommerce_app.data.repository.OrderRepository;
import com.example.ecommerce_app.data.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * CheckoutViewModel - ViewModel cho checkout process
 * 
 * Xử lý tạo order từ cart, cập nhật stock
 */
public class CheckoutViewModel extends AndroidViewModel {
    
    private OrderRepository orderRepository;
    private CartRepository cartRepository;
    private ProductRepository productRepository;
    
    private MutableLiveData<Long> currentUserId = new MutableLiveData<>();
    private MutableLiveData<Double> totalAmount = new MutableLiveData<>(0.0);
    private MutableLiveData<String> shippingAddress = new MutableLiveData<>();
    private MutableLiveData<String> shippingPhone = new MutableLiveData<>();
    private MutableLiveData<String> note = new MutableLiveData<>();
    
    private MutableLiveData<Boolean> isProcessing = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> checkoutSuccess = new MutableLiveData<>(false);
    private MutableLiveData<Long> createdOrderId = new MutableLiveData<>();
    
    public CheckoutViewModel(@NonNull Application application) {
        super(application);
        orderRepository = new OrderRepository(application);
        cartRepository = new CartRepository(application);
        productRepository = new ProductRepository(application);
    }
    
    // ==================== GETTERS ====================
    
    public LiveData<Double> getTotalAmount() {
        return totalAmount;
    }
    
    public LiveData<String> getShippingAddress() {
        return shippingAddress;
    }
    
    public LiveData<String> getShippingPhone() {
        return shippingPhone;
    }
    
    public LiveData<Boolean> getIsProcessing() {
        return isProcessing;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<Boolean> getCheckoutSuccess() {
        return checkoutSuccess;
    }
    
    public LiveData<Long> getCreatedOrderId() {
        return createdOrderId;
    }
    
    // ==================== SETTERS ====================
    
    public void setUserId(long userId) {
        currentUserId.setValue(userId);
    }
    
    public void setTotalAmount(double amount) {
        totalAmount.setValue(amount);
    }
    
    public void setShippingAddress(String address) {
        shippingAddress.setValue(address);
    }
    
    public void setShippingPhone(String phone) {
        shippingPhone.setValue(phone);
    }
    
    public void setNote(String noteText) {
        note.setValue(noteText);
    }
    
    // ==================== CHECKOUT PROCESS ====================
    
    /**
     * Thực hiện checkout
     * 1. Tạo order
     * 2. Tạo order items từ cart
     * 3. Giảm stock của products
     * 4. Xóa cart
     */
    public void processCheckout() {
        Long userId = currentUserId.getValue();
        Double amount = totalAmount.getValue();
        String address = shippingAddress.getValue();
        String phone = shippingPhone.getValue();
        
        // Validate
        if (userId == null || userId <= 0) {
            errorMessage.setValue("Vui lòng đăng nhập");
            return;
        }
        
        if (address == null || address.trim().isEmpty()) {
            errorMessage.setValue("Vui lòng nhập địa chỉ giao hàng");
            return;
        }
        
        if (phone == null || phone.trim().isEmpty()) {
            errorMessage.setValue("Vui lòng nhập số điện thoại");
            return;
        }
        
        if (amount == null || amount <= 0) {
            errorMessage.setValue("Giỏ hàng trống");
            return;
        }
        
        isProcessing.setValue(true);
        
        try {
            // 1. Lấy cart items
            List<CartItem> cartItems = cartRepository.getCartItemsSync(userId).get();
            
            if (cartItems == null || cartItems.isEmpty()) {
                errorMessage.setValue("Giỏ hàng trống");
                isProcessing.setValue(false);
                return;
            }
            
            // 2. Kiểm tra stock
            for (CartItem item : cartItems) {
                Product product = productRepository.getProductByIdSync(item.getProductId()).get();
                if (product == null || product.getStock() < item.getQuantity()) {
                    errorMessage.setValue("Sản phẩm " + (product != null ? product.getName() : "") + " không đủ hàng");
                    isProcessing.setValue(false);
                    return;
                }
            }
            
            // 3. Tạo order
            long orderId = orderRepository.createOrder(
                userId,
                amount,
                address,
                phone,
                note.getValue()
            ).get();
            
            if (orderId <= 0) {
                errorMessage.setValue("Không thể tạo đơn hàng");
                isProcessing.setValue(false);
                return;
            }
            
            // 4. Tạo order items
            List<OrderItem> orderItems = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                Product product = productRepository.getProductByIdSync(cartItem.getProductId()).get();
                
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(orderId);
                orderItem.setProductId(cartItem.getProductId());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(product.getPrice());
                orderItem.setSubtotal(product.getPrice() * cartItem.getQuantity());
                
                orderItems.add(orderItem);
            }
            
            orderRepository.addOrderItems(orderItems);
            
            // 5. Giảm stock
            for (CartItem cartItem : cartItems) {
                productRepository.decreaseStock(cartItem.getProductId(), cartItem.getQuantity());
            }
            
            // 6. Xóa cart
            cartRepository.clearCart(userId);
            
            // Success
            createdOrderId.setValue(orderId);
            checkoutSuccess.setValue(true);
            errorMessage.setValue("Đặt hàng thành công!");
            
        } catch (ExecutionException | InterruptedException e) {
            errorMessage.setValue("Lỗi: " + e.getMessage());
            checkoutSuccess.setValue(false);
        } finally {
            isProcessing.setValue(false);
        }
    }
    
    /**
     * Reset checkout state
     */
    public void reset() {
        checkoutSuccess.setValue(false);
        errorMessage.setValue(null);
        createdOrderId.setValue(null);
    }
}
