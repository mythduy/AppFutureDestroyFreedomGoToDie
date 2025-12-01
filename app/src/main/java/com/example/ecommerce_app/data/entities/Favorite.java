package com.example.ecommerce_app.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Entity Favorite - Sản phẩm yêu thích
 * 
 * Các trường:
 * - id: ID tự động tăng
 * - userId: ID người dùng (foreign key)
 * - productId: ID sản phẩm (foreign key)
 * - addedAt: Ngày thêm vào danh sách yêu thích
 */
@Entity(tableName = "favorites",
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
public class Favorite {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private long userId;
    private long productId;
    private Date addedAt;
    
    // Constructor
    public Favorite() {
        this.addedAt = new Date();
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
    
    public Date getAddedAt() {
        return addedAt;
    }
    
    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }
}
