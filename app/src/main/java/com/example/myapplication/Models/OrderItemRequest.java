package com.example.myapplication.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class OrderItemRequest implements Serializable {
    @SerializedName("seatTypeId")
    private String seatTypeId;

    @SerializedName("quantity")
    private int quantity;

    public OrderItemRequest(String seatTypeId, int quantity) {
        this.seatTypeId = seatTypeId;
        this.quantity = quantity;
    }
}