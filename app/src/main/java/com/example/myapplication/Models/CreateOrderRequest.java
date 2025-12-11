package com.example.myapplication.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class CreateOrderRequest implements Serializable {
    @SerializedName("userId")
    private String userId;

    @SerializedName("eventId")
    private String eventId;

    @SerializedName("items")
    private List<OrderItemRequest> items;
    private String voucherCode;
    public CreateOrderRequest(String userId, String eventId, List<OrderItemRequest> items, String voucherCode) {
        this.userId = userId;
        this.eventId = eventId;
        this.items = items;
        this.voucherCode = voucherCode;

    }
}