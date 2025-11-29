package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyTicketAdapter extends RecyclerView.Adapter<MyTicketAdapter.MyTicketViewHolder> {

    private final Context context;
    private final List<MyTicket> myTicketList;
    private final int COLOR_GREEN = 0xFF28A745;
    private final int COLOR_ORANGE = 0xFFFFC107;

    public MyTicketAdapter(Context context, List<MyTicket> myTicketList) { // 💡 Cấu trúc này cần Context
        this.context = context;
        this.myTicketList = myTicketList;
    }

    // ... (onBindViewHolder và các phần khác giữ nguyên)

    @NonNull
    @Override
    public MyTicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_ticket, parent, false);
        return new MyTicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTicketViewHolder holder, int position) {
        MyTicket ticket = myTicketList.get(position);

        holder.tvEventName.setText(ticket.getEventName());
        holder.tvTicketCode.setText("Mã vé: " + ticket.getTicketCode());

        // Load ảnh sự kiện (dùng Glide)
        Glide.with(context)
                .load(ticket.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivEventImage);

        // Cập nhật trạng thái vé và icon
        if (ticket.isScanned()) {
            holder.ivTicketStatus.setImageResource(android.R.drawable.checkbox_on_background);
            holder.ivTicketStatus.setColorFilter(COLOR_GREEN);
            holder.tvTicketStatusText.setText("Đã điểm danh");
            holder.tvTicketStatusText.setTextColor(COLOR_GREEN);
            holder.btnScanTicket.setVisibility(View.GONE);
        } else {
            holder.ivTicketStatus.setImageResource(android.R.drawable.checkbox_off_background);
            holder.ivTicketStatus.setColorFilter(COLOR_ORANGE);
            holder.tvTicketStatusText.setText("Chưa điểm danh");
            holder.tvTicketStatusText.setTextColor(COLOR_ORANGE);
            holder.btnScanTicket.setVisibility(View.VISIBLE);
        }

        // Xử lý sự kiện bấm nút Scan QR
        holder.btnScanTicket.setOnClickListener(v -> {
            // Logic MỞ CAMERA VÀ QUÉT QR
            Toast.makeText(context, "Mở Camera để quét mã QR cho vé: " + ticket.getTicketCode(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return myTicketList.size();
    }

    public static class MyTicketViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTicketStatus, ivEventImage;
        TextView tvEventName, tvTicketCode, tvTicketStatusText;
        ImageButton btnScanTicket;

        public MyTicketViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTicketStatus = itemView.findViewById(R.id.ivTicketStatus);
            ivEventImage = itemView.findViewById(R.id.ivEventImage);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvTicketCode = itemView.findViewById(R.id.tvTicketCode);
            tvTicketStatusText = itemView.findViewById(R.id.tvTicketStatusText);
            btnScanTicket = itemView.findViewById(R.id.btnScanTicket);
        }
    }
}