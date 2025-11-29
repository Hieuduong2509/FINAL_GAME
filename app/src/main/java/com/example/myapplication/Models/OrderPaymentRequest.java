package com.example.myapplication.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class OrderPaymentRequest implements Serializable {
    @SerializedName("orderId")
    private String orderId;

    @SerializedName("paymentMethod") // 💡 PHẢI CÓ THAM SỐ NÀY
    private String paymentMethod;

    // 💡 SỬA HÀM TẠO: Thêm paymentMethod
    public OrderPaymentRequest(String orderId, String paymentMethod) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
    }
}