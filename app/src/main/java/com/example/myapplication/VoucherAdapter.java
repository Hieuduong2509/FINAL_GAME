package com.example.myapplication;

import android.content.Context;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Voucher;
import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {
    private Context context;
    private List<Voucher> voucherList;
    private OnVoucherActionListener listener;

    public interface OnVoucherActionListener {
        void onRedeemClick(Voucher voucher);
    }
    public VoucherAdapter(Context context, List<Voucher> voucherList, OnVoucherActionListener listener) {
        this.context = context;
        this.voucherList = voucherList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_voucher_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);

        // Hiển thị thông tin cơ bản
        holder.tvDiscount.setText("DISCOUNT " + voucher.getDiscountPercentage() + "%");
        String dateStr = (voucher.getValidTo() != null && voucher.getValidTo().length() >= 10)
                ? voucher.getValidTo().substring(0, 10)
                : "INFINITE";
        holder.tvDate.setText("DATE: " + dateStr);
        int cost = voucher.getPointCost();
        if (cost > 0) {
            holder.tvCode.setText("••••••••");
            holder.btnAction.setText("CHANGE " + cost + " COINS");
            holder.btnAction.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRedeemClick(voucher);
                }
            });
        } else {
            holder.tvCode.setText(voucher.getCode());
            holder.btnAction.setText("COPY");
            holder.btnAction.setBackgroundColor(context.getResources().getColor(R.color.black));
            holder.btnAction.setOnClickListener(v -> {
                copyToClipboard(voucher.getCode());
            });
        }
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Voucher Code", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "COPY SUCCESS: " + text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() { return voucherList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvDiscount, tvDate;
        Button btnAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tv_voucher_code);
            tvDiscount = itemView.findViewById(R.id.tv_voucher_discount);
            tvDate = itemView.findViewById(R.id.tv_voucher_date);

            btnAction = itemView.findViewById(R.id.btn_voucher_action);
        }
    }
}