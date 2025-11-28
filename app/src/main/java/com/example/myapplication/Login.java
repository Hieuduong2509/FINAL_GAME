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

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {
    Button login, signUp, forgotPass;
    EditText userName, pass;
    CheckBox remember;

    // 🔹 Thêm các biến hằng để quản lý SharedPreferences
    SharedPreferences sharedPreferences;
    public static final String MY_PREFS = "MyLoginPrefs";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_REMEMBER = "remember";


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        login = findViewById(R.id.btnLogin);
        signUp = findViewById(R.id.signUp);
        userName = findViewById(R.id.userName);
        pass = findViewById(R.id.password);
        forgotPass = findViewById(R.id.forgot);
        remember = findViewById(R.id.remember);

        // 🔹 Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);

        // 🔹 Kiểm tra xem có dữ liệu đã lưu không
        loadPreferences();

        login.setOnClickListener(v -> {
            String u = userName.getText().toString().trim();
            String p = pass.getText().toString().trim();

            if(u.isEmpty() || p.isEmpty()){
                Toast.makeText(Login.this, "Vui lòng nhập UserName hoặc Password", Toast.LENGTH_SHORT).show();
            }
            else if(u.equals("admin") && p.equals("123")){

                // 🔹 XỬ LÝ LƯU TRẠNG THÁI
                if (remember.isChecked()) {
                    // Nếu "Remember Me" được chọn, lưu lại
                    savePreferences(u, p);
                } else {
                    // Nếu không, xoá dữ liệu đã lưu
                    clearPreferences();
                }

                Toast.makeText(Login.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this, HomeActivity.class); // Bạn có thể đổi Weather.class thành HomeActivity.class nếu muốn
                startActivity(intent);
                finish();
            }
            else if(!checkPass(p)){
                Toast.makeText(Login.this, "Mật khẩu không đúng yêu cầu", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(Login.this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });

        forgotPass.setOnClickListener(v -> {
            if(userName.getText().toString().equals("admin")){
                Toast.makeText(Login.this, "Forgot password thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Login.this, "Vui long nhap UserName", Toast.LENGTH_SHORT).show();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, signUp.class);
                startActivity(intent);
                finish(); // Cân nhắc có nên finish() ở đây không
            }
        });
    }

    boolean checkPass(String pass){
        if(pass.length() < 8) return false;
        boolean hasLow = false, hasUp = false;
        for(int i = 0; i < pass.length(); i++){
            char c  = pass.charAt(i);
            if(Character.isUpperCase(c)) hasUp = true;
            if(Character.isLowerCase(c)) hasLow = true;
            if(hasLow && hasUp) return true;
        }
        return false;
    }

    // 🔹 ----- CÁC HÀM MỚI ĐỂ LƯU VÀ TẢI ----- 🔹

    /**
     * Lưu thông tin đăng nhập vào SharedPreferences
     */
    private void savePreferences(String u, String p) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, u);
        editor.putString(KEY_PASSWORD, p);
        editor.putBoolean(KEY_REMEMBER, true);
        editor.apply(); // Lưu
    }

    /**
     * Xoá thông tin đã lưu
     */
    private void clearPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Xoá tất cả dữ liệu
        editor.apply();
    }

    /**
     * Tải thông tin đã lưu (nếu có) khi mở app
     */
    private void loadPreferences() {
        // Đọc giá trị, nếu không tìm thấy "KEY_REMEMBER" thì mặc định là false
        boolean isRemembered = sharedPreferences.getBoolean(KEY_REMEMBER, false);

        if (isRemembered) {
            // Nếu có lưu, lấy username và password
            String u = sharedPreferences.getString(KEY_USERNAME, "");
            String p = sharedPreferences.getString(KEY_PASSWORD, "");

            // Điền lại vào EditText
            userName.setText(u);
            pass.setText(p);
            // Check lại vào ô remember
            remember.setChecked(true);
        }
    }
}