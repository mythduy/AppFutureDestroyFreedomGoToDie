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

import java.util.ArrayList;
import java.util.List;

/**
 * BannerAdapter - Adapter for banner ViewPager2
 */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private Context context;
    private List<BannerItem> banners;

    public BannerAdapter(Context context) {
        this.context = context;
        this.banners = new ArrayList<>();
        initializeBanners();
    }

    private void initializeBanners() {
        banners.add(new BannerItem(
            "24% off shipping today\non bag purchases",
            "By Kutuku Store",
            R.drawable.ic_launcher_background
        ));
        banners.add(new BannerItem(
            "New Collection Available",
            "Shop Now",
            R.drawable.ic_launcher_background
        ));
        banners.add(new BannerItem(
            "Special Offers This Week",
            "Limited Time Only",
            R.drawable.ic_launcher_background
        ));
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        BannerItem banner = banners.get(position);
        holder.bind(banner);
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;
        TextView bannerTitle, bannerSubtitle;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.banner_image);
            bannerTitle = itemView.findViewById(R.id.banner_title);
            bannerSubtitle = itemView.findViewById(R.id.banner_subtitle);
        }

        public void bind(BannerItem banner) {
            bannerTitle.setText(banner.title);
            bannerSubtitle.setText(banner.subtitle);
            bannerImage.setImageResource(banner.imageRes);
        }
    }

    public static class BannerItem {
        String title;
        String subtitle;
        int imageRes;

        public BannerItem(String title, String subtitle, int imageRes) {
            this.title = title;
            this.subtitle = subtitle;
            this.imageRes = imageRes;
        }
    }
}
