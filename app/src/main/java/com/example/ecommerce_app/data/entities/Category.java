package com.example.ecommerce_app.data.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Entity Category - Danh mục sản phẩm
 * 
 * Các trường:
 * - id: ID tự động tăng
 * - name: Tên danh mục (unique)
 * - description: Mô tả danh mục
 * - imageFilename: Tên file ảnh trong assets/images/categories/
 * - createdAt: Ngày tạo
 */
@Entity(tableName = "categories",
        indices = {@Index(value = "name", unique = true)})
public class Category {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String name;
    private String description;
    private String imageFilename; // Tên file ảnh trong assets
    private Date createdAt;
    
    // Constructor
    public Category() {
        this.createdAt = new Date();
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImageFilename() {
        return imageFilename;
    }
    
    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
