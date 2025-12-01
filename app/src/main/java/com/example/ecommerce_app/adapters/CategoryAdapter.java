package com.example.ecommerce_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.R;
import com.example.ecommerce_app.data.entities.Category;
import com.example.ecommerce_app.utils.ImageHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * CategoryAdapter - Adapter for displaying categories in RecyclerView
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categories;
    private List<Integer> productCounts;
    private OnCategoryClickListener listener;

    public CategoryAdapter(Context context) {
        this.context = context;
        this.categories = new ArrayList<>();
        this.productCounts = new ArrayList<>();
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories != null ? categories : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setProductCounts(List<Integer> counts) {
        this.productCounts = counts != null ? counts : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        int productCount = position < productCounts.size() ? productCounts.get(position) : 0;
        holder.bind(category, productCount);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryName, categoryCount;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.category_image);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryCount = itemView.findViewById(R.id.category_count);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onCategoryClick(categories.get(position));
                    }
                }
            });
        }

        public void bind(Category category, int productCount) {
            categoryName.setText(category.getName());
            categoryCount.setText(context.getString(R.string.products_count, productCount));

            // Load category image
            if (category.getImageFilename() != null && !category.getImageFilename().isEmpty()) {
                String imagePath = "images/categories/" + category.getImageFilename();
                ImageHelper.loadImageFromAssets(context, imagePath, categoryImage);
            } else {
                categoryImage.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
}
