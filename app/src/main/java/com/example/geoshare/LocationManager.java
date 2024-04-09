package com.example.geoshare;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LocationManager {
    private static volatile LocationManager instance;
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final WeakReference<Context> contextRef;
    private Location currentLocation;
    private final long UPDATE_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1); // 1 minute
    private final Handler handler;
    private final Map<String, Location> friendLocations = new HashMap<>();
    private final DatabaseReference databaseRef;

    private LocationManager(Context context) {
        this.contextRef = new WeakReference<>(context.getApplicationContext());
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        this.handler = new Handler(Looper.getMainLooper());

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabase.getReference("locations");
    }

    public static LocationManager getInstance(Context context) {
        if (instance == null) {
            synchronized (LocationManager.class) {
                if (instance == null) {
                    instance = new LocationManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public void startLocationUpdates() {
        Context context = contextRef.get();
        if (context == null || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted or context is null. Handle requesting permissions here or return early.
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .build();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Update the current location marker on the map
                    updateCurrentLocationMarker(location);

                    // Update Firebase with a delay
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(() -> updateLocationToFirebase(location.getLatitude(), location.getLongitude()), UPDATE_INTERVAL_MS);
                }
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void updateCurrentLocationMarker(Location location) {
        // Update the current location marker on the map
        currentLocation = location;
        // Your map marker update logic here
    }

    private void updateLocationToFirebase(double latitude, double longitude) {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        currentLocation.setLatitude(latitude);
        currentLocation.setLongitude(longitude);

        Location newLocation = new Location("");
        newLocation.setLatitude(latitude);
        newLocation.setLongitude(longitude);
        newLocation.setTime(System.currentTimeMillis());

        databaseRef.child(uid).setValue(newLocation);
    }

    public void getLocationForFriends(List<String> friendIds) {
        // Your logic to get locations for friends from Firebase
        // Example:
        for (String friendId : friendIds) {
            Location friendLocation = friendLocations.get(friendId);
            // Update friend's marker on the map
            // ...
        }
    }

    public Location getCurrentLocation(){
        return currentLocation;
    }

    public void updateFriendLocation(String friendId, Location location) {
        friendLocations.put(friendId, location);
    }
}
