package com.example.myapplication.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Voucher implements Serializable {

    @SerializedName("voucherId")
    private String voucherId;

    @SerializedName("code")
    private String code;

    // 💡 SỬA 2: Đổi int thành Double vì Server trả về "10.00"
    @SerializedName("discountPercentage")
    private Double discountPercentage;

    @SerializedName("validFrom")
    private String validFrom;

    @SerializedName("validTo")
    private String validTo;

    @SerializedName("usageLimit")
    private int usageLimit;
    @SerializedName("pointCost")
    private int pointCost;

    public String getVoucherId() { return voucherId; }

    public String getCode() { return code; }
    public int getPointCost() {return pointCost;}
    public int getDiscountPercentage() {
        return discountPercentage != null ? discountPercentage.intValue() : 0;
    }

    public String getValidTo() { return validTo; }
}