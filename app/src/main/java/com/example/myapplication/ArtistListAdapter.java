package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.Artist;
import com.example.myapplication.Network.ApiClient;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class ArtistListAdapter extends RecyclerView.Adapter<ArtistListAdapter.ArtistViewHolder> {

    private final Context context;
    private List<Artist> originalList;
    private List<Artist> displayList;

    public ArtistListAdapter(Context context, List<Artist> artistList) {
        this.context = context;
        this.originalList = artistList;
        this.displayList = new ArrayList<>(artistList);
    }
    public void filter(String query) {
        displayList.clear();
        if (query == null || query.isEmpty()) {
            displayList.addAll(originalList);
        } else {
            String filterPattern = query.toLowerCase().trim();
            for (Artist item : originalList) {
                if (item.getName() != null && item.getName().toLowerCase().contains(filterPattern)) {
                    displayList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artist_list, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = displayList.get(position);
        holder.tvName.setText(artist.getName());
        holder.tvCategory.setText(artist.getFollower() + " FOLLOWERS");
        int eventCount = (artist.getUpcomingEvents() != null) ? artist.getUpcomingEvents().size() : 0;
        holder.tvEventCount.setText(eventCount + " UPCOMING EVENTS ");
        String imageUrl = artist.getAvatarUrl();
        if (imageUrl != null && !imageUrl.startsWith("http")) {
            imageUrl = ApiClient.BASE_URL + imageUrl;
        }

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .into(holder.ivAvatar);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArtistDetailActivity.class);
            // Đảm bảo Model Artist của bạn đã implements Serializable
            intent.putExtra("ARTIST_OBJECT", artist);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivAvatar;
        TextView tvName, tvCategory, tvEventCount;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ đúng các ID trong layout item_artist_list.xml của bạn
            ivAvatar = itemView.findViewById(R.id.iv_artist_list_avatar);
            tvName = itemView.findViewById(R.id.tv_artist_list_name);
            tvCategory = itemView.findViewById(R.id.tv_artist_list_category);
            tvEventCount = itemView.findViewById(R.id.tv_artist_list_event_count);
        }
    }
}