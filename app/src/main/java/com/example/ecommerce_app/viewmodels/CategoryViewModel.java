package com.example.ecommerce_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce_app.data.entities.Category;
import com.example.ecommerce_app.data.repository.CategoryRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * CategoryViewModel - ViewModel cho quản lý danh mục
 */
public class CategoryViewModel extends AndroidViewModel {
    
    private CategoryRepository categoryRepository;
    
    private LiveData<List<Category>> categories;
    private MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    public CategoryViewModel(@NonNull Application application) {
        super(application);
        categoryRepository = new CategoryRepository(application);
        loadAllCategories();
    }
    
    // ==================== GETTERS ====================
    
    public LiveData<List<Category>> getCategories() {
        return categories;
    }
    
    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    // ==================== LOAD CATEGORIES ====================
    
    /**
     * Load tất cả categories
     */
    public void loadAllCategories() {
        categories = categoryRepository.getAllCategories();
    }
    
    /**
     * Tìm kiếm categories
     */
    public void searchCategories(String query) {
        searchQuery.setValue(query);
        
        if (query == null || query.trim().isEmpty()) {
            loadAllCategories();
        } else {
            categories = categoryRepository.searchCategories(query);
        }
    }
    
    /**
     * Lấy category theo ID
     */
    public LiveData<Category> getCategoryById(long categoryId) {
        return categoryRepository.getCategoryById(categoryId);
    }
    
    /**
     * Lấy category theo ID (sync)
     */
    public void getCategoryByIdSync(long categoryId, OnCategoryCallback callback) {
        try {
            Category category = categoryRepository.getCategoryByIdSync(categoryId).get();
            callback.onResult(category);
        } catch (ExecutionException | InterruptedException e) {
            callback.onResult(null);
        }
    }
    
    /**
     * Tìm category theo tên
     */
    public void findByName(String name, OnCategoryCallback callback) {
        try {
            Category category = categoryRepository.findByName(name).get();
            callback.onResult(category);
        } catch (ExecutionException | InterruptedException e) {
            callback.onResult(null);
        }
    }
    
    // ==================== STATISTICS ====================
    
    /**
     * Đếm số lượng categories
     */
    public void getCategoryCount(OnCountCallback callback) {
        try {
            int count = categoryRepository.getCount().get();
            callback.onResult(count);
        } catch (ExecutionException | InterruptedException e) {
            callback.onResult(0);
        }
    }
    
    // ==================== ADMIN OPERATIONS ====================
    
    /**
     * Thêm category mới (Admin only)
     */
    public void addCategory(String name, String description, String imageFilename) {
        if (name == null || name.trim().isEmpty()) {
            errorMessage.setValue("Vui lòng nhập tên danh mục");
            return;
        }
        
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setImageFilename(imageFilename);
        
        try {
            long id = categoryRepository.insert(category).get();
            if (id > 0) {
                errorMessage.setValue("Thêm danh mục thành công");
                loadAllCategories();
            } else {
                errorMessage.setValue("Lỗi khi thêm danh mục");
            }
        } catch (ExecutionException | InterruptedException e) {
            errorMessage.setValue("Lỗi: " + e.getMessage());
        }
    }
    
    /**
     * Cập nhật category (Admin only)
     */
    public void updateCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            errorMessage.setValue("Vui lòng nhập tên danh mục");
            return;
        }
        
        categoryRepository.update(category);
        errorMessage.setValue("Cập nhật danh mục thành công");
        loadAllCategories();
    }
    
    /**
     * Xóa category (Admin only)
     */
    public void deleteCategory(Category category) {
        categoryRepository.delete(category);
        errorMessage.setValue("Đã xóa danh mục");
        loadAllCategories();
    }
    
    // Callback interfaces
    public interface OnCategoryCallback {
        void onResult(Category category);
    }
    
    public interface OnCountCallback {
        void onResult(int count);
    }
}
