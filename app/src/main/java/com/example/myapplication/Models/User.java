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
    private String role; // Vd: 'user', 'organizer'

    @SerializedName("phone")
    private String phone;

    @SerializedName("follow")
    private int follow;

    @SerializedName("image")
    private String image; // URL ảnh đại diện

    // Thêm các trường khác nếu cần (ví dụ: passwordHash, createdAt,...)

    // Constructor (Chủ yếu dùng cho Gson khi nhận data)
    public User() {}

    // Constructor cho các request (Register/Update)
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

    // Setters (Chủ yếu dùng cho Update)
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setImage(String image) { this.image = image; }
}