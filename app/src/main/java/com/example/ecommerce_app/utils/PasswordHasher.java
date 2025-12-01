package com.example.ecommerce_app.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * PasswordHasher - Utility class để hash và verify mật khẩu
 * 
 * Sử dụng BCrypt algorithm để hash mật khẩu an toàn
 * BCrypt tự động thêm salt và có cost factor để chống brute-force
 */
public class PasswordHasher {
    
    // Cost factor cho BCrypt (12 là mức an toàn tốt)
    // Càng cao càng an toàn nhưng càng chậm
    private static final int BCRYPT_COST = 12;
    
    /**
     * Hash mật khẩu plain text thành BCrypt hash
     * 
     * @param plainPassword Mật khẩu gốc
     * @return Mật khẩu đã hash (BCrypt string)
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, plainPassword.toCharArray());
    }
    
    /**
     * Verify mật khẩu nhập vào với hash đã lưu
     * 
     * @param plainPassword Mật khẩu người dùng nhập
     * @param hashedPassword Hash password đã lưu trong database
     * @return true nếu khớp, false nếu không khớp
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        
        BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword);
        return result.verified;
    }
    
    /**
     * Kiểm tra độ mạnh của mật khẩu
     * 
     * @param password Mật khẩu cần kiểm tra
     * @return true nếu mật khẩu đủ mạnh
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            else if (Character.isLowerCase(c)) hasLowerCase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecialChar = true;
        }
        
        // Yêu cầu ít nhất 3 trong 4 điều kiện
        int criteriaCount = 0;
        if (hasUpperCase) criteriaCount++;
        if (hasLowerCase) criteriaCount++;
        if (hasDigit) criteriaCount++;
        if (hasSpecialChar) criteriaCount++;
        
        return criteriaCount >= 3;
    }
}
