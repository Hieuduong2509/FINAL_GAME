package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Artist;
import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiResponse;
import com.example.myapplication.Network.ApiService;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArtistListAdapter adapter;
    private List<Artist> artistList;
    private ApiService apiService;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_list);
        MaterialToolbar toolbar = findViewById(R.id.toolbarArtistList);
        etSearch = findViewById(R.id.et_search_artist);
        recyclerView = findViewById(R.id.recyclerArtistList);
        toolbar.setNavigationOnClickListener(v -> finish());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        artistList = new ArrayList<>();
        adapter = new ArtistListAdapter(this, artistList);
        recyclerView.setAdapter(adapter);
        apiService = ApiClient.getApiService();
        loadArtists();
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadArtists();
    }
    private void loadArtists() {
        apiService.getAllInviters().enqueue(new Callback<ApiResponse<List<Artist>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Artist>>> call, Response<ApiResponse<List<Artist>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Artist> data = response.body().getData();
                    if (data != null) {
                        artistList.clear();
                        artistList.addAll(data);
                        adapter = new ArtistListAdapter(ArtistListActivity.this, artistList);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<Artist>>> call, Throwable t) {
                Toast.makeText(ArtistListActivity.this, "ERROR CONNECT TO INTERNET!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}