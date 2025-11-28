package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity; // 🔹 Import
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment; // 🔹 Phải là Fragment
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// 1. Implement interface của Adapter
public class OrderFragment extends Fragment implements CartAdapter.CartListener {

    // --- Các biến giao diện ---
    Toolbar toolbar;
    RecyclerView rvCartItems;
    CheckBox cbSelectAll;
    TextView tvTotalPrice;
    MaterialButton btnCheckout;

    // --- Các biến logic ---
    private List<CartTicket> cartList;
    private CartAdapter cartAdapter;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private boolean isUpdatingSelectAll = false; // Cờ chống lặp vô hạn


    /**
     * 2. Dùng onCreateView thay vì onCreate
     * Đây là nơi Fragment "vẽ" giao diện của nó.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 3. Gắn layout "cart.xml" vào Fragment
        View view = inflater.inflate(R.layout.cart, container, false);

        // 4. Ánh xạ View (QUAN TRỌNG: phải dùng "view.findViewById")
        toolbar = view.findViewById(R.id.toolbarCart);
        rvCartItems = view.findViewById(R.id.rvCartItems);
        cbSelectAll = view.findViewById(R.id.cbSelectAll);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        // 5. Cài đặt Toolbar (Hơi khác so với Activity)
        // Yêu cầu Activity "chủ" sử dụng toolbar này
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Xử lý nút back trên toolbar
        toolbar.setNavigationOnClickListener(v -> {
            // Quay lại màn hình trước đó
            requireActivity().onBackPressed();
        });

        // 6. Khởi tạo RecyclerView (Dùng getContext())
        setupRecyclerView();

        // 7. Tải dữ liệu (giả lập)
        loadDummyData();

        // 8. Cài đặt sự kiện cho các nút
        setupListeners();

        // 9. Cập nhật UI lần đầu
        updateCartState();

        // 10. Trả về view đã được tạo
        return view;
    }

    // --- CÁC HÀM BÊN DƯỚI GẦN GIỐNG HỆT CARTACTIVITY ---
    // --- Chỉ thay 'this' bằng 'getContext()' ---

    private void setupRecyclerView() {
        cartList = new ArrayList<>();
        // Dùng getContext() thay vì 'this' cho Adapter
        cartAdapter = new CartAdapter(getContext(), cartList, this);
        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext())); // Dùng getContext()
        rvCartItems.setAdapter(cartAdapter);
    }

    private void loadDummyData() {
        // Dữ liệu giả lập
        cartList.add(new CartTicket("Sự kiện EDM Hè Sôi Động", 500000, 2));
        cartList.add(new CartTicket("Workshop Sáng tạo Nội dung", 150000, 1));
        cartList.add(new CartTicket("Show Nhạc Acoustic", 200000, 3));

        cartAdapter.notifyDataSetChanged(); // Báo cho Adapter cập nhật
    }

    private void setupListeners() {
        // Nút "Đặt hàng" -> Chuyển sang màn hình Checkout
        btnCheckout.setOnClickListener(v -> {
            double total = calculateTotalPrice();
            if (total == 0) {
                // Dùng getContext() cho Toast
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất 1 vé", Toast.LENGTH_SHORT).show();
            } else {
                // Dùng getContext() cho Intent
                Intent intent = new Intent(getContext(), Checkout.class);
                // TODO: Gửi danh sách vé được chọn + tổng tiền qua Intent
                startActivity(intent);
            }
        });

        // Checkbox "Tất cả" (Logic giữ nguyên)
        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUpdatingSelectAll) return;

            isUpdatingSelectAll = true;
            for (CartTicket ticket : cartList) {
                ticket.setSelected(isChecked);
            }
            cartAdapter.notifyDataSetChanged();
            updateCartState();
            isUpdatingSelectAll = false;
        });
    }

    // Hàm tính tổng tiền (Logic giữ nguyên)
    private double calculateTotalPrice() {
        double total = 0;
        for (CartTicket ticket : cartList) {
            if (ticket.isSelected()) {
                total += ticket.getPrice() * ticket.getQuantity();
            }
        }
        return total;
    }

    // Hàm cập nhật toàn bộ UI (Logic giữ nguyên)
    private void updateCartState() {
        double total = calculateTotalPrice();
        tvTotalPrice.setText(currencyFormatter.format(total));

        if (!isUpdatingSelectAll && !cartList.isEmpty()) {
            isUpdatingSelectAll = true;
            boolean allSelected = true;
            for (CartTicket ticket : cartList) {
                if (!ticket.isSelected()) {
                    allSelected = false;
                    break;
                }
            }
            cbSelectAll.setChecked(allSelected);
            isUpdatingSelectAll = false;
        } else if (cartList.isEmpty()) {
            cbSelectAll.setChecked(false);
        }
    }

    // --- Implement 2 hàm của Interface (Logic giữ nguyên) ---

    @Override
    public void onCartUpdated() {
        updateCartState(); // Tính lại tổng tiền và cập nhật checkbox "Tất cả"
    }

    @Override
    public void onItemRemoved(CartTicket ticket) {
        // Dùng getContext() cho Toast
        Toast.makeText(getContext(), "Đã xóa: " + ticket.getEventName(), Toast.LENGTH_SHORT).show();
        updateCartState();
    }
}