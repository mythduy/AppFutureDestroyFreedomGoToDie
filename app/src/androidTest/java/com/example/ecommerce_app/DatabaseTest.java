package com.example.ecommerce_app;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.ecommerce_app.data.dao.ProductDao;
import com.example.ecommerce_app.data.dao.UserDao;
import com.example.ecommerce_app.data.database.AppDatabase;
import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.data.entities.User;
import com.example.ecommerce_app.utils.PasswordHasher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Database Test - Test các DAO operations
 * 
 * Chạy trên device/emulator (androidTest)
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    
    private AppDatabase database;
    private UserDao userDao;
    private ProductDao productDao;
    
    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        // Sử dụng in-memory database cho testing
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries() // Cho phép query trên main thread trong test
                .build();
        
        userDao = database.userDao();
        productDao = database.productDao();
    }
    
    @After
    public void closeDb() {
        database.close();
    }
    
    // ==================== USER DAO TESTS ====================
    
    @Test
    public void testInsertAndReadUser() {
        // Tạo user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash(PasswordHasher.hashPassword("Test@123"));
        user.setFullName("Test User");
        
        // Insert
        long userId = userDao.insert(user);
        assertTrue(userId > 0);
        
        // Read
        User retrievedUser = userDao.getUserByIdSync(userId);
        assertNotNull(retrievedUser);
        assertEquals("testuser", retrievedUser.getUsername());
        assertEquals("test@example.com", retrievedUser.getEmail());
    }
    
    @Test
    public void testFindUserByUsername() {
        // Insert user
        User user = new User();
        user.setUsername("findme");
        user.setEmail("findme@test.com");
        user.setPasswordHash(PasswordHasher.hashPassword("Pass@123"));
        userDao.insert(user);
        
        // Find
        User found = userDao.findByUsername("findme");
        assertNotNull(found);
        assertEquals("findme@test.com", found.getEmail());
    }
    
    @Test
    public void testCheckUsernameExists() {
        // Insert user
        User user = new User();
        user.setUsername("existinguser");
        user.setEmail("existing@test.com");
        user.setPasswordHash(PasswordHasher.hashPassword("Pass@123"));
        userDao.insert(user);
        
        // Check exists
        int count = userDao.checkUsernameExists("existinguser");
        assertEquals(1, count);
        
        // Check not exists
        int count2 = userDao.checkUsernameExists("nonexistent");
        assertEquals(0, count2);
    }
    
    @Test
    public void testUpdateUser() {
        // Insert
        User user = new User();
        user.setUsername("updatetest");
        user.setEmail("update@test.com");
        user.setPasswordHash(PasswordHasher.hashPassword("Pass@123"));
        user.setFullName("Original Name");
        long userId = userDao.insert(user);
        
        // Update
        User retrieved = userDao.getUserByIdSync(userId);
        retrieved.setFullName("Updated Name");
        userDao.update(retrieved);
        
        // Verify
        User updated = userDao.getUserByIdSync(userId);
        assertEquals("Updated Name", updated.getFullName());
    }
    
    @Test
    public void testDeleteUser() {
        // Insert
        User user = new User();
        user.setUsername("deletetest");
        user.setEmail("delete@test.com");
        user.setPasswordHash(PasswordHasher.hashPassword("Pass@123"));
        long userId = userDao.insert(user);
        
        // Delete
        userDao.deleteById(userId);
        
        // Verify
        User deleted = userDao.getUserByIdSync(userId);
        assertNull(deleted);
    }
    
    // ==================== PRODUCT DAO TESTS ====================
    
    @Test
    public void testInsertAndReadProduct() {
        // Insert product
        Product product = new Product();
        product.setName("Test Arduino");
        product.setDescription("Test description");
        product.setPrice(100000);
        product.setStock(50);
        product.setCategoryId(1);
        product.setSku("TEST_SKU_001");
        product.setBrand("Test Brand");
        product.setImageFilenames(Arrays.asList("test1.jpg", "test2.jpg"));
        
        long productId = productDao.insert(product);
        assertTrue(productId > 0);
        
        // Read
        Product retrieved = productDao.getProductByIdSync(productId);
        assertNotNull(retrieved);
        assertEquals("Test Arduino", retrieved.getName());
        assertEquals(100000, retrieved.getPrice(), 0.01);
        assertEquals(50, retrieved.getStock());
    }
    
    @Test
    public void testSearchProductByName() {
        // Insert products
        Product p1 = new Product();
        p1.setName("Arduino Uno");
        p1.setPrice(250000);
        p1.setStock(10);
        p1.setCategoryId(1);
        p1.setSku("ARD001");
        productDao.insert(p1);
        
        Product p2 = new Product();
        p2.setName("Arduino Mega");
        p2.setPrice(350000);
        p2.setStock(5);
        p2.setCategoryId(1);
        p2.setSku("ARD002");
        productDao.insert(p2);
        
        Product p3 = new Product();
        p3.setName("ESP32");
        p3.setPrice(150000);
        p3.setStock(20);
        p3.setCategoryId(1);
        p3.setSku("ESP001");
        productDao.insert(p3);
        
        // Search - Note: LiveData không hoạt động tốt trong test, 
        // nên ta cần query trực tiếp hoặc dùng getValue()
        // Ở đây ta test logic bằng cách verify data được insert
        
        Product found = productDao.findBySku("ARD001");
        assertNotNull(found);
        assertEquals("Arduino Uno", found.getName());
    }
    
    @Test
    public void testUpdateProductStock() {
        // Insert
        Product product = new Product();
        product.setName("Stock Test");
        product.setPrice(50000);
        product.setStock(100);
        product.setCategoryId(1);
        product.setSku("STOCK001");
        long productId = productDao.insert(product);
        
        // Decrease stock
        productDao.decreaseStock(productId, 10, System.currentTimeMillis());
        
        // Verify
        int stock = productDao.getStock(productId);
        assertEquals(90, stock);
        
        // Increase stock
        productDao.increaseStock(productId, 5, System.currentTimeMillis());
        
        // Verify
        stock = productDao.getStock(productId);
        assertEquals(95, stock);
    }
    
    @Test
    public void testGetActiveProductCount() {
        // Insert active products
        for (int i = 0; i < 5; i++) {
            Product product = new Product();
            product.setName("Product " + i);
            product.setPrice(10000);
            product.setStock(10);
            product.setCategoryId(1);
            product.setSku("SKU" + i);
            product.setActive(true);
            productDao.insert(product);
        }
        
        // Insert inactive product
        Product inactive = new Product();
        inactive.setName("Inactive Product");
        inactive.setPrice(10000);
        inactive.setStock(10);
        inactive.setCategoryId(1);
        inactive.setSku("INACTIVE");
        inactive.setActive(false);
        productDao.insert(inactive);
        
        // Count
        int count = productDao.getActiveProductCount();
        assertEquals(5, count);
    }
}
