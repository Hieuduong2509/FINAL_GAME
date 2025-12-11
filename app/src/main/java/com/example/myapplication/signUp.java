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
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup);

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
                if (u.isEmpty() || e.isEmpty() || p.isEmpty() || cp.isEmpty()) {
                    Toast.makeText(signUp.this, "PLEASE ENTER ALL FIELDS", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                    Toast.makeText(signUp.this, "Email Invalid", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!p.equals(cp)) {
                    Toast.makeText(signUp.this, "Confirm password does not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!agree.isChecked()) {
                    Toast.makeText(signUp.this, "Please agree to the terms and conditions", Toast.LENGTH_SHORT).show();
                    return;
                }
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

    boolean checkPass(String pass) {
        boolean hasLow = false, hasUp = false;
        for (char c : pass.toCharArray()) {
            if (Character.isUpperCase(c)) hasUp = true;
            if (Character.isLowerCase(c)) hasLow = true;
            if (hasLow && hasUp) return true;
        }
        return false;
    }

    private void performRegistration(String fullname, String email, String password) {
        RegisterRequest request = new RegisterRequest(fullname, email, password);
        apiService.register(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(signUp.this, "SIGN UP SUCCESSFUL.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(signUp.this, Login.class);
                    startActivity(intent);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Failed to register.";
                    Toast.makeText(signUp.this, message, Toast.LENGTH_LONG).show();
                    Log.e("REGISTER_API", "Error: " + response.code() + ", Message: " + message);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                Toast.makeText(signUp.this, "Failed to register.", Toast.LENGTH_LONG).show();
                Log.e("REGISTER_API", "Failure: " + t.getMessage(), t);
            }
        });
    }
}