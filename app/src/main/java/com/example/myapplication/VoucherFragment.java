package com.example.myapplication;

import android.content.Context;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.User;
import com.example.myapplication.Models.Voucher;
import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiResponse;
import com.example.myapplication.Network.ApiService;
import com.google.gson.JsonObject; // Import JsonObject

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherFragment extends Fragment {

    private RecyclerView recyclerView;
    private VoucherAdapter adapter;
    private List<Voucher> voucherList;
    private ApiService apiService;
    private TextView tvCoinCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_voucher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getApiService();
        tvCoinCount = view.findViewById(R.id.tv_coin_count);
        recyclerView = view.findViewById(R.id.recycler_voucher_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        voucherList = new ArrayList<>();
        adapter = new VoucherAdapter(getContext(), voucherList, new VoucherAdapter.OnVoucherActionListener(){
            @Override
            public void onRedeemClick(Voucher voucher) {
                showConfirmDialog(voucher);
            }
        });
        recyclerView.setAdapter(adapter);
        loadUserInfo();
        loadVouchers();
    }
    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();
        loadVouchers();
    }
    private void loadUserInfo() {
        String userId = ApiClient.getUserIdFromToken();
        if (userId == null) return;

        apiService.getUserById(userId).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    User user = response.body().getData();
                    tvCoinCount.setText("COINS: " + user.getCoins());
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {}
        });
    }
    private void showConfirmDialog(Voucher voucher) {
        new AlertDialog.Builder(getContext())
                .setTitle("ACCEPT")
                .setMessage("YOU ARE ABOUT TO REDEEM THIS VOUCHER!")
                .setPositiveButton("Đổi ngay", (dialog, which) -> {
                    processRedeem(voucher);
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void processRedeem(Voucher voucher) {
        // Body request: { "amount": 1000 }
        JsonObject body = new JsonObject();
        body.addProperty("amount", 1000);

        apiService.deductCoins(body).enqueue(new Callback<ApiResponse<JsonObject>>() {
            @Override
            public void onResponse(Call<ApiResponse<JsonObject>> call, Response<ApiResponse<JsonObject>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    copyToClipboard(voucher.getCode());
                    Toast.makeText(getContext(), "COPY SUCCESS", Toast.LENGTH_LONG).show();
                    try {
                        int coinsLeft = response.body().getData().get("coinsLeft").getAsInt();
                        tvCoinCount.setText("COINS: " + coinsLeft);
                    } catch (Exception e) {
                        loadUserInfo();
                    }

                } else {
                    String msg = "FAIL";
                    if (response.body() != null) msg = response.body().getMessage();
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<JsonObject>> call, Throwable t) {
                Toast.makeText(getContext(), "ERROR!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void copyToClipboard(String text) {
        if (getContext() == null) return;
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Voucher Code", text);
        clipboard.setPrimaryClip(clip);
    }

    private void loadVouchers() {
        apiService.getAllVouchers().enqueue(new Callback<ApiResponse<List<Voucher>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Voucher>>> call, Response<ApiResponse<List<Voucher>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Voucher> list = response.body().getData();
                    if (list != null) {
                        voucherList.clear();
                        voucherList.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<Voucher>>> call, Throwable t) {}
        });
    }
}