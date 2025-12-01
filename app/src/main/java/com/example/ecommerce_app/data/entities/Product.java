package com.example.ecommerce_app.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.ecommerce_app.data.converters.StringListConverter;

import java.util.Date;
import java.util.List;

/**
 * Entity Product - Sản phẩm (linh kiện điện tử)
 * 
 * Các trường:
 * - id: ID tự động tăng
 * - name: Tên sản phẩm
 * - description: Mô tả chi tiết
 * - price: Giá bán
 * - stock: Số lượng tồn kho
 * - categoryId: ID danh mục (foreign key)
 * - imageFilenames: Danh sách tên file ảnh (product_1.jpg, product_1_1.jpg, ...)
 * - specifications: Thông số kỹ thuật (JSON string)
 * - brand: Thương hiệu
 * - sku: Mã sản phẩm (unique)
 * - isActive: Còn kinh doanh hay không
 * - createdAt: Ngày tạo
 * - updatedAt: Ngày cập nhật
 */
@Entity(tableName = "products",
        foreignKeys = @ForeignKey(
            entity = Category.class,
            parentColumns = "id",
            childColumns = "categoryId",
            onDelete = ForeignKey.CASCADE
        ),
        indices = {
            @Index(value = "categoryId"),
            @Index(value = "sku", unique = true),
            @Index(value = "name")
        })
public class Product {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String name;
    private String description;
    private double price;
    private int stock;
    private long categoryId;
    
    @TypeConverters(StringListConverter.class)
    private List<String> imageFilenames; // Danh sách tên file ảnh
    
    private String specifications; // JSON string chứa thông số kỹ thuật
    private String brand;
    private String sku; // Stock Keeping Unit - Mã sản phẩm
    private boolean isActive;
    private Date createdAt;
    private Date updatedAt;
    
    // Constructor
    public Product() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.isActive = true;
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
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
    
    public long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }
    
    public List<String> getImageFilenames() {
        return imageFilenames;
    }
    
    public void setImageFilenames(List<String> imageFilenames) {
        this.imageFilenames = imageFilenames;
    }
    
    public String getSpecifications() {
        return specifications;
    }
    
    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getSku() {
        return sku;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
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
