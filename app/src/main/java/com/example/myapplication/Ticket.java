package com.example.myapplication;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Ticket implements Serializable {

    @SerializedName("event_id")
    private String eventId;

    @SerializedName("title")
    public String eventName;

    @SerializedName("place")
    public String location;

    @SerializedName("date")
    private String dateFull;

    @SerializedName("time")
    private String timeOnly;

    @SerializedName("price")
    private String price;

    @SerializedName("description")
    private String description;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("PosterBase64")
    private String posterBase64;

    public int total;
    public int remain;

    public Ticket() {}

    // --- GETTERS ---
    public String getEventId() { return eventId; }
    public String getPrice() { return price; }

    public String getDescription() {
        return description != null ? description : "Chưa có mô tả.";
    }
    public String getPosterBase64() {
        return posterBase64;
    }
    public String getImageUrl() {
        if (posterBase64 != null && !posterBase64.isEmpty()) {
            return posterBase64;
        }
        return imageUrl;
    }
    public String getDateTime() {
        if (dateFull == null || timeOnly == null) return "N/A";
        String datePart = dateFull.length() >= 10 ? dateFull.substring(0, 10) : dateFull;
        return datePart + " @ " + timeOnly;
    }

    public String getSeat() {
        return "Price: " + (price != null ? price + " VNĐ" : "N/A");
    }

    public String getCode() {
        return "ID: " + (eventId != null && eventId.length() >= 8 ? eventId.substring(0, 8) : "N/A");
    }
}