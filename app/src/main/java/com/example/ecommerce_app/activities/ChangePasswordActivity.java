package com.example.ecommerce_app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce_app.R;
import com.example.ecommerce_app.data.database.AppDatabase;
import com.example.ecommerce_app.data.entities.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * ChangePasswordActivity - Change user password with validation and hashing
 */
public class ChangePasswordActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextInputLayout currentPasswordLayout;
    private TextInputEditText currentPasswordInput;
    private TextInputLayout newPasswordLayout;
    private TextInputEditText newPasswordInput;
    private TextInputLayout confirmPasswordLayout;
    private TextInputEditText confirmPasswordInput;
    private MaterialButton changePasswordButton;

    private AppDatabase database;
    private ExecutorService executorService;
    private User currentUser;

    // Password validation patterns
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initializeViews();
        initializeDatabase();
        setupClickListeners();
        loadUserData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        currentPasswordLayout = findViewById(R.id.currentPasswordLayout);
        currentPasswordInput = findViewById(R.id.currentPasswordInput);
        newPasswordLayout = findViewById(R.id.newPasswordLayout);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        changePasswordButton = findViewById(R.id.changePasswordButton);
    }

    private void initializeDatabase() {
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        changePasswordButton.setOnClickListener(v -> validateAndChangePassword());
    }

    private void loadUserData() {
        long userId = getIntent().getLongExtra("USER_ID", 0);

        if (userId == 0) {
            // No user ID provided, load first user
            executorService.execute(() -> {
                User user = database.userDao().getAllUsersSync().stream().findFirst().orElse(null);
                currentUser = user;
            });
        } else {
            executorService.execute(() -> {
                currentUser = database.userDao().getUserByIdSync(userId);
            });
        }
    }

    private void validateAndChangePassword() {
        String currentPassword = currentPasswordInput.getText().toString();
        String newPassword = newPasswordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        // Clear previous errors
        currentPasswordLayout.setError(null);
        newPasswordLayout.setError(null);
        confirmPasswordLayout.setError(null);

        boolean isValid = true;

        // Check if all fields are filled
        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, R.string.empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate new password length
        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            newPasswordLayout.setError(getString(R.string.password_too_short));
            isValid = false;
        }

        // Validate password strength
        if (!isPasswordStrong(newPassword)) {
            newPasswordLayout.setError(getString(R.string.password_weak));
            isValid = false;
        }

        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordLayout.setError(getString(R.string.passwords_not_match));
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Verify current password and change
        changePassword(currentPassword, newPassword);
    }

    private boolean isPasswordStrong(String password) {
        return UPPERCASE_PATTERN.matcher(password).find() &&
               LOWERCASE_PATTERN.matcher(password).find() &&
               NUMBER_PATTERN.matcher(password).find();
    }

    private void changePassword(String currentPassword, String newPassword) {
        changePasswordButton.setEnabled(false);

        executorService.execute(() -> {
            try {
                if (currentUser == null) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                        changePasswordButton.setEnabled(true);
                    });
                    return;
                }

                // Verify current password
                if (!TextUtils.isEmpty(currentUser.getPasswordHash())) {
                    BCrypt.Result result = BCrypt.verifyer().verify(currentPassword.toCharArray(), currentUser.getPasswordHash());
                    if (!result.verified) {
                        runOnUiThread(() -> {
                            currentPasswordLayout.setError(getString(R.string.invalid_current_password));
                            changePasswordButton.setEnabled(true);
                        });
                        return;
                    }
                }

                // Hash new password
                String hashedPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());

                // Update password in database
                database.userDao().updatePassword(
                    currentUser.getId(),
                    hashedPassword,
                    new Date().getTime()
                );

                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.password_changed, Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.password_change_failed, Toast.LENGTH_SHORT).show();
                    changePasswordButton.setEnabled(true);
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
