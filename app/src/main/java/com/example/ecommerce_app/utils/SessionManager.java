package com.example.ecommerce_app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager - Quản lý session và trạng thái đăng nhập của user
 * 
 * Chức năng:
 * - Lưu/xóa thông tin đăng nhập
 * - Kiểm tra trạng thái login
 * - Lưu user ID và thông tin cơ bản
 */
public class SessionManager {
    
    private static final String PREF_NAME = "EcommerceAppSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;
    
    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }
    
    /**
     * Lưu thông tin đăng nhập sau khi login thành công
     */
    public void createLoginSession(long userId, String username, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }
    
    /**
     * Kiểm tra user đã đăng nhập chưa
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    /**
     * Lấy user ID của user đang đăng nhập
     */
    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, 0);
    }
    
    /**
     * Lấy username của user đang đăng nhập
     */
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "Guest");
    }
    
    /**
     * Lấy email của user đang đăng nhập
     */
    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }
    
    /**
     * Xóa session khi logout
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
    
    /**
     * Kiểm tra và yêu cầu login nếu cần
     * Trả về true nếu đã login, false nếu cần login
     */
    public boolean checkLoginRequired(Context context, String returnTo) {
        if (isLoggedIn()) {
            return true;
        }
        
        // Chưa login - chuyển sang LoginActivity
        android.content.Intent intent = new android.content.Intent(context, com.example.ecommerce_app.LoginActivity.class);
        intent.putExtra("RETURN_TO", returnTo);
        context.startActivity(intent);
        return false;
    }
    
    /**
     * Kiểm tra và yêu cầu login cho checkout/đặt hàng
     * Dùng riêng cho flow checkout
     * @return true nếu đã login và có thể tiếp tục checkout
     */
    public boolean checkLoginForCheckout(Context context) {
        return checkLoginRequired(context, "CHECKOUT");
    }
    
    /**
     * Hiển thị thông tin user cho UI (dùng trong Home Fragment)
     * Trả về "Guest" nếu chưa login, hoặc username nếu đã login
     */
    public String getDisplayName() {
        if (isLoggedIn()) {
            return getUsername();
        }
        return "Guest";
    }
}
