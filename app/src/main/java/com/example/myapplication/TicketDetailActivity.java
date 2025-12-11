package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.Artist;
import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiService;
import com.example.myapplication.Network.ApiResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketDetailActivity extends AppCompatActivity {

    private String currentEventId;
    private TextView tvEventTitle, tvEventDateTime, tvEventLocation, tvEventDescription;
    private ImageView ivEventImage;
    private Button btnNextStep;

    private RecyclerView recyclerArtists;
    private EventArtistAdapter artistAdapter;
    private List<Artist> artistList;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_ticket);

        apiService = ApiClient.getApiService();
        Intent intent = getIntent();
        currentEventId = intent.getStringExtra("EVENT_ID");
        Toolbar toolbar = findViewById(R.id.toolbarTicketDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        btnNextStep = findViewById(R.id.btnBuyNow);
        tvEventTitle = findViewById(R.id.textView);
        tvEventDateTime = findViewById(R.id.textViewDate);
        tvEventLocation = findViewById(R.id.textViewVenue);
        tvEventDescription = findViewById(R.id.tv_event_description);
        ivEventImage = findViewById(R.id.iv_event_image);
        recyclerArtists = findViewById(R.id.recycler_artists);
        if (recyclerArtists != null) {
            recyclerArtists.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            artistList = new ArrayList<>();
            artistAdapter = new EventArtistAdapter(this, artistList);
            recyclerArtists.setAdapter(artistAdapter);
        }
        if (currentEventId == null) {
            Toast.makeText(this, "EVENT ID IS NULL", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        loadEventDetails(currentEventId);
        loadEventArtists(currentEventId);
        if (btnNextStep != null) {
            btnNextStep.setOnClickListener(v -> {
                String name = tvEventTitle.getText().toString();
                String dateTime = tvEventDateTime.getText().toString().replace("Date: ", "");
                String location = tvEventLocation.getText().toString().replace("Venue: ", "");

                Intent seatIntent = new Intent(TicketDetailActivity.this, SelectSeatActivity.class);
                seatIntent.putExtra("EVENT_ID", currentEventId);
                seatIntent.putExtra("EVENT_NAME", name);
                seatIntent.putExtra("EVENT_DATETIME", dateTime);
                seatIntent.putExtra("EVENT_LOCATION", location);
                startActivity(seatIntent);
            });
        }
    }

    private void loadEventDetails(String eventId) {
        apiService.getEventDetails(eventId).enqueue(new Callback<ApiResponse<Ticket>>() {
            @Override
            public void onResponse(Call<ApiResponse<Ticket>> call, Response<ApiResponse<Ticket>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Ticket event = response.body().getData();

                    if (event != null) {
                        if (tvEventTitle != null) tvEventTitle.setText(event.eventName);
                        if (tvEventDateTime != null) tvEventDateTime.setText("Date: " + event.getDateTime());
                        if (tvEventLocation != null) tvEventLocation.setText("Venue: " + event.location);
                        if (tvEventDescription != null) {
                            tvEventDescription.setText(event.getDescription());
                        }
                        if (ivEventImage != null) {
                            String base64Img = event.getPosterBase64();
                            String urlImg = event.getImageUrl();

                            if (base64Img != null && base64Img.length() > 100) {
                                try {
                                    byte[] imageBytes = Base64.decode(base64Img, Base64.DEFAULT);
                                    Glide.with(TicketDetailActivity.this)
                                            .load(imageBytes)
                                            .placeholder(R.drawable.ic_launcher_background)
                                            .error(R.drawable.ic_launcher_foreground)
                                            .into(ivEventImage);
                                } catch (Exception e) {
                                    Log.e("IMAGE_LOAD", "Base64 decode failed: " + e.getMessage());
                                }
                            }
                            else if (urlImg != null && !urlImg.isEmpty()) {
                                String fullUrl = urlImg.startsWith("http") ? urlImg : ApiClient.BASE_URL + urlImg;
                                Glide.with(TicketDetailActivity.this)
                                        .load(fullUrl)
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .error(R.drawable.ic_launcher_foreground)
                                        .into(ivEventImage);
                            }
                        }
                    }
                } else {
                    Toast.makeText(TicketDetailActivity.this, "CANNOT LOAD EVENT.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Ticket>> call, Throwable t) {
                Toast.makeText(TicketDetailActivity.this, "CANNOT LOAD EVENT.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadEventArtists(String eventId) {
        apiService.getArtistsByEvent(eventId).enqueue(new Callback<ApiResponse<List<Artist>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Artist>>> call, Response<ApiResponse<List<Artist>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Artist> artists = response.body().getData();

                    if (artists != null && !artists.isEmpty()) {
                        artistList.clear();
                        artistList.addAll(artists);
                        artistAdapter.notifyDataSetChanged();
                        if (recyclerArtists != null) recyclerArtists.setVisibility(View.VISIBLE);
                    } else {
                        if (recyclerArtists != null) recyclerArtists.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Artist>>> call, Throwable t) {
                Log.e("ARTIST_LOAD", "Failed to load artists: " + t.getMessage());
            }
        });
    }
}