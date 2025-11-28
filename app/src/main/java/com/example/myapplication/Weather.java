package com.example.myapplication;

import androidx.annotation.NonNull; // 🔹 ĐÃ THÊM IMPORT
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem; // 🔹 ĐÃ THÊM IMPORT
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide; // Thư viện Glide

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class Weather extends AppCompatActivity {

    TextView location, temperature, weatherStatus, detailedDescription;
    ImageView currentWeatherIcon;
    LinearLayout hourlyForecastContainer, dailyForecastContainer;

    String API_KEY = "7db45ffd0f22d9b763fcc34afe6b4984";
    private WeatherApi apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_show);

        location = findViewById(R.id.location);
        temperature = findViewById(R.id.temperature);
        weatherStatus = findViewById(R.id.weather_status);
        detailedDescription = findViewById(R.id.detailed_description);
        currentWeatherIcon = findViewById(R.id.current_weather_icon);
        hourlyForecastContainer = findViewById(R.id.hourly_forecast_container);
        dailyForecastContainer = findViewById(R.id.daily_forecast_container);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.weather_toolbar);
        setSupportActionBar(toolbar);

        // 3. Code cũ của bạn để bật nút back (mũi tên)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(null);
        }
        // location = findViewById(R.id.location); // 🔹 Dòng này bị lặp, đã xóa

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(WeatherApi.class);

        double lat = 10.762622;
        double lon = 106.660172;

        getCurrentWeather(lat, lon);
        getForecast(lat, lon);

        // 🔹 LỖI Ở ĐÂY: Hàm onOptionsItemSelected đã bị di chuyển ra ngoài
    } // ⬅️ DẤU NGOẶC KẾT THÚC CỦA `onCreate`

    // 🔹 HÀM ĐƯỢC DI CHUYỂN RA ĐÂY (NẰM TRONG CLASS, BÊN NGOÀI onCreate)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Kiểm tra xem ID có phải là nút "home" (mũi tên quay lại) không
        if (item.getItemId() == android.R.id.home) {
            finish(); // Đóng Activity này và quay lại Activity trước
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ---------------- API INTERFACE ----------------
    private interface WeatherApi {
        // ... (Giữ nguyên)
        @GET("data/2.5/weather")
        Call<CurrentWeatherResponse> getCurrentWeather(
                @Query("lat") double lat,
                @Query("lon") double lon,
                @Query("units") String units,
                @Query("lang") String lang,
                @Query("appid") String apiKey
        );

        @GET("data/2.5/forecast")
        Call<ForecastResponse> getForecast(
                @Query("lat") double lat,
                @Query("lon") double lon,
                @Query("units") String units,
                @Query("lang") String lang,
                @Query("appid") String apiKey
        );
    }

    // ---------------- LẤY DỮ LIỆU API ----------------

    private void getCurrentWeather(double lat, double lon) {
        // ... (Giữ nguyên)
        apiService.getCurrentWeather(lat, lon, "metric", "vi", API_KEY)
                .enqueue(new Callback<CurrentWeatherResponse>() {
                    @Override
                    public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            CurrentWeatherResponse data = response.body();
                            double temp = data.main.temp;
                            String desc = data.weather.get(0).description;
                            String iconCode = data.weather.get(0).icon;
                            double windSpeed = data.wind.speed;

                            temperature.setText(Math.round(temp) + "°");
                            weatherStatus.setText(capitalize(desc));
                            location.setText(data.name);

                            Glide.with(Weather.this)
                                    .load("https://openweathermap.org/img/wn/" + iconCode + "@2x.png")
                                    .into(currentWeatherIcon);
                            currentWeatherIcon.setVisibility(View.VISIBLE);

                            String detailedText = "Gió: " + Math.round(windSpeed) + " m/s";
                            if (desc.toLowerCase().contains("mưa")) {
                                detailedText = "Trời có mưa. " + detailedText;
                            } else if (desc.toLowerCase().contains("mây")) {
                                detailedText = "Trời nhiều mây. " + detailedText;
                            }
                            detailedDescription.setText(detailedText);
                            detailedDescription.setVisibility(View.VISIBLE);

                        } else {
                            Toast.makeText(Weather.this, "Không thể tải thời tiết hiện tại", Toast.LENGTH_SHORT).show();
                            Log.e("WEATHER_APP", "CURRENT WEATHER ERROR: " + response.code() + " - " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                        Toast.makeText(Weather.this, "Lỗi kết nối (hiện tại): " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("WEATHER_APP", "CURRENT WEATHER FAILURE: " + t.getMessage(), t);
                    }
                });
    }

    private void getForecast(double lat, double lon) {
        // ... (Giữ nguyên)
        apiService.getForecast(lat, lon, "metric", "vi", API_KEY)
                .enqueue(new Callback<ForecastResponse>() {
                    @Override
                    public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<ForecastResponse.ForecastItem> forecastList = response.body().list;
                            showHourlyForecast(forecastList);
                            showDailyForecast(forecastList);
                        } else {
                            Toast.makeText(Weather.this, "Không thể tải dự báo", Toast.LENGTH_SHORT).show();
                            Log.e("WEATHER_APP", "FORECAST ERROR: " + response.code() + " - " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ForecastResponse> call, Throwable t) {
                        Toast.makeText(Weather.this, "Lỗi kết nối (dự báo): " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("WEATHER_APP", "FORECAST FAILURE: " + t.getMessage(), t);
                    }
                });
    }


    // ---------------- HIỂN THỊ DỮ LIỆU LÊN GIAO DIỆN ----------------

    private void showHourlyForecast(List<ForecastResponse.ForecastItem> list) {
        // ... (Giữ nguyên code của bạn)
        hourlyForecastContainer.removeAllViews();
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH", Locale.getDefault());
        int numToShow = Math.min(8, list.size());

        int widthInDp = 65;
        float scale = getResources().getDisplayMetrics().density;
        int widthInPixels = (int) (widthInDp * scale + 0.5f);

        for (int i = 0; i < numToShow; i++) {
            ForecastResponse.ForecastItem item = list.get(i);

            LinearLayout hourlyItem = new LinearLayout(this);
            hourlyItem.setOrientation(LinearLayout.VERTICAL);
            hourlyItem.setGravity(Gravity.CENTER_HORIZONTAL);

            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                    widthInPixels, // Chiều rộng cố định
                    LinearLayout.LayoutParams.WRAP_CONTENT // Chiều cao tự động
            );
            hourlyItem.setLayoutParams(itemParams);

            TextView timeTv = new TextView(this);
            if (i == 0) {
                timeTv.setText("Bây giờ");
                timeTv.setSingleLine(false);
                timeTv.setMaxLines(2);
            } else {
                timeTv.setText(sdfTime.format(new Date(item.dt * 1000)));
                timeTv.setSingleLine(true);
            }
            timeTv.setTextColor(getResources().getColor(android.R.color.white));
            timeTv.setTextSize(18);
            timeTv.setGravity(Gravity.CENTER);
            hourlyItem.addView(timeTv);

            ImageView iconIv = new ImageView(this);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(80, 80); // Giữ 80x80 của bạn
            iconParams.setMargins(0, 8, 0, 8);
            iconIv.setLayoutParams(iconParams);
            Glide.with(this)
                    .load("https://openweathermap.org/img/wn/" + item.weather.get(0).icon + ".png")
                    .into(iconIv);
            hourlyItem.addView(iconIv);

            TextView tempTv = new TextView(this);
            tempTv.setText(Math.round(item.main.temp) + "°");
            tempTv.setTextColor(getResources().getColor(android.R.color.white));
            tempTv.setTextSize(20);
            tempTv.setTypeface(null, android.graphics.Typeface.BOLD);
            tempTv.setGravity(Gravity.CENTER);
            hourlyItem.addView(tempTv);

            hourlyForecastContainer.addView(hourlyItem);
        }
    }

    private void showDailyForecast(List<ForecastResponse.ForecastItem> list) {
        // ... (Giữ nguyên code của bạn)
        dailyForecastContainer.removeAllViews();
        SimpleDateFormat sdfDay = new SimpleDateFormat("EEE", new Locale("vi"));

        Map<String, List<ForecastResponse.ForecastItem>> dailyGroupedForecast = new HashMap<>();

        for (ForecastResponse.ForecastItem item : list) {
            String dateKey = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(item.dt * 1000));
            if (!dailyGroupedForecast.containsKey(dateKey)) {
                dailyGroupedForecast.put(dateKey, new ArrayList<>());
            }
            dailyGroupedForecast.get(dateKey).add(item);
        }

        List<String> sortedDates = new ArrayList<>(dailyGroupedForecast.keySet());
        java.util.Collections.sort(sortedDates);

        int daysToShow = sortedDates.size();
        for (int i = 0; i < daysToShow; i++) {
            String dateKey = sortedDates.get(i);
            List<ForecastResponse.ForecastItem> dayItems = dailyGroupedForecast.get(dateKey);

            double minTemp = Double.MAX_VALUE;
            double maxTemp = Double.MIN_VALUE;
            String dayIcon = "";
            double dayTemp = 0.0;

            if (dayItems != null && !dayItems.isEmpty()) {
                for (ForecastResponse.ForecastItem item : dayItems) {
                    if (item.main.temp_min < minTemp) minTemp = item.main.temp_min;
                    if (item.main.temp_max > maxTemp) maxTemp = item.main.temp_max;
                }

                ForecastResponse.ForecastItem midDayItem = dayItems.stream()
                        .filter(item -> item.dt_txt.contains("12:00:00"))
                        .findFirst()
                        .orElse(dayItems.get(0));

                dayIcon = midDayItem.weather.get(0).icon;
                dayTemp = midDayItem.main.temp;
            }

            LinearLayout dailyItem = new LinearLayout(this);
            dailyItem.setOrientation(LinearLayout.HORIZONTAL);
            dailyItem.setGravity(Gravity.CENTER_VERTICAL);
            dailyItem.setPadding(0, 18, 0, 18);

            TextView dayTv = new TextView(this);
            String dayOfWeek;
            if (i == 0) {
                dayOfWeek = "Hôm nay";
            } else {
                dayOfWeek = sdfDay.format(new Date(dayItems.get(0).dt * 1000));
            }
            dayTv.setText(dayOfWeek);
            dayTv.setTextColor(getResources().getColor(android.R.color.white));
            dayTv.setTextSize(20);
            dayTv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
            dailyItem.addView(dayTv);

            ImageView iconIv = new ImageView(this);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(100, 100); // Giữ 100x100 của bạn
            iconIv.setLayoutParams(iconParams);
            Glide.with(this)
                    .load("https://openweathermap.org/img/wn/" + dayIcon + ".png")
                    .into(iconIv);
            dailyItem.addView(iconIv);

            TextView tempRangeTv = new TextView(this);
            tempRangeTv.setText(Math.round(dayTemp) + "°");

            tempRangeTv.setTextColor(getResources().getColor(android.R.color.white));
            tempRangeTv.setTextSize(20);
            tempRangeTv.setGravity(Gravity.END);
            tempRangeTv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
            dailyItem.addView(tempRangeTv);

            dailyForecastContainer.addView(dailyItem);

            if (i < daysToShow - 1) {
                View divider = new View(this);
                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
                dividerParams.setMargins(0, 8, 0, 8);
                divider.setLayoutParams(dividerParams);
                divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                dailyForecastContainer.addView(divider);
            }
        }
    }

    // Hàm viết hoa chữ cái đầu
    private String capitalize(String s) {
        // ... (Giữ nguyên)
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    // ---------------- MODEL ----------------
    // ... (Giữ nguyên)
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

    public static class ForecastResponse {
        public List<ForecastItem> list;

        public static class ForecastItem {
            public long dt;
            public Main main;
            public List<WeatherData> weather;
            public String dt_txt;
        }

        public static class Main {
            public double temp;
            public double temp_min;
            public double temp_max;
        }

        public static class WeatherData {
            public String description;
            public String icon;
        }
    }
}