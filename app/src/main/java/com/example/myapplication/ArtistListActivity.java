package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class ArtistListActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private ArtistListAdapter adapter;
    private List<Artist> artistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_list);

        toolbar = findViewById(R.id.toolbarArtistList);
        recyclerView = findViewById(R.id.recyclerArtistList);

        // 1. Thiết lập Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // 2. Tải dữ liệu giả lập
        loadMockArtistData();

        // 3. Thiết lập RecyclerView
        adapter = new ArtistListAdapter(this, artistList);
        recyclerView.setAdapter(adapter);
    }

    // Xử lý nút back
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Hàm giả lập tải dữ liệu nghệ sĩ từ API/Database
     */
    private void loadMockArtistData() {
        artistList = new ArrayList<>();

        // Tạo sự kiện giả lập (Dùng Model Ticket đã có)
        List<Ticket> eventsST = new ArrayList<>();
        eventsST.add(new Ticket("Concert Mùa Hè", "12/2025", "SVĐ Mỹ Đình", "VIP", "CST001", 1000, 500));
        eventsST.add(new Ticket("Sky Tour", "01/2026", "Nhà Hát Lớn", "Standard", "CST002", 500, 100));

        List<Ticket> eventsHAT = new ArrayList<>();
        eventsHAT.add(new Ticket("The Story", "11/2025", "TP.HCM", "Luxury", "HAT001", 200, 10));

        // Thêm các nghệ sĩ
        artistList.add(new Artist("A001", "Sơn Tùng M-TP", "Ca sĩ, nhạc sĩ, rapper nổi tiếng hàng đầu Việt Nam. Sở hữu lượng fan hâm mộ đông đảo.",
                "https://placehold.co/100x100/176B87/FFFFFF?text=ST", "Ca sĩ Pop", eventsST));
        artistList.add(new Artist("A002", "Hà Anh Tuấn", "Giọng ca lãng mạn, chuyên tổ chức các đêm nhạc acoustic và phòng trà cao cấp.",
                "https://placehold.co/100x100/64CCC5/000000?text=HAT", "Ca sĩ Acoustic", eventsHAT));
        artistList.add(new Artist("A003", "SpaceSpeaker", "Nhóm DJ/Producer hàng đầu Việt Nam, chuyên về nhạc điện tử và Hip-hop.",
                "https://placehold.co/100x100/DAFFFB/000000?text=SS", "DJ/Producer", new ArrayList<>()));
    }
}