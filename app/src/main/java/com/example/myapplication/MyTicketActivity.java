package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

// Activity này hiển thị danh sách vé đã mua của người dùng
public class MyTicketActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private MyTicketAdapter adapter;
    private List<MyTicket> myTicketList; // Sử dụng Model Vé Đã Mua

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ticket);

        toolbar = findViewById(R.id.toolbarMyTickets);
        recyclerView = findViewById(R.id.recyclerMyTickets);

        // 1. Thiết lập Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // 2. Tải dữ liệu giả lập (thay bằng API call sau này)
        loadMyTicketData();

        // 3. Thiết lập RecyclerView
        adapter = new MyTicketAdapter(this, myTicketList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    // Xử lý nút back
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Hàm giả lập load danh sách vé đã mua của người dùng
     */
    private void loadMyTicketData() {
        myTicketList = new ArrayList<>();
        // Giả lập dữ liệu vé đã mua và đã thanh toán
        myTicketList.add(new MyTicket("Hòa Nhạc Giao Hưởng", "SYM12345", 2,
                "https://placehold.co/100x100/176B87/FFFFFF?text=SYM", false)); // Chưa điểm danh
        myTicketList.add(new MyTicket("Vũ Hội Hóa Trang", "VHM98765", 1,
                "https://placehold.co/100x100/64CCC5/000000?text=VHH", false)); // Chưa điểm danh
        myTicketList.add(new MyTicket("Triển Lãm Công Nghệ", "TLT67890", 3,
                "https://placehold.co/100x100/DAFFFB/000000?text=TLT", true)); // Đã điểm danh
    }
}