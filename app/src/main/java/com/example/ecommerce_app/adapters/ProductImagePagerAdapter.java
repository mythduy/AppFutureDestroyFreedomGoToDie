package com.example.ecommerce_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.R;
import com.example.ecommerce_app.utils.ImageHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * ProductImagePagerAdapter - Adapter for ViewPager2 to display product images
 */
public class ProductImagePagerAdapter extends RecyclerView.Adapter<ProductImagePagerAdapter.ImageViewHolder> {
    
    private Context context;
    private long productId;
    private List<String> imageNames;
    
    public ProductImagePagerAdapter(Context context, long productId) {
        this.context = context;
        this.productId = productId;
        this.imageNames = new ArrayList<>();
    }
    
    public void setImageNames(List<String> imageNames) {
        this.imageNames = imageNames != null ? imageNames : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_image_pager, parent, false);
        return new ImageViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageName = imageNames.get(position);
        String imagePath = ImageHelper.getProductImagePath(productId, imageName);
        ImageHelper.loadImageFromAssets(context, imagePath, holder.imageView);
    }
    
    @Override
    public int getItemCount() {
        return imageNames.size();
    }
    
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivProductImage);
        }
    }
}
