package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Models.ResetPasswordRequest;
import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiResponse;
import com.example.myapplication.Network.ApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etNewPass, etConfirmPass;
    private MaterialButton btnConfirm;
    private TextView tvBack;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        etEmail = findViewById(R.id.et_forgot_email);
        etNewPass = findViewById(R.id.et_new_password);
        etConfirmPass = findViewById(R.id.et_confirm_password);
        btnConfirm = findViewById(R.id.btn_confirm_reset);
        tvBack = findViewById(R.id.tv_back_to_login);
        apiService = ApiClient.getApiService();
        tvBack.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> handleResetPassword());
    }
    private void handleResetPassword() {
        String email = etEmail.getText().toString().trim();
        String pass = etNewPass.getText().toString().trim();
        String confirm = etConfirmPass.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "PLEASE ENTER EMAIL AND PASSWORD.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirm)) {
            Toast.makeText(this, "PASSWORD DOES NOT MATCH.", Toast.LENGTH_SHORT).show();
            return;
        }
        ResetPasswordRequest request = new ResetPasswordRequest(email, pass);
        apiService.resetPassword(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ForgotPasswordActivity.this, "SUCCESS.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "ERROR: " + response.code();
                    Toast.makeText(ForgotPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "NOT TO CONNECT SERVER", Toast.LENGTH_SHORT).show();
            }
        });
    }
}