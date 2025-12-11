package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.Random;

public class NotificationHelper {

    private static final String CHANNEL_ID = "ARTIST_EVENT_CHANNEL";
    private static final String CHANNEL_NAME = "Nghệ sĩ & Sự kiện";

    public static void showNotification(Context context, String artistName, String eventName, String eventId) {
        createNotificationChannel(context);
        Intent intent = new Intent(context, TicketDetailActivity.class);
        intent.putExtra("EVENT_ID", eventId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notifications_24dp_e3e3e3_fill0_wght400_grad0_opsz24)
                .setContentTitle("Sự kiện mới từ " + artistName + "!")
                .setContentText(artistName + " vừa tham gia: " + eventName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat.from(context).notify(new Random().nextInt(), builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Thông báo sự kiện mới");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }
}