package com.example.ecommerce_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

/**
 * WelcomeActivity - Màn hình chào mừng
 * Design từ Figma: node-id=32-7803
 * - Let's get started (Đăng ký)
 * - I already have an account (Đăng nhập)
 */
public class WelcomeActivity extends AppCompatActivity {

    private MaterialButton btnGetStarted;
    private LinearLayout layoutSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        btnGetStarted = findViewById(R.id.btnGetStarted);
        layoutSignIn = findViewById(R.id.layoutSignIn);
    }

    private void setupClickListeners() {
        // Let's get started button -> Sign Up
        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // I already have an account -> Login
        layoutSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
