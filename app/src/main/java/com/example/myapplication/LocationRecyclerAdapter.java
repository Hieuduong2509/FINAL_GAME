package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

// Tên class này PHẢI khớp với tên file: LocationRecyclerAdapter.java
public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.LocationViewHolder> {

    private final Context context;
    private final ArrayList<Location> locationList;
    private final LayoutInflater inflater;
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

    // Interface (Giữ nguyên)
    public interface OnShowtimeClickListener {
        void onShowtimeClick(Location location, Date time);
    }
    private OnShowtimeClickListener showtimeClickListener;

    public void setOnShowtimeClickListener(OnShowtimeClickListener listener) {
        this.showtimeClickListener = listener;
    }

    // Constructor (Giữ nguyên)
    public LocationRecyclerAdapter(Context context, ArrayList<Location> locationList) {
        this.context = context;
        this.locationList = locationList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 🎨 SỬA LỖI: Tên file layout phải là "item_location.xml"
        View view = inflater.inflate(R.layout.location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location location = locationList.get(position);

        // Gán dữ liệu (Giữ nguyên)
        holder.tvLocationName.setText(location.getName());
        holder.tvLocationAddress.setText(location.getAddress());

        // Xử lý logic ẩn/hiện (Giữ nguyên)
        holder.clickableArea.setOnClickListener(v -> {
            boolean isVisible = holder.expandableLayout.getVisibility() == View.VISIBLE;
            if (isVisible) {
                holder.expandableLayout.setVisibility(View.GONE);
                holder.ivExpandArrow.setRotation(0);
            } else {
                holder.expandableLayout.setVisibility(View.VISIBLE);
                holder.ivExpandArrow.setRotation(180);

                if (holder.showtimesContainer.getChildCount() == 0) {
                    addShowtimeButtons(holder.showtimesContainer, location);
                }
            }
        });
    }

    // Hàm thêm các nút giờ chiếu (Giữ nguyên)
    private void addShowtimeButtons(LinearLayout container, Location location) {
        for (Date time : location.getTimes()) {
            MaterialButton button = (MaterialButton) inflater.inflate(R.layout.item_showtime, container, false);
            button.setText(timeFormatter.format(time));
            button.setOnClickListener(v -> {
                if (showtimeClickListener != null) {
                    showtimeClickListener.onShowtimeClick(location, time);
                }
            });
            container.addView(button);
        }
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    // ViewHolder (Giữ nguyên)
    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        View clickableArea;
        TextView tvLocationName, tvLocationAddress;
        ImageView ivExpandArrow;
        LinearLayout expandableLayout, showtimesContainer;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            clickableArea = itemView.findViewById(R.id.clickable_area);
            tvLocationName = itemView.findViewById(R.id.tv_location_name);
            tvLocationAddress = itemView.findViewById(R.id.tv_location_address);
            ivExpandArrow = itemView.findViewById(R.id.iv_expand_arrow);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            showtimesContainer = itemView.findViewById(R.id.showtimes_container);
        }
    }
}