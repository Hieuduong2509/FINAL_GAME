package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class signUp extends AppCompatActivity {

    Button register, signIn;
    EditText userName, email, pass, confirmPass;
    CheckBox agree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup);
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
                String u = userName.getText().toString().trim();
                String e = email.getText().toString().trim();
                String p = pass.getText().toString();
                String cp = confirmPass.getText().toString();

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

                if (p.length() < 6) {
                    Toast.makeText(signUp.this, "Mật khẩu phải dài hơn 6 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!checkPass(p)) {
                    Toast.makeText(signUp.this, "Mật khẩu phải có ít nhất 1 ký tự thường và 1 ký tự in hoa", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!agree.isChecked()) {
                    Toast.makeText(signUp.this, "Bạn phải đồng ý điều khoản", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(signUp.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
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
}
