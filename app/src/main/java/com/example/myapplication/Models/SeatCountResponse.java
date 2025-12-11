package com.example.myapplication.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class SeatCountResponse implements Serializable {

    @SerializedName("data")
    public List<SeatType> seatList;

    @SerializedName("totalAvailableSeats")
    public int totalAvailableSeats;

    public static class SeatType implements Serializable {


        @SerializedName("seatTypeId")
        public String seatTypeId;

        @SerializedName("seatName")
        public String seatName;

        @SerializedName("price")
        public double price;

        @SerializedName("availableSeats")
        public int availableSeats;
    }
}