package com.example.myapplication.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("userId")
    private String userId;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("role")
    private String role;

    @SerializedName("phone")
    private String phone;

    @SerializedName("follow")
    private int follow;

    @SerializedName("image")
    private String image;
    @SerializedName("balance")
    private long balance;
    @SerializedName("coins")
    private int coins;
    public User() {}

    public User(String fullName, String email, String phone, String image) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.image = image;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getPhone() { return phone; }
    public int getFollow() { return follow; }
    public String getImage() { return image; }

    // Setters
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setImage(String image) { this.image = image; }
    public long getBalance() { return balance; }
    public void setBalance(long balance) { this.balance = balance; }
    public int getCoins() { return coins; }
    public void setCoins(int coins) { this.coins = coins; }
}