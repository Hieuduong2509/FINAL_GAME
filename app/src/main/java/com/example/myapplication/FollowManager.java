package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

// ⚠️ Lưu ý: Import đúng class Artist và Ticket mà ApiService đang dùng
import com.example.myapplication.Models.Artist; // Hoặc com.example.myapplication.Artist tùy project bạn
import com.example.myapplication.Ticket;        // Class Ticket bạn vừa gửi

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FollowManager {
    private static final String PREF_NAME = "FollowPrefs";
    private static final String KEY_FOLLOWED_ARTISTS = "FollowedArtists";

    private SharedPreferences prefs;
    private Gson gson;
    private Context context;

    public FollowManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public List<Artist> getFollowedArtists() {
        String json = prefs.getString(KEY_FOLLOWED_ARTISTS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Artist>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public boolean isFollowed(String artistId) {
        if (artistId == null) return false;
        List<Artist> list = getFollowedArtists();
        for (Artist a : list) {
            if (a.getId().equals(artistId)) return true;
        }
        return false;
    }

    public Artist getSavedArtist(String artistId) {
        List<Artist> list = getFollowedArtists();
        for (Artist a : list) {
            if (a.getId().equals(artistId)) {
                return a;
            }
        }
        return null;
    }

    public void saveArtist(Artist artist) {
        List<Artist> list = getFollowedArtists();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(artist.getId())) {
                list.remove(i);
                break;
            }
        }
        list.add(artist);
        prefs.edit().putString(KEY_FOLLOWED_ARTISTS, gson.toJson(list)).apply();
    }

    public void removeArtist(String artistId) {
        List<Artist> list = getFollowedArtists();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(artistId)) {
                list.remove(i);
                break;
            }
        }
        prefs.edit().putString(KEY_FOLLOWED_ARTISTS, gson.toJson(list)).apply();
    }


    public void checkForNewEvents(Artist serverArtist) {
        if (serverArtist == null || serverArtist.getUpcomingEvents() == null) return;
        String key = "EVENTS_OF_" + serverArtist.getId();
        String savedEventIds = prefs.getString(key, "");
        StringBuilder newSavedIds = new StringBuilder(savedEventIds);
        boolean hasNew = false;
        for (Ticket event : serverArtist.getUpcomingEvents()) {
            String eventId = event.getEventId();
            if (eventId != null && !savedEventIds.contains(eventId)) {
                NotificationHelper.showNotification(
                        context,
                        serverArtist.getName(),
                        event.eventName,
                        eventId
                );
                newSavedIds.append(eventId).append(",");
                hasNew = true;
            }
        }
        if (hasNew) {
            prefs.edit().putString(key, newSavedIds.toString()).apply();
        }
    }
}