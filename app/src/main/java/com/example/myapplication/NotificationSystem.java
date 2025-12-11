package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationSystem {

    private static final String CHANNEL_ID = "PAYMENT_CHANNEL";
    private static final String CHANNEL_NAME = "\n" + "Payment notice";
    private static final int NOTIFICATION_ID = 1; // ID để cập nhật thông báo

    public static void sendNotification(Context context, String title, String message) {
        createNotificationChannel(context);

        // Tạo Intent để khi bấm vào thông báo sẽ mở màn hình "Thông Báo"
        Intent intent = new Intent(context, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Cấu hình giao diện thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notifications_24dp_e3e3e3_fill0_wght400_grad0_opsz24) // Dùng icon chuông có sẵn
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Quan trọng cao để hiện ngay
                .setAutoCancel(true) // Tự biến mất khi bấm vào
                .setContentIntent(pendingIntent); // Gắn sự kiện click

        // Hiển thị thông báo
        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("\n" + "Order status notification channel");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}