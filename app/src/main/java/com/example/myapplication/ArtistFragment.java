package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Artist;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class ArtistFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArtistListAdapter adapter;
    private FollowManager followManager;
    private MaterialToolbar toolbar;
    private TextView tvEmpty;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_artist_list, container, false);
        followManager = new FollowManager(getContext());
        toolbar = view.findViewById(R.id.toolbarArtistList);
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
            toolbar.setTitle("Nghệ sĩ theo dõi");
            toolbar.setNavigationIcon(null);
        }
        recyclerView = view.findViewById(R.id.recyclerArtistList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadFollowedArtists();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFollowedArtists();
    }

    private void loadFollowedArtists() {
        if (getContext() == null) return;
        List<Artist> followedList = followManager.getFollowedArtists();
        if (adapter == null) {
            adapter = new ArtistListAdapter(getContext(), followedList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter = new ArtistListAdapter(getContext(), followedList);
            recyclerView.setAdapter(adapter);
        }
    }
}