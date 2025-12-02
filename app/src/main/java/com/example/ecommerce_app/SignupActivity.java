package com.example.ecommerce_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce_app.viewmodels.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * SignupActivity - Màn hình đăng ký tài khoản
 * Design từ Figma: 02 Create Account (node-id=4-284)
 */
public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etName, etPhone, etEmail, etPassword;
    private MaterialButton btnDone;
    private TextView tvCancel, tvAlreadyHaveAccount;
    
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnDone = findViewById(R.id.btnDone);
        tvCancel = findViewById(R.id.tvCancel);
        tvAlreadyHaveAccount = findViewById(R.id.tvAlreadyHaveAccount);
    }

    private void setupObservers() {
        // Observe registration status
        authViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                // Đăng ký và login thành công - Lưu session
                com.example.ecommerce_app.utils.SessionManager sessionManager = 
                    new com.example.ecommerce_app.utils.SessionManager(this);
                sessionManager.createLoginSession(user.getId(), user.getUsername(), user.getEmail());
                
                Toast.makeText(this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                
                // Navigate to HomeActivity
                Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Observe error messages
        authViewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });

        // Observe loading state
        authViewModel.getIsLoading().observe(this, isLoading -> {
            btnDone.setEnabled(!isLoading);
            if (isLoading) {
                btnDone.setText("Đang xử lý...");
            } else {
                btnDone.setText("Done");
            }
        });
    }

    private void setupClickListeners() {
        // Done button - Register
        btnDone.setOnClickListener(v -> handleSignup());

        // Cancel button - Back to Welcome
        tvCancel.setOnClickListener(v -> {
            finish();
        });

        // Already have account link - Navigate to Login
        tvAlreadyHaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void handleSignup() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(name, phone, email, password)) {
            return;
        }
        
        // Generate username from name (use name as username)
        // Remove spaces and special characters, convert to lowercase
        String username = name.toLowerCase()
                .replaceAll("\\s+", "")  // Remove spaces
                .replaceAll("[^a-z0-9]", "");  // Remove special characters
        
        // If username is empty after cleaning, use email prefix
        if (username.isEmpty() && !email.isEmpty()) {
            username = email.split("@")[0].replaceAll("[^a-z0-9]", "");
        }
        
        // Perform registration
        // Parameters: username, email, password, fullName, phone
        authViewModel.register(username, email, password, name, phone);
    }

    private boolean validateInputs(String name, String phone, String email, String password) {
        // Validate name
        if (TextUtils.isEmpty(name)) {
            etName.setError("Vui lòng nhập tên");
            etName.requestFocus();
            return false;
        }

        if (name.length() < 2) {
            etName.setError("Tên phải có ít nhất 2 ký tự");
            etName.requestFocus();
            return false;
        }

        // Validate phone
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            etPhone.requestFocus();
            return false;
        }

        if (!phone.matches("^[0-9]{10,11}$")) {
            etPhone.setError("Số điện thoại không hợp lệ (10-11 chữ số)");
            etPhone.requestFocus();
            return false;
        }

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

        if (password.length() < 8) {
            etPassword.setError("Mật khẩu phải có ít nhất 8 ký tự");
            etPassword.requestFocus();
            return false;
        }

        // Check password strength (optional but recommended)
        if (!isStrongPassword(password)) {
            etPassword.setError("Mật khẩu cần có chữ hoa, chữ thường, số và ký tự đặc biệt");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Kiểm tra độ mạnh của mật khẩu
     */
    private boolean isStrongPassword(String password) {
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        // Yêu cầu ít nhất 3 trong 4 điều kiện
        int criteriaMet = 0;
        if (hasUpper) criteriaMet++;
        if (hasLower) criteriaMet++;
        if (hasDigit) criteriaMet++;
        if (hasSpecial) criteriaMet++;

        return criteriaMet >= 3;
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
