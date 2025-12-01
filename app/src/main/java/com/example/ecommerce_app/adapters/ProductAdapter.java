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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ProductAdapter - Adapter for displaying products in RecyclerView
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> products;
    private OnProductClickListener listener;

    public ProductAdapter(Context context) {
        this.context = context;
        this.products = new ArrayList<>();
    }

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage, btnFavorite;
        TextView productName, productBrand, productPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
            productName = itemView.findViewById(R.id.product_name);
            productBrand = itemView.findViewById(R.id.product_brand);
            productPrice = itemView.findViewById(R.id.product_price);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onProductClick(products.get(position));
                    }
                }
            });

            btnFavorite.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onFavoriteClick(products.get(position));
                    }
                }
            });
        }

        public void bind(Product product) {
            productName.setText(product.getName());
            productBrand.setText(product.getBrand() != null ? product.getBrand() : "");
            productPrice.setText(String.format(Locale.US, "$%.2f", product.getPrice()));

            // Load product image
            if (product.getImageFilenames() != null && !product.getImageFilenames().isEmpty()) {
                String imagePath = "images/products/" + product.getImageFilenames().get(0);
                ImageHelper.loadImageFromAssets(context, imagePath, productImage);
            } else {
                productImage.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onFavoriteClick(Product product);
    }
}
