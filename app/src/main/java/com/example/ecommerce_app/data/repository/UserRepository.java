package com.example.ecommerce_app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.ecommerce_app.data.dao.UserDao;
import com.example.ecommerce_app.data.database.AppDatabase;
import com.example.ecommerce_app.data.entities.User;
import com.example.ecommerce_app.utils.PasswordHasher;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * UserRepository - Repository cho User entity
 * 
 * Tách logic truy cập database ra khỏi ViewModel
 * Xử lý tất cả operations liên quan đến User
 */
public class UserRepository {
    
    private UserDao userDao;
    
    public UserRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        userDao = database.userDao();
    }
    
    // ==================== AUTHENTICATION ====================
    
    /**
     * Đăng ký user mới
     * @return User ID nếu thành công, -1 nếu username đã tồn tại, -2 nếu email đã tồn tại
     */
    public Future<Long> register(String username, String email, String password, String fullName, String phone) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            // Kiểm tra username đã tồn tại
            if (userDao.checkUsernameExists(username) > 0) {
                return -1L; // Username đã tồn tại
            }
            
            // Kiểm tra email đã tồn tại
            if (userDao.checkEmailExists(email) > 0) {
                return -2L; // Email đã tồn tại
            }
            
            // Tạo user mới
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPasswordHash(PasswordHasher.hashPassword(password));
            user.setFullName(fullName);
            user.setPhone(phone);
            
            return userDao.insert(user);
        });
    }
    
    /**
     * Đăng nhập
     * @return User nếu thành công, null nếu thất bại
     */
    public Future<User> login(String username, String password) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            User user = userDao.findByUsername(username);
            
            if (user == null) {
                return null; // User không tồn tại
            }
            
            // Verify password
            if (PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
                return user;
            }
            
            return null; // Sai mật khẩu
        });
    }
    
    // ==================== CRUD OPERATIONS ====================
    
    public LiveData<List<User>> getAllUsers() {
        return userDao.getAllUsers();
    }
    
    public LiveData<User> getUserById(long userId) {
        return userDao.getUserById(userId);
    }
    
    public Future<User> getUserByIdSync(long userId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return userDao.getUserByIdSync(userId);
        });
    }
    
    public void insert(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insert(user);
        });
    }
    
    public void update(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            user.setUpdatedAt(new Date());
            userDao.update(user);
        });
    }
    
    public void delete(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.delete(user);
        });
    }
    
    // ==================== ADVANCED QUERIES ====================
    
    public LiveData<List<User>> searchUsers(String keyword) {
        return userDao.searchUsers(keyword);
    }
    
    public LiveData<List<User>> getUsersByRole(String role) {
        return userDao.getUsersByRole(role);
    }
    
    public Future<Boolean> changePassword(long userId, String oldPassword, String newPassword) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            User user = userDao.getUserByIdSync(userId);
            
            if (user == null) {
                return false;
            }
            
            // Verify old password
            if (!PasswordHasher.verifyPassword(oldPassword, user.getPasswordHash())) {
                return false;
            }
            
            // Update password
            String newPasswordHash = PasswordHasher.hashPassword(newPassword);
            userDao.updatePassword(userId, newPasswordHash, new Date().getTime());
            
            return true;
        });
    }
    
    public void updateRole(long userId, String role) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.updateRole(userId, role, new Date().getTime());
        });
    }
}
