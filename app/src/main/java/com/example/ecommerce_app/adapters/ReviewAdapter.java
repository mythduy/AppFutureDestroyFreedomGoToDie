package com.example.ecommerce_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.R;
import com.example.ecommerce_app.data.models.ReviewWithUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ReviewAdapter - Adapter cho RecyclerView hiển thị reviews
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<ReviewWithUser> reviews = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    public void setReviews(List<ReviewWithUser> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewWithUser reviewWithUser = reviews.get(position);
        holder.bind(reviewWithUser);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        
        TextView tvInitials, tvUserName, tvDateTime, tvRating, tvComment;
        LinearLayout layoutStars;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitials = itemView.findViewById(R.id.tvInitials);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvComment = itemView.findViewById(R.id.tvComment);
            layoutStars = itemView.findViewById(R.id.layoutStars);
        }

        public void bind(ReviewWithUser reviewWithUser) {
            // Set user name
            String fullName = reviewWithUser.user != null ? reviewWithUser.user.getFullName() : "Anonymous";
            tvUserName.setText(fullName);
            
            // Set initials (first letters of name)
            String initials = getInitials(fullName);
            tvInitials.setText(initials);
            
            // Set date
            if (reviewWithUser.review.getCreatedAt() != null) {
                tvDateTime.setText(dateFormat.format(reviewWithUser.review.getCreatedAt()));
            }
            
            // Set rating
            int rating = reviewWithUser.review.getRating();
            tvRating.setText(String.format(Locale.US, "%.1f", (double) rating));
            
            // Set comment
            tvComment.setText(reviewWithUser.review.getComment());
            
            // Display stars
            displayStars(rating);
        }
        
        /**
         * Get initials from full name
         */
        private String getInitials(String fullName) {
            if (fullName == null || fullName.isEmpty()) {
                return "??";
            }
            
            String[] parts = fullName.trim().split("\\s+");
            if (parts.length == 1) {
                return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
            } else {
                return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
            }
        }
        
        /**
         * Display star rating
         */
        private void displayStars(int rating) {
            layoutStars.removeAllViews();
            
            for (int i = 1; i <= 5; i++) {
                ImageView star = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpToPx(13), dpToPx(13)
                );
                if (i > 1) {
                    params.leftMargin = dpToPx(2);
                }
                star.setLayoutParams(params);
                
                if (i <= rating) {
                    star.setImageResource(R.drawable.ic_star_filled);
                } else {
                    star.setImageResource(R.drawable.ic_star_outline);
                }
                
                layoutStars.addView(star);
            }
        }
        
        private int dpToPx(int dp) {
            float density = context.getResources().getDisplayMetrics().density;
            return Math.round((float) dp * density);
        }
    }
}
