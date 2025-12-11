package com.example.myapplication.Models;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("refreshToken")
    private String refreshToken;

    @SerializedName("user")
    private User user;

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public User getUser() { return user; }
}