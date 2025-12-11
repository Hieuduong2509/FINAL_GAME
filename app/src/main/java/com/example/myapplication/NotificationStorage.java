package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotificationStorage {
    private static final String PREF_NAME = "APP_NOTIFICATIONS";
    private static final String KEY_LIST = "LIST_DATA";
    private static final String KEY_UNREAD = "UNREAD_COUNT";
    public static void addNotification(Context context, NotificationModel notification) {
        List<NotificationModel> list = getNotifications(context);
        list.add(0, notification);
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(list);
        editor.putString(KEY_LIST, json);
        int currentUnread = getUnreadCount(context);
        editor.putInt(KEY_UNREAD, currentUnread + 1);
        editor.apply();
    }

    public static List<NotificationModel> getNotifications(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_LIST, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<NotificationModel>>(){}.getType();
        return new Gson().fromJson(json, type);
    }
    public static int getUnreadCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_UNREAD, 0);
    }

    public static void clearUnreadCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_UNREAD, 0).apply();
    }
}