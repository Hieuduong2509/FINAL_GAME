package com.example.myapplication;

import java.io.Serializable;

// Model cho các vé đã mua/thanh toán của người dùng
public class MyTicket implements Serializable {
    private String eventName;
    private String ticketCode;
    private int quantity;
    private String imageUrl;
    private boolean isScanned; // Trạng thái: true nếu đã điểm danh/sử dụng

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