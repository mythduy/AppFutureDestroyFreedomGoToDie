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
import com.example.ecommerce_app.data.entities.Product;
import com.example.ecommerce_app.utils.ImageHelper;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * FavoriteProductAdapter - Adapter for displaying favorite products in a grid
 */
public class FavoriteProductAdapter extends RecyclerView.Adapter<FavoriteProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> products = new ArrayList<>();
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onFavoriteClick(Product product);
    }

    public FavoriteProductAdapter(Context context) {
        this.context = context;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void removeProduct(Product product) {
        int position = products.indexOf(product);
        if (position != -1) {
            products.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_favorite, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        // Set product name
        holder.tvProductName.setText(product.getName());

        // Set brand (from description or brand field)
        if (product.getBrand() != null && !product.getBrand().isEmpty()) {
            holder.tvProductBrand.setText(product.getBrand());
            holder.tvProductBrand.setVisibility(View.VISIBLE);
        } else {
            holder.tvProductBrand.setVisibility(View.GONE);
        }

        // Set price
        holder.tvProductPrice.setText(String.format(Locale.getDefault(), "$%.2f", product.getPrice()));

        // Load product image theo cấu trúc MỚI: products/product_{id}/main.jpg
        String imagePath = ImageHelper.getProductMainImagePath(product.getId());
        ImageHelper.loadImageFromAssets(context, imagePath, holder.ivProductImage);

        // Set favorite icon (always filled in favorite list)
        holder.ivFavorite.setImageResource(R.drawable.ic_favorite_filled);
        holder.ivFavorite.setColorFilter(context.getResources().getColor(R.color.status_cancelled, null));

        // Click listeners
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });

        holder.ivFavorite.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFavoriteClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView ivProductImage;
        ImageView ivFavorite;
        TextView tvProductName;
        TextView tvProductBrand;
        TextView tvProductPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            ivFavorite = itemView.findViewById(R.id.iv_favorite);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductBrand = itemView.findViewById(R.id.tv_product_brand);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
        }
    }
}
