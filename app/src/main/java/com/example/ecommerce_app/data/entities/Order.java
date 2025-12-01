package com.example.ecommerce_app.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Entity Order - Đơn hàng
 * 
 * Các trường:
 * - id: ID tự động tăng
 * - userId: ID người dùng (foreign key)
 * - orderNumber: Mã đơn hàng (unique)
 * - totalAmount: Tổng tiền
 * - status: Trạng thái (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
 * - shippingAddress: Địa chỉ giao hàng
 * - shippingPhone: SĐT nhận hàng
 * - note: Ghi chú
 * - createdAt: Ngày tạo đơn
 * - updatedAt: Ngày cập nhật
 */
@Entity(tableName = "orders",
        foreignKeys = @ForeignKey(
            entity = User.class,
            parentColumns = "id",
            childColumns = "userId",
            onDelete = ForeignKey.CASCADE
        ),
        indices = {
            @Index(value = "userId"),
            @Index(value = "orderNumber", unique = true),
            @Index(value = "status")
        })
public class Order {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private long userId;
    private String orderNumber; // Mã đơn hàng duy nhất
    private double totalAmount;
    private String status; // PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    private String shippingAddress;
    private String shippingPhone;
    private String note;
    private Date createdAt;
    private Date updatedAt;
    
    // Constructor
    public Order() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.status = "PENDING";
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getUserId() {
        return userId;
    }
    
    public void setUserId(long userId) {
        this.userId = userId;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public String getShippingPhone() {
        return shippingPhone;
    }
    
    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
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
