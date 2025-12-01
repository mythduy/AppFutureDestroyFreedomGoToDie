package com.example.ecommerce_app.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Entity Review - Đánh giá sản phẩm
 * 
 * Các trường:
 * - id: ID tự động tăng
 * - userId: ID người dùng (foreign key)
 * - productId: ID sản phẩm (foreign key)
 * - rating: Số sao (1-5)
 * - comment: Nội dung đánh giá
 * - createdAt: Ngày đánh giá
 * - updatedAt: Ngày cập nhật
 */
@Entity(tableName = "reviews",
        foreignKeys = {
            @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                entity = Product.class,
                parentColumns = "id",
                childColumns = "productId",
                onDelete = ForeignKey.CASCADE
            )
        },
        indices = {
            @Index(value = "userId"),
            @Index(value = "productId"),
            @Index(value = {"userId", "productId"}, unique = true)
        })
public class Review {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private long userId;
    private long productId;
    private int rating; // 1-5 sao
    private String comment;
    private Date createdAt;
    private Date updatedAt;
    
    // Constructor
    public Review() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
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
    
    public long getProductId() {
        return productId;
    }
    
    public void setProductId(long productId) {
        this.productId = productId;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
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
