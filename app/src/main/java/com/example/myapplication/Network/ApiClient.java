package com.example.myapplication.Network;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.myapplication.Login; // 💡 THÊM IMPORT
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:5000/";
    private static Retrofit retrofit = null;
    private static Context applicationContext;

    // 💡 HẰNG SỐ DUY NHẤT ĐỂ LƯU TOKEN
    private static final String ACCESS_TOKEN_KEY = "ACCESS_TOKEN";

    public static void initialize(Context context) {
        applicationContext = context.getApplicationContext();
    }

    // 💡 LẤY SharedPreferences TỪ TÊN FILE CHUNG CỦA LOGIN
    private static SharedPreferences getAuthPrefs() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApiClient not initialized.");
        }
        // SỬ DỤNG MY_PREFS (Đã định nghĩa trong Login) để truy cập Token
        return applicationContext.getSharedPreferences(Login.MY_PREFS, Context.MODE_PRIVATE);
    }

    // 🔹 LƯU TOKEN 🔹
    public static void saveToken(String token) {
        SharedPreferences prefs = getAuthPrefs();
        prefs.edit().putString(ACCESS_TOKEN_KEY, token).apply();
    }

    // 🔹 LẤY TOKEN 🔹
    public static String getToken() {
        SharedPreferences prefs = getAuthPrefs();
        return prefs.getString(ACCESS_TOKEN_KEY, null);
    }

    // 🔹 XÓA TOKEN 🔹
    public static void clearToken() {
        SharedPreferences prefs = getAuthPrefs();
        prefs.edit().remove(ACCESS_TOKEN_KEY).apply();
    }


    public static ApiService getApiService() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApiClient not initialized. Call ApiClient.initialize(Context) first.");
        }

        if (retrofit == null) {

            // 1. Interceptor để thêm Authorization Header (JWT Token)
            Interceptor authInterceptor = chain -> {
                Request original = chain.request();
                String token = getToken(); // LẤY TOKEN ĐÃ LƯU

                Request.Builder builder = original.newBuilder();
                if (token != null) {
                    builder.header("Authorization", "Bearer " + token);
                }
                builder.method(original.method(), original.body());
                return chain.proceed(builder.build());
            };

            // 2. Logging Interceptor (Giữ nguyên)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 3. Tạo OkHttpClient (Giữ nguyên)
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(logging)
                    .build();

            // 4. Khởi tạo Retrofit (Giữ nguyên)
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}