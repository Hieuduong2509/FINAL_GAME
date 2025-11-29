package com.example.myapplication;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Ticket implements Serializable {

    // 1. ÁNH XẠ CÁC TRƯỜNG TỪ JSON (Sử dụng private fields)
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

    // 2. CÁC TRƯỜNG CỦA MODEL CŨ (Sẽ được gán giá trị/tính toán/đặt mặc định)
    public String dateTime;
    public String seat;
    public String code;
    public int total;
    public int remain;

    public Ticket() {} // Constructor mặc định cho Gson

    // 3. GETTERS CẦN THIẾT
    public String getEventId() { return eventId; }
    public String getPrice() { return price; }

    /**
     * Tính toán chuỗi ngày giờ để hiển thị trên Adapter.
     * @return Chuỗi có dạng "YYYY-MM-DD @ HH:MM:SS"
     */
    public String getDateTime() {
        if (dateFull == null || timeOnly == null) return "N/A";
        // Lấy phần ngày (YYYY-MM-DD)
        String datePart = dateFull.length() >= 10 ? dateFull.substring(0, 10) : dateFull;
        return datePart + " @ " + timeOnly;
    }

    /**
     * Dùng trường seat để hiển thị giá tiền (vì Event listing không có thông tin ghế).
     */
    public String getSeat() {
        return "Giá: " + (price != null ? price + " VNĐ" : "N/A");
    }

    /**
     * Dùng trường code để hiển thị Event ID.
     */
    public String getCode() {
        // Chỉ hiển thị 8 ký tự đầu của ID cho ngắn gọn
        return "ID: " + (eventId != null && eventId.length() >= 8 ? eventId.substring(0, 8) : "N/A");
    }

    // Giữ lại các Getter/Setter nếu cần cho các trường cũ (total, remain)
    public int getTotal() { return total; }
    public int getRemain() { return remain; }
}