package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // 🔹 THÊM IMPORT
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiService;
import com.example.myapplication.Network.ApiResponse;
import com.example.myapplication.Models.AuthResponse; // 🔹 THÊM IMPORT
import com.example.myapplication.Models.RegisterRequest; // 🔹 THÊM IMPORT

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class signUp extends AppCompatActivity {

    Button register, signIn;
    EditText userName, email, pass, confirmPass;
    CheckBox agree;
    private ApiService apiService; // 🔹 THÊM BIẾN API SERVICE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup);

        // 🔹 KHỞI TẠO API CLIENT
        apiService = ApiClient.getApiService();

        register = findViewById(R.id.register);
        signIn = findViewById(R.id.signIn);
        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        confirmPass = findViewById(R.id.confirmPass);
        agree = findViewById(R.id.agree);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u = userName.getText().toString().trim(); // Full Name
                String e = email.getText().toString().trim(); // Email
                String p = pass.getText().toString().trim(); // Pass
                String cp = confirmPass.getText().toString().trim(); // ConfPass

                // 🔹 CHỈ GIỮ LẠI VALIDATE LOGIC CƠ BẢN (Backend đã lo hashing và kiểm tra trùng lặp)
                if (u.isEmpty() || e.isEmpty() || p.isEmpty() || cp.isEmpty()) {
                    Toast.makeText(signUp.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                    Toast.makeText(signUp.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!p.equals(cp)) {
                    Toast.makeText(signUp.this, "Confirm password không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!agree.isChecked()) {
                    Toast.makeText(signUp.this, "Bạn phải đồng ý điều khoản", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 🔹 GỌI API REGISTER 🔹
                performRegistration(u, e, p);
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signUp.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // 🔹 LOGIC CHECK PASS NÀY CÓ THỂ ĐƯỢC XOÁ HOẶC GIỮ NẾU BACKEND KHÔNG ÉP BUỘC ĐỘ DÀI
    boolean checkPass(String pass) {
        // Giữ lại logic của bạn: phải có ít nhất 1 thường, 1 hoa
        boolean hasLow = false, hasUp = false;
        for (char c : pass.toCharArray()) {
            if (Character.isUpperCase(c)) hasUp = true;
            if (Character.isLowerCase(c)) hasLow = true;
            if (hasLow && hasUp) return true;
        }
        return false;
    }

    // 🔹 HÀM GỌI API REGISTER
    private void performRegistration(String fullname, String email, String password) {
        RegisterRequest request = new RegisterRequest(fullname, email, password);
        apiService.register(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Đăng ký thành công, chuyển sang màn hình Login
                    Toast.makeText(signUp.this, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(signUp.this, Login.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Lỗi: Email đã tồn tại hoặc lỗi khác từ server
                    String message = response.body() != null ? response.body().getMessage() : "Đăng ký thất bại. Vui lòng thử lại.";
                    Toast.makeText(signUp.this, message, Toast.LENGTH_LONG).show();
                    Log.e("REGISTER_API", "Error: " + response.code() + ", Message: " + message);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                Toast.makeText(signUp.this, "Lỗi kết nối: Không thể kết nối đến server.", Toast.LENGTH_LONG).show();
                Log.e("REGISTER_API", "Failure: " + t.getMessage(), t);
            }
        });
    }
}