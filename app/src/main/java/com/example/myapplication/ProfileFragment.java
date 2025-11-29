package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Models.User;
import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiService;
import com.example.myapplication.Network.ApiResponse;
import com.google.android.material.imageview.ShapeableImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserJob, tvFollowers, tvFollowing, tvUserPhone, tvUserMail, tvUserAddress, tvUserHomepage;
    private ImageButton editButton;
    private ShapeableImageView profileImage;

    private String currentUserId;
    private ApiService apiService;
    private User currentUserModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ Views
        tvUserName = view.findViewById(R.id.UserName);
        tvUserJob = view.findViewById(R.id.UserJob);
        tvFollowers = view.findViewById(R.id.txtFollowers);
        tvFollowing = view.findViewById(R.id.txtFollowing);
        tvUserPhone = view.findViewById(R.id.UserPhone);
        tvUserMail = view.findViewById(R.id.UserMail);
        tvUserAddress = view.findViewById(R.id.UserAddress);
        tvUserHomepage = view.findViewById(R.id.UserHomepage);

        editButton = view.findViewById(R.id.btn_edit);
        profileImage = view.findViewById(R.id.imageView);

        apiService = ApiClient.getApiService();
        currentUserId = getUserIdFromPrefs();

        // 2. Load dữ liệu khi fragment khởi tạo
        if (currentUserId != null) {
            loadUserProfile(currentUserId);
        } else {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập để xem hồ sơ.", Toast.LENGTH_LONG).show();
        }

        // 3. Xử lý nút Edit - 💡 CHỈ GỌI startActivity
        editButton.setOnClickListener(v -> {
            if (currentUserModel != null) {
                Intent intent = new Intent(requireActivity(), EditProfile.class);
                intent.putExtra("USER_OBJECT", currentUserModel);
                startActivity(intent); // 💡 Loại bỏ forResult
            } else {
                Toast.makeText(requireContext(), "Đang tải dữ liệu hồ sơ, vui lòng chờ...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 💡 ĐÃ XÓA onActivityResult và logic truyền ngược dữ liệu.

    /**
     * 💡 PHƯƠNG PHÁP DỰ PHÒNG: Bắt buộc tải lại dữ liệu từ DB mỗi khi quay lại
     */
    @Override
    public void onResume() {
        super.onResume();
        if (currentUserId != null) {
            Log.d("PROFILE_REFRESH", "onResume: Forcing profile reload from API.");
            loadUserProfile(currentUserId);
        }
    }

    private String getUserIdFromPrefs() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(Login.MY_PREFS, Context.MODE_PRIVATE);
        return prefs.getString("USER_ID", null);
    }

    private void loadUserProfile(String userId) {
        apiService.getUserById(userId).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    currentUserModel = user;
                    Log.d("PROFILE_API_DATA", "Loaded FullName: " + user.getFullName() + ", Phone: " + user.getPhone());
                    updateUI(user);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Response body is null.";
                    Log.e("PROFILE_API_ERROR", "Failed to load profile. HTTP: " + response.code() + ", Message: " + errorMsg);
                    Toast.makeText(requireContext(), "Không thể tải hồ sơ. Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("PROFILE_API_FAILURE", "Connection error: " + t.getMessage());
                Toast.makeText(requireContext(), "Lỗi kết nối khi tải hồ sơ.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(User user) {
        // Cập nhật giao diện với dữ liệu thật
        tvUserName.setText(user.getFullName() != null ? user.getFullName() : "Chưa đặt tên");
        tvUserJob.setText(user.getRole() != null ? user.getRole() : "Chưa cập nhật");
        tvFollowers.setText(String.valueOf(user.getFollow()));
        tvUserPhone.setText(user.getPhone() != null ? user.getPhone() : "Chưa cập nhật");
        tvUserMail.setText(user.getEmail());

        tvUserAddress.setText("Chưa cập nhật");
        tvUserHomepage.setText("Chưa cập nhật");
        // ... (Load ảnh)
    }
}