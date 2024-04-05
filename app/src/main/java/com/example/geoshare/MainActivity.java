package com.example.geoshare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    ImageButton buttonProfile, buttonInvite, buttonLocation, buttonChat, buttonSearch;
    private GoogleMap maps;
    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    public GoogleMap getMaps(){
        return maps;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser == null){
            Intent intent = new Intent(getApplicationContext(), SignIn.class);
            startActivity(intent);
            finish();
        }

        buttonInvite = findViewById(R.id.btnInvite);
        buttonProfile =findViewById(R.id.btnProfile);
        buttonLocation = findViewById(R.id.btnCurrentLocation);
        buttonChat = findViewById(R.id.btnChat);
        buttonSearch = findViewById(R.id.btnSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Search.class);
                startActivity(intent);
                finish();
            }
        });
        buttonInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Invite.class);
                startActivity(intent);
                finish();
            }
        });

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
                finish();
            }
        });

        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusToMyLocation();
            }
        });

        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Chat.class);
                startActivity(intent);
                finish();
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }



    private int getCurrentBatteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;

        return (int)(batteryPct*100);
    }
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    currentLocation = location;

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
                    mapFragment.getMapAsync(MainActivity.this);
                }
            }
        });


    }
    public void focusToMyLocation() {
        getLastLocation();
        CameraPosition camPos = new CameraPosition.Builder()
                .target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                .zoom(12)
                .bearing(currentLocation.getBearing())
                .tilt(70)
                .build();
        maps.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
        maps.animateCamera(camUpd3);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        maps = googleMap;

        YourBatteryChangeListener batteryChangeListener = new YourBatteryChangeListener();
        batteryChangeListener.startBatteryChangeListener();

        LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        // Tạo một marker mới với thông tin từ Firebase và sử dụng Custom Marker Adapter
        MarkerOptions markerOptions = new MarkerOptions()
                .position(myLocation)
                .title("My location")
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.mipmap.ic_launcher, "Battery: 100%")));
//                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromImage(R.drawable.avatar)));

        // Thêm marker vào bản đồ
        maps.addMarker(markerOptions);
        maps.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

        // Đặt listener để lắng nghe sự thay đổi trên Firebase và cập nhật marker

        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("batteryLevel").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Lấy dữ liệu từ dataSnapshot và cập nhật giá trị pin
                String batteryInfo = dataSnapshot.child("currentBattery").getValue(String.class);
                // Cập nhật thông tin pin của marker
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.mipmap.ic_launcher, "Battery: " + batteryInfo)));
                // Xóa marker cũ và thêm marker mới với thông tin đã cập nhật
                maps.clear();
                maps.addMarker(markerOptions);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }




    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId, String batteryInfo) {
        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.market_custom, null);
        ImageView markerImageView = customMarkerView.findViewById(R.id.avatarImageView);
        TextView markerBatteryTextView = customMarkerView.findViewById(R.id.batteryTextView);
        markerImageView.setImageResource(resId);
        markerBatteryTextView.setText(batteryInfo);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    private class YourBatteryChangeListener {
        private BroadcastReceiver batteryReceiver;

        public void startBatteryChangeListener() {
            batteryReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    float batteryPct = level / (float) scale;
                    String batteryInfo = String.valueOf((int) (batteryPct * 100)) + "%";

                    // Cập nhật dữ liệu pin lên Firebase
                    updateBatteryInfo(batteryInfo);
                }
            };

            // Đăng ký BroadcastReceiver để lắng nghe sự thay đổi pin
            registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }

        public void stopBatteryChangeListener() {
            // Ngừng lắng nghe sự thay đổi pin
            if (batteryReceiver != null) {
                unregisterReceiver(batteryReceiver);
            }
        }
    }

    // Cập nhật dữ liệu pin lên Firebase
    private void updateBatteryInfo(String batteryInfo) {
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("batteryLevel").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        firebaseRef.child("currentBattery").setValue(batteryInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "Dữ liệu pin đã được cập nhật thành công.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase", "Lỗi khi cập nhật dữ liệu pin: " + e.getMessage());
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }else {
                Toast.makeText(this,"Location permission is denied, please allow permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void transfer(String sou, String des){
        des = sou;
    }
    // Hàm để tạo Bitmap từ layout XML
    private Bitmap createMarkerBitmap() {
        View customMarkerView = getLayoutInflater().inflate(R.layout.market_custom, null);
        ImageView avatarImageView = customMarkerView.findViewById(R.id.avatarImageView);
        TextView batteryTextView = customMarkerView.findViewById(R.id.batteryTextView);
//        String batteryPercentage = String.valueOf(getCurrentBatteryLevel());
        String batteryPercentage = "";
        Integer batteryInteger;

        DatabaseReference batteryRef = FirebaseDatabase.getInstance().getReference().child("batteryLevel").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        batteryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer battery = dataSnapshot.child("currentBattery").getValue(Integer.class);
                    Toast.makeText(MainActivity.this, String.valueOf(battery),Toast.LENGTH_SHORT).show();
                    batteryTextView.setText(battery + "%");
//                    transfer(battery, batteryPercentage);
                } else {
                    // Không tìm thấy thông tin người dùng, xử lý tương ứng
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });

        batteryTextView.setText(batteryPercentage + "%");
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        customMarkerView.draw(canvas);
        return bitmap;
    }

    private void getDirection(LatLng origin, LatLng dest){
        // Getting URL to the Google Directions API
        String url = UrlGenerator.getDirectionsUrl(origin, dest);

        UrlDownloader urlDownloader = new UrlDownloader(MainActivity.this);

        // Start downloading json data from Google Directions API
        // and draw routes
        urlDownloader.execute(url);
    }

}