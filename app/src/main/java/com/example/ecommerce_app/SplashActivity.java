package com.example.ecommerce_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce_app.utils.SessionManager;

/**
 * SplashActivity - Màn hình splash
 * Luồng: 
 * - Nếu đã login: Splash → Home
 * - Nếu chưa login: Splash → Welcome (chọn Sign Up / Login / Continue as Guest)
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);

        // Sau 2 giây, kiểm tra login status
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                
                if (sessionManager.isLoggedIn()) {
                    // Đã login, vào Home
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                } else {
                    // Chưa login, vào Welcome để chọn Sign Up/Login/Guest
                    intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                }
                
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DELAY);
    }
}
