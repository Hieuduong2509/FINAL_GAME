package com.example.myapplication;

import java.util.ArrayList;
import java.util.Date;

public class Location {
    private String name;
    private String address;
    private ArrayList<Date> times;

    public Location(String name, String address, ArrayList<Date> times) {
        this.name = name;
        this.address = address;
        this.times = times;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public ArrayList<Date> getTimes() {
        return times;
    }
}