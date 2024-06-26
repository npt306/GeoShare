package com.example.geoshare;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.geoshare.Database.FirebaseSingleton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class LocationManager {
    private static volatile LocationManager instance;
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation = null;
    private long UPDATE_INTERVAL_MS = 0;
    private final Handler handler;
    private final DatabaseReference databaseRef;

    private LocationManager() {
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.getInstance());
        this.handler = new Handler(Looper.getMainLooper());

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabase.getReference("Locations");
    }

    public static LocationManager getInstance() {
        if (instance == null) {
            synchronized (LocationManager.class) {
                if (instance == null) {
                    instance = new LocationManager();
                }
            }
        }
        return instance;
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(MainActivity.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.getInstance(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100).build();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    updateCurrentLocationMarker(location);

                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(() -> updateLocationToFirebase(location.getLatitude(), location.getLongitude()), UPDATE_INTERVAL_MS);

                    UPDATE_INTERVAL_MS = 100;
                }
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void updateCurrentLocationMarker(Location location) {
        FirebaseUser firebaseUser = FirebaseSingleton.getInstance().getFirebaseAuth().getCurrentUser();

        if (firebaseUser == null) {
            return;
        }

//        if (!getLocationVisibility()) {
//            return;
//        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        MarkerManager.getInstance().createMarker(latLng, uid);
    }

    private void updateLocationToFirebase(double latitude, double longitude) {
        FirebaseUser firebaseUser = FirebaseSingleton.getInstance().getFirebaseAuth().getCurrentUser();

        if (firebaseUser == null) {
            return;
        }

        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        Location newLocation = new Location("gps");
        newLocation.setLatitude(latitude);
        newLocation.setLongitude(longitude);

        long MIN_DISTANCE_FOR_UPDATE = 5;
        if (currentLocation == null || newLocation.distanceTo(currentLocation) > MIN_DISTANCE_FOR_UPDATE) {
            if (currentLocation == null) currentLocation = new Location("gps");

            currentLocation.setLatitude(latitude);
            currentLocation.setLongitude(longitude);
            databaseRef.child(uid).updateChildren(new HashMap<String, Object>() {{
                put("latitude", latitude);
                put("longitude", longitude);
                put("time", System.currentTimeMillis());
                put("visible", "true");
            }});

            Log.d("LocationManager", "Location updated to Firebase: " + newLocation.toString());

            currentLocation = newLocation;
        } else {
            Log.d("LocationManager", "Location change is not significant.");
        }
    }

    public void getLocationVisibility() {

    }


    public void setLocationVisibility(boolean visible) {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        databaseRef.child(uid).updateChildren(new HashMap<String, Object>() {{
            if (visible) {
                put("visible", "true");
            } else {
                put("visible", "false");
            }
        }});
    }

    public void getLocationForFriends() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference friendsRef = firebaseDatabase.getReference("Friends");
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        friendsRef.child(uid).child("friendList").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> friendIds = (List<String>) task.getResult().getValue();
                if (friendIds != null) {
                    for (String friendId : friendIds) {
                        databaseRef.child(friendId).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful() && !friendId.equals("empty")) {
                                Log.d("LocationManager", "Friend location updated: " + task1.getResult().toString());

                                Location friendLocation = new Location("");
                                friendLocation.setLatitude((Double) task1.getResult().child("latitude").getValue());
                                friendLocation.setLongitude((Double) task1.getResult().child("longitude").getValue());

                                updateFriendLocation(friendId, friendLocation);
                            }
                        });
                    }
                }
            }
        });
    }

    public void getLocationForCommunity() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference communityRef = firebaseDatabase.getReference("Community");
        DatabaseReference communityListRef = firebaseDatabase.getReference("UsersCommunity");
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        communityListRef.child(uid).child("CommunityList").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> communityIds = (List<String>) task.getResult().getValue();
                if (communityIds != null) {
                    for (String communityId : communityIds) {
                        communityRef.child(communityId).child("membersList").get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                List<String> memberIds = (List<String>) task1.getResult().getValue();
                                if (memberIds != null) {
                                    for (String memberId : memberIds) {
                                        databaseRef.child(memberId).get().addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful() && !memberId.equals("empty")) {
                                                Log.d("LocationManager", "Community member location updated: " + task2.getResult().toString());

                                                Location memberLocation = new Location("");
                                                memberLocation.setLatitude((Double) task2.getResult().child("latitude").getValue());
                                                memberLocation.setLongitude((Double) task2.getResult().child("longitude").getValue());

                                                displayCommunityLocation(memberId, memberLocation);
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void updateFriendLocation(String friendId, Location location) {
        FirebaseUser firebaseUser = FirebaseSingleton.getInstance().getFirebaseAuth().getCurrentUser();

        if (firebaseUser == null) {
            return;
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerManager.getInstance().createMarker(latLng, friendId);

        databaseRef.child(friendId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String visible = String.valueOf(dataSnapshot.child("visible").getValue());
                if (Objects.equals(visible, "true")) {
                    double latitude = (double) dataSnapshot.child("latitude").getValue();
                    double longitude = (double) dataSnapshot.child("longitude").getValue();
                    LatLng latLng = new LatLng(latitude, longitude);

                    MarkerManager.getInstance().createMarker(latLng, friendId);
                } else {
                    MarkerManager.getInstance().hideMarker(friendId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }

    public void displayCommunityLocation(String memberId, Location location) {
        MarkerManager.getInstance().createMarker(new LatLng(location.getLatitude(), location.getLongitude()), memberId);
    }
}
