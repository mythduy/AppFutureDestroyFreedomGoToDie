package com.example.ecommerce_app.data.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Entity User - Lưu thông tin người dùng
 * 
 * Các trường:
 * - id: ID tự động tăng
 * - username: Tên đăng nhập (unique)
 * - email: Email (unique)
 * - passwordHash: Mật khẩu đã được hash bằng BCrypt
 * - fullName: Họ tên đầy đủ
 * - phone: Số điện thoại
 * - address: Địa chỉ
 * - role: Vai trò (USER, ADMIN)
 * - createdAt: Ngày tạo tài khoản
 * - updatedAt: Ngày cập nhật cuối
 */
@Entity(tableName = "users",
        indices = {
            @Index(value = "username", unique = true),
            @Index(value = "email", unique = true)
        })
public class User {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String username;
    private String email;
    private String passwordHash; // Mật khẩu đã hash bằng BCrypt
    private String fullName;
    private String phone;
    private String address;
    private String role; // USER, ADMIN
    private Date createdAt;
    private Date updatedAt;
    
    // Constructor
    public User() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.role = "USER"; // Mặc định là USER
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
