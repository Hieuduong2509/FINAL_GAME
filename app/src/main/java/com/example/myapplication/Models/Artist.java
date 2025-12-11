    package com.example.myapplication.Models; // 1. Sửa package thành Models

    import com.example.myapplication.Ticket; // 2. Import Ticket từ package gốc
    import com.google.gson.annotations.SerializedName;
    import java.io.Serializable;
    import java.util.List;

    public class Artist implements Serializable {
        @SerializedName("inviterId")
        private String id;

        @SerializedName("fullName")
        private String name;

        @SerializedName("email")
        private String email;

        @SerializedName("follower")
        private int follower;

        @SerializedName("image")
        private String avatarUrl;

        @SerializedName("upcomingEvents")
        private List<Ticket> upcomingEvents;

        // Getters & Setters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getAvatarUrl() { return avatarUrl; }
        public String getEmail() { return email; }
        public int getFollower() { return follower; }
        public List<Ticket> getUpcomingEvents() { return upcomingEvents; }
        public void setFollower(int follower) {
            this.follower = follower;
        }
    }