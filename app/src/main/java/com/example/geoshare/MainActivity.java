package com.example.geoshare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{
    FirebaseUser firebaseUser;
    ImageButton buttonProfile, buttonInvite, buttonLocation, buttonChat, buttonCommunity, buttonGhost, buttonSearch, buttonSetting;
    private GoogleMap maps;
    private final int FINE_PERMISSION_CODE = 1;
    private long pressedTime;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Polyline currentPolyline = null;
    private List<String> searchHistoryList;
    private ConnectivityReceiver connectivityReceiver;
    private boolean checkGhost;

    public void setCurrentPolyline(Polyline polyline) {
        if (this.currentPolyline != null) this.currentPolyline.remove();
        this.currentPolyline = polyline;
    }

    public GoogleMap getMaps() {
        return maps;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public Polyline getCurrentPolyline() {
        return currentPolyline;
    }

    public List<String> getSearchHistoryList() { return searchHistoryList; }

    private static MainActivity instance;


    public MainActivity() {
        instance = this;
        Log.d("Mainactivity", "tao moi");
    }


    public static MainActivity getInstance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkInternetConnection();
        checkGhost = false;
        firebaseUser = FirebaseSingleton.getInstance().getFirebaseAuth().getCurrentUser();
        if (firebaseUser == null) {
            Intent intent = new Intent(getApplicationContext(), SignIn.class);
            startActivity(intent);
            finish();
        }

        // check if user is an admin
        DatabaseReference userRef = RealtimeDatabase.getInstance().getUsersReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check admin
                String valueAdmin = snapshot.child("isAdmin").getValue(String.class);

                if (Objects.equals(valueAdmin, "true")) {
                    Toast.makeText(MainActivity.this, "This is an admin account, redirecting...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                    startActivity(intent);
                    finish();
                }

                // check ban
                String strUnbanDate = snapshot.child("unbanDate").getValue(String.class);

                if (strUnbanDate != null) {
                    checkBan(strUnbanDate);
                }

                createView();

                if (firebaseUser != null) {
                    Intent batteryService = new Intent(MainActivity.this, BatteryService.class);
                    startService(batteryService);
                }

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                getLastLocation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createView() {
        searchHistoryList = new ArrayList<>();
        buttonInvite = findViewById(R.id.btnInvite);
        buttonProfile = findViewById(R.id.btnProfile);
        buttonLocation = findViewById(R.id.btnCurrentLocation);
        buttonChat = findViewById(R.id.btnChat);
        buttonSearch = findViewById(R.id.btnSearch);
        buttonCommunity = findViewById(R.id.btnCommunity);
        buttonSetting = findViewById(R.id.btnSetting);
        buttonGhost = findViewById(R.id.btnGhost);
        buttonGhost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkGhost==false) {
                    buttonGhost.setImageResource(R.drawable.ghost);
                    checkGhost = true;
                } else {
                    buttonGhost.setImageResource(R.drawable.ic_ghost3);
                    checkGhost = false;
                }
            }
        });
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentPolyline == null) {
                    Intent intent = new Intent(getApplicationContext(), Search.class);
                    startActivity(intent);
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
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Setting.class);
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
        buttonCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Community.class);
                startActivity(intent);
            }
        });
    }


    private void checkBan(String strUnbanDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date unbanDate;
        try {
            unbanDate = dateFormat.parse(strUnbanDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Date currentDate = new Date();
        long millis = currentDate.getTime() - unbanDate.getTime();

        if (millis >= 0) {
            // unban
            RealtimeDatabase.getInstance()
                    .getUsersReference()
                    .child(Authentication.getInstance().getCurrentUserId())
                    .child("unbanDate").removeValue();
        } else {
            // configure builder
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Notification")
                    .setMessage("Your account is being banned until " + strUnbanDate + ".")
                    .setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Authentication.getInstance().signOut();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNeutralButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });

            // build dialog
            AlertDialog dialog = builder.create();
            dialog.show();

            Log.d("DEBUG TAG", "Showed dialog");
        }
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
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
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
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        maps = googleMap;

        MarkerManager.getInstance().setGoogleMap(maps);
        MarkLocation markLocation = new MarkLocation(MainActivity.this, maps);
        markLocation.readMarkersFromDatabase();
        MarkerManager.getInstance().setMarkerClickListener();
        maps.setOnMapLongClickListener(markLocation);

        LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerManager.getInstance().createMarker(myLocation, Authentication.getInstance().getCurrentUserId());

        float zoomLevel = 12.0f;
        maps.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoomLevel));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission is denied, please allow permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            showNoInternetDialog();
        }
    }
    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // You can perform any action on OK click if needed
                    dialog.dismiss();
                    finish();
                })
                .setCancelable(false); // Prevent dialog from being dismissed by tapping outside

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void changeSearchIcon() {
        // change search button
        buttonSearch.setImageDrawable(ContextCompat.getDrawable(
                MainActivity.this, R.drawable.ic_search_close));
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkInternetConnection();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            LocationManager.getInstance().startLocationUpdates();
            LocationManager.getInstance().getLocationForFriends();
            LocationManager.getInstance().getLocationForCommunity();
        }
    }
}