package com.example.ecommerce_app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecommerce_app.data.entities.User;

import java.util.List;

/**
 * UserDao - Data Access Object cho User entity
 * 
 * Các phương thức CRUD + advanced queries cho User
 */
@Dao
public interface UserDao {
    
    // ==================== CREATE ====================
    
    /**
     * Thêm user mới vào database
     * @return ID của user vừa insert
     */
    @Insert
    long insert(User user);
    
    /**
     * Thêm nhiều users
     */
    @Insert
    void insertAll(List<User> users);
    
    // ==================== READ ====================
    
    /**
     * Lấy tất cả users (LiveData để observe thay đổi)
     */
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    LiveData<List<User>> getAllUsers();
    
    /**
     * Lấy user theo ID
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    LiveData<User> getUserById(long userId);
    
    /**
     * Lấy user theo ID (không dùng LiveData, cho synchronous call)
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    User getUserByIdSync(long userId);
    
    /**
     * Tìm user theo username
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User findByUsername(String username);
    
    /**
     * Tìm user theo email
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User findByEmail(String email);
    
    /**
     * Kiểm tra username đã tồn tại chưa
     */
    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int checkUsernameExists(String username);
    
    /**
     * Kiểm tra email đã tồn tại chưa
     */
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int checkEmailExists(String email);
    
    /**
     * Tìm kiếm user theo tên hoặc email
     */
    @Query("SELECT * FROM users WHERE fullName LIKE '%' || :keyword || '%' OR email LIKE '%' || :keyword || '%' ORDER BY createdAt DESC")
    LiveData<List<User>> searchUsers(String keyword);
    
    /**
     * Lấy users theo role
     */
    @Query("SELECT * FROM users WHERE role = :role ORDER BY createdAt DESC")
    LiveData<List<User>> getUsersByRole(String role);
    
    // ==================== UPDATE ====================
    
    /**
     * Cập nhật thông tin user
     */
    @Update
    void update(User user);
    
    /**
     * Cập nhật password
     */
    @Query("UPDATE users SET passwordHash = :newPasswordHash, updatedAt = :updatedAt WHERE id = :userId")
    void updatePassword(long userId, String newPasswordHash, long updatedAt);
    
    /**
     * Cập nhật role
     */
    @Query("UPDATE users SET role = :role, updatedAt = :updatedAt WHERE id = :userId")
    void updateRole(long userId, String role, long updatedAt);
    
    // ==================== DELETE ====================
    
    /**
     * Xóa user
     */
    @Delete
    void delete(User user);
    
    /**
     * Xóa user theo ID
     */
    @Query("DELETE FROM users WHERE id = :userId")
    void deleteById(long userId);
    
    /**
     * Xóa tất cả users (dùng cho testing)
     */
    @Query("DELETE FROM users")
    void deleteAll();
}
