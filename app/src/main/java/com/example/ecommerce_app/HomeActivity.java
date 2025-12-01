package com.example.ecommerce_app;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ecommerce_app.fragments.FavoriteFragment;
import com.example.ecommerce_app.fragments.HomeFragment;
import com.example.ecommerce_app.fragments.MyOrderFragment;
import com.example.ecommerce_app.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * HomeActivity - Main activity with bottom navigation
 */
public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_home);

            bottomNavigationView = findViewById(R.id.bottom_navigation);

            // Set default fragment
            if (savedInstanceState == null) {
                loadFragment(new com.example.ecommerce_app.fragments.HomeFragment());
            }

        // Bottom navigation item selected listener
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    fragment = new com.example.ecommerce_app.fragments.HomeFragment();
                } else if (itemId == R.id.nav_my_order) {
                    fragment = new com.example.ecommerce_app.fragments.MyOrderFragment();
                } else if (itemId == R.id.nav_favorite) {
                    fragment = new com.example.ecommerce_app.fragments.FavoriteFragment();
                } else if (itemId == R.id.nav_profile) {
                    fragment = new com.example.ecommerce_app.fragments.ProfileFragment();
                }

                return loadFragment(fragment);
            }
        });
        } catch (Exception e) {
            android.util.Log.e("HomeActivity", "Error in onCreate", e);
            finish();
        }
    }

    /**
     * Load fragment into container
     */
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
