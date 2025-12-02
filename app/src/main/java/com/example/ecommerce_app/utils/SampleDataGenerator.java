package com.example.ecommerce_app.utils;

import com.example.ecommerce_app.data.dao.CategoryDao;
import com.example.ecommerce_app.data.dao.ProductDao;
import com.example.ecommerce_app.data.dao.ReviewDao;
import com.example.ecommerce_app.data.dao.UserDao;
import com.example.ecommerce_app.data.entities.Category;
import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.data.entities.Review;
import com.example.ecommerce_app.data.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * SampleDataGenerator - Tạo dữ liệu mẫu cho database
 * 
 * Được gọi khi database được tạo lần đầu tiên
 * Tạo users, categories, products, reviews mẫu
 */
public class SampleDataGenerator {
    
    /**
     * Populate database với dữ liệu mẫu
     */
    public static void populateDatabase(UserDao userDao, CategoryDao categoryDao, 
                                       ProductDao productDao, ReviewDao reviewDao) {
        
        // 1. Tạo users mẫu
        List<User> users = createSampleUsers();
        for (User user : users) {
            userDao.insert(user);
        }
        
        // 2. Tạo categories
        List<Category> categories = createSampleCategories();
        for (Category category : categories) {
            categoryDao.insert(category);
        }
        
        // 3. Tạo products
        List<Product> products = createSampleProducts();
        for (Product product : products) {
            productDao.insert(product);
        }
        
        // 4. Tạo reviews
        List<Review> reviews = createSampleReviews();
        for (Review review : reviews) {
            reviewDao.insert(review);
        }
    }
    
    /**
     * Tạo users mẫu
     */
    private static List<User> createSampleUsers() {
        List<User> users = new ArrayList<>();
        
        // Admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@ecommerce.com");
        admin.setPasswordHash(PasswordHasher.hashPassword("Admin@123"));
        admin.setFullName("Quản Trị Viên");
        admin.setPhone("0123456789");
        admin.setAddress("123 Nguyễn Huệ, Q1, TP.HCM");
        admin.setRole("ADMIN");
        users.add(admin);
        
        // Regular user
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPasswordHash(PasswordHasher.hashPassword("User@123"));
        user1.setFullName("Nguyễn Văn A");
        user1.setPhone("0987654321");
        user1.setAddress("456 Lê Lợi, Q1, TP.HCM");
        user1.setRole("USER");
        users.add(user1);
        
        // Another user
        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPasswordHash(PasswordHasher.hashPassword("User@123"));
        user2.setFullName("Trần Thị B");
        user2.setPhone("0912345678");
        user2.setAddress("789 Hai Bà Trưng, Q3, TP.HCM");
        user2.setRole("USER");
        users.add(user2);
        
        return users;
    }
    
    /**
     * Tạo categories mẫu
     */
    private static List<Category> createSampleCategories() {
        List<Category> categories = new ArrayList<>();
        
        Category cat1 = new Category();
        cat1.setName("Vi Điều Khiển");
        cat1.setDescription("Arduino, ESP32, STM32 và các board vi điều khiển");
        cat1.setImageFilename("category_mcu.jpg");
        categories.add(cat1);
        
        Category cat2 = new Category();
        cat2.setName("Cảm Biến");
        cat2.setDescription("Cảm biến nhiệt độ, độ ẩm, ánh sáng, chuyển động");
        cat2.setImageFilename("category_sensors.jpg");
        categories.add(cat2);
        
        Category cat3 = new Category();
        cat3.setName("Module Truyền Thông");
        cat3.setDescription("WiFi, Bluetooth, RF, LoRa modules");
        cat3.setImageFilename("category_comm.jpg");
        categories.add(cat3);
        
        Category cat4 = new Category();
        cat4.setName("Linh Kiện Điện Tử");
        cat4.setDescription("Transistor, IC, điện trở, tụ điện, LED");
        cat4.setImageFilename("category_components.jpg");
        categories.add(cat4);
        
        Category cat5 = new Category();
        cat5.setName("Module Hiển Thị");
        cat5.setDescription("LCD, OLED, LED Matrix, TFT displays");
        cat5.setImageFilename("category_displays.jpg");
        categories.add(cat5);
        
        return categories;
    }
    
    /**
     * Tạo products mẫu (linh kiện điện tử)
     */
    private static List<Product> createSampleProducts() {
        List<Product> products = new ArrayList<>();
        
        // Arduino Uno
        Product p1 = new Product();
        p1.setName("Arduino Uno R3");
        p1.setDescription("Board Arduino Uno R3 chính hãng, ATmega328P, 14 digital I/O pins, 6 analog inputs");
        p1.setPrice(250000);
        p1.setStock(50);
        p1.setCategoryId(1); // Vi Điều Khiển
        p1.setSku("ARDUINO_UNO_R3");
        p1.setBrand("Arduino");
        p1.setImageFilenames(Arrays.asList("main.jpg", "image_1.jpg", "image_2.jpg"));
        p1.setSpecifications("{\"voltage\":\"5V\",\"clock\":\"16MHz\",\"flash\":\"32KB\",\"sram\":\"2KB\"}");
        products.add(p1);
        
        // ESP32
        Product p2 = new Product();
        p2.setName("ESP32 DevKit V1");
        p2.setDescription("Module ESP32 WiFi + Bluetooth, dual-core 240MHz, 520KB SRAM");
        p2.setPrice(150000);
        p2.setStock(100);
        p2.setCategoryId(1); // Vi Điều Khiển
        p2.setSku("ESP32_DEVKIT_V1");
        p2.setBrand("Espressif");
        p2.setImageFilenames(Arrays.asList("main.jpg", "image_1.jpg"));
        p2.setSpecifications("{\"voltage\":\"3.3V\",\"wifi\":\"802.11b/g/n\",\"bluetooth\":\"4.2\"}");
        products.add(p2);
        
        // DHT22
        Product p3 = new Product();
        p3.setName("DHT22 Cảm Biến Nhiệt Độ Độ Ẩm");
        p3.setDescription("Cảm biến DHT22 đo nhiệt độ và độ ẩm chính xác cao");
        p3.setPrice(85000);
        p3.setStock(200);
        p3.setCategoryId(2); // Cảm Biến
        p3.setSku("DHT22_SENSOR");
        p3.setBrand("Generic");
        p3.setImageFilenames(Arrays.asList("main.jpg"));
        p3.setSpecifications("{\"temp_range\":\"-40~80°C\",\"humidity_range\":\"0-100%RH\",\"accuracy\":\"±0.5°C\"}");
        products.add(p3);
        
        // HC-SR04
        Product p4 = new Product();
        p4.setName("HC-SR04 Cảm Biến Siêu Âm");
        p4.setDescription("Cảm biến siêu âm đo khoảng cách, tầm 2cm-4m");
        p4.setPrice(35000);
        p4.setStock(150);
        p4.setCategoryId(2); // Cảm Biến
        p4.setSku("HC_SR04_ULTRASONIC");
        p4.setBrand("Generic");
        p4.setImageFilenames(Arrays.asList("main.jpg"));
        p4.setSpecifications("{\"range\":\"2-400cm\",\"accuracy\":\"3mm\",\"voltage\":\"5V\"}");
        products.add(p4);
        
        // NRF24L01
        Product p5 = new Product();
        p5.setName("NRF24L01+ Module RF");
        p5.setDescription("Module truyền thông RF 2.4GHz, tầm 100m");
        p5.setPrice(45000);
        p5.setStock(80);
        p5.setCategoryId(3); // Module Truyền Thông
        p5.setSku("NRF24L01_PLUS");
        p5.setBrand("Nordic");
        p5.setImageFilenames(Arrays.asList("main.jpg"));
        p5.setSpecifications("{\"frequency\":\"2.4GHz\",\"range\":\"100m\",\"data_rate\":\"2Mbps\"}");
        products.add(p5);
        
        // OLED Display
        Product p6 = new Product();
        p6.setName("OLED 0.96 inch I2C");
        p6.setDescription("Màn hình OLED 128x64, giao tiếp I2C, màu xanh");
        p6.setPrice(75000);
        p6.setStock(120);
        p6.setCategoryId(5); // Module Hiển Thị
        p6.setSku("OLED_096_I2C");
        p6.setBrand("Generic");
        p6.setImageFilenames(Arrays.asList("main.jpg", "image_1.jpg"));
        p6.setSpecifications("{\"resolution\":\"128x64\",\"interface\":\"I2C\",\"voltage\":\"3.3-5V\"}");
        products.add(p6);
        
        // LED RGB
        Product p7 = new Product();
        p7.setName("LED RGB 5mm Common Cathode");
        p7.setDescription("LED RGB 5mm chân chung âm, 4 chân");
        p7.setPrice(5000);
        p7.setStock(500);
        p7.setCategoryId(4); // Linh Kiện Điện Tử
        p7.setSku("LED_RGB_5MM_CC");
        p7.setBrand("Generic");
        p7.setImageFilenames(Arrays.asList("main.jpg"));
        p7.setSpecifications("{\"type\":\"Common Cathode\",\"voltage\":\"2-3.2V\",\"current\":\"20mA\"}");
        products.add(p7);
        
        // Breadboard
        Product p8 = new Product();
        p8.setName("Breadboard 830 điểm");
        p8.setDescription("Breadboard 830 holes, kích thước 165x55mm");
        p8.setPrice(30000);
        p8.setStock(200);
        p8.setCategoryId(4); // Linh Kiện Điện Tử
        p8.setSku("BREADBOARD_830");
        p8.setBrand("Generic");
        p8.setImageFilenames(Arrays.asList("main.jpg"));
        p8.setSpecifications("{\"holes\":\"830\",\"size\":\"165x55mm\"}");
        products.add(p8);
        
        // LCD 16x2
        Product p9 = new Product();
        p9.setName("LCD 16x2 I2C");
        p9.setDescription("Màn hình LCD 16x2 kèm module I2C, nền xanh chữ đen");
        p9.setPrice(65000);
        p9.setStock(90);
        p9.setCategoryId(5); // Module Hiển Thị
        p9.setSku("LCD_16X2_I2C");
        p9.setBrand("Generic");
        p9.setImageFilenames(Arrays.asList("main.jpg"));
        p9.setSpecifications("{\"display\":\"16x2\",\"interface\":\"I2C\",\"voltage\":\"5V\"}");
        products.add(p9);
        
        // Servo Motor
        Product p10 = new Product();
        p10.setName("Servo SG90 9g");
        p10.setDescription("Servo motor mini SG90, góc quay 180°, trọng lượng 9g");
        p10.setPrice(40000);
        p10.setStock(150);
        p10.setCategoryId(4); // Linh Kiện Điện Tử
        p10.setSku("SERVO_SG90");
        p10.setBrand("TowerPro");
        p10.setImageFilenames(Arrays.asList("main.jpg"));
        p10.setSpecifications("{\"angle\":\"180°\",\"torque\":\"1.8kg/cm\",\"voltage\":\"4.8-6V\"}");
        products.add(p10);
        
        return products;
    }
    
    /**
     * Tạo reviews mẫu
     */
    private static List<Review> createSampleReviews() {
        List<Review> reviews = new ArrayList<>();
        Date now = new Date();
        
        // Reviews cho Arduino Uno (product id = 1) - 5 reviews
        Review r1 = new Review();
        r1.setUserId(2); // user1
        r1.setProductId(1);
        r1.setRating(5);
        r1.setComment("Sản phẩm chính hãng, chất lượng tốt!\n\nGiao hàng nhanh, đóng gói cẩn thận. Board hoạt động ổn định, upload code không có vấn đề gì. Rất hài lòng với sản phẩm!");
        r1.setCreatedAt(new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)); // 7 days ago
        r1.setUpdatedAt(r1.getCreatedAt());
        reviews.add(r1);
        
        Review r2 = new Review();
        r2.setUserId(3); // user2
        r2.setProductId(1);
        r2.setRating(4);
        r2.setComment("Board Arduino chính hãng tốt\n\nChất lượng ổn, nhưng giá hơi cao so với các board clone. Tuy nhiên vẫn đáng đồng tiền bát gạo vì độ tin cậy cao.");
        r2.setCreatedAt(new Date(now.getTime() - 5 * 24 * 60 * 60 * 1000)); // 5 days ago
        r2.setUpdatedAt(r2.getCreatedAt());
        reviews.add(r2);
        
        Review r1a = new Review();
        r1a.setUserId(1); // admin
        r1a.setProductId(1);
        r1a.setRating(5);
        r1a.setComment("Excellent quality board!\n\nPerfect for beginners and professionals. All pins work correctly, no issues with uploading sketches. Highly recommended for Arduino projects.");
        r1a.setCreatedAt(new Date(now.getTime() - 3 * 24 * 60 * 60 * 1000)); // 3 days ago
        r1a.setUpdatedAt(r1a.getCreatedAt());
        reviews.add(r1a);
        
        Review r1b = new Review();
        r1b.setUserId(2); // user1
        r1b.setProductId(1);
        r1b.setRating(5);
        r1b.setComment("Tuyệt vời cho dự án IoT\n\nĐã dùng để làm hệ thống tưới cây tự động, hoạt động rất tốt. Pin nguồn ổn định, không bị reset bất thường.");
        r1b.setCreatedAt(new Date(now.getTime() - 2 * 24 * 60 * 60 * 1000)); // 2 days ago
        r1b.setUpdatedAt(r1b.getCreatedAt());
        reviews.add(r1b);
        
        Review r1c = new Review();
        r1c.setUserId(3); // user2
        r1c.setProductId(1);
        r1c.setRating(4);
        r1c.setComment("Good value for money\n\nThe board works great, good build quality. Only minor issue is the USB cable is a bit short, but that's not a big deal.");
        r1c.setCreatedAt(new Date(now.getTime() - 1 * 24 * 60 * 60 * 1000)); // 1 day ago
        r1c.setUpdatedAt(r1c.getCreatedAt());
        reviews.add(r1c);
        
        // Reviews cho ESP32 (product id = 2) - 4 reviews
        Review r3 = new Review();
        r3.setUserId(2); // user1
        r3.setProductId(2);
        r3.setRating(5);
        r3.setComment("ESP32 cực kỳ mạnh mẽ!\n\nWiFi + Bluetooth hoạt động rất tốt, xử lý nhanh. Dùng để làm smart home controller, kết nối với app mobile qua WiFi rất mượt.");
        r3.setCreatedAt(new Date(now.getTime() - 6 * 24 * 60 * 60 * 1000));
        r3.setUpdatedAt(r3.getCreatedAt());
        reviews.add(r3);
        
        Review r3a = new Review();
        r3a.setUserId(3); // user2
        r3a.setProductId(2);
        r3a.setRating(5);
        r3a.setComment("Perfect for IoT projects\n\nDual-core processor is very powerful, WiFi range is impressive. Battery life is decent when using deep sleep mode.");
        r3a.setCreatedAt(new Date(now.getTime() - 4 * 24 * 60 * 60 * 1000));
        r3a.setUpdatedAt(r3a.getCreatedAt());
        reviews.add(r3a);
        
        Review r3b = new Review();
        r3b.setUserId(1); // admin
        r3b.setProductId(2);
        r3b.setRating(4);
        r3b.setComment("Rất hài lòng với hiệu năng\n\nESP32 cho phép xử lý đa nhiệm tốt. Tuy nhiên cần cẩn thận về mức điện áp input cho các chân ADC (chỉ chịu được 3.3V).");
        r3b.setCreatedAt(new Date(now.getTime() - 2 * 24 * 60 * 60 * 1000));
        r3b.setUpdatedAt(r3b.getCreatedAt());
        reviews.add(r3b);
        
        Review r3c = new Review();
        r3c.setUserId(2); // user1
        r3c.setProductId(2);
        r3c.setRating(5);
        r3c.setComment("Best MCU for wireless projects!\n\nBuilt-in WiFi and Bluetooth make it super convenient. No need for additional modules. Great documentation and community support.");
        r3c.setCreatedAt(new Date(now.getTime() - 12 * 60 * 60 * 1000)); // 12 hours ago
        r3c.setUpdatedAt(r3c.getCreatedAt());
        reviews.add(r3c);
        
        // Reviews cho DHT22 (product id = 3) - 3 reviews
        Review r4 = new Review();
        r4.setUserId(3); // user2
        r4.setProductId(3);
        r4.setRating(5);
        r4.setComment("Cảm biến chính xác và bền\n\nĐo nhiệt độ và độ ẩm rất chính xác so với nhiệt kế điện tử. Giá cả hợp lý, đáng mua!");
        r4.setCreatedAt(new Date(now.getTime() - 8 * 24 * 60 * 60 * 1000));
        r4.setUpdatedAt(r4.getCreatedAt());
        reviews.add(r4);
        
        Review r4a = new Review();
        r4a.setUserId(1); // admin
        r4a.setProductId(3);
        r4a.setRating(4);
        r4a.setComment("Good sensor for weather monitoring\n\nAccuracy is good, response time is acceptable. Make sure to add a pull-up resistor for reliable operation.");
        r4a.setCreatedAt(new Date(now.getTime() - 3 * 24 * 60 * 60 * 1000));
        r4a.setUpdatedAt(r4a.getCreatedAt());
        reviews.add(r4a);
        
        Review r4b = new Review();
        r4b.setUserId(2); // user1
        r4b.setProductId(3);
        r4b.setRating(5);
        r4b.setComment("Tuyệt vời cho hệ thống giám sát\n\nDùng cho nhà kính, đo ổn định. Có thể đọc dữ liệu mỗi 2 giây mà không bị lỗi. Library của Arduino rất dễ dùng.");
        r4b.setCreatedAt(new Date(now.getTime() - 6 * 60 * 60 * 1000)); // 6 hours ago
        r4b.setUpdatedAt(r4b.getCreatedAt());
        reviews.add(r4b);
        
        // Review cho Servo SG90 (product id = 10) - 2 reviews
        Review r5 = new Review();
        r5.setUserId(2); // user1
        r5.setProductId(10);
        r5.setRating(4);
        r5.setComment("Servo nhỏ gọn, hoạt động tốt\n\nTrọng lượng nhẹ, phù hợp cho mô hình robot nhỏ. Chạy êm, tiếng ồn ít. Giá rất tốt!");
        r5.setCreatedAt(new Date(now.getTime() - 5 * 24 * 60 * 60 * 1000));
        r5.setUpdatedAt(r5.getCreatedAt());
        reviews.add(r5);
        
        Review r5a = new Review();
        r5a.setUserId(3); // user2
        r5a.setProductId(10);
        r5a.setRating(5);
        r5a.setComment("Perfect for hobby projects\n\nGreat little servo motor, works smoothly. Used it for a pan-tilt camera mount. Very responsive and accurate positioning.");
        r5a.setCreatedAt(new Date(now.getTime() - 24 * 60 * 60 * 1000)); // 1 day ago
        r5a.setUpdatedAt(r5a.getCreatedAt());
        reviews.add(r5a);
        
        return reviews;
    }
}
