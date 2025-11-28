package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // 🎨 Dùng Toolbar này

public class TicketDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvEventName, tvVenue, tvDate;
    Button btnBuyNow;

    private String eventName, eventLocation, eventDate, eventCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_ticket);

        // --- 2. Ánh xạ View (Đã khớp với layout "như cũ") ---
        toolbar = findViewById(R.id.toolbarTicketDetail);
        tvEventName = findViewById(R.id.textView);
        tvVenue = findViewById(R.id.textViewVenue);
        tvDate = findViewById(R.id.textViewDate);
        btnBuyNow = findViewById(R.id.btnBuyNow);

        // --- 3. Thiết lập Toolbar và nút Back ---
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // --- 4. Lấy dữ liệu từ TicketAdapter ---
        eventName = getIntent().getStringExtra("EVENT_NAME");
        eventLocation = getIntent().getStringExtra("EVENT_LOCATION");
        eventDate = getIntent().getStringExtra("EVENT_DATE");
        eventCode = getIntent().getStringExtra("EVENT_CODE");

        // --- 5. Hiển thị dữ liệu ---
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(eventName);
        }
        tvEventName.setText(eventName);
        tvVenue.setText("Venue: " + eventLocation);
        tvDate.setText("Date: " + eventDate);

        // --- 6. Xử lý nút "BUY NOW" ---
        btnBuyNow.setOnClickListener(v -> {
            Intent intent = new Intent(TicketDetailActivity.this, BuyTicketActivity.class);
            intent.putExtra("EVENT_NAME", eventName);
            intent.putExtra("EVENT_CODE", eventCode);
            startActivity(intent);
        });
    }

    // --- 7. Xử lý nút Back trên Toolbar ---
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}