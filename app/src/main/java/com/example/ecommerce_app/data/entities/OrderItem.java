package com.example.ecommerce_app.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity OrderItem - Chi tiết sản phẩm trong đơn hàng
 * 
 * Các trường:
 * - id: ID tự động tăng
 * - orderId: ID đơn hàng (foreign key)
 * - productId: ID sản phẩm (foreign key)
 * - quantity: Số lượng
 * - price: Giá tại thời điểm mua (lưu để tránh thay đổi khi giá product thay đổi)
 * - subtotal: Thành tiền (quantity * price)
 */
@Entity(tableName = "order_items",
        foreignKeys = {
            @ForeignKey(
                entity = Order.class,
                parentColumns = "id",
                childColumns = "orderId",
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
            @Index(value = "orderId"),
            @Index(value = "productId")
        })
public class OrderItem {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private long orderId;
    private long productId;
    private int quantity;
    private double price; // Giá tại thời điểm mua
    private double subtotal; // Thành tiền
    
    // Constructor
    public OrderItem() {
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
    
    public long getProductId() {
        return productId;
    }
    
    public void setProductId(long productId) {
        this.productId = productId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public double getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
