package com.example.geoshare;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.geoshare.Battery.BatteryService;
import com.example.geoshare.Database.Authentication.Authentication;
import com.example.geoshare.Database.FirebaseSingleton;
import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.example.geoshare.MarkLocation.MarkLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    ImageButton buttonProfile, buttonInvite, buttonLocation, buttonChat, buttonSearch;
    private GoogleMap maps;
    private final int FINE_PERMISSION_CODE = 1;
    private MarkerManager markerManager;
    private long pressedTime;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Polyline currentPolyline = null;
    public void setCurrentPolyline(Polyline polyline){this.currentPolyline = polyline;}
    public GoogleMap getMaps(){
        return maps;
    }
    public Location getCurrentLocation(){return currentLocation;}
    public Polyline getCurrentPolyline(){return currentPolyline;}


    // Implement to get context from other Intent
    private static MainActivity instance;

    public MainActivity() {
        instance = this;
    }

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseUser = FirebaseSingleton.getInstance().getFirebaseAuth().getCurrentUser();
        if(firebaseUser == null){
            Intent intent = new Intent(getApplicationContext(), SignIn.class);
            startActivity(intent);
            finish();
        }

        // check if user is an admin
        DatabaseReference userRef = RealtimeDatabase.getInstance().getUsersReference()
                .child(FirebaseSingleton.getInstance().getFirebaseAuth().getCurrentUser().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.child("isAdmin").getValue(String.class);

                if (Objects.equals(value, "true")){
                    Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttonInvite = findViewById(R.id.btnInvite);
        buttonProfile =findViewById(R.id.btnProfile);
        buttonLocation = findViewById(R.id.btnCurrentLocation);
        buttonChat = findViewById(R.id.btnChat);
        buttonSearch = findViewById(R.id.btnSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentPolyline == null){
                    Intent intent = new Intent(getApplicationContext(), Search.class);
                    startActivity(intent);
//                    LatLng des = new LatLng(10.8270849, 106.6892387);
//                    Location location = MainActivity.getInstance().getCurrentLocation();
//                    LatLng curLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                    getDirection(curLocation, des);
                } else {
                    currentPolyline.remove();
                    currentPolyline = null;
                    buttonSearch.setImageDrawable(ContextCompat.getDrawable(
                            MainActivity.this, R.drawable.ic_search));
                }
            }
        });
        buttonInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Invite.class);
                startActivity(intent);
            }
        });
        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
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
            }
        });

        if(firebaseUser != null) {
            // Bắt đầu battery service
            Intent batteryService = new Intent(this, BatteryService.class);
            startService(batteryService);
            // chưa kết thúc battery service
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

//        markerManager = new MarkerManager(MainActivity.this);
    }

    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
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
                .zoom(17)
                .bearing(currentLocation.getBearing())
                .tilt(70)
                .build();
        maps.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
        maps.animateCamera(camUpd3);

        Log.d("DEBUG TAG", "Focusing on current location");
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        maps = googleMap;
        MarkLocation markLocation = new MarkLocation(MainActivity.this, maps);
        markLocation.readMarkersFromDatabase();
        maps.setOnMapLongClickListener(markLocation);

        LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerManager.getInstance().createMarker(myLocation, Authentication.getInstance().getCurrentUserId());

        float zoomLevel = 12.0f;
        maps.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoomLevel));

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


    public void getDirection(LatLng origin, LatLng dest){
        // Getting URL to the Google Directions API
        Log.d("DEBUG TAG", "Preparing to draw path");
        String url = UrlGenerator.getDirectionsUrl(origin, dest);
        // Start downloading json data from Google Directions API
        // and draw routes
        new UrlDownloader().execute(url);

        // change search button
        buttonSearch.setImageDrawable(ContextCompat.getDrawable(
                MainActivity.this, R.drawable.ic_search_close));

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            LocationManager.getInstance().startLocationUpdates();
//            LocationManager.getInstance().getLocationForFriends();
        }
    }
}