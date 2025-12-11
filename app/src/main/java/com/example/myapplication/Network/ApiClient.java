package com.example.myapplication.Network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.example.myapplication.Login; // Đảm bảo import đúng file Login
import org.json.JSONObject;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // Nếu chạy máy ảo Android thì giữ nguyên 10.0.2.2
    // Nếu chạy máy thật thì đổi thành IP LAN của máy tính (VD: 192.168.1.x)
    public static final String BASE_URL = "http://10.0.2.2:5000/";

    private static Retrofit retrofit = null;
    private static Context applicationContext;
    private static final String ACCESS_TOKEN_KEY = "ACCESS_TOKEN";

    public static void initialize(Context context) {
        applicationContext = context.getApplicationContext();
    }

    private static SharedPreferences getAuthPrefs() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApiClient not initialized. Call ApiClient.initialize(Context) first.");
        }
        // SỬA LỖI Ở ĐÂY: Dùng SHARED_PREF_NAME thay vì MY_PREFS
        return applicationContext.getSharedPreferences(Login.SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveToken(String token) {
        SharedPreferences prefs = getAuthPrefs();
        prefs.edit().putString(ACCESS_TOKEN_KEY, token).apply();
    }

    public static String getToken() {
        SharedPreferences prefs = getAuthPrefs();
        return prefs.getString(ACCESS_TOKEN_KEY, null);
    }

    public static void clearToken() {
        SharedPreferences prefs = getAuthPrefs();
        prefs.edit().remove(ACCESS_TOKEN_KEY).apply();
    }

    // Hàm giải mã Token để lấy User ID (nếu cần dùng gấp)
    public static String getUserIdFromToken() {
        String token = getToken();
        if (token == null) return null;

        try {
            String[] split = token.split("\\.");
            if (split.length < 2) return null;
            String payload = new String(Base64.decode(split[1], Base64.URL_SAFE));
            JSONObject jsonObject = new JSONObject(payload);
            // Lưu ý: Key này phải khớp với key trong Token mà Backend tạo ra (thường là "userId" hoặc "id")
            return jsonObject.getString("userId");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ApiService getApiService() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApiClient not initialized. Call ApiClient.initialize(Context) first.");
        }

        if (retrofit == null) {
            // Tự động thêm Token vào Header mỗi lần gọi API
            Interceptor authInterceptor = chain -> {
                Request original = chain.request();
                String token = getToken();

                Request.Builder builder = original.newBuilder();
                if (token != null) {
                    builder.header("Authorization", "Bearer " + token);
                }
                builder.method(original.method(), original.body());
                return chain.proceed(builder.build());
            };

            // Log API ra màn hình Logcat để debug cho dễ
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}