package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<Ticket> originalList; // Danh sách gốc (Dữ liệu đầy đủ từ API)
    private List<Ticket> displayList;  // Danh sách hiển thị (Đã qua lọc)

    public TicketAdapter(List<Ticket> ticketList) {
        this.originalList = ticketList;
        this.displayList = new ArrayList<>(ticketList); // Ban đầu hiển thị tất cả
    }

    // 👇 HÀM LỌC DỮ LIỆU
    public void filter(String query) {
        displayList.clear();
        if (query == null || query.isEmpty()) {
            displayList.addAll(originalList);
        } else {
            String filterPattern = query.toLowerCase().trim();
            for (Ticket item : originalList) {
                // Kiểm tra tên sự kiện hoặc địa điểm
                if (item.eventName.toLowerCase().contains(filterPattern) ||
                        item.location.toLowerCase().contains(filterPattern)) {
                    displayList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = displayList.get(position);

        holder.tvEventName.setText(ticket.eventName);
        holder.tvLocation.setText("" + ticket.location);
        holder.tvDate.setText("📅 " + ticket.getDateTime());
        holder.tvSeat.setText(ticket.getSeat());
        holder.tvTicketCode.setText(ticket.getCode());
        if (ticket.total > 0) {
            holder.tvTotalTicket.setText("QUANTITY: " + ticket.total);
            holder.tvRemainedTicket.setText("STILL AVAILABLE: " + ticket.remain);
        } else {
            holder.tvTotalTicket.setText("QUANTITY: LOADING...");
            holder.tvRemainedTicket.setText("STILL AVAILABLE: N/A");
        }

        holder.btnShare.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, TicketDetailActivity.class);
            intent.putExtra("EVENT_ID", ticket.getEventId());
            intent.putExtra("EVENT_NAME", ticket.eventName);
            intent.putExtra("EVENT_DATETIME", ticket.getDateTime());
            intent.putExtra("EVENT_LOCATION", ticket.location);
            context.startActivity(intent);
        });

        holder.btnBuyTicket.setOnClickListener(v -> {
            if (ticket.getEventId() == null) {
                Toast.makeText(v.getContext(), "EVENT ID IS NULL", Toast.LENGTH_SHORT).show();
                return;
            }
            Context context = v.getContext();
            Intent intent = new Intent(context, SelectSeatActivity.class);
            intent.putExtra("EVENT_ID", ticket.getEventId());
            intent.putExtra("EVENT_DATETIME", ticket.getDateTime());
            intent.putExtra("EVENT_LOCATION", ticket.location);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvDate, tvLocation, tvSeat, tvTicketCode, tvTotalTicket, tvRemainedTicket;
        Button btnBuyTicket, btnShare;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvSeat = itemView.findViewById(R.id.tvSeat);
            tvTicketCode = itemView.findViewById(R.id.tvTicketCode);
            btnBuyTicket = itemView.findViewById(R.id.btnBuyTicket);
            btnShare = itemView.findViewById(R.id.btnShare);
            tvTotalTicket = itemView.findViewById(R.id.totalTicket);
            tvRemainedTicket = itemView.findViewById(R.id.remainedTicket);
        }
    }
}