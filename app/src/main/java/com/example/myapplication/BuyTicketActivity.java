package com.example.myapplication;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.appbar.MaterialToolbar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class BuyTicketActivity extends AppCompatActivity {

    RecyclerView dateRecyclerView;
    DateAdapter dateAdapter;
    TextView tvSelectedDateFull;
    private final DateTimeFormatter fullDateFormatter =
            DateTimeFormatter.ofPattern("E, dd 'Tháng' MM, yyyy", new Locale("vi", "VN"));


    RecyclerView locationRecyclerView;
    ArrayList<Location> locationData;
    LocationRecyclerAdapter locationAdapter;


    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_ticket);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        tvSelectedDateFull = findViewById(R.id.tv_selected_date_full);
        setupDateRecyclerView();
        updateSelectedDateText(LocalDate.now());


        // 1. Ánh xạ ID mới
        locationRecyclerView = findViewById(R.id.location_recycler_view);

        // 2. Tải dữ liệu (giữ nguyên)
        loadMockLocationData();

        // 3. Khởi tạo và gán Adapter MỚI
        locationAdapter = new LocationRecyclerAdapter(this, locationData);

        // 4. Gán LayoutManager VÀ Adapter cho RecyclerView
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationRecyclerView.setAdapter(locationAdapter);

        locationAdapter.setOnShowtimeClickListener((location, time) -> {
            Intent intent = new Intent(BuyTicketActivity.this, SelectSeatActivity.class);
            intent.putExtra("LOCATION_ADDRESS", location.getAddress()); // Vd: "F110"
            intent.putExtra("SELECTED_TIME_MS", time.getTime()); // Vd: 10:30
            intent.putExtra("MOVIE_NAME", "Tên phim của bạn..."); // Thêm tên phim
            startActivity(intent);
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupDateRecyclerView() {
        // ... (Code của bạn giữ nguyên)
        dateRecyclerView = findViewById(R.id.date_recycler_view);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int itemWidth = screenWidth / 7;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        dateRecyclerView.setLayoutManager(layoutManager);
        dateAdapter = new DateAdapter(itemWidth);
        dateRecyclerView.setAdapter(dateAdapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(dateRecyclerView);
        dateAdapter.setOnDateClickListener(date -> {
            updateSelectedDateText(date);
            Toast.makeText(this, "Đang tải địa điểm cho ngày: " + date.toString(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateSelectedDateText(LocalDate date) {
        // ... (Code của bạn giữ nguyên)
        String formattedDate = fullDateFormatter.format(date);
        String capitalizedDate = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);
        tvSelectedDateFull.setText(capitalizedDate);
    }

    private void loadMockLocationData() {
        locationData = new ArrayList<>();
        ArrayList<Date> timesA = new ArrayList<>();
        timesA.add(new Date(System.currentTimeMillis() + 3600000));
        timesA.add(new Date(System.currentTimeMillis() + 7200000));
        locationData.add(new Location("Đại Học Tôn Đức Thắng", "F110", timesA));
        ArrayList<Date> timesB = new ArrayList<>();
        timesB.add(new Date(System.currentTimeMillis() + 8000000));
        locationData.add(new Location("Đại Học Tôn Đức Thắng", "A105", timesB));
        ArrayList<Date> timesC = new ArrayList<>();
        timesC.add(new Date(System.currentTimeMillis() + 1200000));
        locationData.add(new Location("Đại Học Tôn Đức Thắng", "F602", timesC));
    }
}