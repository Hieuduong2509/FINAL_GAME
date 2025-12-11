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

import com.example.myapplication.MyTicket;
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

    public static final String SHARED_PREF_NAME = "prefShare";
    public static final String KEY_USER_ID = "USER_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ticket);

        apiService = ApiClient.getApiService();
        recyclerView = findViewById(R.id.recyclerMyTickets);

        Toolbar toolbar = findViewById(R.id.toolbarMyTickets);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(MyTicketActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = prefs.getString(KEY_USER_ID, null);

        Log.d("MY_TICKET", "User ID loaded: " + currentUserId);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (currentUserId != null) {
            loadMyTickets(currentUserId);
        } else {
            Toast.makeText(this, "Please login first.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MyTicketActivity.this, Login.class);
            startActivity(intent);
            finish();
        }
    }

    private void loadMyTickets(String userId) {
        apiService.getUserTickets(userId).enqueue(new Callback<ApiResponse<List<MyTicket>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MyTicket>>> call, Response<ApiResponse<List<MyTicket>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<MyTicket> tickets = response.body().getData();
                    if (tickets != null && !tickets.isEmpty()) {
                        adapter = new MyTicketAdapter(MyTicketActivity.this, tickets);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(MyTicketActivity.this, "You don't have any tickets.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("MY_TICKET", "API Error code: " + response.code());
                    Toast.makeText(MyTicketActivity.this, "Cannot load your tickets.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<MyTicket>>> call, Throwable t) {
                Log.e("MY_TICKET", "Network Fail: " + t.getMessage());
                Toast.makeText(MyTicketActivity.this, "Error to connect internet.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}