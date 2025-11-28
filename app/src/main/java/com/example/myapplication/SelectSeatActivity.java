package com.example.myapplication; // 🔹 Đảm bảo đúng package

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SelectSeatActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvCinemaName, tvShowTime, tvMovieName, tvSelectedSeat, tvTotalPrice;
    TableLayout tableSeats;
    Button btnContinue;
    AutoCompleteTextView actvArea;
    ImageView ivAreaMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_seats);

        // --- 1. BẮT ĐẦU KÍCH HOẠT TOOLBAR ---
        toolbar = findViewById(R.id.select_seat_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // --- 2. Ánh xạ các View ---
        tvCinemaName = findViewById(R.id.tvCinemaName);
        tvShowTime = findViewById(R.id.tvShowTime);
        tvMovieName = findViewById(R.id.tvMovieName);
        btnContinue = findViewById(R.id.btnContinue);
        tvSelectedSeat = findViewById(R.id.tvSelectedSeat);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        // Ánh xạ các view mới
        actvArea = findViewById(R.id.actvArea);
        ivAreaMap = findViewById(R.id.ivAreaMap);

        // --- 3. Lấy dữ liệu từ Intent ---
        String locationAddress = getIntent().getStringExtra("LOCATION_ADDRESS");
        long selectedTimeMs = getIntent().getLongExtra("SELECTED_TIME_MS", 0);

        // Định dạng lại thời gian
        Date selectedTime = new Date(selectedTimeMs);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeString = timeFormatter.format(selectedTime);

        // --- 4. Hiển thị dữ liệu lên TextView ---
        if (locationAddress != null) {
            tvCinemaName.setText(locationAddress);
        }
        tvShowTime.setText(timeString);

        // --- 5. CẤU HÌNH DROPDOWN CHỌN KHU VỰC ---
        // Giả lập dữ liệu các khu vực (Bạn nên lấy list này từ API hoặc Intent nếu có)
        List<String> areaList = new ArrayList<>();
        areaList.add("Khu A - Phổ thông");
        areaList.add("Khu B - VIP (Giữa rạp)");
        areaList.add("Khu C - Cặp đôi (Cuối rạp)");

        // Tạo Adapter để kết nối dữ liệu với Dropdown
        // android.R.layout.simple_dropdown_item_1line là layout mặc định của Android cho 1 dòng text
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, areaList);

        // Gán Adapter cho AutoCompleteTextView
        actvArea.setAdapter(areaAdapter);

        // (Tùy chọn) Đặt giá trị mặc định ban đầu là phần tử đầu tiên
        if (!areaList.isEmpty()) {
            actvArea.setText(areaList.get(0), false); // false để không hiện dropdown ngay lúc set text
        }

        // Xử lý sự kiện khi người dùng chọn một khu vực
        actvArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedArea = (String) parent.getItemAtPosition(position);
                // TODO: Xử lý logic khi chọn khu vực ở đây.
                // Ví dụ: Tải lại sơ đồ ghế (loadSeatsForArea(selectedArea)), cập nhật giá tiền, thay đổi bản đồ...
                Toast.makeText(SelectSeatActivity.this, "Đã chọn: " + selectedArea, Toast.LENGTH_SHORT).show();

                // Ví dụ đổi ảnh bản đồ tùy theo khu vực (nếu bạn có ảnh khác nhau)
                // if (position == 0) ivAreaMap.setImageResource(R.drawable.map_area_a);
                // else if (position == 1) ivAreaMap.setImageResource(R.drawable.map_area_b);
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectSeatActivity.this, Checkout.class);
                startActivity(intent);
                finish();
            }
        });

        // (Đây là nơi bạn sẽ tiếp tục code để vẽ các ghế vào tableSeats)
        // loadSeatsForArea(actvArea.getText().toString()); // Ví dụ gọi hàm load ghế ban đầu
    }

    // --- 6. Xử lý nút Back trên Toolbar ---
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}