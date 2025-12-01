package com.example.ecommerce_app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * ImageHelper - Utility class để làm việc với ảnh từ assets
 * 
 * Hỗ trợ load ảnh từ assets/images/ folder
 * Xử lý gallery ảnh cho sản phẩm (product_1.jpg, product_1_1.jpg, product_1_2.jpg, ...)
 */
public class ImageHelper {
    
    // Đường dẫn mặc định cho images trong assets
    private static final String IMAGES_PATH = "images/";
    private static final String PRODUCTS_PATH = IMAGES_PATH + "products/";
    private static final String CATEGORIES_PATH = IMAGES_PATH + "categories/";
    
    /**
     * Load bitmap từ assets
     * 
     * @param context Application context
     * @param filename Tên file ảnh (ví dụ: "product_1.jpg")
     * @return Bitmap hoặc null nếu không tìm thấy
     */
    public static Bitmap loadImageFromAssets(Context context, String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        
        try {
            InputStream inputStream = context.getAssets().open(PRODUCTS_PATH + filename);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Load image from assets asynchronously and set it into ImageView
     * This overload allows direct loading into ImageView without blocking UI thread
     * 
     * @param context Application context (must not be null)
     * @param assetPath Full path to image in assets (e.g., "images/products/product_1.jpg")
     * @param targetImageView ImageView to display the loaded image (must not be null)
     */
    public static void loadImageFromAssets(final Context context, final String assetPath, final ImageView targetImageView) {
        // Null safety checks
        if (context == null || assetPath == null || targetImageView == null) {
            return;
        }
        
        // Load image off the main thread
        Executors.newSingleThreadExecutor().execute(() -> {
            Bitmap bitmap = null;
            try (InputStream is = context.getAssets().open(assetPath)) {
                bitmap = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                // Image not found or error reading - will use fallback
            } catch (Exception e) {
                // Other exceptions - will use fallback
            }
            
            // Post result back to UI thread
            final Bitmap finalBitmap = bitmap;
            new Handler(Looper.getMainLooper()).post(() -> {
                if (finalBitmap != null) {
                    targetImageView.setImageBitmap(finalBitmap);
                } else {
                    // Fallback: try to find placeholder drawable
                    int placeholderId = context.getResources().getIdentifier(
                            "ic_image_placeholder", "drawable", context.getPackageName());
                    if (placeholderId != 0) {
                        targetImageView.setImageResource(placeholderId);
                    } else {
                        // No placeholder found, set to null (will show nothing)
                        targetImageView.setImageDrawable(null);
                    }
                }
            });
        });
    }
    
    /**
     * Load category image từ assets
     */
    public static Bitmap loadCategoryImage(Context context, String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        
        try {
            InputStream inputStream = context.getAssets().open(CATEGORIES_PATH + filename);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Load tất cả ảnh của một sản phẩm (gallery)
     * 
     * Ví dụ: Với SKU "ARDUINO_UNO", sẽ tìm:
     * - arduino_uno.jpg (ảnh chính)
     * - arduino_uno_1.jpg
     * - arduino_uno_2.jpg
     * - ...
     * 
     * @param context Application context
     * @param imageFilenames Danh sách tên file ảnh
     * @return List of Bitmaps
     */
    public static List<Bitmap> loadProductGallery(Context context, List<String> imageFilenames) {
        List<Bitmap> gallery = new ArrayList<>();
        
        if (imageFilenames == null || imageFilenames.isEmpty()) {
            return gallery;
        }
        
        for (String filename : imageFilenames) {
            Bitmap bitmap = loadImageFromAssets(context, filename);
            if (bitmap != null) {
                gallery.add(bitmap);
            }
        }
        
        return gallery;
    }
    
    /**
     * Tạo đường dẫn đầy đủ cho product image trong assets
     */
    public static String getProductImagePath(String filename) {
        return PRODUCTS_PATH + filename;
    }
    
    /**
     * Tạo đường dẫn đầy đủ cho category image trong assets
     */
    public static String getCategoryImagePath(String filename) {
        return CATEGORIES_PATH + filename;
    }
    
    /**
     * Kiểm tra file có tồn tại trong assets không
     */
    public static boolean imageExists(Context context, String filename) {
        try {
            InputStream inputStream = context.getAssets().open(PRODUCTS_PATH + filename);
            inputStream.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Tạo tên file cho các ảnh trong gallery
     * Ví dụ: generateGalleryFilenames("arduino_uno", 3)
     * => ["arduino_uno.jpg", "arduino_uno_1.jpg", "arduino_uno_2.jpg"]
     */
    public static List<String> generateGalleryFilenames(String baseName, int count) {
        List<String> filenames = new ArrayList<>();
        
        // Ảnh chính
        filenames.add(baseName + ".jpg");
        
        // Các ảnh phụ
        for (int i = 1; i < count; i++) {
            filenames.add(baseName + "_" + i + ".jpg");
        }
        
        return filenames;
    }
}
