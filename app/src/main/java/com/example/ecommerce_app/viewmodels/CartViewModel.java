package com.example.ecommerce_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce_app.data.entities.CartItem;
import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.data.repository.CartRepository;
import com.example.ecommerce_app.data.repository.ProductRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * CartViewModel - ViewModel cho giỏ hàng
 * 
 * Quản lý cart items, tính tổng tiền, update quantity
 */
public class CartViewModel extends AndroidViewModel {
    
    private CartRepository cartRepository;
    private ProductRepository productRepository;
    
    private MutableLiveData<Long> currentUserId = new MutableLiveData<>();
    private LiveData<List<CartItem>> cartItems;
    private LiveData<Integer> cartItemCount;
    private MutableLiveData<Double> totalPrice = new MutableLiveData<>(0.0);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    public CartViewModel(@NonNull Application application) {
        super(application);
        cartRepository = new CartRepository(application);
        productRepository = new ProductRepository(application);
    }
    
    // ==================== GETTERS ====================
    
    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }
    
    public LiveData<Integer> getCartItemCount() {
        return cartItemCount;
    }
    
    public LiveData<Double> getTotalPrice() {
        return totalPrice;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    // ==================== SETUP ====================
    
    /**
     * Set user ID và load giỏ hàng
     */
    public void setUserId(long userId) {
        currentUserId.setValue(userId);
        cartItems = cartRepository.getCartItems(userId);
        cartItemCount = cartRepository.getCartItemCount(userId);
    }
    
    // ==================== CART OPERATIONS ====================
    
    /**
     * Thêm sản phẩm vào giỏ
     */
    public void addToCart(long productId, int quantity) {
        Long userId = currentUserId.getValue();
        if (userId == null || userId <= 0) {
            errorMessage.setValue("Vui lòng đăng nhập");
            return;
        }
        
        try {
            boolean success = cartRepository.addToCart(userId, productId, quantity).get();
            if (success) {
                errorMessage.setValue("Đã thêm vào giỏ hàng");
                calculateTotalPrice();
            } else {
                errorMessage.setValue("Không thể thêm vào giỏ hàng");
            }
        } catch (ExecutionException | InterruptedException e) {
            errorMessage.setValue("Lỗi: " + e.getMessage());
        }
    }
    
    /**
     * Cập nhật số lượng
     */
    public void updateQuantity(long cartItemId, int quantity) {
        if (quantity <= 0) {
            errorMessage.setValue("Số lượng phải lớn hơn 0");
            return;
        }
        
        cartRepository.updateQuantity(cartItemId, quantity);
        calculateTotalPrice();
    }
    
    /**
     * Xóa sản phẩm khỏi giỏ
     */
    public void removeFromCart(long productId) {
        Long userId = currentUserId.getValue();
        if (userId == null || userId <= 0) {
            return;
        }
        
        cartRepository.removeFromCart(userId, productId);
        calculateTotalPrice();
    }
    
    /**
     * Xóa toàn bộ giỏ hàng
     */
    public void clearCart() {
        Long userId = currentUserId.getValue();
        if (userId == null || userId <= 0) {
            return;
        }
        
        cartRepository.clearCart(userId);
        totalPrice.setValue(0.0);
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Tính tổng giá trị giỏ hàng
     */
    public void calculateTotalPrice() {
        Long userId = currentUserId.getValue();
        if (userId == null || userId <= 0) {
            totalPrice.setValue(0.0);
            return;
        }
        
        try {
            List<CartItem> items = cartRepository.getCartItemsSync(userId).get();
            double total = 0.0;
            
            for (CartItem item : items) {
                Product product = productRepository.getProductByIdSync(item.getProductId()).get();
                if (product != null) {
                    total += product.getPrice() * item.getQuantity();
                }
            }
            
            totalPrice.setValue(total);
        } catch (ExecutionException | InterruptedException e) {
            errorMessage.setValue("Lỗi tính tổng tiền: " + e.getMessage());
        }
    }
    
    /**
     * Kiểm tra sản phẩm có trong giỏ không
     */
    public void checkProductInCart(long productId, OnCheckCallback callback) {
        Long userId = currentUserId.getValue();
        if (userId == null || userId <= 0) {
            callback.onResult(false);
            return;
        }
        
        try {
            boolean inCart = cartRepository.isInCart(userId, productId).get();
            callback.onResult(inCart);
        } catch (ExecutionException | InterruptedException e) {
            callback.onResult(false);
        }
    }
    
    // Callback interface
    public interface OnCheckCallback {
        void onResult(boolean isInCart);
    }
}
