package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiService;
import com.example.myapplication.Network.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketDetailActivity extends AppCompatActivity {

    private String currentEventId;

    private TextView tvEventTitle, tvEventDateTime, tvEventLocation, tvEventDescription;
    private Button btnNextStep;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_ticket); // Layout chi tiết

        apiService = ApiClient.getApiService();

        // --- 1. NHẬN EVENT ID TỪ INTENT ---
        Intent intent = getIntent();
        currentEventId = intent.getStringExtra("EVENT_ID");

        // --- 2. Ánh xạ Views ---
        Toolbar toolbar = findViewById(R.id.toolbarTicketDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        btnNextStep = findViewById(R.id.btnBuyNow);

        tvEventTitle = findViewById(R.id.textView); // Tên sự kiện
        tvEventDateTime = findViewById(R.id.textViewDate); // Ngày giờ
        tvEventLocation = findViewById(R.id.textViewVenue); // Địa điểm
        tvEventDescription = findViewById(R.id.tv_event_description);

        if (currentEventId == null) {
            Toast.makeText(this, "Lỗi: ID sự kiện không hợp lệ.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 3. GỌI API ĐỂ TẢI DỮ LIỆU CHI TIẾT
        loadEventDetails(currentEventId);

        // --- 4. LOGIC CHUYỂN SANG CHỌN GHẾ ---
        if (btnNextStep != null) {
            btnNextStep.setOnClickListener(v -> {
                // Lấy dữ liệu hiện tại đang hiển thị trên UI (do API đã tải về)
                String name = tvEventTitle.getText().toString(); // 💡 LẤY TÊN SỰ KIỆN
                String dateTime = tvEventDateTime.getText().toString().replace("Ngày: ", "");
                String location = tvEventLocation.getText().toString().replace("Địa điểm: ", "");

                Intent seatIntent = new Intent(TicketDetailActivity.this, SelectSeatActivity.class);

                // TRUYỀN ĐẦY ĐỦ DỮ LIỆU
                seatIntent.putExtra("EVENT_ID", currentEventId);
                seatIntent.putExtra("EVENT_NAME", name); // 💡 QUAN TRỌNG: Truyền tên sự kiện
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
                        // Cập nhật UI
                        if (tvEventTitle != null) tvEventTitle.setText(event.eventName);
                        if (tvEventDateTime != null) tvEventDateTime.setText("Ngày: " + event.getDateTime());
                        if (tvEventLocation != null) tvEventLocation.setText("Địa điểm: " + event.location);

                        // Nếu có description trong model Ticket thì bỏ comment dòng dưới
                        // if (tvEventDescription != null) tvEventDescription.setText(event.description);
                    }
                } else {
                    Log.e("EVENT_DETAIL", "Failed to load details: " + response.code());
                    Toast.makeText(TicketDetailActivity.this, "Lỗi tải chi tiết.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Ticket>> call, Throwable t) {
                Log.e("EVENT_DETAIL", "Connection failure: " + t.getMessage());
                Toast.makeText(TicketDetailActivity.this, "Lỗi kết nối.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}