package com.example.ecommerce_app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.ecommerce_app.data.dao.CategoryDao;
import com.example.ecommerce_app.data.database.AppDatabase;
import com.example.ecommerce_app.data.entities.Category;

import java.util.List;
import java.util.concurrent.Future;

/**
 * CategoryRepository - Repository cho Category entity
 */
public class CategoryRepository {
    
    private CategoryDao categoryDao;
    
    public CategoryRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        categoryDao = database.categoryDao();
    }
    
    // ==================== CRUD OPERATIONS ====================
    
    public LiveData<List<Category>> getAllCategories() {
        return categoryDao.getAllCategories();
    }
    
    public Future<List<Category>> getAllCategoriesSync() {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return categoryDao.getAllCategoriesSync();
        });
    }
    
    public LiveData<Category> getCategoryById(long categoryId) {
        return categoryDao.getCategoryById(categoryId);
    }
    
    public Future<Category> getCategoryByIdSync(long categoryId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return categoryDao.getCategoryByIdSync(categoryId);
        });
    }
    
    public Future<Long> insert(Category category) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return categoryDao.insert(category);
        });
    }
    
    public void update(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            categoryDao.update(category);
        });
    }
    
    public void delete(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            categoryDao.delete(category);
        });
    }
    
    // ==================== SEARCH ====================
    
    public LiveData<List<Category>> searchCategories(String keyword) {
        return categoryDao.searchCategories(keyword);
    }
    
    public Future<Category> findByName(String name) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return categoryDao.findByName(name);
        });
    }
    
    public Future<Integer> getCount() {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            return categoryDao.getCount();
        });
    }
}
