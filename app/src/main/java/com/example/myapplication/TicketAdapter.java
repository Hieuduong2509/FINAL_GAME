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

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private final List<Ticket> ticketList;

    public TicketAdapter(List<Ticket> ticketList) {
        this.ticketList = ticketList;
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
        Ticket ticket = ticketList.get(position);

        // 1. DỮ LIỆU CƠ BẢN (tên, địa điểm)
        holder.tvEventName.setText(ticket.eventName);
        holder.tvLocation.setText("📍 " + ticket.location);
        holder.tvDate.setText("📅 " + ticket.getDateTime()); // Lấy ngày giờ đã format

        // 2. HIỂN THỊ GIÁ VÀ ID
        holder.tvSeat.setText(ticket.getSeat());
        holder.tvTicketCode.setText(ticket.getCode());

        // 3. HIỂN THỊ SỐ GHẾ THỰC TẾ
        if (ticket.total > 0) {
            holder.tvTotalTicket.setText("Số lượng: " + ticket.total);
            holder.tvRemainedTicket.setText("Còn trống: " + ticket.remain);
        } else {
            holder.tvTotalTicket.setText("Số lượng: Đang tải...");
            holder.tvRemainedTicket.setText("Còn trống: N/A");
        }

        // 💡 4. LOGIC CHUYỂN SANG TRANG CHI TIẾT
        holder.btnShare.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, TicketDetailActivity.class);

            // TRUYỀN TẤT CẢ CÁC TRƯỜNG DỮ LIỆU CẦN THIẾT
            intent.putExtra("EVENT_ID", ticket.getEventId());
            intent.putExtra("EVENT_NAME", ticket.eventName);
            intent.putExtra("EVENT_DATETIME", ticket.getDateTime()); // 💡 ĐÃ SỬA: TRUYỀN NGÀY GIỜ ĐÃ FORMAT
            intent.putExtra("EVENT_LOCATION", ticket.location);     // 💡 ĐÃ SỬA: TRUYỀN ĐỊA ĐIỂM

            context.startActivity(intent);
        });

        // 💡 LOGIC NÚT MUA VÉ NGAY
        holder.btnBuyTicket.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Đã chọn '" + ticket.eventName + "' (Price: " + ticket.getPrice() + ")", Toast.LENGTH_SHORT).show();
            // CHUYỂN SANG TRANG CHỌN GHẾ
            Context context = v.getContext();
            Intent intent = new Intent(context, SelectSeatActivity.class);
            intent.putExtra("EVENT_ID", ticket.getEventId());
            // TRUYỀN CÁC TRƯỜNG CẦN THIẾT CHO SELECT SEAT
            intent.putExtra("EVENT_DATETIME", ticket.getDateTime());
            intent.putExtra("EVENT_LOCATION", ticket.location);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
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