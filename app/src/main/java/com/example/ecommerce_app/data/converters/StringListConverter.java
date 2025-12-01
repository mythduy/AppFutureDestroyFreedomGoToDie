package com.example.ecommerce_app.data.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * StringListConverter - Chuyển đổi List<String> <-> JSON String cho Room Database
 * 
 * Sử dụng Gson để serialize/deserialize danh sách String thành JSON
 * Dùng để lưu danh sách tên file ảnh của sản phẩm
 */
public class StringListConverter {
    
    private static final Gson gson = new Gson();
    
    /**
     * Chuyển đổi từ JSON String sang List<String>
     */
    @TypeConverter
    public static List<String> fromString(String value) {
        if (value == null) {
            return null;
        }
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(value, listType);
    }
    
    /**
     * Chuyển đổi từ List<String> sang JSON String
     */
    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null) {
            return null;
        }
        return gson.toJson(list);
    }
}
