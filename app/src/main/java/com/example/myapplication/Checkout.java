package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

public class Checkout extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.check_out);

        // 1. Ánh xạ Toolbar từ layout
        toolbar = findViewById(R.id.checkout_toolbar);

        // 2. Kích hoạt Toolbar làm ActionBar
        setSupportActionBar(toolbar);

        // 3. Hiển thị nút Back (Up button)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // Tùy chọn: Đặt tiêu đề cho Toolbar nếu muốn
            // getSupportActionBar().setTitle("Thanh toán");
        }
    }

    // 4. Xử lý sự kiện khi nhấn nút Back trên Toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Kiểm tra xem item được nhấn có phải là nút home/up không
        if (item.getItemId() == android.R.id.home) {
            finish(); // Đóng Activity hiện tại và quay lại màn hình trước
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}