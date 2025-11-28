package com.example.myapplication;
import java.io.Serializable;
public class Ticket implements Serializable {
    String eventName;
    String dateTime;
    String location;
    String seat;
    String code;
    int total,remain;
    public Ticket(String eventName, String dateTime, String location, String seat, String code, int total, int remain) {
        this.eventName = eventName;
        this.dateTime = dateTime;
        this.location = location;
        this.seat = seat;
        this.code = code;
        this.total=total;
        this.remain=remain;
    }
}
