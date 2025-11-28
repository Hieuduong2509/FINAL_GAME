// 1. Đảm bảo package là com.example.myapplication
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
        holder.tvEventName.setText(ticket.eventName);
        holder.tvDate.setText("📅 " + ticket.dateTime);
        holder.tvLocation.setText("📍 " + ticket.location);
        holder.tvSeat.setText("Ghế: " + ticket.seat);
        holder.tvTicketCode.setText("Mã vé: " + ticket.code);
        holder.tvTotalTicket.setText("Số lượng: "+ticket.total);
        holder.tvRemainedTicket.setText("Còn trống: "+ticket.remain);

        // 🔹 2. SỬA LOGIC NÚT "THÊM VÀO GIỎ" 🔹
        holder.btnBuyTicket.setOnClickListener(v -> {
            // (Sau này bạn sẽ thêm logic thêm vào CSDL/Giỏ hàng ở đây)
            Toast.makeText(v.getContext(), "Đã thêm '" + ticket.eventName + "' vào giỏ", Toast.LENGTH_SHORT).show();
        });

        // 🔹 3. SỬA LOGIC NÚT "CHI TIẾT" 🔹
        holder.btnShare.setOnClickListener(v -> {
            Context context = v.getContext();

            // Mở trang chi tiết (TicketDetailActivity)
            Intent intent = new Intent(context, TicketDetailActivity.class);

            // Gửi dữ liệu của vé này sang trang chi tiết
            intent.putExtra("EVENT_NAME", ticket.eventName);
            intent.putExtra("EVENT_LOCATION", ticket.location);
            intent.putExtra("EVENT_DATE", ticket.dateTime);
            intent.putExtra("EVENT_CODE", ticket.code); // Gửi cả code vé đi

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
            btnShare = itemView.findViewById(R.id.btnShare); // ID vẫn là btnShare
            tvTotalTicket = itemView.findViewById(R.id.totalTicket);
            tvRemainedTicket = itemView.findViewById(R.id.remainedTicket);
        }
    }
}