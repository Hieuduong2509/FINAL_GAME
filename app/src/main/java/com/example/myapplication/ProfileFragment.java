package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Models.User;
import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiService;
import com.example.myapplication.Network.ApiResponse;
import java.text.DecimalFormat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    // 1. KHAI BÁO BIẾN TOÀN CỤC Ở ĐÂY
    private User currentUser;

    private TextView tvName, tvJob, tvBalance, tvPhone, tvEmail, tvAddress, tvHomepage;
    private Button btnLogout;
    private ImageButton btnEdit;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = ApiClient.getApiService();

        // Ánh xạ View
        tvName = view.findViewById(R.id.UserName);
        tvJob = view.findViewById(R.id.UserJob);
        tvBalance = view.findViewById(R.id.UserBalance);
        tvPhone = view.findViewById(R.id.UserPhone);
        tvEmail = view.findViewById(R.id.UserMail);
        tvAddress = view.findViewById(R.id.UserAddress);
        tvHomepage = view.findViewById(R.id.UserHomepage);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEdit = view.findViewById(R.id.btn_edit);

        // Sự kiện nút Edit
        btnEdit.setOnClickListener(v -> {
            // Kiểm tra biến currentUser (giờ đã được khai báo)
            if (currentUser == null) {
                Toast.makeText(getContext(), "Đang tải dữ liệu...", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getContext(), EditProfile.class);
            // Key khớp với bên EditProfile
            intent.putExtra("USER_OBJECT", currentUser);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> performLogout());
    }

    // 2. DÙNG onResume ĐỂ TỰ ĐỘNG LOAD LẠI DỮ LIỆU KHI TỪ TRANG EDIT TRỞ VỀ
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getContext().getSharedPreferences(Login.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String userId = prefs.getString(Login.KEY_USER_ID, null);
        if (userId != null) {
            loadUserProfile(userId);
        } else {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserProfile(String userId) {
        apiService.getUserById(userId).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {

                    // 3. LƯU DỮ LIỆU VÀO BIẾN TOÀN CỤC currentUser
                    currentUser = response.body().getData();

                    if (currentUser != null) {
                        tvName.setText(currentUser.getFullName());
                        tvEmail.setText(currentUser.getEmail());
                        tvPhone.setText(currentUser.getPhone());
                        String role = currentUser.getRole();
                        tvJob.setText(role != null ? role.toUpperCase() : "MEMBER");

                        DecimalFormat formatter = new DecimalFormat("#,###");
                        double balance = currentUser.getBalance();
                        tvBalance.setText(formatter.format(balance) + " VNĐ");

                        // Set thêm address/homepage nếu có (tránh null)
                        tvAddress.setText("N/A");
                        tvHomepage.setText("N/A");
                    }
                } else {
                    // Xử lý lỗi nhẹ nhàng, không Toast liên tục nếu đang reload ngầm
                    // Toast.makeText(getContext(), "Cannot load user profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                // Log.e("ProfileError", t.getMessage());
            }
        });
    }

    private void performLogout() {
        if (getContext() == null) return;
        ApiClient.clearToken();
        SharedPreferences prefs = getContext().getSharedPreferences(Login.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Login.KEY_USER_ID);
        editor.remove(Login.KEY_EMAIL);
        editor.remove(Login.KEY_PASS);
        editor.remove(Login.KEY_REMEMBER);
        editor.apply();
        Intent intent = new Intent(getContext(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa lịch sử Activity để không back lại được
        startActivity(intent);

        Toast.makeText(getContext(), "Logout Success!", Toast.LENGTH_SHORT).show();
    }
}