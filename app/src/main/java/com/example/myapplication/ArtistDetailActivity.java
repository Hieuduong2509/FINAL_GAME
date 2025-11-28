package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.button.MaterialButton;

// Import TicketAdapter bạn đã có để hiển thị danh sách sự kiện
import com.example.myapplication.TicketAdapter;

public class ArtistDetailActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private ShapeableImageView ivAvatar;
    private TextView tvName, tvCategory, tvDescription;
    private RecyclerView recyclerEvents;
    private MaterialButton btnFollow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        // 1. Ánh xạ Views
        toolbar = findViewById(R.id.toolbarArtistDetail);
        ivAvatar = findViewById(R.id.iv_artist_detail_avatar);
        tvName = findViewById(R.id.tv_artist_detail_name);
        tvCategory = findViewById(R.id.tv_artist_detail_category);
        tvDescription = findViewById(R.id.tv_artist_detail_description);
        recyclerEvents = findViewById(R.id.recyclerArtistEvents);
        btnFollow = findViewById(R.id.btn_follow_artist);

        // 2. Thiết lập Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // 3. Lấy dữ liệu từ Intent
        Artist artist = (Artist) getIntent().getSerializableExtra("ARTIST_OBJECT");

        if (artist != null) {
            // 4. Hiển thị thông tin Nghệ sĩ
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(artist.getName());
            }
            tvName.setText(artist.getName());
            tvCategory.setText(artist.getCategory());
            tvDescription.setText(artist.getDescription());

            // Load ảnh
            Glide.with(this)
                    .load(artist.getAvatarUrl())
                    .placeholder(R.drawable.profile)
                    .into(ivAvatar);

            // 5. Thiết lập RecyclerView Sự kiện
            TicketAdapter eventAdapter = new TicketAdapter(artist.getUpcomingEvents());
            recyclerEvents.setLayoutManager(new LinearLayoutManager(this));
            recyclerEvents.setAdapter(eventAdapter);

            // 6. Xử lý nút Follow
            btnFollow.setOnClickListener(v -> {
                Toast.makeText(ArtistDetailActivity.this, "Đã theo dõi " + artist.getName(), Toast.LENGTH_SHORT).show();
            });

        } else {
            Toast.makeText(this, "Không tìm thấy thông tin nghệ sĩ.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // Xử lý nút back trên Toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}