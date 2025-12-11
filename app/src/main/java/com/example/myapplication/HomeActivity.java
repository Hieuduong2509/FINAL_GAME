package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

// 👇 CÁC IMPORT MỚI CẦN THÊM
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        // 1. Kích hoạt chạy ngầm ngay khi mở App
        startBackgroundJob();

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        // Setup Bottom Navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new MainFragment();
            } else if (id == R.id.nav_voucher) {
                selectedFragment = new VoucherFragment();
            } else if (id == R.id.nav_ticket) {
                selectedFragment = new TicketFragment();
            } else if (id == R.id.nav_artist) {
                selectedFragment = new ArtistFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MainFragment())
                    .commit();
        }
    }
    private void startBackgroundJob() {
        PeriodicWorkRequest checkEventRequest = new PeriodicWorkRequest.Builder(
                EventWorker.class,
                15,
                TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "CHECK_NEW_EVENTS_WORK",
                ExistingPeriodicWorkPolicy.KEEP,
                checkEventRequest
        );
    }
}