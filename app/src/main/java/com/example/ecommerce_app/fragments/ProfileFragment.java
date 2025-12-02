package com.example.ecommerce_app.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ecommerce_app.R;
import com.example.ecommerce_app.activities.EditProfileActivity;
import com.example.ecommerce_app.activities.ChangePasswordActivity;
import com.example.ecommerce_app.data.database.AppDatabase;
import com.example.ecommerce_app.data.entities.User;
import com.example.ecommerce_app.utils.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ProfileFragment - User profile and settings screen
 * Yêu cầu login trước khi truy cập
 */
public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView usernameText;
    private TextView emailText;
    private SessionManager sessionManager;
    
    private LinearLayout editProfileItem;
    private LinearLayout changePasswordItem;
    private LinearLayout notificationsItem;
    private LinearLayout securityItem;
    private LinearLayout languageItem;
    private LinearLayout legalItem;
    private LinearLayout helpItem;
    private LinearLayout logoutItem;
    
    private AppDatabase database;
    private ExecutorService executorService;
    private User currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        // Khởi tạo SessionManager
        sessionManager = new SessionManager(requireContext());
        
        // Kiểm tra login - yêu cầu đăng nhập để xem profile
        if (!sessionManager.checkLoginRequired(requireContext(), "PROFILE")) {
            // Chưa login, đã chuyển sang LoginActivity
            return view;
        }
        
        initializeViews(view);
        initializeDatabase();
        setupClickListeners();
        loadUserProfile();
        
        return view;
    }
    
    private void initializeViews(View view) {
        profileImage = view.findViewById(R.id.profileImage);
        usernameText = view.findViewById(R.id.usernameText);
        emailText = view.findViewById(R.id.emailText);
        
        editProfileItem = view.findViewById(R.id.editProfileItem);
        changePasswordItem = view.findViewById(R.id.changePasswordItem);
        notificationsItem = view.findViewById(R.id.notificationsItem);
        securityItem = view.findViewById(R.id.securityItem);
        languageItem = view.findViewById(R.id.languageItem);
        legalItem = view.findViewById(R.id.legalItem);
        helpItem = view.findViewById(R.id.helpItem);
        logoutItem = view.findViewById(R.id.logoutItem);
    }
    
    private void initializeDatabase() {
        database = AppDatabase.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();
    }
    
    private void setupClickListeners() {
        editProfileItem.setOnClickListener(v -> navigateToEditProfile());
        changePasswordItem.setOnClickListener(v -> navigateToChangePassword());
        notificationsItem.setOnClickListener(v -> showComingSoon("Notifications"));
        securityItem.setOnClickListener(v -> showComingSoon("Security"));
        languageItem.setOnClickListener(v -> showComingSoon("Language"));
        legalItem.setOnClickListener(v -> showComingSoon("Legal & Policies"));
        helpItem.setOnClickListener(v -> showComingSoon("Help & Support"));
        logoutItem.setOnClickListener(v -> showLogoutDialog());
    }
    
    private void loadUserProfile() {
        executorService.execute(() -> {
            // Lấy user từ session
            long userId = sessionManager.getUserId();
            User user = null;
            
            if (userId > 0) {
                user = database.userDao().getUserByIdSync(userId);
            }
            
            if (user == null) {
                // Nếu không tìm thấy, lấy user đầu tiên hoặc tạo demo user
                user = database.userDao().getAllUsersSync().stream().findFirst().orElse(null);
                
                if (user == null) {
                    // Tạo default demo user
                    user = new User();
                    user.setUsername(sessionManager.getUsername());
                    user.setEmail(sessionManager.getEmail());
                    user.setFullName(sessionManager.getUsername());
                }
            }
            
            currentUser = user;
            
            requireActivity().runOnUiThread(() -> {
                if (currentUser != null) {
                    usernameText.setText(currentUser.getUsername());
                    emailText.setText(currentUser.getEmail());
                    // TODO: Load profile image if available
                }
            });
        });
    }
    
    private void navigateToEditProfile() {
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        if (currentUser != null) {
            intent.putExtra("USER_ID", currentUser.getId());
        }
        startActivity(intent);
    }
    
    private void navigateToChangePassword() {
        Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
        if (currentUser != null) {
            intent.putExtra("USER_ID", currentUser.getId());
        }
        startActivity(intent);
    }
    
    private void showComingSoon(String feature) {
        Toast.makeText(requireContext(), feature + " - Coming Soon", Toast.LENGTH_SHORT).show();
    }
    
    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
            .setTitle(R.string.logout_title)
            .setMessage(R.string.logout_message)
            .setPositiveButton(R.string.yes, (dialog, which) -> performLogout())
            .setNegativeButton(R.string.no, null)
            .show();
    }
    
    private void performLogout() {
        // Xóa session
        sessionManager.logout();
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        
        // Quay về WelcomeActivity
        Intent intent = new Intent(requireContext(), com.example.ecommerce_app.WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        
        // Đóng HomeActivity
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Reload profile when returning from edit screen
        loadUserProfile();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
