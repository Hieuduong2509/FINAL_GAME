package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.Artist;
import com.example.myapplication.Models.FollowResponse; // Đảm bảo bạn đã tạo model này
import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiResponse;
import com.example.myapplication.Network.ApiService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistDetailActivity extends AppCompatActivity {

    // Views
    private MaterialToolbar toolbar;
    private ShapeableImageView ivAvatar;
    private TextView tvName, tvFollower, tvEmail;
    private RecyclerView recyclerEvents;
    private MaterialButton btnFollow;

    // Data & Logic
    private ApiService apiService;
    private String artistId;
    private Artist currentArtist;
    private FollowManager followManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        // 1. Khởi tạo
        apiService = ApiClient.getApiService();
        followManager = new FollowManager(this);

        // 2. Ánh xạ Views
        toolbar = findViewById(R.id.toolbarArtistDetail);
        ivAvatar = findViewById(R.id.iv_artist_detail_avatar);
        tvName = findViewById(R.id.tv_artist_detail_name);
        tvFollower = findViewById(R.id.tv_artist_detail_category);
        tvEmail = findViewById(R.id.tv_artist_detail_description);
        recyclerEvents = findViewById(R.id.recyclerArtistEvents);
        btnFollow = findViewById(R.id.btn_follow_artist);

        // 3. Setup Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // 4. Nhận dữ liệu từ Intent
        Artist intentArtist = (Artist) getIntent().getSerializableExtra("ARTIST_OBJECT");

        if (intentArtist != null) {
            artistId = intentArtist.getId();

            // --- 👇 QUAN TRỌNG: KHẮC PHỤC LỖI RESET SỐ LƯỢNG 👇 ---

            // Tìm xem nghệ sĩ này đã được lưu trong máy (FollowManager) chưa?
            // (Hàm getSavedArtist bạn vừa thêm vào FollowManager)
            Artist savedArtist = followManager.getSavedArtist(artistId);

            if (savedArtist != null) {
                // TRƯỜNG HỢP 1: ĐÃ FOLLOW
                // Dùng dữ liệu đã lưu trong máy (savedArtist)
                // Vì biến này chứa số follower MỚI NHẤT (ví dụ: 101)
                currentArtist = savedArtist;

                // Cập nhật nút thành UNFOLLOW
                updateButtonUI(true);
            } else {
                // TRƯỜNG HỢP 2: CHƯA FOLLOW
                // Dùng dữ liệu cũ từ Intent (ví dụ: 100)
                currentArtist = intentArtist;

                // Cập nhật nút thành FOLLOW
                updateButtonUI(false);
            }

            // Hiển thị thông tin lên giao diện (Lúc này số follower sẽ đúng)
            displayBasicInfo(currentArtist);

            // Gọi API để lấy thêm danh sách sự kiện (Events)
            loadArtistDetail(artistId);

        } else {
            Toast.makeText(this, "NOT FOUND ARTIST", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 5. Xử lý sự kiện bấm nút Follow
        btnFollow.setOnClickListener(v -> handleFollowClick());
    }

    /**
     * Xử lý logic khi bấm nút Follow/Unfollow
     */
    private void handleFollowClick() {
        // Khóa nút để tránh spam
        btnFollow.setEnabled(false);

        // Gọi API
        apiService.followInviter(artistId).enqueue(new Callback<ApiResponse<FollowResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FollowResponse>> call, Response<ApiResponse<FollowResponse>> response) {
                btnFollow.setEnabled(true); // Mở lại nút

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Lấy trạng thái từ Server trả về
                    FollowResponse data = response.body().getData();
                    String status = data.getStatus(); // "followed" hoặc "unfollowed"

                    if ("followed".equals(status)) {
                        // --- TRƯỜNG HỢP: FOLLOW THÀNH CÔNG ---

                        // 1. Tăng số lượng hiển thị (UI)
                        currentArtist.setFollower(currentArtist.getFollower() + 1);

                        // 2. Lưu vào FollowManager (để đồng bộ với ArtistFragment)
                        followManager.saveArtist(currentArtist);

                        // 3. Cập nhật nút thành UNFOLLOW
                        updateButtonUI(true);

                        Toast.makeText(ArtistDetailActivity.this, "FOLLOW!", Toast.LENGTH_SHORT).show();

                    } else if ("unfollowed".equals(status)) {
                        // --- TRƯỜNG HỢP: UNFOLLOW THÀNH CÔNG ---

                        // 1. Giảm số lượng hiển thị (không cho nhỏ hơn 0)
                        int newCount = Math.max(0, currentArtist.getFollower() - 1);
                        currentArtist.setFollower(newCount);

                        // 2. Xóa khỏi FollowManager
                        followManager.removeArtist(artistId);

                        // 3. Cập nhật nút thành FOLLOW
                        updateButtonUI(false);

                        Toast.makeText(ArtistDetailActivity.this, "UNFOLLOW!", Toast.LENGTH_SHORT).show();
                    }

                    // Cập nhật text hiển thị số lượng
                    tvFollower.setText(currentArtist.getFollower() + " FOLLOWERS");

                } else {
                    Toast.makeText(ArtistDetailActivity.this, "ERROR: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<FollowResponse>> call, Throwable t) {
                btnFollow.setEnabled(true);
                Toast.makeText(ArtistDetailActivity.this, "ERROR CONNECT TO INTERNET!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateButtonUI(boolean isFollowed) {
        if (isFollowed) {
            btnFollow.setText("UNFOLLOW");
            btnFollow.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            btnFollow.setText("FOLLOW");
            btnFollow.setBackgroundColor(getResources().getColor(R.color.black)); // Hoặc R.color.purple_500 tùy theme
        }
    }

    private void displayBasicInfo(Artist artist) {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(artist.getName());
        tvName.setText(artist.getName());
        tvFollower.setText(artist.getFollower() + " FOLLOWERS");
        tvEmail.setText(artist.getEmail() != null ? artist.getEmail() : "");

        String imageUrl = artist.getAvatarUrl();
        // Xử lý đường dẫn ảnh nếu cần (thêm Base URL nếu thiếu)
        if (imageUrl != null && !imageUrl.startsWith("http")) {
            // Đảm bảo ApiClient.BASE_URL không có dấu / ở cuối hoặc imageUrl không có / ở đầu để tránh trùng
            // Đây là ví dụ đơn giản:
            imageUrl = "http://10.0.2.2:5000" + imageUrl;
        }

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .into(ivAvatar);
    }

    private void loadArtistDetail(String id) {
        apiService.getInviterDetail(id).enqueue(new Callback<ApiResponse<Artist>>() {
            @Override
            public void onResponse(Call<ApiResponse<Artist>> call, Response<ApiResponse<Artist>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Artist fullDetailArtist = response.body().getData();

                    if (fullDetailArtist != null) {
                        // Cập nhật danh sách sự kiện
                        List<com.example.myapplication.Ticket> events = fullDetailArtist.getUpcomingEvents();

                        if (events != null && !events.isEmpty()) {
                            TicketAdapter eventAdapter = new TicketAdapter(events);
                            recyclerEvents.setLayoutManager(new LinearLayoutManager(ArtistDetailActivity.this));
                            recyclerEvents.setAdapter(eventAdapter);
                        } else {
                            Toast.makeText(ArtistDetailActivity.this, "The artist has no upcoming events yet.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Artist>> call, Throwable t) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}