package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

import com.example.myapplication.Models.CreateOrderRequest;
import com.example.myapplication.Models.OrderItemRequest;
import com.example.myapplication.Models.OrderCreationResponse;
import com.example.myapplication.Models.OrderPaymentRequest;
import com.example.myapplication.Models.ValidateVoucherRequest;
import com.example.myapplication.Models.Voucher;
import com.example.myapplication.MyTicket;
import com.example.myapplication.Models.User;
import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiService;
import com.example.myapplication.Network.ApiResponse;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Checkout extends AppCompatActivity {

    private String selectedEventId;
    private String selectedSeatTypeId;
    private int selectedQuantity;
    private double subtotalPrice;
    private double finalPrice;
    private String currentUserId;
    private String eventName;

    private RadioGroup rgPaymentMethods;

    private TextInputEditText etCustomerName, etCustomerEmail, etCustomerPhone;
    private TextView tvEventNameCheckout, tvQuantityInfo, tvItemTotal, tvFinalTotal;
    private Button btnDatHang;

    private EditText etVoucherCode;
    private Button btnApplyVoucher;
    private TextView tvDiscountInfo;

    private String appliedVoucherCode = null;
    private int discountPercentage = 0;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_out);

        apiService = ApiClient.getApiService();

        Intent intent = getIntent();
        selectedEventId = intent.getStringExtra("EVENT_ID");
        selectedSeatTypeId = intent.getStringExtra("SEAT_TYPE_ID");
        selectedQuantity = intent.getIntExtra("QUANTITY", 1);
        subtotalPrice = intent.getDoubleExtra("TOTAL_PRICE", 0.0);
        finalPrice = subtotalPrice;
        eventName = intent.getStringExtra("EVENT_NAME");

        SharedPreferences prefs = getSharedPreferences(Login.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        currentUserId = prefs.getString(Login.KEY_USER_ID, null);

        btnDatHang = findViewById(R.id.btnDatHang);
        rgPaymentMethods = findViewById(R.id.rgPaymentMethods);

        etCustomerName = findViewById(R.id.etCustomerName);
        etCustomerEmail = findViewById(R.id.etCustomerEmail);
        etCustomerPhone = findViewById(R.id.etCustomerPhone);

        tvEventNameCheckout = findViewById(R.id.tvEventNameCheckout);
        tvQuantityInfo = findViewById(R.id.tvQuantityInfo);
        tvItemTotal = findViewById(R.id.tvItemTotal);
        tvFinalTotal = findViewById(R.id.tvFinalTotal);

        etVoucherCode = findViewById(R.id.etVoucherCode);
        btnApplyVoucher = findViewById(R.id.btnApplyVoucher);
        tvDiscountInfo = findViewById(R.id.tvDiscountInfo);

        Toolbar toolbar = findViewById(R.id.checkout_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        updateTicketUI();

        if (currentUserId != null) {
            loadCustomerInfo(currentUserId);
        } else {
            Toast.makeText(this, "Error: Missing User ID.", Toast.LENGTH_LONG).show();
        }

        if (btnApplyVoucher != null) {
            btnApplyVoucher.setOnClickListener(v -> {
                String code = etVoucherCode.getText().toString().trim();
                if (!code.isEmpty()) {
                    validateVoucher(code);
                } else {
                    Toast.makeText(this, "Please enter a voucher code.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        btnDatHang.setOnClickListener(v -> {
            if (currentUserId == null) {
                Toast.makeText(this, "Error: Missing User ID.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedEventId == null) {
                Toast.makeText(this, "Missing Event ID.", Toast.LENGTH_SHORT).show();
                return;
            }
            btnDatHang.setEnabled(false);
            createOrder();
        });
    }

    private void updateTicketUI() {
        DecimalFormat formatter = new DecimalFormat("#,###");
        if (tvEventNameCheckout != null) tvEventNameCheckout.setText(eventName != null ? eventName : "Event Name");

        if (tvQuantityInfo != null) {
            double unitPrice = selectedQuantity > 0 ? subtotalPrice / selectedQuantity : 0;
            tvQuantityInfo.setText(selectedQuantity + " ticket x " + formatter.format(unitPrice) + " VND");
        }

        String subTotalStr = formatter.format(subtotalPrice) + " VND";
        if (tvItemTotal != null) tvItemTotal.setText(subTotalStr);

        double discountAmount = subtotalPrice * discountPercentage / 100.0;
        finalPrice = subtotalPrice - discountAmount;

        String finalTotalStr = formatter.format(finalPrice) + " VND";
        if (tvFinalTotal != null) tvFinalTotal.setText(finalTotalStr);

        if (tvDiscountInfo != null) {
            if (discountPercentage > 0) {
                tvDiscountInfo.setText("Discount: -" + formatter.format(discountAmount) + " VND (" + discountPercentage + "%)");
                tvDiscountInfo.setVisibility(View.VISIBLE);
            } else {
                tvDiscountInfo.setVisibility(View.GONE);
            }
        }
    }

    private String getSelectedPaymentMethod() {
        int selectedId = rgPaymentMethods.getCheckedRadioButtonId();
        if (selectedId == R.id.rbTienMat) return "CASH";
        if (selectedId == R.id.rbChuyenKhoan) return "TRANSFER";
        if (selectedId == R.id.rbCod) return "COD";
        return "COD";
    }

    private void validateVoucher(String code) {
        if (currentUserId == null) return;
        ValidateVoucherRequest request = new ValidateVoucherRequest(code, currentUserId);
        apiService.validateVoucher(request).enqueue(new Callback<ApiResponse<Voucher>>() {
            @Override
            public void onResponse(Call<ApiResponse<Voucher>> call, Response<ApiResponse<Voucher>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Voucher voucher = response.body().getData();
                    appliedVoucherCode = voucher.getCode();
                    discountPercentage = voucher.getDiscountPercentage();
                    Toast.makeText(Checkout.this, "Voucher Applied: -" + discountPercentage + "%", Toast.LENGTH_SHORT).show();
                    updateTicketUI();
                } else {
                    Toast.makeText(Checkout.this, "Invalid Voucher Code", Toast.LENGTH_SHORT).show();
                    appliedVoucherCode = null;
                    discountPercentage = 0;
                    updateTicketUI();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Voucher>> call, Throwable t) {
                Toast.makeText(Checkout.this, "Error Check Voucher", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCustomerInfo(String userId) {
        apiService.getUserById(userId).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getData();
                    if (user != null) {
                        if (etCustomerName != null) etCustomerName.setText(user.getFullName());
                        if (etCustomerEmail != null) etCustomerEmail.setText(user.getEmail());
                        if (etCustomerPhone != null) etCustomerPhone.setText(user.getPhone());
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {}
        });
    }

    private void createOrder() {
        List<OrderItemRequest> items = new ArrayList<>();
        items.add(new OrderItemRequest(selectedSeatTypeId, selectedQuantity));

        CreateOrderRequest orderRequest = new CreateOrderRequest(
                currentUserId,
                selectedEventId,
                items,
                appliedVoucherCode
        );

        apiService.createOrder(orderRequest).enqueue(new Callback<ApiResponse<OrderCreationResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderCreationResponse>> call, Response<ApiResponse<OrderCreationResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String orderId = response.body().getData().getOrderId();

                    if (orderId != null) {
                        Log.d("ORDER_FLOW", "Create order success. ID: " + orderId);
                        payOrder(orderId);
                    } else {
                        Toast.makeText(Checkout.this, "Error creating order", Toast.LENGTH_SHORT).show();
                        btnDatHang.setEnabled(true);
                    }
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Error Server";
                    Toast.makeText(Checkout.this, "Failed: " + msg, Toast.LENGTH_LONG).show();
                    btnDatHang.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderCreationResponse>> call, Throwable t) {
                Toast.makeText(Checkout.this, "Cannot connect to server", Toast.LENGTH_SHORT).show();
                btnDatHang.setEnabled(true);
            }
        });
    }

    private void payOrder(String orderId) {
        OrderPaymentRequest request = new OrderPaymentRequest(orderId, getSelectedPaymentMethod());

        apiService.payOrder(request).enqueue(new Callback<ApiResponse<MyTicket>>() {
            @Override
            public void onResponse(Call<ApiResponse<MyTicket>> call, Response<ApiResponse<MyTicket>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Checkout.this, "Check Out Success!", Toast.LENGTH_LONG).show();
                    String time = new java.text.SimpleDateFormat("HH:mm dd/MM/yyyy").format(new java.util.Date());
                    String content = "You buy complete " + selectedQuantity + " event tickets for " + eventName + ".";
                    try {
                        NotificationModel notif = new NotificationModel("Check Out Success", content, time);
                        NotificationStorage.addNotification(Checkout.this, notif);
                        NotificationSystem.sendNotification(Checkout.this, "Check Out Success!", "Đơn hàng cho sự kiện " + eventName + " đã được xác nhận.");
                    } catch (Exception e) {
                        Log.e("NOTI_ERROR", "Error Notification: " + e.getMessage());
                    }

                    Intent intent = new Intent(Checkout.this, MyTicketActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Error Checkout";
                    Toast.makeText(Checkout.this, "Failed to Check Out: " + msg, Toast.LENGTH_LONG).show();
                    btnDatHang.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MyTicket>> call, Throwable t) {
                Toast.makeText(Checkout.this, "Error to connect Internet", Toast.LENGTH_SHORT).show();
                btnDatHang.setEnabled(true);
            }
        });
    }
}