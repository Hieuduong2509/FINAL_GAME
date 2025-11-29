package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiService;
import com.example.myapplication.Network.ApiResponse;
import com.example.myapplication.Models.SeatCountResponse; // 💡 THÊM IMPORT NÀY

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketFragment extends Fragment {

    RecyclerView recyclerTickets;
    TicketAdapter adapter;

    private List<Ticket> eventList; // 💡 THAY ĐỔI: Sử dụng biến List này để lưu trữ dữ liệu
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_ticket, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getApiService();

        recyclerTickets = view.findViewById(R.id.recyclerTickets);
        if (recyclerTickets != null) {
            recyclerTickets.setLayoutManager(new LinearLayoutManager(getContext()));
            loadEvents();
        }
    }

    private void loadEvents() {
        // 1. GỌI API LẤY DANH SÁCH SỰ KIỆN GỐC
        apiService.getAllEvents().enqueue(new Callback<ApiResponse<List<Ticket>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Ticket>>> call, Response<ApiResponse<List<Ticket>>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    eventList = response.body().getData(); // Lưu trữ danh sách

                    if (eventList != null && !eventList.isEmpty()) {
                        // Khởi tạo Adapter
                        adapter = new TicketAdapter(eventList);
                        recyclerTickets.setAdapter(adapter);

                        // 2. KHỞI CHẠY HÀM GỌI API GHẾ CHO TỪNG EVENT (N+1)
                        loadSeatsForEvents();

                    } else {
                        Toast.makeText(getContext(), "Không có sự kiện nào được tìm thấy.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("EVENT_API", "Failed to load events. HTTP: " + response.code());
                    Toast.makeText(getContext(), "Lỗi tải sự kiện.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Ticket>>> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("EVENT_API", "Failure: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Lỗi kết nối.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 3. HÀM N+1 CALL ĐỂ LẤY SỐ LƯỢNG GHẾ
    private void loadSeatsForEvents() {
        if (eventList == null) return;

        for (int i = 0; i < eventList.size(); i++) {
            Ticket event = eventList.get(i);
            String eventId = event.getEventId();

            if (eventId == null) continue;

            final int position = i;

            apiService.getEventSeats(eventId).enqueue(new Callback<ApiResponse<SeatCountResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<SeatCountResponse>> call, Response<ApiResponse<SeatCountResponse>> response) {
                    if (!isAdded() || adapter == null) return;

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        SeatCountResponse seatData = response.body().getData();

                        if (seatData != null) {
                            // 💡 Cập nhật theo yêu cầu: Dùng availableSeats cho cả total và remain
//                            event.total = seatData.availableSeats;
//                            event.remain = seatData.availableSeats; // Gán cùng giá trị

                            adapter.notifyItemChanged(position);
                        }
                    } else {
                        Log.w("SEAT_API", "Failed to load seats for " + eventId + ". HTTP: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<SeatCountResponse>> call, Throwable t) {
                    if (!isAdded()) return;
                    Log.e("SEAT_API", "Connection failure for event " + eventId, t);
                }
            });
        }
    }
}