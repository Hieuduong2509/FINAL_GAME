package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// import androidx.activity.EdgeToEdge; // 1. Xóa
import androidx.appcompat.app.AppCompatActivity;
// import androidx.core.graphics.Insets; // 1. Xóa
// import androidx.core.view.ViewCompat; // 1. Xóa
// import androidx.core.view.WindowInsetsCompat; // 1. Xóa
import androidx.appcompat.widget.Toolbar;

public class EditProfile extends AppCompatActivity {
    Button btn2;
    EditText position;
    EditText username2;
    EditText email2;
    EditText address2;
    EditText homepage2;
    EditText phone2;
    // TextView MainName; // Dòng này chưa được ánh xạ, có thể gây lỗi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this); // 1. Xóa
        setContentView(R.layout.edit_profile);

        /* 1. Xóa toàn bộ khối setOnApplyWindowInsetsListener
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */


        // 2. Cập nhật tất cả các ID
        btn2 = findViewById(R.id.btn_save); // Cập nhật ID
        position = findViewById(R.id.edittext_position); // Cập nhật ID
        username2 = findViewById(R.id.edittext_username); // Cập nhật ID
        email2 = findViewById(R.id.edittext_email); // Cập nhật ID
        phone2 = findViewById(R.id.edittext_phone); // Cập nhật ID
        address2 = findViewById(R.id.edittext_address); // Cập nhật ID
        homepage2 = findViewById(R.id.edittext_homepage); // Cập nhật ID
        //        MainName = findViewById(R.id.ID_CUA_MAINNAME); // Bạn cần ánh xạ dòng này
        //        String temp = getIntent().getStringExtra("mainname1");
        //        MainName.setText(temp);

        // --- Logic nhận Intent (Giữ nguyên) ---
        Intent intent = getIntent();
        position.setText(intent.getStringExtra("UserJob"));
        username2.setText(intent.getStringExtra("UserName"));
        phone2.setText(intent.getStringExtra("UserPhone"));
        email2.setText(intent.getStringExtra("UserMail"));
        address2.setText(intent.getStringExtra("UserAddress"));
        homepage2.setText(intent.getStringExtra("UserHomepage"));

        // --- Logic trả về dữ liệu (Giữ nguyên) ---
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnData = new Intent();
                returnData.putExtra("position",position.getText().toString());
                returnData.putExtra("username",username2.getText().toString());
                returnData.putExtra("phone",phone2.getText().toString());
                returnData.putExtra("email",email2.getText().toString());
                returnData.putExtra("address",address2.getText().toString());
                returnData.putExtra("homepage",homepage2.getText().toString());
                setResult(RESULT_OK, returnData);
                finish();
            }
        });

        // 3. Xử lý Toolbar (Dọn dẹp lại)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hiển thị nút back và gán sự kiện click
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish()); // Đơn giản hóa nút back

    }
}