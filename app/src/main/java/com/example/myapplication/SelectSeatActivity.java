package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.Models.SeatCountResponse;
import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiService;
import com.example.myapplication.Network.ApiResponse;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectSeatActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvCinemaName, tvShowTime, tvMovieName, tvSelectedSeat, tvTotalPrice;
    Button btnContinue;
    AutoCompleteTextView actvArea;
    ImageView ivAreaMap;

    private String eventId;
    private String eventDateTime;
    private String eventLocation;
    private String eventName;
    private ApiService apiService;

    // Danh sách chi tiết ghế để quản lý logic
    private List<SeatTypeDetail> seatDetailsList = new ArrayList<>();

    private String selectedSeatTypeId;
    private double selectedSeatPrice = 0.0;
    private int quantity = 1; // 💡 Mặc định 1 vé

    // Lớp nội bộ để hiển thị lên Dropdown đẹp hơn
    private static class SeatTypeDetail {
        String id;
        String name;
        double price;
        int available;

        public SeatTypeDetail(String id, String name, double price, int available) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.available = available;
        }

        @NonNull
        @Override
        public String toString() {
            DecimalFormat formatter = new DecimalFormat("#,###");
            // Vd: VIP - 200.000đ (Còn: 50)
            return name + " - " + formatter.format(price) + "đ (Còn: " + available + ")";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_seats);

        apiService = ApiClient.getApiService();

        // --- Ánh xạ Views ---
        toolbar = findViewById(R.id.select_seat_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvCinemaName = findViewById(R.id.tvCinemaName);
        tvShowTime = findViewById(R.id.tvShowTime);
        tvMovieName = findViewById(R.id.tvMovieName);
        btnContinue = findViewById(R.id.btnContinue);
        tvSelectedSeat = findViewById(R.id.tvSelectedSeat);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        actvArea = findViewById(R.id.actvArea);
        ivAreaMap = findViewById(R.id.ivAreaMap);

        // --- Nhận dữ liệu ---
        Intent intent = getIntent();
        eventId = intent.getStringExtra("EVENT_ID");
        eventDateTime = intent.getStringExtra("EVENT_DATETIME");
        eventLocation = intent.getStringExtra("EVENT_LOCATION");
        eventName = intent.getStringExtra("EVENT_NAME");

        // --- Hiển thị thông tin ---
        if (eventLocation != null) tvCinemaName.setText(eventLocation);
        if (eventDateTime != null) tvShowTime.setText(eventDateTime);
        if (eventName != null) tvMovieName.setText(eventName);

        // --- Tải ghế ---
        if (eventId != null) {
            loadSeatTypes(eventId);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID sự kiện", Toast.LENGTH_SHORT).show();
        }

        // --- Xử lý nút Tiếp tục ---
        btnContinue.setOnClickListener(v -> {
            double finalTotalPrice = selectedSeatPrice * quantity;

            if (eventId == null || selectedSeatTypeId == null) {
                Toast.makeText(SelectSeatActivity.this, "Vui lòng chờ tải dữ liệu hoặc chọn loại vé.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent checkoutIntent = new Intent(SelectSeatActivity.this, Checkout.class);
            checkoutIntent.putExtra("EVENT_ID", eventId);
            checkoutIntent.putExtra("EVENT_NAME", eventName);
            checkoutIntent.putExtra("SEAT_TYPE_ID", selectedSeatTypeId);
            checkoutIntent.putExtra("QUANTITY", quantity);
            checkoutIntent.putExtra("TOTAL_PRICE", finalTotalPrice);

            startActivity(checkoutIntent);
        });
    }

    private void loadSeatTypes(String eventId) {
        apiService.getEventSeats(eventId).enqueue(new Callback<ApiResponse<SeatCountResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<SeatCountResponse>> call, Response<ApiResponse<SeatCountResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    SeatCountResponse data = response.body().getData();

                    // 💡 Kiểm tra null và danh sách rỗng
                    if (data != null && data.seatList != null && !data.seatList.isEmpty()) {
                        seatDetailsList.clear();

                        for (SeatCountResponse.SeatType seat : data.seatList) {
                            seatDetailsList.add(new SeatTypeDetail(
                                    seat.seatTypeId,
                                    seat.seatName,
                                    seat.price,
                                    seat.availableSeats
                            ));
                        }
                        setupDropdown();
                    } else {
                        Toast.makeText(SelectSeatActivity.this, "Sự kiện này chưa mở bán vé.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e("SEAT_LOAD", "Lỗi tải ghế: " + response.code());
                    Toast.makeText(SelectSeatActivity.this, "Không tải được danh sách ghế.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SeatCountResponse>> call, Throwable t) {
                Log.e("SEAT_LOAD", "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(SelectSeatActivity.this, "Lỗi kết nối mạng.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDropdown() {
        List<String> areaNames = new ArrayList<>();
        for (SeatTypeDetail detail : seatDetailsList) {
            areaNames.add(detail.toString());
        }

        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, areaNames);
        actvArea.setAdapter(areaAdapter);

        actvArea.setOnItemClickListener((parent, view, position, id) -> {
            SeatTypeDetail selected = seatDetailsList.get(position);
            selectedSeatTypeId = selected.id;
            selectedSeatPrice = selected.price;

            updatePriceUI();
            tvSelectedSeat.setText("Loại vé: " + selected.name);
        });

        // 💡 Tự động chọn loại ghế đầu tiên để người dùng không bị lỗi null khi bấm tiếp tục ngay
        if (!seatDetailsList.isEmpty()) {
            actvArea.setText(areaNames.get(0), false);

            SeatTypeDetail initial = seatDetailsList.get(0);
            selectedSeatTypeId = initial.id;
            selectedSeatPrice = initial.price;

            updatePriceUI();
            tvSelectedSeat.setText("Loại vé: " + initial.name);
        }
    }

    private void updatePriceUI() {
        double total = selectedSeatPrice * quantity;
        DecimalFormat formatter = new DecimalFormat("#,###");
        tvTotalPrice.setText(formatter.format(total) + "đ");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}