package com.example.ecommerce_app.data.models;

import com.example.ecommerce_app.data.entities.Review;
import com.example.ecommerce_app.data.entities.User;

/**
 * ReviewWithUser - POJO kết hợp Review với User info
 * Để hiển thị reviews với tên người dùng
 */
public class ReviewWithUser {
    
    public Review review;
    public User user;
    
    public ReviewWithUser() {
    }
    
    public ReviewWithUser(Review review, User user) {
        this.review = review;
        this.user = user;
    }
    
    public Review getReview() {
        return review;
    }
    
    public void setReview(Review review) {
        this.review = review;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
}
