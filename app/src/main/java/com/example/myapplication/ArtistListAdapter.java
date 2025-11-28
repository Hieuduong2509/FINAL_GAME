package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class ArtistListAdapter extends RecyclerView.Adapter<ArtistListAdapter.ArtistViewHolder> {

    private final List<Artist> artistList;
    private final Context context;

    public ArtistListAdapter(Context context, List<Artist> artistList) {
        this.context = context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artist_list, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = artistList.get(position);

        holder.tvName.setText(artist.getName());
        holder.tvCategory.setText(artist.getCategory());
        holder.tvEventCount.setText(artist.getUpcomingEvents().size() + " sự kiện sắp tới");

        // Load ảnh đại diện bằng Glide (giả lập URL)
        Glide.with(context)
                .load(artist.getAvatarUrl().isEmpty() ? R.drawable.person_24dp_e3e3e3_fill0_wght400_grad0_opsz24 : artist.getAvatarUrl())
                .placeholder(R.drawable.profile) // Bạn cần có một drawable icon placeholder
                .into(holder.ivAvatar);

        // Xử lý sự kiện click chuyển sang trang chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArtistDetailActivity.class);
            // Truyền đối tượng Artist (phải implement Serializable)
            intent.putExtra("ARTIST_OBJECT", artist);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivAvatar;
        TextView tvName, tvCategory, tvEventCount;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_artist_list_avatar);
            tvName = itemView.findViewById(R.id.tv_artist_list_name);
            tvCategory = itemView.findViewById(R.id.tv_artist_list_category);
            tvEventCount = itemView.findViewById(R.id.tv_artist_list_event_count);
        }
    }
}