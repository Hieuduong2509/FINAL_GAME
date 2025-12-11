package com.example.myapplication.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Review implements Serializable {
    @SerializedName("reviewId")
    private String reviewId;

    @SerializedName("userId")
    private String userId;

    @SerializedName("eventId")
    private String eventId;

    @SerializedName("rating")
    private int rating;

    @SerializedName("comment")
    private String comment;

    @SerializedName("created_at")
    private String createdAt;


    public Review(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    // Getters
    public int getRating() { return rating; }
    public String getComment() { return comment; }
}