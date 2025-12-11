package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<NotificationModel> list;

    public NotificationAdapter(List<NotificationModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel item = list.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvContent.setText(item.getContent());
        holder.tvTime.setText(item.getTime());
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvContent = itemView.findViewById(R.id.tvNotifContent);
            tvTime = itemView.findViewById(R.id.tvNotifTime);
        }
    }
}