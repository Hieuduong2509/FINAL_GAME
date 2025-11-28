package com.example.myapplication;

import java.io.Serializable;
import java.util.List;

// Implement Serializable để có thể truyền qua Intent
public class Artist implements Serializable {
    private String id;
    private String name;
    private String description;
    private String avatarUrl;
    private String category; // Vd: Ca sĩ, DJ, Nhóm nhạc
    private List<Ticket> upcomingEvents; // Danh sách các sự kiện sắp tới

    public Artist(String id, String name, String description, String avatarUrl, String category, List<Ticket> upcomingEvents) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.avatarUrl = avatarUrl;
        this.category = category;
        this.upcomingEvents = upcomingEvents;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getCategory() { return category; }
    public List<Ticket> getUpcomingEvents() { return upcomingEvents; }
}