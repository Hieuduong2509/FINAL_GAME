package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView; // 🎨 XÓA DÒNG NÀY NẾU KHÔNG CÒN DÙNG
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager; // Import dòng này
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VoucherFragment extends Fragment {

    RecyclerView mainRecycler;
    VoucherCategoryAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_voucher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainRecycler = view.findViewById(R.id.recycler_voucher_categories);
        List<VoucherCategory> data = loadMockData();
        categoryAdapter = new VoucherCategoryAdapter(data);
        mainRecycler.setAdapter(categoryAdapter);
    }

    // Hàm tạo dữ liệu mẫu (Giữ nguyên)
    private List<VoucherCategory> loadMockData() {
        // ... (Giữ nguyên code của bạn)
        List<VoucherCategory> categories = new ArrayList<>();
        List<Voucher> artistVouchers = new ArrayList<>();
        artistVouchers.add(new Voucher("Sự kiện Sơn Tùng", "Giảm 20%", "Tối đa 100K", "Sơn Tùng M-TP"));
        artistVouchers.add(new Voucher("Show của Hà Anh Tuấn", "Giảm 15%", "Tối đa 50K", "Hà Anh Tuấn"));
        categories.add(new VoucherCategory("Nghệ sĩ", artistVouchers));
        List<Voucher> seminarVouchers = new ArrayList<>();
        seminarVouchers.add(new Voucher("Hội thảo AI", "Giảm 10%", "Tối đa 30K", "Tech Conference"));
        categories.add(new VoucherCategory("Hội thảo", seminarVouchers));
        return categories;
    }

    // 🔹 ----- CÁC CLASS GỘP VÀO ----- 🔹

    // 🔹 1. Model Voucher (Giữ nguyên) 🔹
    public static class Voucher {
        String eventName;
        String discountTitle;
        String discountSubtitle;
        String partnerName;
        public Voucher(String eventName, String discountTitle, String discountSubtitle, String partnerName) {
            this.eventName = eventName;
            this.discountTitle = discountTitle;
            this.discountSubtitle = discountSubtitle;
            this.partnerName = partnerName;
        }
    }

    // 🔹 2. Model VoucherCategory (Giữ nguyên) 🔹
    public static class VoucherCategory {
        String categoryTitle;
        List<Voucher> vouchers;
        public VoucherCategory(String categoryTitle, List<Voucher> vouchers) {
            this.categoryTitle = categoryTitle;
            this.vouchers = vouchers;
        }
    }

    // 🔹 3. Adapter DỌC (Đã sửa lỗi LayoutManager) 🔹
    public static class VoucherCategoryAdapter extends RecyclerView.Adapter<VoucherCategoryAdapter.CategoryViewHolder> {
        private List<VoucherCategory> categoryList;
        private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

        public VoucherCategoryAdapter(List<VoucherCategory> categoryList) {
            this.categoryList = categoryList;
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_voucher_category, parent, false);
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            VoucherCategory category = categoryList.get(position);
            holder.tvCategoryTitle.setText(category.categoryTitle);

            // Cài đặt cho RecyclerView NGANG
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(
                    holder.horizontalRecycler.getContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
            );
            VoucherCardAdapter cardAdapter = new VoucherCardAdapter(category.vouchers);
            holder.horizontalRecycler.setLayoutManager(horizontalLayoutManager);
            holder.horizontalRecycler.setAdapter(cardAdapter);
            holder.horizontalRecycler.setRecycledViewPool(viewPool);
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }

        static class CategoryViewHolder extends RecyclerView.ViewHolder {
            TextView tvCategoryTitle;
            RecyclerView horizontalRecycler;
            public CategoryViewHolder(@NonNull View itemView) {
                super(itemView);
                tvCategoryTitle = itemView.findViewById(R.id.tv_category_title);
                horizontalRecycler = itemView.findViewById(R.id.recycler_voucher_cards);
            }
        }
    }

    // 🔹 4. Adapter NGANG (Đã sửa lỗi ViewHolder) 🔹
    public static class VoucherCardAdapter extends RecyclerView.Adapter<VoucherCardAdapter.VoucherCardViewHolder> {
        private List<Voucher> voucherList;

        public VoucherCardAdapter(List<Voucher> voucherList) {
            this.voucherList = voucherList;
        }

        @NonNull
        @Override
        public VoucherCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Đảm bảo tên layout này khớp (XML bạn gửi có vẻ thiếu thẻ đóng)
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_voucher_card, parent, false);
            return new VoucherCardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VoucherCardViewHolder holder, int position) {
            Voucher voucher = voucherList.get(position);
            holder.tvPartnerName.setText(voucher.partnerName);
            holder.tvDiscountTitle.setText(voucher.discountTitle);
            holder.tvDiscountSubtitle.setText(voucher.discountSubtitle);

            holder.btnCollect.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "Đã thu thập: " + voucher.discountTitle, Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return voucherList.size();
        }

        // 🎨 ----- BẮT ĐẦU SỬA LỖI ----- 🎨
        static class VoucherCardViewHolder extends RecyclerView.ViewHolder {
            // Xóa 2 dòng ImageView
            // ImageView ivBanner, ivLogo;
            TextView tvPartnerName, tvDiscountTitle, tvDiscountSubtitle;
            Button btnCollect;

            public VoucherCardViewHolder(@NonNull View itemView) {
                super(itemView);
                // Xóa 2 dòng findViewById
                // ivBanner = itemView.findViewById(R.id.iv_voucher_banner);
                // ivLogo = itemView.findViewById(R.id.iv_partner_logo);

                tvPartnerName = itemView.findViewById(R.id.tv_partner_name);
                tvDiscountTitle = itemView.findViewById(R.id.tv_discount_title);
                tvDiscountSubtitle = itemView.findViewById(R.id.tv_discount_subtitle);
                btnCollect = itemView.findViewById(R.id.btn_collect);
            }
        }
        // 🎨 ----- KẾT THÚC SỬA LỖI ----- 🎨
    }
}