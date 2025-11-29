// File: com.example.myapplication.Login.java

package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiService;
import com.example.myapplication.Network.ApiResponse;
import com.example.myapplication.Models.AuthResponse;
import com.example.myapplication.Models.LoginRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    Button login, signUp, forgotPass;
    EditText userName, pass;
    CheckBox remember;

    SharedPreferences sharedPreferences;
    public static final String MY_PREFS = "MyLoginPrefs";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_REMEMBER = "remember";

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // 🔹 KHỞI TẠO API CLIENT
        ApiClient.initialize(getApplicationContext());
        apiService = ApiClient.getApiService();

        login = findViewById(R.id.btnLogin);
        signUp = findViewById(R.id.signUp);
        userName = findViewById(R.id.userName);
        pass = findViewById(R.id.password);
        forgotPass = findViewById(R.id.forgot);
        remember = findViewById(R.id.remember);

        // 💡 sharedPreferences dùng cho EMAIL và USER_ID
        sharedPreferences = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);

        loadPreferences();

        login.setOnClickListener(v -> {
            String emailStr = userName.getText().toString().trim();
            String passwordStr = pass.getText().toString().trim();

            if(emailStr.isEmpty() || passwordStr.isEmpty()){
                Toast.makeText(Login.this, "Vui lòng nhập Email và Mật khẩu", Toast.LENGTH_SHORT).show();
            }
            else {
                performLogin(emailStr, passwordStr);
            }
        });

        forgotPass.setOnClickListener(v -> {
            Toast.makeText(Login.this, "Tính năng Quên Mật Khẩu chưa sẵn sàng.", Toast.LENGTH_SHORT).show();
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, signUp.class);
                startActivity(intent);
            }
        });
    }

    // 🔹 HÀM GỌI API LOGIN
    private void performLogin(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        apiService.login(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {

                    AuthResponse authData = response.body().getData();
                    String accessToken = authData.getAccessToken();
                    String userId = authData.getUser().getUserId();

                    // 💡 LƯU TOKEN QUA API CLIENT MỚI (Khắc phục lỗi 401)
                    ApiClient.saveToken(accessToken);

                    // LƯU USERID và TÙY CHỌN REMEMBER ME
                    if (remember.isChecked()) {
                        savePreferences(email, userId);
                    } else {
                        clearPreferences();
                        saveUserId(userId);
                    }

                    Toast.makeText(Login.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, HomeActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Sai email hoặc mật khẩu";
                    Toast.makeText(Login.this, "Đăng nhập thất bại: " + message, Toast.LENGTH_LONG).show();
                    Log.e("LOGIN_API", "Error: " + response.code() + ", Message: " + message);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                Toast.makeText(Login.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LOGIN_API", "Failure: " + t.getMessage(), t);
            }
        });
    }

    // 🔹 ----- CÁC HÀM LƯU VÀ TẢI (CẬP NHẬT) ----- 🔹
    private void savePreferences(String email, String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString("USER_ID", userId);
        editor.putBoolean(KEY_REMEMBER, true);
        editor.apply();
    }

    private void saveUserId(String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_ID", userId);
        editor.apply();
    }

    private void clearPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Xóa tất cả các pref trong MY_PREFS
        editor.clear();
        editor.apply();
        // Xóa token riêng biệt trong AuthPrefs (thực tế đang dùng MY_PREFS)
        ApiClient.clearToken();
    }

    private void loadPreferences() {
        boolean isRemembered = sharedPreferences.getBoolean(KEY_REMEMBER, false);
        String token = ApiClient.getToken(); // LẤY TOKEN TỪ API CLIENT MỚI

        if (isRemembered) {
            String email = sharedPreferences.getString(KEY_EMAIL, "");

            if (!email.isEmpty() && token != null) {
                userName.setText(email);
                remember.setChecked(true);
            }
        }
    }
}