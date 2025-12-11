package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.Artist;
import com.example.myapplication.Ticket;
import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiResponse;
import com.example.myapplication.Network.ApiService;
import com.example.myapplication.NotificationStorage;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainFragment extends Fragment {
    String API_KEY = "7db45ffd0f22d9b763fcc34afe6b4984";
    private WeatherApi apiServiceWeather;
    private ApiService apiService;

    TextView snippetLocation, snippetTemp, snippetStatus;
    ImageView snippetIcon;
    View weatherCard;
    private RecyclerView rvFeaturedEvents;
    private LinearLayout funcBuyTicket, funcOrders, funcVoucher;

    private RecyclerView rvTopArtists;
    private HomeArtistAdapter topArtistAdapter;
    private List<Artist> topArtistList;
    private TextView tvNotificationBadge;
    private View searchBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        snippetLocation = view.findViewById(R.id.weather_snippet_location);
        snippetTemp = view.findViewById(R.id.weather_snippet_temp);
        snippetStatus = view.findViewById(R.id.weather_snippet_status);
        snippetIcon = view.findViewById(R.id.weather_snippet_icon);
        weatherCard = view.findViewById(R.id.card_weather);
        ImageButton iconNotification = view.findViewById(R.id.icon_notification);
        tvNotificationBadge = view.findViewById(R.id.tv_notification_badge);
        searchBar = view.findViewById(R.id.search_bar);

        if (searchBar != null) {
            searchBar.setOnClickListener(v -> {
                BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation_view);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.nav_ticket);
                }
            });
        }
        apiService = ApiClient.getApiService();
        iconNotification.setOnClickListener(v -> {
            NotificationStorage.clearUnreadCount(getContext());
            updateNotificationBadge();
            Intent intent = new Intent(getContext(), NotificationActivity.class);
            startActivity(intent);
        });
        setupWeather(view);
        setupFunctionGrid(view);
        setupFeaturedEvents(view);
        setupTopArtists(view);
        View cardMap = view.findViewById(R.id.card_map_explore);
        if (cardMap != null) {
            cardMap.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), LocationActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupTopArtists(View view) {
        rvTopArtists = view.findViewById(R.id.rv_top_artists);

        if (rvTopArtists != null) {
            rvTopArtists.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            topArtistList = new ArrayList<>();

            // --- CẬP NHẬT GỌN NHẸ ---
            topArtistAdapter = new HomeArtistAdapter(getContext(), topArtistList, new HomeArtistAdapter.OnArtistItemClickListener() {
                @Override
                public void onArtistClick(Artist artist) {
                    // Chỉ còn logic chuyển trang
                    Intent intent = new Intent(getContext(), ArtistDetailActivity.class);
                    intent.putExtra("ARTIST_OBJECT", artist);
                    startActivity(intent);
                }
            });

            rvTopArtists.setAdapter(topArtistAdapter);
            loadTopArtists();
        }
    }

    private void loadTopArtists() {
        if (apiService == null) return;

        apiService.getAllInviters().enqueue(new Callback<ApiResponse<List<Artist>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Artist>>> call, Response<ApiResponse<List<Artist>>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Artist> allArtists = response.body().getData();

                    if (allArtists != null && !allArtists.isEmpty()) {
                        Collections.sort(allArtists, new Comparator<Artist>() {
                            @Override
                            public int compare(Artist o1, Artist o2) {
                                return Integer.compare(o2.getFollower(), o1.getFollower());
                            }
                        });
                        topArtistList.clear();
                        int limit = Math.min(allArtists.size(), 3);
                        for (int i = 0; i < limit; i++) {
                            topArtistList.add(allArtists.get(i));
                        }

                        topArtistAdapter.notifyDataSetChanged();
                        Log.d("TOP_ARTIST", "Loaded " + topArtistList.size() + " top artists.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Artist>>> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("TOP_ARTIST", "Connection failure: " + t.getMessage());
            }
        });
    }

    private void setupFeaturedEvents(View view) {
        rvFeaturedEvents = view.findViewById(R.id.rv_featured_events);
        if (rvFeaturedEvents != null) {
            rvFeaturedEvents.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            loadFeaturedEvents();
        }
    }

    private void loadFeaturedEvents() {
        if (apiService == null) return;
        apiService.getAllEvents().enqueue(new Callback<ApiResponse<List<Ticket>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Ticket>>> call, Response<ApiResponse<List<Ticket>>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Ticket> eventList = response.body().getData();
                    if (eventList != null && !eventList.isEmpty()) {
                        FeaturedEventAdapter adapter = new FeaturedEventAdapter(getContext(), eventList);
                        rvFeaturedEvents.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<Ticket>>> call, Throwable t) { }
        });
    }

    private void setupFunctionGrid(View view) {
        funcBuyTicket = view.findViewById(R.id.func_buy_ticket);
        funcOrders = view.findViewById(R.id.func_orders);
        funcVoucher = view.findViewById(R.id.func_voucher);
        setTextAndIcon(funcBuyTicket, "ARTISTS", R.drawable.person_heart_24dp_e3e3e3_fill0_wght400_grad0_opsz24);
        funcBuyTicket.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ArtistListActivity.class);
            startActivity(intent);
        });
        setTextAndIcon(funcOrders, "MY TICKET", R.drawable.local_activity_24dp_e3e3e3_fill0_wght400_grad0_opsz24);
        funcOrders.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyTicketActivity.class);
            startActivity(intent);
        });
        setTextAndIcon(funcVoucher, "QR", R.drawable.qr_code_scanner_24dp_e3e3e3_fill0_wght400_grad0_opsz24);
        funcVoucher.setOnClickListener(v -> {});
    }

    private void setTextAndIcon(LinearLayout containerView, String text, int iconResId) {
        TextView textView = containerView.findViewById(R.id.function_text);
        ImageView iconView = containerView.findViewById(R.id.function_icon);
        if (textView != null) textView.setText(text);
        if (iconView != null) iconView.setImageResource(iconResId);
    }

    private void setupWeather(View view) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiServiceWeather = retrofit.create(WeatherApi.class);
        getCurrentWeather(10.762622, 106.660172);
        view.findViewById(R.id.weather_snippet_details_btn).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Weather.class);
            startActivity(intent);
        });
    }

    private interface WeatherApi {
        @GET("data/2.5/weather")
        Call<CurrentWeatherResponse> getCurrentWeather(@Query("lat") double lat, @Query("lon") double lon, @Query("units") String units, @Query("lang") String lang, @Query("appid") String apiKey);
    }

    private void getCurrentWeather(double lat, double lon) {
        if (getContext() == null) return;
        apiServiceWeather.getCurrentWeather(lat, lon, "metric", "vi", API_KEY).enqueue(new Callback<CurrentWeatherResponse>() {
            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                if (!isAdded() || getContext() == null) return;
                if (response.isSuccessful() && response.body() != null) {
                    CurrentWeatherResponse data = response.body();
                    snippetTemp.setText(Math.round(data.main.temp) + "°");
                    snippetStatus.setText(capitalize(data.weather.get(0).description));
                    snippetLocation.setText(data.name);
                    Glide.with(getContext()).load("https://openweathermap.org/img/wn/" + data.weather.get(0).icon + "@2x.png").into(snippetIcon);
                    snippetIcon.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {}
        });
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNotificationBadge();
    }

    private void updateNotificationBadge() {
        if (getContext() == null || tvNotificationBadge == null) return;
        int count = NotificationStorage.getUnreadCount(getContext());
        if (count > 0) {
            tvNotificationBadge.setVisibility(View.VISIBLE);
            tvNotificationBadge.setText(count > 99 ? "99+" : String.valueOf(count));
        } else {
            tvNotificationBadge.setVisibility(View.GONE);
        }
    }

    public static class CurrentWeatherResponse {
        public Main main;
        public List<WeatherData> weather;
        public Wind wind;
        public String name;
        public static class Main { public double temp; }
        public static class WeatherData { public String description; public String icon; }
        public static class Wind { public double speed; }
    }
}