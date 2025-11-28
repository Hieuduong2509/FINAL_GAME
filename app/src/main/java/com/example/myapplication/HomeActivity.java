package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

// Chỉ cần BottomNavigationView
import com.google.android.material.bottomnavigation.BottomNavigationView;
// Không cần FloatingActionButton nữa
// import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    // private FloatingActionButton fabQrScan; // 🔹 XOÁ BIẾN NÀY

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page); // Load layout home_page

        // Ánh xạ ID
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        // fabQrScan = findViewById(R.id.nav_order); // 🔹 XOÁ DÒNG NÀY (Dòng này sai)

        // 🔹 XOÁ CÁC DÒNG NÀY
        // fabQrScan.bringToFront();
        // setupFab();

        // 🔹 SỬA LẠI TOÀN BỘ LISTENER
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new MainFragment();
            } else if (id == R.id.nav_voucher) {
                selectedFragment = new VoucherFragment();
                Toast.makeText(this, "Tính năng ưu đãi đang phát triển", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_ticket) { // 🔹 THÊM CASE MỚI
                selectedFragment = new TicketFragment(); // (Bạn cần tạo Fragment này)
                Toast.makeText(this, "Đặt Vé", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_order) { // 🔹 THÊM CASE MỚI
                selectedFragment = new OrderFragment(); // (Bạn cần tạo Fragment này)
                Toast.makeText(this, "Giỏ Hàng", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment) // 🔹 Sửa lỗi typo
                        .commit();
            }
            return true;
        });

        // Tải Fragment mặc định (giữ nguyên)
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MainFragment()) // 🔹 Sửa lỗi typo
                    .commit();
        }
    }

    // 🔹 XOÁ TOÀN BỘ HÀM NÀY
    /*
    private void setupFab() {
        // Giữ nguyên
        fabQrScan.setOnClickListener(v ->
                Toast.makeText(HomeActivity.this, "Mở chức năng quét QR...", Toast.LENGTH_SHORT).show()
        );
    }
    */
}