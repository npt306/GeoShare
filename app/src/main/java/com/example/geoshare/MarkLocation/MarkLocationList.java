package com.example.geoshare.MarkLocation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.geoshare.Database.RealtimeDatabase.MarkLocationDatabase;
import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.example.geoshare.LocationManager;
import com.example.geoshare.MainActivity;
import com.example.geoshare.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MarkLocationList extends AppCompatActivity {
    ImageButton buttonBack;
    RecyclerView recyclerViewMarkerLocationList;
    MarkLocationListAdapter markLocationListAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_location_list);
        recyclerViewMarkerLocationList = findViewById(R.id.recycles_view_mark_location_list);
        buttonBack = findViewById(R.id.btnMarkLocationListBack);

        markLocationListAdapter = new MarkLocationListAdapter(getApplicationContext());
        recyclerViewMarkerLocationList.setLayoutManager(new LinearLayoutManager(this));

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                LocationManager.getInstance().startLocationUpdates();
            }
        });


        DatabaseReference markLocationReference = MarkLocationDatabase.getInstance().getReferenceToCurrentUser();
        markLocationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                markLocationListAdapter.clearMarkerLocationList();
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    String key = snapshot.getKey();
                    String address = snapshot.child("address").getValue(String.class);
                    double latitude = snapshot.child("latLng").child("latitude").getValue(Double.class);
                    double longitude = snapshot.child("latLng").child("longitude").getValue(Double.class);
                    String typeMarker = snapshot.child("typeMarker").getValue(String.class);
                    LatLng latLng = new LatLng(latitude,longitude);
                    MarkerLocationModel marker = new MarkerLocationModel(latLng, typeMarker, address);
                    marker.setKey(key);
                    markLocationListAdapter.addMarkerLocationToList(marker);
                    markLocationListAdapter.notifyDataSetChanged();
                }
                recyclerViewMarkerLocationList.setAdapter((markLocationListAdapter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
