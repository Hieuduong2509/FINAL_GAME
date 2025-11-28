package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private int selectedPosition = 0;
    private OnDateClickListener clickListener;
    private Context context;

    private int itemWidth; // <-- THÊM BIẾN: để lưu kích thước 1/7 màn hình

    private final DateTimeFormatter dayNumberFormatter = DateTimeFormatter.ofPattern("d");

    // --- SỬA HÀM CONSTRUCTOR ---
    public DateAdapter(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    public interface OnDateClickListener {
        void onDateClick(LocalDate date);
    }

    public void setOnDateClickListener(OnDateClickListener listener) {
        this.clickListener = listener;
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        // ... (giữ nguyên)
        TextView tvDayOfWeek;
        TextView tvDate;
        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayOfWeek = itemView.findViewById(R.id.tv_day_of_week);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_date, parent, false);

        // --- THÊM LOGIC NÀY ---
        // Ép chiều rộng của item (khuôn) = 1/7 màn hình
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = this.itemWidth;
        view.setLayoutParams(params);
        // ------------------------

        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        // ... (Code onBindViewHolder của bạn giữ nguyên, nó đã đúng)
        LocalDate date = LocalDate.now().plusDays(position);

        String dayOfWeekText;
        if (position == 0) {
            dayOfWeekText = "Hôm nay";
        } else {
            dayOfWeekText = getVietnameseDayAbbreviation(date.getDayOfWeek());
        }
        holder.tvDayOfWeek.setText(dayOfWeekText);
        holder.tvDate.setText(dayNumberFormatter.format(date));

        if (position == selectedPosition) {
            holder.tvDayOfWeek.setTextColor(Color.parseColor("#E53935")); // Đỏ
            holder.tvDate.setTextColor(Color.WHITE);
            holder.tvDate.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_date_selected));
        } else {
            holder.tvDayOfWeek.setTextColor(Color.parseColor("#9E9E9E")); // Xám
            holder.tvDate.setTextColor(Color.parseColor("#9E9E9E")); // Xám
            holder.tvDate.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_date_unselected));
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return;
                int previousPosition = selectedPosition;
                selectedPosition = currentPosition;
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
                clickListener.onDateClick(date);
            }
        });
    }

    // ... (Hàm getVietnameseDayAbbreviation giữ nguyên)
    private String getVietnameseDayAbbreviation(DayOfWeek day) {
        switch (day) {
            case MONDAY: return "T2";
            case TUESDAY: return "T3";
            case WEDNESDAY: return "T4";
            case THURSDAY: return "T5";
            case FRIDAY: return "T6";
            case SATURDAY: return "T7";
            case SUNDAY: return "CN";
            default: return "";
        }
    }


    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE; // Cuộn vô tận
    }
}