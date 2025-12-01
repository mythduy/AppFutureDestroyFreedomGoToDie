package com.example.ecommerce_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce_app.data.entities.User;
import com.example.ecommerce_app.data.repository.UserRepository;

import java.util.concurrent.ExecutionException;

/**
 * AuthViewModel - ViewModel cho Authentication (Login/Register)
 * 
 * Xử lý logic đăng nhập, đăng ký, quản lý session
 */
public class AuthViewModel extends AndroidViewModel {
    
    private UserRepository userRepository;
    
    // LiveData cho UI observe
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    public AuthViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }
    
    // ==================== GETTERS ====================
    
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    // ==================== AUTHENTICATION ====================
    
    /**
     * Đăng nhập
     */
    public void login(String username, String password) {
        isLoading.setValue(true);
        
        try {
            User user = userRepository.login(username, password).get();
            
            if (user != null) {
                currentUser.setValue(user);
                errorMessage.setValue(null);
            } else {
                errorMessage.setValue("Sai tên đăng nhập hoặc mật khẩu");
            }
        } catch (ExecutionException | InterruptedException e) {
            errorMessage.setValue("Lỗi đăng nhập: " + e.getMessage());
        } finally {
            isLoading.setValue(false);
        }
    }
    
    /**
     * Đăng ký
     */
    public void register(String username, String email, String password, String fullName, String phone) {
        isLoading.setValue(true);
        
        try {
            long userId = userRepository.register(username, email, password, fullName, phone).get();
            
            if (userId > 0) {
                // Đăng ký thành công, tự động đăng nhập
                login(username, password);
            } else if (userId == -1) {
                errorMessage.setValue("Tên đăng nhập đã tồn tại");
            } else if (userId == -2) {
                errorMessage.setValue("Email đã được sử dụng");
            } else {
                errorMessage.setValue("Đăng ký thất bại");
            }
        } catch (ExecutionException | InterruptedException e) {
            errorMessage.setValue("Lỗi đăng ký: " + e.getMessage());
        } finally {
            isLoading.setValue(false);
        }
    }
    
    /**
     * Đăng xuất
     */
    public void logout() {
        currentUser.setValue(null);
        errorMessage.setValue(null);
    }
    
    /**
     * Kiểm tra đã đăng nhập chưa
     */
    public boolean isLoggedIn() {
        return currentUser.getValue() != null;
    }
    
    /**
     * Lấy user ID hiện tại
     */
    public long getCurrentUserId() {
        User user = currentUser.getValue();
        return user != null ? user.getId() : -1;
    }
    
    /**
     * Đổi mật khẩu
     */
    public void changePassword(String oldPassword, String newPassword) {
        User user = currentUser.getValue();
        if (user == null) {
            errorMessage.setValue("Chưa đăng nhập");
            return;
        }
        
        isLoading.setValue(true);
        
        try {
            boolean success = userRepository.changePassword(user.getId(), oldPassword, newPassword).get();
            
            if (success) {
                errorMessage.setValue("Đổi mật khẩu thành công");
            } else {
                errorMessage.setValue("Mật khẩu cũ không đúng");
            }
        } catch (ExecutionException | InterruptedException e) {
            errorMessage.setValue("Lỗi đổi mật khẩu: " + e.getMessage());
        } finally {
            isLoading.setValue(false);
        }
    }
    
    /**
     * Cập nhật thông tin user
     */
    public void updateUserInfo(User user) {
        userRepository.update(user);
        currentUser.setValue(user);
    }
}
