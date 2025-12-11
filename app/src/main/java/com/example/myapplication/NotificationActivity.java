package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        MaterialToolbar toolbar = findViewById(R.id.toolbarNotification);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        RecyclerView recyclerView = findViewById(R.id.recyclerNotification);
        List<NotificationModel> list = NotificationStorage.getNotifications(this);

        if (list.isEmpty()) {
            Toast.makeText(this, "You have no notifications yet.", Toast.LENGTH_SHORT).show();
        }
        NotificationAdapter adapter = new NotificationAdapter(list);
        recyclerView.setAdapter(adapter);
    }
}