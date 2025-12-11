package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.Models.User;
import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiService;
import com.example.myapplication.Network.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends AppCompatActivity {
    Button btn2;
    EditText position;
    EditText username2;
    EditText email2;
    EditText address2;
    EditText homepage2;
    EditText phone2;

    private ApiService apiService;
    private User originalUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        apiService = ApiClient.getApiService();
        btn2 = findViewById(R.id.btn_save);
        position = findViewById(R.id.edittext_position);
        username2 = findViewById(R.id.edittext_username);
        email2 = findViewById(R.id.edittext_email);
        phone2 = findViewById(R.id.edittext_phone);
        address2 = findViewById(R.id.edittext_address);
        homepage2 = findViewById(R.id.edittext_homepage); // Lỗi ID
        Intent intent = getIntent();
        originalUser = (User) intent.getSerializableExtra("USER_OBJECT");
        if (originalUser != null) {
            Log.d("EDIT_PROFILE_DEBUG", "User ID loaded: " + originalUser.getUserId() + ", FullName: " + originalUser.getFullName());
            username2.setText(originalUser.getFullName() != null ? originalUser.getFullName() : "");
            email2.setText(originalUser.getEmail());
            phone2.setText(originalUser.getPhone() != null ? originalUser.getPhone() : "");
            position.setText(originalUser.getRole() != null ? originalUser.getRole() : "");
            if (email2.getText() != null) {
                email2.setEnabled(false);
            }
            address2.setText("N/A");
            homepage2.setText("N/A");
        } else {
            Toast.makeText(this, "ERROR: UNABLE TO LOAD USER.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originalUser == null) {
                    Toast.makeText(EditProfile.this, "ERROR: UNABLE TO LOAD USER.", Toast.LENGTH_SHORT).show();
                    return;
                }
                originalUser.setFullName(username2.getText().toString());
                originalUser.setPhone(phone2.getText().toString());
                updateProfileApi(originalUser);
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    private void updateProfileApi(User user) {
        Log.d("EDIT_PROFILE_DEBUG", "Attempting to save FullName: " + user.getFullName() + ", Phone: " + user.getPhone() + " for User ID: " + user.getUserId());

        apiService.updateCurrentUser(user).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {

                    Toast.makeText(EditProfile.this, "UPDATE SUCCESSFUL!", Toast.LENGTH_SHORT).show();

                    // 💡 CHỈ CẦN ĐÓNG ACTIVITY. Fragment sẽ tự reload qua onResume.
                    finish();

                } else {
                    String message = response.body() != null ? response.body().getMessage() : "UPDATE UNSUCCESSFUL.";

                    Log.e("UPDATE_API_ERROR", "Failed to update profile. HTTP: " + response.code() + ", Message: " + message);

                    Toast.makeText(EditProfile.this, "UPDATE UNSUCCESSFUL: " + message + ". ERROR: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(EditProfile.this, "NOT TO CONNECTED.", Toast.LENGTH_LONG).show();
                Log.e("UPDATE_API_FAILURE", "Connection error: " + t.getMessage(), t);
            }
        });
    }
}