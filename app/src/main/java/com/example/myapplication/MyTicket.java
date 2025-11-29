package com.example.myapplication;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

// Model cho các vé đã mua/thanh toán của người dùng
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

    public MyTicket(String eventName, String ticketCode, int quantity, String imageUrl, boolean isScanned) {
        this.eventName = eventName;
        this.ticketCode = ticketCode;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.isScanned = isScanned;
    }

    // Getters and Setters (Đảm bảo có đủ để truy cập dữ liệu trong Adapter)
    public String getEventName() { return eventName; }
    public String getTicketCode() { return ticketCode; }
    public int getQuantity() { return quantity; }
    public String getImageUrl() { return imageUrl; }
    public boolean isScanned() { return isScanned; }

    public void setScanned(boolean scanned) { isScanned = scanned; }
}