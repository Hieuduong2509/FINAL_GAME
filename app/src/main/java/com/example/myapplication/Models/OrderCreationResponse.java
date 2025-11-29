package com.example.myapplication.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class OrderCreationResponse implements Serializable {

    @SerializedName("order")
    private OrderInfo order;

    public String getOrderId() {
        return order != null ? order.orderId : null;
    }

    private static class OrderInfo {
        @SerializedName("orderId") // Backend trả về camelCase "orderId" trong model Order
        String orderId;
    }
}