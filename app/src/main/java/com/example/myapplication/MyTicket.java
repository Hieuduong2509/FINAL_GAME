package com.example.myapplication;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class MyTicket implements Serializable {

    @SerializedName("eventName")
    private String eventName;

    @SerializedName("ticketCode")
    private String ticketCode;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("isScanned")
    private boolean isScanned;
    @SerializedName("eventId")
    private String eventId;
    @SerializedName("date")
    private String date;

    public MyTicket(String eventName, String ticketCode, int quantity, String imageUrl, boolean isScanned, String eventId, String date) {
        this.eventName = eventName;
        this.ticketCode = ticketCode;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.isScanned = isScanned;
        this.eventId = eventId;
        this.date = date;
    }

    public String getEventName() { return eventName; }
    public String getTicketCode() { return ticketCode; }
    public int getQuantity() { return quantity; }
    public String getImageUrl() { return imageUrl; }
    public boolean isScanned() { return isScanned; }

    public String getEventId() { return eventId; }

    public String getDate() { return date; }

    public void setScanned(boolean scanned) { isScanned = scanned; }
}