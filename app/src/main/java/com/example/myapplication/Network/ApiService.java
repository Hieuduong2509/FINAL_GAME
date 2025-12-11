package com.example.myapplication.Network;

import com.example.myapplication.Models.User;
import com.example.myapplication.Models.AuthResponse;
import com.example.myapplication.Models.LoginRequest;
import com.example.myapplication.Models.RegisterRequest;
import com.example.myapplication.Ticket;
import com.example.myapplication.MyTicket;
import com.example.myapplication.Models.SeatCountResponse;
import com.example.myapplication.Models.TicketCreationRequest;
import com.example.myapplication.Models.OrderCreationResponse;
import com.example.myapplication.Models.OrderPaymentRequest;
import com.example.myapplication.Models.CreateOrderRequest;
import com.example.myapplication.Models.Artist;
import com.example.myapplication.Models.ResetPasswordRequest;
import com.example.myapplication.Models.Voucher;
import com.example.myapplication.Models.ValidateVoucherRequest;
import com.example.myapplication.Models.Review;
import com.example.myapplication.Models.ReviewRequest;
import com.example.myapplication.Models.FollowResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Body;
import com.google.gson.JsonObject;
public interface ApiService {

    // ================= 1. AUTH =================
    @POST("/api/auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    @POST("/api/auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);
    @POST("/api/users/deduct-coins")
    Call<ApiResponse<JsonObject>> deductCoins(@Body JsonObject body);
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

    // ================= 4. TICKETS =================
    @GET("/api/tickets/user/{userId}")
    Call<ApiResponse<List<MyTicket>>> getUserTickets(@Path("userId") String userId);

    // ================= 5. ORDERS =================
    @POST("/api/orders")
    Call<ApiResponse<OrderCreationResponse>> createOrder(@Body CreateOrderRequest request);

    @POST("/api/orders/pay")
    Call<ApiResponse<MyTicket>> payOrder(@Body OrderPaymentRequest request);

    // ================= 6. INVITERS (ARTISTS) =================
    @GET("/api/inviters")
    Call<ApiResponse<List<Artist>>> getAllInviters();

    @GET("/api/inviters/{id}")
    Call<ApiResponse<Artist>> getInviterDetail(@Path("id") String id);

    // ================= 7. KHÁC =================
    @POST("/api/auth/reset-password")
    Call<ApiResponse<Void>> resetPassword(@Body ResetPasswordRequest request);

    @GET("/api/vouchers")
    Call<ApiResponse<List<Voucher>>> getAllVouchers();

    @POST("/api/vouchers/validate")
    Call<ApiResponse<Voucher>> validateVoucher(@Body ValidateVoucherRequest request);

    @POST("/api/reviews/{eventId}")
    Call<ApiResponse<Review>> createReview(@Path("eventId") String eventId, @Body ReviewRequest request);

    @GET("/api/reviews/{eventId}")
    Call<ApiResponse<List<Review>>> getEventReviews(@Path("eventId") String eventId);

    @GET("/api/events-inviters/get-inviters-by-event/{eventId}")
    Call<ApiResponse<List<Artist>>> getArtistsByEvent(@Path("eventId") String eventId);

    @POST("/api/inviters/{id}/follow")
    Call<ApiResponse<FollowResponse>> followInviter(@Path("id") String inviterId);
}