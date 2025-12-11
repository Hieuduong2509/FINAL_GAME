package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.Review;
import com.example.myapplication.Models.ReviewRequest;
import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiResponse;
import com.example.myapplication.Network.ApiService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTicketAdapter extends RecyclerView.Adapter<MyTicketAdapter.MyTicketViewHolder> {

    private final Context context;
    private final List<MyTicket> myTicketList;

    private final int COLOR_GREEN = 0xFF28A745;
    private final int COLOR_ORANGE = 0xFFFFC107;

    public MyTicketAdapter(Context context, List<MyTicket> myTicketList) {
        this.context = context;
        this.myTicketList = myTicketList;
    }

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
        holder.tvTicketCode.setText("TICKET ID: " + ticket.getTicketCode());

        String imageUrl = ticket.getImageUrl();
        if (imageUrl != null && !imageUrl.startsWith("http")) {
            imageUrl = ApiClient.BASE_URL + imageUrl;
        }
        Glide.with(context)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivEventImage);
        holder.btnShare.setOnClickListener(v -> {
            shareTicketEvent(ticket);
        });
        boolean isEventFinished = checkIsEventFinished(ticket.getDate());

        if (isEventFinished) {
            holder.btnScanTicket.setVisibility(View.GONE);
            holder.btnReview.setVisibility(View.VISIBLE);
            holder.tvTicketStatusText.setText("EVENT CLOSED");
            holder.tvTicketStatusText.setTextColor(Color.GRAY);
            holder.ivTicketStatus.setColorFilter(Color.GRAY);
            holder.ivTicketStatus.setImageResource(android.R.drawable.ic_menu_agenda);
        } else {
            holder.btnReview.setVisibility(View.GONE);
            if (ticket.isScanned()) {
                holder.ivTicketStatus.setImageResource(android.R.drawable.checkbox_on_background);
                holder.ivTicketStatus.setColorFilter(COLOR_GREEN);
                holder.tvTicketStatusText.setText("ROLL CALL");
                holder.tvTicketStatusText.setTextColor(COLOR_GREEN);
                holder.btnScanTicket.setVisibility(View.GONE);
            } else {
                holder.ivTicketStatus.setImageResource(android.R.drawable.checkbox_off_background);
                holder.ivTicketStatus.setColorFilter(COLOR_ORANGE);
                holder.tvTicketStatusText.setText("READY");
                holder.tvTicketStatusText.setTextColor(COLOR_ORANGE);
                holder.btnScanTicket.setVisibility(View.VISIBLE);
            }
        }

        holder.btnScanTicket.setOnClickListener(v -> {
            Toast.makeText(context, "OPEN CAMERA TO CHECK QR: " + ticket.getTicketCode(), Toast.LENGTH_SHORT).show();
        });

        holder.btnReview.setOnClickListener(v -> {
            showReviewDialog(ticket.getEventId());
        });
    }
    private void shareTicketEvent(MyTicket ticket) {
        String content = "I just bought tickets for this amazing event!\n\n" +
                "🎉 Event: " + ticket.getEventName() + "\n" +
                "📅 Date: " + ticket.getDate() + "\n" +
                "🎫 Quantity: " + ticket.getQuantity() + "\n\n" +
                "Download the app and join me!";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Share with friends via:");
        context.startActivity(shareIntent);
    }

    @Override
    public int getItemCount() {
        return myTicketList.size();
    }

    private boolean checkIsEventFinished(String eventDateStr) {
        if (eventDateStr == null) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date eventDate = sdf.parse(eventDateStr);
            Date now = new Date();
            return now.after(eventDate);
        } catch (ParseException e) {
            return false;
        }
    }

    private void showReviewDialog(String eventId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_write_review, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        EditText etComment = view.findViewById(R.id.etComment);
        Button btnSubmit = view.findViewById(R.id.btnSubmitReview);
        btnSubmit.setOnClickListener(v -> {
            int rating = (int) ratingBar.getRating();
            String comment = etComment.getText().toString().trim();
            if (rating == 0) {
                Toast.makeText(context, "Please select number of stars to rate!", Toast.LENGTH_SHORT).show();
                return;
            }
            submitReviewToApi(eventId, rating, comment, dialog);
        });
        dialog.show();
    }

    private void submitReviewToApi(String eventId, int rating, String comment, AlertDialog dialog) {
        ApiService apiService = ApiClient.getApiService();
        ReviewRequest request = new ReviewRequest(rating, comment);
        apiService.createReview(eventId, request).enqueue(new Callback<ApiResponse<Review>>() {
            @Override
            public void onResponse(Call<ApiResponse<Review>> call, Response<ApiResponse<Review>> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "Review Success!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Review Failed!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Review>> call, Throwable t) {
                Toast.makeText(context, "Error network", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class MyTicketViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTicketStatus, ivEventImage;
        TextView tvEventName, tvTicketCode, tvTicketStatusText;
        ImageButton btnScanTicket, btnShare; // Thêm btnShare
        Button btnReview;

        public MyTicketViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTicketStatus = itemView.findViewById(R.id.ivTicketStatus);
            ivEventImage = itemView.findViewById(R.id.ivEventImage);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvTicketCode = itemView.findViewById(R.id.tvTicketCode);
            tvTicketStatusText = itemView.findViewById(R.id.tvTicketStatusText);
            btnScanTicket = itemView.findViewById(R.id.btnScanTicket);
            btnReview = itemView.findViewById(R.id.btnReview);
            btnShare = itemView.findViewById(R.id.btnShare);
        }
    }
}