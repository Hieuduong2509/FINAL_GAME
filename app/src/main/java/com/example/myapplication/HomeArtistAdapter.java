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

import java.util.List;

public class HomeArtistAdapter extends RecyclerView.Adapter<HomeArtistAdapter.ViewHolder> {
    private final Context context;
    private final List<Artist> artistList;
    private final OnArtistItemClickListener listener;
    public interface OnArtistItemClickListener {
        void onArtistClick(Artist artist);
    }
    public HomeArtistAdapter(Context context, List<Artist> artistList, OnArtistItemClickListener listener) {
        this.context = context;
        this.artistList = artistList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_artist, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Artist artist = artistList.get(position);
        holder.tvName.setText(artist.getName());
        holder.tvFollowers.setText(artist.getFollower() + " Followers");

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
            if (listener != null) {
                listener.onArtistClick(artist);
            }
        });
    }
    @Override
    public int getItemCount() {
        return artistList != null ? artistList.size() : 0;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivAvatar;
        TextView tvName, tvFollowers;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_home_artist_avatar);
            tvName = itemView.findViewById(R.id.tv_home_artist_name);
            tvFollowers = itemView.findViewById(R.id.tv_home_artist_followers);
        }
    }
}