package com.example.ecommerce_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * ProfileFragment - User profile screen
 */
public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(requireContext());
        textView.setText("Profile Fragment - Coming Soon");
        textView.setTextSize(20);
        textView.setGravity(android.view.Gravity.CENTER);
        return textView;
    }
}
