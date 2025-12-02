package com.example.ecommerce_app.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce_app.R;
import com.example.ecommerce_app.data.database.AppDatabase;
import com.example.ecommerce_app.data.entities.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * EditProfileActivity - Edit user profile information
 */
public class EditProfileActivity extends AppCompatActivity {

    private ImageView backButton;
    private ImageView profileImage;
    private MaterialCardView editPhotoButton;
    private TextInputLayout usernameLayout;
    private TextInputEditText usernameInput;
    private TextInputLayout emailLayout;
    private TextInputEditText emailInput;
    private TextView googleStatusText;
    private MaterialButton saveButton;

    private AppDatabase database;
    private ExecutorService executorService;
    private User currentUser;
    private Uri selectedImageUri;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initializeViews();
        initializeDatabase();
        setupImagePicker();
        setupClickListeners();
        loadUserData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        profileImage = findViewById(R.id.profileImage);
        editPhotoButton = findViewById(R.id.editPhotoButton);
        usernameLayout = findViewById(R.id.usernameLayout);
        usernameInput = findViewById(R.id.usernameInput);
        emailLayout = findViewById(R.id.emailLayout);
        emailInput = findViewById(R.id.emailInput);
        googleStatusText = findViewById(R.id.googleStatusText);
        saveButton = findViewById(R.id.saveButton);
    }

    private void initializeDatabase() {
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        profileImage.setImageURI(selectedImageUri);
                    }
                }
            }
        );
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        
        editPhotoButton.setOnClickListener(v -> showPhotoOptionsDialog());
        
        findViewById(R.id.googleAccountItem).setOnClickListener(v -> {
            Toast.makeText(this, "Google Account integration coming soon", Toast.LENGTH_SHORT).show();
        });
        
        saveButton.setOnClickListener(v -> validateAndSaveProfile());
    }

    private void loadUserData() {
        long userId = getIntent().getLongExtra("USER_ID", 0);
        
        if (userId == 0) {
            // No user ID provided, create demo user
            executorService.execute(() -> {
                User user = database.userDao().getAllUsersSync().stream().findFirst().orElse(null);
                
                if (user == null) {
                    // Create default user
                    user = new User();
                    user.setUsername("Magdalena Succrose");
                    user.setEmail("magdalena83@gmail.com");
                    user.setFullName("Magdalena Succrose");
                }
                
                currentUser = user;
                
                runOnUiThread(() -> updateUI());
            });
        } else {
            executorService.execute(() -> {
                currentUser = database.userDao().getUserByIdSync(userId);
                runOnUiThread(() -> updateUI());
            });
        }
    }

    private void updateUI() {
        if (currentUser != null) {
            usernameInput.setText(currentUser.getUsername());
            emailInput.setText(currentUser.getEmail());
            // TODO: Load profile image if available
        }
    }

    private void showPhotoOptionsDialog() {
        String[] options = {
            getString(R.string.select_photo),
            getString(R.string.take_photo),
            getString(R.string.remove_photo)
        };

        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.edit_photo))
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0: // Select from gallery
                        openImagePicker();
                        break;
                    case 1: // Take photo
                        Toast.makeText(this, "Camera feature coming soon", Toast.LENGTH_SHORT).show();
                        break;
                    case 2: // Remove photo
                        profileImage.setImageResource(R.drawable.ic_user);
                        selectedImageUri = null;
                        break;
                }
            })
            .setNegativeButton(R.string.cancel, null)
            .show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void validateAndSaveProfile() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();

        // Clear previous errors
        usernameLayout.setError(null);
        emailLayout.setError(null);

        boolean isValid = true;

        // Validate username
        if (TextUtils.isEmpty(username)) {
            usernameLayout.setError(getString(R.string.invalid_username));
            isValid = false;
        }

        // Validate email
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError(getString(R.string.invalid_email));
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Save profile
        saveProfile(username, email);
    }

    private void saveProfile(String username, String email) {
        saveButton.setEnabled(false);
        
        executorService.execute(() -> {
            try {
                if (currentUser != null) {
                    // Update existing user
                    currentUser.setUsername(username);
                    currentUser.setEmail(email);
                    currentUser.setUpdatedAt(new Date());
                    
                    database.userDao().update(currentUser);
                } else {
                    // Create new user
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setEmail(email);
                    newUser.setFullName(username);
                    newUser.setPasswordHash(""); // Set default or hash
                    
                    long userId = database.userDao().insert(newUser);
                    newUser.setId(userId);
                    currentUser = newUser;
                }
                
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.profile_update_failed, Toast.LENGTH_SHORT).show();
                    saveButton.setEnabled(true);
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
