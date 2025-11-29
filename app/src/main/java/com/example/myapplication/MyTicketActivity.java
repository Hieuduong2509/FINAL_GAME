package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.User;
import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiService;
import com.example.myapplication.Network.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTicketActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyTicketAdapter adapter;
    private ApiService apiService;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ticket);

        apiService = ApiClient.getApiService();

        // Ánh xạ RecyclerView (dùng ID chính xác từ XML)
        recyclerView = findViewById(R.id.recyclerMyTickets);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarMyTickets);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Lấy User ID
        SharedPreferences prefs = getSharedPreferences(Login.MY_PREFS, Context.MODE_PRIVATE);
        currentUserId = prefs.getString("USER_ID", null);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (currentUserId != null) {
            loadMyTickets(currentUserId);
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập để xem vé.", Toast.LENGTH_LONG).show();
        }
    }

    private void loadMyTickets(String userId) {
        // Gọi API GET /api/tickets/user/{userId}
        apiService.getUserTickets(userId).enqueue(new Callback<ApiResponse<List<MyTicket>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MyTicket>>> call, Response<ApiResponse<List<MyTicket>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<MyTicket> tickets = response.body().getData();

                    if (tickets != null && !tickets.isEmpty()) {
                        // 💡 ĐÃ SỬA: Thêm Context (MyTicketActivity.this) vào hàm tạo
                        adapter = new MyTicketAdapter(MyTicketActivity.this, tickets);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(MyTicketActivity.this, "Bạn chưa có vé nào.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Lỗi tải vé.";
                    Log.e("MYTICKET_API", "Load failed: " + response.code() + ", Msg: " + message);
                    Toast.makeText(MyTicketActivity.this, "Không thể tải vé: " + message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<MyTicket>>> call, Throwable t) {
                Log.e("MYTICKET_API", "Connection Failure", t);
                Toast.makeText(MyTicketActivity.this, "Lỗi kết nối.", Toast.LENGTH_LONG).show();
            }
        });
    }
}