package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
// 💡 Import các Model mới
import com.example.myapplication.Models.CreateOrderRequest;
import com.example.myapplication.Models.OrderItemRequest;
import com.example.myapplication.Models.OrderCreationResponse;
import com.example.myapplication.Models.OrderPaymentRequest;
import com.example.myapplication.MyTicket;
import com.example.myapplication.Models.User;
import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiService;
import com.example.myapplication.Network.ApiResponse;

import android.widget.RadioGroup;
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
    private String currentUserId;
    private String eventName;

    private RadioGroup rgPaymentMethods;

    // UI Components
    private TextInputEditText etCustomerName, etCustomerEmail, etCustomerPhone;
    private TextView tvEventNameCheckout, tvQuantityInfo, tvItemTotal, tvFinalTotal;
    private Button btnDatHang;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_out);

        apiService = ApiClient.getApiService();

        // 1. Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        selectedEventId = intent.getStringExtra("EVENT_ID");
        selectedSeatTypeId = intent.getStringExtra("SEAT_TYPE_ID");
        selectedQuantity = intent.getIntExtra("QUANTITY", 1);
        subtotalPrice = intent.getDoubleExtra("TOTAL_PRICE", 0.0);
        eventName = intent.getStringExtra("EVENT_NAME");

        SharedPreferences prefs = getSharedPreferences(Login.MY_PREFS, Context.MODE_PRIVATE);
        currentUserId = prefs.getString("USER_ID", null);

        // 2. Ánh xạ Views
        btnDatHang = findViewById(R.id.btnDatHang);
        rgPaymentMethods = findViewById(R.id.rgPaymentMethods);

        etCustomerName = findViewById(R.id.etCustomerName);
        etCustomerEmail = findViewById(R.id.etCustomerEmail);
        etCustomerPhone = findViewById(R.id.etCustomerPhone);

        tvEventNameCheckout = findViewById(R.id.tvEventNameCheckout);
        tvQuantityInfo = findViewById(R.id.tvQuantityInfo);
        tvItemTotal = findViewById(R.id.tvItemTotal);
        tvFinalTotal = findViewById(R.id.tvFinalTotal);

        // 3. Setup Toolbar
        Toolbar toolbar = findViewById(R.id.checkout_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 4. Cập nhật UI với dữ liệu vé
        updateTicketUI();

        // 5. Tải thông tin khách hàng
        if (currentUserId != null) {
            loadCustomerInfo(currentUserId);
        }

        // 6. Logic Đặt hàng
        btnDatHang.setOnClickListener(v -> {
            if (currentUserId == null || selectedEventId == null) {
                Toast.makeText(this, "Thiếu thông tin đặt vé.", Toast.LENGTH_SHORT).show();
                return;
            }
            // BẮT ĐẦU QUY TRÌNH TẠO ĐƠN HÀNG
            createOrder();
        });
    }

    private void updateTicketUI() {
        DecimalFormat formatter = new DecimalFormat("#,###");
        if (tvEventNameCheckout != null) tvEventNameCheckout.setText(eventName != null ? eventName : "Sự kiện");

        if (tvQuantityInfo != null) {
            double unitPrice = selectedQuantity > 0 ? subtotalPrice / selectedQuantity : 0;
            tvQuantityInfo.setText(selectedQuantity + " vé x " + formatter.format(unitPrice) + "đ");
        }

        String totalStr = formatter.format(subtotalPrice) + "đ";
        if (tvItemTotal != null) tvItemTotal.setText(totalStr);
        if (tvFinalTotal != null) tvFinalTotal.setText(totalStr);
    }

    private String getSelectedPaymentMethod() {
        int selectedId = rgPaymentMethods.getCheckedRadioButtonId();
        if (selectedId == R.id.rbTienMat) return "CASH";
        if (selectedId == R.id.rbChuyenKhoan) return "TRANSFER";
        if (selectedId == R.id.rbCod) return "COD";
        return "COD"; // Mặc định
    }

    private void loadCustomerInfo(String userId) {
        apiService.getUserById(userId).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getData();
                    if (user != null) {
                        etCustomerName.setText(user.getFullName());
                        etCustomerEmail.setText(user.getEmail());
                        etCustomerPhone.setText(user.getPhone());
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {}
        });
    }

    // =======================================================
    // 💡 BƯỚC 1: TẠO ĐƠN HÀNG (POST /api/orders)
    // =======================================================
    private void createOrder() {
        // Chuẩn bị danh sách items (Backend yêu cầu dạng mảng)
        List<OrderItemRequest> items = new ArrayList<>();
        items.add(new OrderItemRequest(selectedSeatTypeId, selectedQuantity));

        CreateOrderRequest orderRequest = new CreateOrderRequest(
                currentUserId,
                selectedEventId,
                items
        );

        apiService.createOrder(orderRequest).enqueue(new Callback<ApiResponse<OrderCreationResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderCreationResponse>> call, Response<ApiResponse<OrderCreationResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Lấy Order ID trả về từ Server
                    String orderId = response.body().getData().getOrderId();

                    if (orderId != null) {
                        Log.d("ORDER_FLOW", "Bước 1 thành công. Order ID: " + orderId);
                        // Chuyển sang bước 2: Thanh toán ngay lập tức
                        payOrder(orderId);
                    } else {
                        Toast.makeText(Checkout.this, "Lỗi: Không lấy được mã đơn hàng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Lỗi Server";
                    Toast.makeText(Checkout.this, "Tạo đơn thất bại: " + msg, Toast.LENGTH_LONG).show();
                    Log.e("ORDER_FLOW", "Create Order Error: " + response.code() + " - " + msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderCreationResponse>> call, Throwable t) {
                Toast.makeText(Checkout.this, "Lỗi kết nối bước 1", Toast.LENGTH_SHORT).show();
                Log.e("ORDER_FLOW", "Create Order Failure", t);
            }
        });
    }

    // =======================================================
    // 💡 BƯỚC 2: THANH TOÁN (POST /api/orders/pay)
    // =======================================================
    private void payOrder(String orderId) {
        OrderPaymentRequest request = new OrderPaymentRequest(orderId, getSelectedPaymentMethod());

        apiService.payOrder(request).enqueue(new Callback<ApiResponse<MyTicket>>() {
            @Override
            public void onResponse(Call<ApiResponse<MyTicket>> call, Response<ApiResponse<MyTicket>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Checkout.this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();

                    // Chuyển sang trang Vé Của Tôi
                    Intent intent = new Intent(Checkout.this, MyTicketActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Lỗi thanh toán";
                    Toast.makeText(Checkout.this, "Thanh toán thất bại: " + msg, Toast.LENGTH_LONG).show();
                    Log.e("ORDER_FLOW", "Pay Error: " + response.code() + " - " + msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MyTicket>> call, Throwable t) {
                Toast.makeText(Checkout.this, "Lỗi kết nối thanh toán", Toast.LENGTH_SHORT).show();
                Log.e("ORDER_FLOW", "Pay Failure", t);
            }
        });
    }
}