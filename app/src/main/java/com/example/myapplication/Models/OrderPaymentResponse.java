package com.example.myapplication.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class OrderPaymentResponse implements Serializable {
    @SerializedName("newBalance")
    public double newBalance;

    @SerializedName("order")
    public Object order;
}