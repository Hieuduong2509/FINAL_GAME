package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// 🔹 Đây là file mới cho tab "Đặt Vé"
public class TicketFragment extends Fragment {

    RecyclerView recyclerTickets;
    TicketAdapter adapter;
    List<Ticket> ticketList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 1. Load layout chứa RecyclerView (file này bạn đã có)
        return inflater.inflate(R.layout.fragment_my_ticket, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 2. Ánh xạ RecyclerView
        recyclerTickets = view.findViewById(R.id.recyclerTickets);
        recyclerTickets.setLayoutManager(new LinearLayoutManager(getContext()));

        // 3. Tạo dữ liệu (danh sách vé để MUA)
        ticketList = new ArrayList<>();
        ticketList.add(new Ticket("Sự kiện âm nhạc", "10/12/2025 20:00", "Sân vận động ABC", "A1-A10", "AD123", 200, 50));
        ticketList.add(new Ticket("Hội thảo Công nghệ", "15/12/2025 09:00", "Trung tâm XYZ", "B5-B15", "TE456", 100, 20));
        ticketList.add(new Ticket("Triển lãm Nghệ thuật", "20/12/2025 10:00", "Bảo tàng CDE", "Tự do", "AR789", 50, 10));

        // 4. Khởi tạo và gán Adapter
        // (Đảm bảo Ticket.java và TicketAdapter.java đã được đổi package)
        adapter = new TicketAdapter(ticketList);
        recyclerTickets.setAdapter(adapter);
    }
}