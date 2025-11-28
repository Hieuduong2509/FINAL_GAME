package com.example.myapplication.Network;

import okhttp3.OkHttpClient; // 🔹 THÊM IMPORT
import okhttp3.logging.HttpLoggingInterceptor; // 🔹 THÊM IMPORT
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:5000/"; // Thêm dấu '/' cuối cùng
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {

            // 1. Tạo Interceptor để log request và response
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // Đặt level BODY để xem Header, Body và Status code
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 2. Tạo OkHttpClient và thêm Interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            // 3. Khởi tạo Retrofit với OkHttpClient đã tạo
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // 🔹 THÊM DÒNG NÀY
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}