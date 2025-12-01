package com.example.ecommerce_app.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.ecommerce_app.data.converters.DateConverter;
import com.example.ecommerce_app.data.converters.StringListConverter;
import com.example.ecommerce_app.data.dao.CartItemDao;
import com.example.ecommerce_app.data.dao.CategoryDao;
import com.example.ecommerce_app.data.dao.FavoriteDao;
import com.example.ecommerce_app.data.dao.OrderDao;
import com.example.ecommerce_app.data.dao.OrderItemDao;
import com.example.ecommerce_app.data.dao.ProductDao;
import com.example.ecommerce_app.data.dao.ReviewDao;
import com.example.ecommerce_app.data.dao.UserDao;
import com.example.ecommerce_app.data.entities.CartItem;
import com.example.ecommerce_app.data.entities.Category;
import com.example.ecommerce_app.data.entities.Favorite;
import com.example.ecommerce_app.data.entities.Order;
import com.example.ecommerce_app.data.entities.OrderItem;
import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.data.entities.Review;
import com.example.ecommerce_app.data.entities.User;
import com.example.ecommerce_app.utils.SampleDataGenerator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AppDatabase - Room Database chính cho ứng dụng
 * 
 * Singleton pattern để đảm bảo chỉ có 1 instance
 * Bao gồm tất cả entities và DAOs
 * Có callback để seed dữ liệu mẫu khi tạo database lần đầu
 */
@Database(
    entities = {
        User.class,
        Category.class,
        Product.class,
        CartItem.class,
        Order.class,
        OrderItem.class,
        Favorite.class,
        Review.class
    },
    version = 1,
    exportSchema = false
)
@TypeConverters({DateConverter.class, StringListConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    
    // Tên database
    private static final String DATABASE_NAME = "ecommerce_db";
    
    // Singleton instance
    private static volatile AppDatabase INSTANCE;
    
    // ExecutorService để chạy database operations trên background thread
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = 
        Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    
    // Abstract methods để lấy DAOs
    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract ProductDao productDao();
    public abstract CartItemDao cartItemDao();
    public abstract OrderDao orderDao();
    public abstract OrderItemDao orderItemDao();
    public abstract FavoriteDao favoriteDao();
    public abstract ReviewDao reviewDao();
    
    /**
     * Lấy instance của database (Singleton pattern)
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        DATABASE_NAME
                    )
                    .addCallback(sRoomDatabaseCallback) // Thêm callback để seed data
                    .addMigrations(MIGRATION_1_2) // Thêm migration nếu có
                    .build();
                }
            }
        }
        return INSTANCE;
    }
    
    /**
     * Callback được gọi khi database được tạo lần đầu
     * Sử dụng để seed dữ liệu mẫu
     */
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            
            // Seed dữ liệu mẫu trên background thread
            databaseWriteExecutor.execute(() -> {
                // Lấy các DAOs
                UserDao userDao = INSTANCE.userDao();
                CategoryDao categoryDao = INSTANCE.categoryDao();
                ProductDao productDao = INSTANCE.productDao();
                ReviewDao reviewDao = INSTANCE.reviewDao();
                
                // Tạo dữ liệu mẫu
                SampleDataGenerator.populateDatabase(
                    userDao, 
                    categoryDao, 
                    productDao, 
                    reviewDao
                );
            });
        }
        
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            // Có thể thêm logic khi mở database
        }
    };
    
    /**
     * Migration từ version 1 sang 2 (ví dụ)
     * Sử dụng khi cần thay đổi schema database
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Ví dụ: Thêm cột mới vào table
            // database.execSQL("ALTER TABLE products ADD COLUMN discount REAL DEFAULT 0");
            
            // Hoặc tạo table mới
            // database.execSQL("CREATE TABLE IF NOT EXISTS `promotions` (...)");
        }
    };
    
    /**
     * Xóa database (dùng cho testing)
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
