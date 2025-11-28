package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartTicket> ticketList;
    private CartListener listener; // Interface để giao tiếp ngược lại với Activity

    // Định dạng số tiền (Vd: 500,000 đ)
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    // 1. Định nghĩa Interface để gửi sự kiện
    public interface CartListener {
        void onCartUpdated(); // Gọi khi có thay đổi (check/xóa) để Activity tính lại tổng tiền
        void onItemRemoved(CartTicket ticket); // Xử lý khi xóa
    }

    // 2. Constructor
    public CartAdapter(Context context, List<CartTicket> ticketList, CartListener listener) {
        this.context = context;
        this.ticketList = ticketList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 3. Kết nối với layout item_cart_ticket.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_ticket, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        // 4. Lấy dữ liệu từ vị trí (position) và gán lên View
        CartTicket ticket = ticketList.get(position);

        holder.tvEventName.setText(ticket.getEventName());
        holder.tvQuantity.setText(String.valueOf(ticket.getQuantity()));
        holder.tvTicketPrice.setText(currencyFormatter.format(ticket.getPrice()));

        // Gán ảnh (nếu có, ở đây dùng ảnh demo)
        // holder.ivEventImage.setImageResource(R.drawable.ten_anh_su_kien);

        // 5. Xử lý CheckBox
        // Đặt trạng thái checked mà không kích hoạt listener
        holder.cbSelectTicket.setOnCheckedChangeListener(null);
        holder.cbSelectTicket.setChecked(ticket.isSelected());

        // Bật lại listener
        holder.cbSelectTicket.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ticket.setSelected(isChecked); // Cập nhật trạng thái isSelected trong model
            listener.onCartUpdated(); // Báo cho Activity biết để cập nhật tổng tiền
        });

        // 6. Xử lý nút Xóa
        holder.btnDeleteTicket.setOnClickListener(v -> {
            // Lấy vị trí chính xác trước khi xóa
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                CartTicket removedTicket = ticketList.remove(currentPosition); // Xóa khỏi danh sách
                notifyItemRemoved(currentPosition); // Báo cho RecyclerView
                notifyItemRangeChanged(currentPosition, ticketList.size()); // Cập nhật lại vị trí các item sau

                listener.onItemRemoved(removedTicket); // Báo cho Activity
                listener.onCartUpdated(); // Cập nhật lại tổng tiền
            }
        });
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    // 0. Class ViewHolder - Ánh xạ các View trong item_cart_ticket.xml
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelectTicket;
        ImageView ivEventImage;
        TextView tvEventName, tvTicketPrice, tvQuantity;
        ImageButton btnDeleteTicket;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelectTicket = itemView.findViewById(R.id.cbSelectTicket);
            ivEventImage = itemView.findViewById(R.id.ivEventImage);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvTicketPrice = itemView.findViewById(R.id.tvTicketPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnDeleteTicket = itemView.findViewById(R.id.btnDeleteTicket);
        }
    }
}