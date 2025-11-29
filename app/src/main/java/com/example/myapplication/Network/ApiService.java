package com.example.myapplication.Network;

import com.example.myapplication.Models.User;
import com.example.myapplication.Models.AuthResponse;
import com.example.myapplication.Models.LoginRequest;
import com.example.myapplication.Models.RegisterRequest;
import com.example.myapplication.Ticket;
import com.example.myapplication.MyTicket;
import com.example.myapplication.Models.SeatCountResponse;
import com.example.myapplication.Models.TicketCreationRequest;
import com.example.myapplication.Models.OrderCreationResponse; // Import mới
import com.example.myapplication.Models.OrderPaymentRequest; // Import mới
import com.example.myapplication.Models.CreateOrderRequest; // Import mới

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Body;

public interface ApiService {

    // ================= 1. AUTH =================
    @POST("/api/auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    @POST("/api/auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    // ================= 2. USERS =================
    @GET("/api/users/{userId}")
    Call<ApiResponse<User>> getUserById(@Path("userId") String userId);

    @PUT("/api/users")
    Call<ApiResponse<User>> updateCurrentUser(@Body User user);

    // ================= 3. EVENTS =================
    @GET("/api/events")
    Call<ApiResponse<List<Ticket>>> getAllEvents();

    @GET("/api/events/{eventId}/seats")
    Call<ApiResponse<SeatCountResponse>> getEventSeats(@Path("eventId") String eventId);

    @GET("/api/events/{eventId}")
    Call<ApiResponse<Ticket>> getEventDetails(@Path("eventId") String eventId);

    // ================= 4. TICKETS (Vé cũ - nếu cần) =================
    @GET("/api/tickets/user/{userId}")
    Call<ApiResponse<List<MyTicket>>> getUserTickets(@Path("userId") String userId);

    // ================= 5. ORDERS (Luồng đặt vé mới) =================

    /**
     * Bước 1: Tạo đơn hàng (Order)
     * Endpoint: POST /api/orders
     */
    @POST("/api/orders")
    Call<ApiResponse<OrderCreationResponse>> createOrder(@Body CreateOrderRequest request);

    /**
     * Bước 2: Thanh toán đơn hàng (Pay)
     * Endpoint: POST /api/orders/pay
     */
    @POST("/api/orders/pay")
    Call<ApiResponse<MyTicket>> payOrder(@Body OrderPaymentRequest request);
}