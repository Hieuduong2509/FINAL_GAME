package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class LocationActivity extends AppCompatActivity {

    private MapView map = null;
    private MyLocationNewOverlay mLocationOverlay;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_location);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        GeoPoint startPoint = new GeoPoint(10.7760, 106.7000);
        map.getController().setZoom(14.0);
        map.getController().setCenter(startPoint);
        findViewById(R.id.btnBackMap).setOnClickListener(v -> finish());
        findViewById(R.id.btnMyLocation).setOnClickListener(v -> {
            if (mLocationOverlay != null && mLocationOverlay.getMyLocation() != null) {
                map.getController().animateTo(mLocationOverlay.getMyLocation());
                map.getController().setZoom(16.0);
            } else {
                Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
            }
        });
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }
    private void requestPermissionsIfNecessary(String[] permissions) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            setupMapOverlays();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE && grantResults.length > 0) {
            setupMapOverlays();
        }
    }
    private void setupMapOverlays() {
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);
        addDemoRestaurantMarkers();
    }

    private void addDemoRestaurantMarkers() {
        String[] titles = {
                "Chợ Bến Thành", "Nhà Thờ Đức Bà", "Dinh Độc Lập", "Bưu Điện Thành Phố", "Phố Đi Bộ Nguyễn Huệ",
                "Tòa nhà Bitexco", "Landmark 81", "Thảo Cầm Viên", "Hồ Con Rùa", "Công Viên Tao Đàn",
                "Bảo tàng Chứng tích Chiến tranh", "Chợ Tân Định", "Diamond Plaza", "Takashimaya Việt Nam", "Vincom Center Đồng Khởi",
                "Phố Bùi Viện", "Công viên 23/9", "Nowzone Fashion Mall", "Chợ Lớn (Bình Tây)", "Chùa Bà Thiên Hậu",
                "Hùng Vương Plaza", "Vạn Hạnh Mall", "Sân bay Tân Sơn Nhất", "Công viên Gia Định", "Chùa Vĩnh Nghiêm",
                "Crescent Mall (Q7)", "SC VivoCity", "Cầu Ánh Sao", "Lotte Mart Quận 7", "Khu đô thị Sala"
        };

        // Danh sách tọa độ tương ứng (Lat, Lon)
        double[][] coordinates = {
                {10.7721, 106.6983}, // Chợ Bến Thành
                {10.7798, 106.6990}, // Nhà Thờ Đức Bà
                {10.7770, 106.6954}, // Dinh Độc Lập
                {10.7801, 106.7001}, // Bưu Điện Thành Phố
                {10.7745, 106.7036}, // Phố Đi Bộ Nguyễn Huệ
                {10.7716, 106.7044}, // Bitexco
                {10.7948, 106.7217}, // Landmark 81
                {10.7877, 106.7051}, // Thảo Cầm Viên
                {10.7830, 106.6960}, // Hồ Con Rùa
                {10.7744, 106.6923}, // Công Viên Tao Đàn
                {10.7795, 106.6922}, // Bảo tàng Chứng tích
                {10.7915, 106.6895}, // Chợ Tân Định
                {10.7812, 106.6985}, // Diamond Plaza
                {10.7733, 106.7011}, // Takashimaya
                {10.7782, 106.7019}, // Vincom Center
                {10.7674, 106.6939}, // Phố Bùi Viện
                {10.7686, 106.6933}, // Công viên 23/9
                {10.7631, 106.6823}, // Nowzone
                {10.7504, 106.6515}, // Chợ Lớn
                {10.7533, 106.6616}, // Chùa Bà Thiên Hậu
                {10.7578, 106.6666}, // Hùng Vương Plaza
                {10.7699, 106.6696}, // Vạn Hạnh Mall
                {10.8185, 106.6588}, // Sân bay Tân Sơn Nhất
                {10.8123, 106.6764}, // Công viên Gia Định
                {10.7909, 106.6829}, // Chùa Vĩnh Nghiêm
                {10.7289, 106.7216}, // Crescent Mall
                {10.7303, 106.7061}, // SC VivoCity
                {10.7278, 106.7222}, // Cầu Ánh Sao
                {10.7352, 106.7029}, // Lotte Mart Q7
                {10.7709, 106.7197}  // Khu đô thị Sala
        };

        for (int i = 0; i < titles.length; i++) {
            String title = titles[i];
            double lat = coordinates[i][0];
            double lon = coordinates[i][1];
            String snippet = "Tọa độ: " + lat + ", " + lon;

            addMarker(lat, lon, title, snippet);
        }
        map.invalidate();
    }
    private void addMarker(double lat, double lon, String title, String snippet) {
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(lat, lon));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        marker.setSnippet(snippet);
        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.location_on_24dp_e3e3e3_fill0_wght400_grad0_opsz24));
        marker.setOnMarkerClickListener((m, mapView) -> {
            m.showInfoWindow();
            return true;
        });

        map.getOverlays().add(marker);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) map.onResume();
        if (mLocationOverlay != null) mLocationOverlay.enableMyLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) map.onPause();
        if (mLocationOverlay != null) mLocationOverlay.disableMyLocation();
    }
}