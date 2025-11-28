package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText; // Cần thiết cho Search Bar
import android.widget.ImageButton; // Cần thiết cho Notification
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

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
    private WeatherApi apiService;

    // --- Components của Weather Snippet ---
    TextView snippetLocation, snippetTemp, snippetStatus;
    ImageView snippetIcon;
    View weatherCard; // Dùng để click mở chi tiết

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

        // 2. Thiết lập Weather API
        setupWeather(view);

        // 3. 🔹 THIẾT LẬP LƯỚI CHỨC NĂNG VÀ CLICK 🔹
        setupFunctionGrid(view);
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
        // Gán nội dung
        setTextAndIcon(funcBuyTicket, "Nghệ Sĩ", R.drawable.person_heart_24dp_e3e3e3_fill0_wght400_grad0_opsz24);
        // Xử lý Click -> Chuyển sang BuyTicketActivity
        funcBuyTicket.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ArtistListActivity.class);
            startActivity(intent);
        });

        // --- MỤC 2: GIỎ HÀNG / ĐƠN HÀNG (ORDERS) ---
        // Gán nội dung
        setTextAndIcon(funcOrders, "Vé Của Tôi", R.drawable.local_activity_24dp_e3e3e3_fill0_wght400_grad0_opsz24); // Thay bằng ID icon của bạn
        // Xử lý Click -> Chuyển sang OrderFragment (Vì OrderFragment đã có trong BottomNav)
        // Cách đơn giản nhất là chuyển sang Activity Checkout (nếu muốn làm nhanh)
        funcOrders.setOnClickListener(v -> {
            // Hoặc chuyển Activity
            Intent intent = new Intent(getActivity(), MyTicketActivity.class);
            startActivity(intent);
        });

        // --- MỤC 3: VOUCHER (PROMOTION) ---
        // Gán nội dung
        setTextAndIcon(funcVoucher, "Quét Mã", R.drawable.qr_code_scanner_24dp_e3e3e3_fill0_wght400_grad0_opsz24); // Thay bằng ID icon của bạn
        // Xử lý Click -> Chuyển sang Fragment hoặc Activity quản lý Voucher
        funcVoucher.setOnClickListener(v -> {
            // Tạm thời hiển thị Toast hoặc chuyển đến màn hình chính chứa VoucherFragment
            Toast.makeText(getContext(), "Đang bật quét mã", Toast.LENGTH_SHORT).show();
            // Nếu bạn muốn chuyển Fragment trực tiếp:
            /*
            requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new VoucherFragment())
                .addToBackStack(null)
                .commit();
            */
        });

    }

    /**
     * Hàm hỗ trợ tìm và thiết lập Text và Icon cho thẻ <include layout="@layout/item_function"/>
     */
    private void setTextAndIcon(LinearLayout containerView, String text, int iconResId) {
        // Ánh xạ Text và Icon bên trong thẻ include
        TextView textView = containerView.findViewById(R.id.function_text);
        ImageView iconView = containerView.findViewById(R.id.function_icon);

        if (textView != null) {
            textView.setText(text);
        }
        if (iconView != null) {
            // Đặt tài nguyên Icon.
            // CẦN THÊM CÁC ICON MỚI VÀO THƯ MỤC DRAWABLE CỦA BẠN.
            iconView.setImageResource(iconResId);
        }
    }

    // --- CÁC HÀM XỬ LÝ THỜI TIẾT (Lấy từ file gốc) ---

    private void setupWeather(View view) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(WeatherApi.class);
        // Gọi API cho tọa độ TP.HCM (lat: 10.762622, lon: 106.660172)
        getCurrentWeather(10.762622, 106.660172);

        // Sự kiện click nút Chi tiết Thời tiết
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
        apiService.getCurrentWeather(lat, lon, "metric", "vi", API_KEY)
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

    // Model cho Weather API (Cần giữ lại)
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
    // Hết Model
}