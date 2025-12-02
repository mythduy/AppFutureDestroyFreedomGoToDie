package com.example.ecommerce_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce_app.utils.SessionManager;
import com.example.ecommerce_app.viewmodels.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * LoginActivity - Màn hình đăng nhập
 * Design từ Figma: 03 Login (node-id=2-910)
 * 
 * Luồng mới: User có thể xem sản phẩm trước, chỉ cần login khi checkout
 * Sau khi login thành công, quay lại màn hình trước đó (Home/Cart/Checkout)
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnNext;
    private TextView tvCancel, tvForgotPassword;
    
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;
    private String returnTo = "HOME"; // Màn hình cần quay lại sau login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo SessionManager
        sessionManager = new SessionManager(this);
        
        // Lấy thông tin màn hình cần quay lại
        if (getIntent().hasExtra("RETURN_TO")) {
            returnTo = getIntent().getStringExtra("RETURN_TO");
        }

        // Khởi tạo ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Khởi tạo views
        initViews();

        // Setup observers
        setupObservers();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnNext = findViewById(R.id.btnNext);
        tvCancel = findViewById(R.id.tvCancel);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
    }

    private void setupObservers() {
        // Observe login status
        authViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                // Login thành công, lưu session
                Log.d("LoginActivity", "Login successful, user: " + user.getUsername());
                sessionManager.createLoginSession(user.getId(), user.getUsername(), user.getEmail());
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                
                try {
                    // Quay lại màn hình trước đó
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("NAVIGATE_TO", returnTo);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.e("LoginActivity", "Error starting HomeActivity", e);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Observe error messages
        authViewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe loading state
        authViewModel.getIsLoading().observe(this, isLoading -> {
            btnNext.setEnabled(!isLoading);
            if (isLoading) {
                btnNext.setText("Đang xử lý...");
            } else {
                btnNext.setText("Next");
            }
        });
    }

    private void setupClickListeners() {
        // Next button - Login
        btnNext.setOnClickListener(v -> handleLogin());

        // Cancel button - Back to previous screen (usually Home)
        tvCancel.setOnClickListener(v -> {
            finish(); // Quay lại màn hình trước đó
        });

        // Forgot password link
        tvForgotPassword.setOnClickListener(v -> {
            // TODO: Navigate to Forgot Password screen
            Toast.makeText(this, "Chức năng Forgot Password đang được phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        Log.d("LoginActivity", "Attempting login for: " + email);

        // Validate inputs
        if (!validateInputs(email, password)) {
            return;
        }

        // Perform login
        try {
            authViewModel.login(email, password);
        } catch (Exception e) {
            Log.e("LoginActivity", "Error calling login", e);
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateInputs(String email, String password) {
        // Validate email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear observers to prevent memory leaks
        authViewModel.getCurrentUser().removeObservers(this);
        authViewModel.getErrorMessage().removeObservers(this);
        authViewModel.getIsLoading().removeObservers(this);
    }
}
