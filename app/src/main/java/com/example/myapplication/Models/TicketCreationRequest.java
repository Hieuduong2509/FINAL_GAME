package com.example.myapplication.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class TicketCreationRequest implements Serializable {
    @SerializedName("eventId")
    private String eventId;

    @SerializedName("userId")
    private String userId;

    @SerializedName("ticketType")
    private String ticketType;

    @SerializedName("price")
    private double price;

    @SerializedName("quantity")
    private int quantity;

    public TicketCreationRequest(String userId, String eventId, String ticketType, double price, int quantity) {
        this.userId = userId;
        this.eventId = eventId;
        this.ticketType = ticketType;
        this.price = price;
        this.quantity = quantity;
    }
}