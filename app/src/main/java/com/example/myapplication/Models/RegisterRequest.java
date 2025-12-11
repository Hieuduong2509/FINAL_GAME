package com.example.myapplication.Models;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("fullname")
    private String fullname;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;

    public RegisterRequest(String fullname, String email, String password) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
    }
}