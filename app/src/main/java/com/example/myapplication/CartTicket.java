package com.example.myapplication;

public class CartTicket {
    private String eventName;
    private double price;
    private int quantity;
    private boolean isSelected;

    // Constructor
    public CartTicket(String eventName, double price, int quantity) {
        this.eventName = eventName;
        this.price = price;
        this.quantity = quantity;
        this.isSelected = true; // Mặc định là đã chọn khi thêm vào giỏ
    }

    // Getters and Setters
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}