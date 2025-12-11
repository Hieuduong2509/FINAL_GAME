package com.example.myapplication.Models;

import com.google.gson.annotations.SerializedName;

public class FollowResponse {
    @SerializedName("status")
    private String status; // Giá trị sẽ là "followed" hoặc "unfollowed"

    public String getStatus() {
        return status;
    }
}