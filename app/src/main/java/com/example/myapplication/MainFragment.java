package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager; // 💡 THÊM IMPORT
import androidx.recyclerview.widget.RecyclerView; // 💡 THÊM IMPORT

import com.bumptech.glide.Glide;
import com.example.myapplication.Network.ApiClient; // 💡 THÊM IMPORT
import com.example.myapplication.Network.ApiService; // 💡 THÊM IMPORT
import com.example.myapplication.Network.ApiResponse; // 💡 THÊM IMPORT

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

    // --- Các thành phần chung ---
    String API_KEY = "7db45ffd0f22d9b763fcc34afe6b4984";
    private WeatherApi apiServiceWeather; // Đổi tên để tránh xung đột
    private ApiService apiService; // 💡 API SERVICE CHÍNH

    // --- Components của Weather Snippet ---
    TextView snippetLocation, snippetTemp, snippetStatus;
    ImageView snippetIcon;
    View weatherCard;

    // --- Components của Event List ---
    private RecyclerView rvFeaturedEvents; // 💡 KHAI BÁO RECYCLERVIEW

    // --- Components của Function Grid ---
    private LinearLayout funcBuyTicket, funcOrders, funcVoucher, funcProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ các view Thời tiết
        snippetLocation = view.findViewById(R.id.weather_snippet_location);
        snippetTemp = view.findViewById(R.id.weather_snippet_temp);
        snippetStatus = view.findViewById(R.id.weather_snippet_status);
        snippetIcon = view.findViewById(R.id.weather_snippet_icon);
        weatherCard = view.findViewById(R.id.card_weather);

        // 💡 KHỞI TẠO API SERVICE CHÍNH
        apiService = ApiClient.getApiService();

        // 2. Thiết lập Weather API
        setupWeather(view);

        // 3. 🔹 THIẾT LẬP LƯỚI CHỨC NĂNG VÀ CLICK 🔹
        setupFunctionGrid(view);

        // 4. 💡 THIẾT LẬP VÀ TẢI SỰ KIỆN NỔI BẬT
        setupFeaturedEvents(view);
    }

    private void setupFeaturedEvents(View view) {
        rvFeaturedEvents = view.findViewById(R.id.rv_featured_events);

        if (rvFeaturedEvents != null) {
            // Thiết lập Layout Manager cuộn ngang
            rvFeaturedEvents.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

            // Tải dữ liệu
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
                        // Lọc và giới hạn danh sách: Adapter sẽ tự giới hạn còn 3.
                        // Lưu ý: Backend phải sắp xếp theo ngày gần nhất.

                        FeaturedEventAdapter adapter = new FeaturedEventAdapter(getContext(), eventList);
                        rvFeaturedEvents.setAdapter(adapter);

                        Log.d("EVENT_LOAD", "Loaded " + eventList.size() + " events for featured list.");
                    }
                } else {
                    Log.e("EVENT_LOAD", "Failed to load featured events. HTTP: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Ticket>>> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("EVENT_LOAD", "Connection failure: " + t.getMessage());
            }
        });
    }

    /**
     * Hàm thiết lập các mục chức năng (Icon + Text) và sự kiện click chuyển trang.
     */
    private void setupFunctionGrid(View view) {

        // 1. Ánh xạ các Container (Sử dụng ID của thẻ <include>)
        funcBuyTicket = view.findViewById(R.id.func_buy_ticket);
        funcOrders = view.findViewById(R.id.func_orders);
        funcVoucher = view.findViewById(R.id.func_voucher);
        funcProfile = view.findViewById(R.id.func_profile);

        // 2. Đặt nội dung động và xử lý click

        // --- MỤC 1: ĐẶT VÉ (BUY TICKET) ---
        setTextAndIcon(funcBuyTicket, "Nghệ Sĩ", R.drawable.person_heart_24dp_e3e3e3_fill0_wght400_grad0_opsz24);
        funcBuyTicket.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ArtistListActivity.class);
            startActivity(intent);
        });

        // --- MỤC 2: GIỎ HÀNG / ĐƠN HÀNG (ORDERS) ---
        setTextAndIcon(funcOrders, "Vé Của Tôi", R.drawable.local_activity_24dp_e3e3e3_fill0_wght400_grad0_opsz24);
        funcOrders.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyTicketActivity.class);
            startActivity(intent);
        });

        // --- MỤC 3: VOUCHER (PROMOTION) ---
        setTextAndIcon(funcVoucher, "Quét Mã", R.drawable.qr_code_scanner_24dp_e3e3e3_fill0_wght400_grad0_opsz24);
        funcVoucher.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đang bật quét mã", Toast.LENGTH_SHORT).show();
        });

        // --- MỤC 4: PROFILE --- (Thường không cần vì đã có BottomNav, nhưng giữ lại nếu bạn có mục đích khác)
        setTextAndIcon(funcProfile, "Profile", R.drawable.person_24dp_e3e3e3_fill0_wght400_grad0_opsz24);
        funcProfile.setOnClickListener(v -> {
            // Có thể chuyển đến ProfileFragment nếu chưa ở đó
            // Ví dụ: ((HomeActivity)requireActivity()).navigateTo(new ProfileFragment());
        });
    }

    /**
     * Hàm hỗ trợ tìm và thiết lập Text và Icon cho thẻ <include layout="@layout/item_function"/>
     */
    private void setTextAndIcon(LinearLayout containerView, String text, int iconResId) {
        TextView textView = containerView.findViewById(R.id.function_text);
        ImageView iconView = containerView.findViewById(R.id.function_icon);

        if (textView != null) {
            textView.setText(text);
        }
        if (iconView != null) {
            iconView.setImageResource(iconResId);
        }
    }

    // --- CÁC HÀM XỬ LÝ THỜI TIẾT (Giữ nguyên) ---

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
        Call<CurrentWeatherResponse> getCurrentWeather(
                @Query("lat") double lat,
                @Query("lon") double lon,
                @Query("units") String units,
                @Query("lang") String lang,
                @Query("appid") String apiKey
        );
    }

    private void getCurrentWeather(double lat, double lon) {
        if (getContext() == null) return;
        apiServiceWeather.getCurrentWeather(lat, lon, "metric", "vi", API_KEY)
                .enqueue(new Callback<CurrentWeatherResponse>() {
                    @Override
                    public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                        if (!isAdded() || getContext() == null) return;
                        if (response.isSuccessful() && response.body() != null) {
                            CurrentWeatherResponse data = response.body();
                            double temp = data.main.temp;
                            String desc = data.weather.get(0).description;
                            String iconCode = data.weather.get(0).icon;

                            snippetTemp.setText(Math.round(temp) + "°");
                            snippetStatus.setText(capitalize(desc));
                            snippetLocation.setText(data.name);

                            Glide.with(getContext())
                                    .load("https://openweathermap.org/img/wn/" + iconCode + "@2x.png")
                                    .into(snippetIcon);
                            snippetIcon.setVisibility(View.VISIBLE);

                        } else {
                            Toast.makeText(getContext(), "Không thể tải thời tiết", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(), "Lỗi kết nối thời tiết", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1);
    }

    // Model cho Weather API (Giữ nguyên)
    public static class CurrentWeatherResponse {
        public Main main;
        public List<WeatherData> weather;
        public Wind wind;
        public String name;
        public static class Main { public double temp; }
        public static class WeatherData {
            public String description;
            public String icon;
        }
        public static class Wind { public double speed; }
    }
}