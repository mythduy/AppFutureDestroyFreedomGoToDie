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
 * CẤU TRÚC FOLDER MỚI:
 * assets/images/
 * ├── products/
 * │   ├── product_1/          (Mỗi sản phẩm có folder riêng)
 * │   │   ├── main.jpg        (Ảnh chính)
 * │   │   ├── image_1.jpg     (Ảnh phụ)
 * │   │   ├── image_2.jpg
 * │   │   └── ...
 * │   ├── product_2/
 * │   │   └── main.jpg
 * │   └── ...
 * ├── categories/
 * │   ├── electronics.jpg
 * │   └── phones.jpg
 * └── banners/
 *     ├── banner_1.jpg
 *     └── banner_2.jpg
 */
public class ImageHelper {
    
    // Đường dẫn mặc định cho images trong assets
    private static final String IMAGES_PATH = "images/";
    private static final String PRODUCTS_PATH = IMAGES_PATH + "products/";
    private static final String CATEGORIES_PATH = IMAGES_PATH + "categories/";
    private static final String BANNERS_PATH = IMAGES_PATH + "banners/";
    
    // Tên file ảnh mặc định
    public static final String MAIN_IMAGE = "main.jpg";
    
    /**
     * Load bitmap từ assets theo path đầy đủ
     * 
     * @param context Application context
     * @param fullPath Path đầy đủ (ví dụ: "images/products/product_1/main.jpg")
     * @return Bitmap hoặc null nếu không tìm thấy
     */
    public static Bitmap loadImageFromAssets(Context context, String fullPath) {
        if (fullPath == null || fullPath.isEmpty()) {
            return null;
        }
        
        try {
            InputStream inputStream = context.getAssets().open(fullPath);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Load ảnh product theo productId và tên file
     * 
     * @param context Application context
     * @param productId ID của sản phẩm
     * @param imageName Tên file (main.jpg, image_1.jpg, ...)
     * @return Bitmap hoặc null nếu không tìm thấy
     */
    public static Bitmap loadProductImage(Context context, long productId, String imageName) {
        String path = getProductImagePath(productId, imageName);
        return loadImageFromAssets(context, path);
    }
    
    /**
     * Load ảnh chính của product
     * 
     * @param context Application context
     * @param productId ID của sản phẩm
     * @return Bitmap hoặc null nếu không tìm thấy
     */
    public static Bitmap loadProductMainImage(Context context, long productId) {
        return loadProductImage(context, productId, MAIN_IMAGE);
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
     * Lấy path đầy đủ của ảnh product
     * 
     * @param productId ID của sản phẩm
     * @param imageName Tên file ảnh (main.jpg, image_1.jpg, ...)
     * @return Path: images/products/product_123/main.jpg
     */
    public static String getProductImagePath(long productId, String imageName) {
        return PRODUCTS_PATH + "product_" + productId + "/" + imageName;
    }
    
    /**
     * DEPRECATED: Backward compatibility cho code cũ
     * Tạo path từ filename (giả định format cũ: product_id.jpg)
     * 
     * @deprecated Dùng getProductImagePath(productId, imageName) thay thế
     */
    @Deprecated
    public static String getProductImagePath(String filename) {
        // Cấu trúc CŨ: images/products/product_1.jpg
        // Để tương thích backward, giữ nguyên
        return PRODUCTS_PATH + filename;
    }
    
    /**
     * Lấy path của ảnh chính của product
     * 
     * @param productId ID của sản phẩm
     * @return Path: images/products/product_123/main.jpg
     */
    public static String getProductMainImagePath(long productId) {
        return getProductImagePath(productId, MAIN_IMAGE);
    }
    
    /**
     * Lấy folder path của product
     * 
     * @param productId ID của sản phẩm
     * @return Path: images/products/product_123/
     */
    public static String getProductFolderPath(long productId) {
        return PRODUCTS_PATH + "product_" + productId + "/";
    }
    
    /**
     * Tạo đường dẫn đầy đủ cho category image trong assets
     */
    public static String getCategoryImagePath(String filename) {
        return CATEGORIES_PATH + filename;
    }
    
    /**
     * Tạo đường dẫn đầy đủ cho banner image trong assets
     */
    public static String getBannerImagePath(String filename) {
        return BANNERS_PATH + filename;
    }
    
    /**
     * Kiểm tra ảnh có tồn tại trong assets không
     * 
     * @param context Application context
     * @param fullPath Path đầy đủ trong assets
     * @return true nếu tồn tại
     */
    public static boolean imageExists(Context context, String fullPath) {
        try {
            InputStream inputStream = context.getAssets().open(fullPath);
            inputStream.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Lấy danh sách tất cả ảnh của một product
     * 
     * @param context Application context
     * @param productId ID của sản phẩm
     * @return Mảng tên file ảnh trong folder product
     */
    public static String[] getProductImageList(Context context, long productId) {
        try {
            String productFolder = PRODUCTS_PATH + "product_" + productId;
            return context.getAssets().list(productFolder);
        } catch (IOException e) {
            e.printStackTrace();
            return new String[0];
        }
    }
    
    /**
     * Tạo danh sách tên file cho gallery của product
     * Format mới: main.jpg, image_1.jpg, image_2.jpg, ...
     * 
     * @param count Số lượng ảnh (bao gồm ảnh chính)
     * @return List tên file
     */
    public static List<String> generateProductImageFilenames(int count) {
        List<String> filenames = new ArrayList<>();
        
        // Ảnh chính
        filenames.add(MAIN_IMAGE);
        
        // Các ảnh phụ
        for (int i = 1; i < count; i++) {
            filenames.add("image_" + i + ".jpg");
        }
        
        return filenames;
    }
    
    /**
     * Lấy URI để dùng với Glide/Picasso
     * Format: file:///android_asset/images/products/product_1/main.jpg
     * 
     * @param assetPath Path trong assets
     * @return URI string
     */
    public static String getAssetUri(String assetPath) {
        return "file:///android_asset/" + assetPath;
    }
    
    /**
     * Lấy URI của ảnh chính product để dùng với Glide
     * 
     * @param productId ID của sản phẩm
     * @return URI string
     */
    public static String getProductMainImageUri(long productId) {
        return getAssetUri(getProductMainImagePath(productId));
    }
    
    /**
     * Lấy URI của ảnh product để dùng với Glide
     * 
     * @param productId ID của sản phẩm
     * @param imageName Tên file ảnh
     * @return URI string
     */
    public static String getProductImageUri(long productId, String imageName) {
        return getAssetUri(getProductImagePath(productId, imageName));
    }
    
    /**
     * Lấy URI của category image để dùng với Glide
     * 
     * @param imageName Tên file ảnh
     * @return URI string
     */
    public static String getCategoryImageUri(String imageName) {
        return getAssetUri(getCategoryImagePath(imageName));
    }
    
    /**
     * Lấy URI của banner image để dùng với Glide
     * 
     * @param imageName Tên file ảnh
     * @return URI string
     */
    public static String getBannerImageUri(String imageName) {
        return getAssetUri(getBannerImagePath(imageName));
    }
}
