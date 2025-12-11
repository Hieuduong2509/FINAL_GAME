package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiService;
import com.example.myapplication.Network.ApiResponse;
import com.example.myapplication.Models.SeatCountResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketFragment extends Fragment {

    RecyclerView recyclerTickets;
    TicketAdapter adapter;
    EditText etSearch; // 1. Khai báo thanh tìm kiếm

    private List<Ticket> eventList;
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Đảm bảo layout này đã có EditText như hướng dẫn ở trên
        return inflater.inflate(R.layout.fragment_my_ticket, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getApiService();

        recyclerTickets = view.findViewById(R.id.recyclerTickets);
        etSearch = view.findViewById(R.id.et_search_ticket); // 2. Ánh xạ

        if (recyclerTickets != null) {
            recyclerTickets.setLayoutManager(new LinearLayoutManager(getContext()));
            loadEvents();
        }

        // 3. XỬ LÝ SỰ KIỆN GÕ PHÍM
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (adapter != null) {
                        adapter.filter(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void loadEvents() {
        apiService.getAllEvents().enqueue(new Callback<ApiResponse<List<Ticket>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Ticket>>> call, Response<ApiResponse<List<Ticket>>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    eventList = response.body().getData();

                    if (eventList != null && !eventList.isEmpty()) {
                        adapter = new TicketAdapter(eventList);
                        recyclerTickets.setAdapter(adapter);

                        // N+1: Lấy ghế
                        loadSeatsForEvents();

                    } else {
                        Toast.makeText(getContext(), "Không có sự kiện nào.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("EVENT_API", "Failed to load events.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Ticket>>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Lỗi kết nối.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSeatsForEvents() {
        if (eventList == null) return;

        for (int i = 0; i < eventList.size(); i++) {
            Ticket event = eventList.get(i);
            String eventId = event.getEventId();
            if (eventId == null) continue;

            apiService.getEventSeats(eventId).enqueue(new Callback<ApiResponse<SeatCountResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<SeatCountResponse>> call, Response<ApiResponse<SeatCountResponse>> response) {
                    if (!isAdded() || adapter == null) return;

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        SeatCountResponse seatData = response.body().getData();
                        if (seatData != null) {
                            // Cập nhật dữ liệu vào object gốc
//                            event.total = seatData.availableSeats;
//                            event.remain = seatData.availableSeats;

                            // ⚠️ QUAN TRỌNG: Khi dùng bộ lọc, vị trí (position) bị thay đổi.
                            // Không dùng notifyItemChanged(i) được nữa vì i của list gốc != i của list đang hiện.
                            // Dùng notifyDataSetChanged() để an toàn (tuy hơi nặng hơn chút).
                            adapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<SeatCountResponse>> call, Throwable t) {}
            });
        }
    }
}