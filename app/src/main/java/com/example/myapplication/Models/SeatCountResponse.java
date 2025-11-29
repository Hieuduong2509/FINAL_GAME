package com.example.myapplication.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class SeatCountResponse implements Serializable {

    // Backend trả về object: { data: [danh_sách], totalAvailableSeats: ... }
    @SerializedName("data")
    public List<SeatType> seatList;

    @SerializedName("totalAvailableSeats")
    public int totalAvailableSeats;

    public static class SeatType implements Serializable {
        // 💡 SỬA: Dùng camelCase để khớp với Node.js Model (EventSeatType)

        @SerializedName("seatTypeId") // Backend: this.seatTypeId
        public String seatTypeId;

        @SerializedName("seatName")   // Backend: this.seatName
        public String seatName;

        @SerializedName("price")      // Backend: this.price
        public double price;

        @SerializedName("availableSeats") // Backend: this.availableSeats
        public int availableSeats;
    }
}