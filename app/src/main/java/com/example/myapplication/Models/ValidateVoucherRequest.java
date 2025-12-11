package com.example.myapplication.Models;

public class ValidateVoucherRequest {
    private String code;
    private String userId;
    public ValidateVoucherRequest(String code, String userId) { this.code = code; this.userId = userId; }
}