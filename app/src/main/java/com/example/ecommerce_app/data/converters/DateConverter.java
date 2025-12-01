package com.example.ecommerce_app.data.converters;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * DateConverter - Chuyển đổi Date <-> Long cho Room Database
 * 
 * Room không hỗ trợ Date trực tiếp, cần convert sang timestamp (Long)
 */
public class DateConverter {
    
    /**
     * Chuyển đổi từ timestamp (Long) sang Date
     */
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }
    
    /**
     * Chuyển đổi từ Date sang timestamp (Long)
     */
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
