package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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

    public static final String SHARED_PREF_NAME = "prefShare";
    public static final String KEY_USER_ID = "USER_ID";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASS = "password";
    public static final String KEY_REMEMBER = "remember";

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ApiClient.initialize(getApplicationContext());
        apiService = ApiClient.getApiService();
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        checkAutoLogin();

        login = findViewById(R.id.btnLogin);
        signUp = findViewById(R.id.signUp);
        userName = findViewById(R.id.userName);
        pass = findViewById(R.id.password);
        forgotPass = findViewById(R.id.forgot);
        remember = findViewById(R.id.remember);
        loadSavedCredentials();

        login.setOnClickListener(v -> {
            String emailStr = userName.getText().toString().trim();
            String passwordStr = pass.getText().toString().trim();

            if(emailStr.isEmpty() || passwordStr.isEmpty()){
                Toast.makeText(Login.this, "Please enter email and password.", Toast.LENGTH_SHORT).show();
            } else {
                performLogin(emailStr, passwordStr);
            }
        });

        forgotPass.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, signUp.class);
            startActivity(intent);
        });
    }
    private void checkAutoLogin() {
        String token = ApiClient.getToken();
        boolean isRemembered = sharedPreferences.getBoolean(KEY_REMEMBER, false);
        if (token != null && isRemembered) {
            String savedEmail = sharedPreferences.getString(KEY_EMAIL, "User");
            Toast.makeText(Login.this, "Welcome back, " + savedEmail + "!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Login.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void loadSavedCredentials() {
        boolean isRemembered = sharedPreferences.getBoolean(KEY_REMEMBER, false);
        if (isRemembered) {
            String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
            String savedPass = sharedPreferences.getString(KEY_PASS, "");

            userName.setText(savedEmail);
            pass.setText(savedPass);
            remember.setChecked(true);
        }
    }

    private void performLogin(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        apiService.login(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {

                    AuthResponse authData = response.body().getData();
                    ApiClient.saveToken(authData.getAccessToken());

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_USER_ID, authData.getUser().getUserId());

                    // LOGIC LƯU TRẠNG THÁI
                    if (remember.isChecked()) {
                        editor.putString(KEY_EMAIL, email);
                        editor.putString(KEY_PASS, password);
                        editor.putBoolean(KEY_REMEMBER, true);
                    } else {
                        editor.remove(KEY_EMAIL);
                        editor.remove(KEY_PASS);
                        editor.remove(KEY_REMEMBER);
                    }
                    editor.apply();

                    Toast.makeText(Login.this, "Login Success!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, HomeActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Wrong email or password.";
                    Toast.makeText(Login.this, "Error: " + message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                Toast.makeText(Login.this, "Error connection", Toast.LENGTH_LONG).show();
            }
        });
    }
}