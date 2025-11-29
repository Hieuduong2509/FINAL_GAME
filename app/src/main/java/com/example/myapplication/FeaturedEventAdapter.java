package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FeaturedEventAdapter extends RecyclerView.Adapter<FeaturedEventAdapter.ViewHolder> {

    private final List<Ticket> eventList;
    private final Context context;

    public FeaturedEventAdapter(Context context, List<Ticket> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng layout mới tạo cho item sự kiện nổi bật
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_featured_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ticket event = eventList.get(position);

        holder.tvName.setText(event.eventName);
        holder.tvTime.setText(event.getDateTime());
        holder.tvLocation.setText(event.location);

        // Xử lý Click (mở trang chi tiết vé)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TicketDetailActivity.class);
            intent.putExtra("EVENT_ID", event.getEventId());
            intent.putExtra("EVENT_NAME", event.eventName);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        // Chỉ hiển thị tối đa 3 sự kiện
        return Math.min(eventList.size(), 3);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_feat_name);
            tvTime = itemView.findViewById(R.id.tv_feat_time);
            tvLocation = itemView.findViewById(R.id.tv_feat_location);
            // Các TextView được ánh xạ từ item_featured_event.xml
        }
    }
}