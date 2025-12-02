package com.example.ecommerce_app;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ecommerce_app.fragments.CartFragment;
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

            // Check if need to navigate to specific fragment after login
            String navigateTo = getIntent().getStringExtra("NAVIGATE_TO");
            
            // Set default fragment
            if (savedInstanceState == null) {
                Fragment startFragment = new com.example.ecommerce_app.fragments.HomeFragment();
                int selectedItemId = R.id.nav_home;
                
                // Navigate to specific fragment if returned from login
                if (navigateTo != null) {
                    switch (navigateTo) {
                        case "ORDER":
                            startFragment = new com.example.ecommerce_app.fragments.MyOrderFragment();
                            selectedItemId = R.id.nav_my_order;
                            break;
                        case "FAVORITE":
                            startFragment = new com.example.ecommerce_app.fragments.FavoriteFragment();
                            selectedItemId = R.id.nav_favorite;
                            break;
                        case "PROFILE":
                            startFragment = new com.example.ecommerce_app.fragments.ProfileFragment();
                            selectedItemId = R.id.nav_profile;
                            break;
                        default:
                            // HOME or unknown - use default
                            break;
                    }
                }
                
                loadFragment(startFragment);
                bottomNavigationView.setSelectedItemId(selectedItemId);
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
                } else if (itemId == R.id.nav_cart) {
                    fragment = new com.example.ecommerce_app.fragments.CartFragment();
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
