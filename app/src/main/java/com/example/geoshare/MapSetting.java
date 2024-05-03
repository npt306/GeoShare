package com.example.geoshare;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MapSetting extends AppCompatActivity {
    ImageButton backButton;
    Button lightButton;
    Button darkButton;
    Button satelliteButton;
    ImageView imageView;
    String currentMapStyle = "standard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_setting_layout);

        // Initialize views
        lightButton = findViewById(R.id.lightButton);
        darkButton = findViewById(R.id.darkButton);
        satelliteButton = findViewById(R.id.satilliteButton);
        imageView = findViewById(R.id.mapImage);
        backButton = findViewById(R.id.back_button);

        // Get the current map style from Firebase
        getCurrentMapStyleForFirebase();

        // Set click listeners
        lightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lightButton.setBackgroundResource(R.drawable.button_click);
                darkButton.setBackgroundResource(R.drawable.button_default);
                satelliteButton.setBackgroundResource(R.drawable.button_default);
                lightButton.setTextColor(getResources().getColor(R.color.white));
                darkButton.setTextColor(getResources().getColor(R.color.black));
                satelliteButton.setTextColor(getResources().getColor(R.color.black));
                imageView.setImageResource(R.drawable.light_map);
                updateMapStyleForFirebase("standard");
                MainActivity.getInstance().getMaps().setMapType(GoogleMap.MAP_TYPE_NORMAL);
                MainActivity.getInstance().getMaps().setMapStyle(MapStyleOptions.loadRawResourceStyle(MainActivity.getInstance(), R.raw.map_standard));
            }
        });

        darkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lightButton.setBackgroundResource(R.drawable.button_default);
                darkButton.setBackgroundResource(R.drawable.button_click);
                satelliteButton.setBackgroundResource(R.drawable.button_default);
                lightButton.setTextColor(getResources().getColor(R.color.black));
                darkButton.setTextColor(getResources().getColor(R.color.white));
                satelliteButton.setTextColor(getResources().getColor(R.color.black));
                imageView.setImageResource(R.drawable.dark_map);
                updateMapStyleForFirebase("dark");
                MainActivity.getInstance().getMaps().setMapType(GoogleMap.MAP_TYPE_NORMAL);
                MainActivity.getInstance().getMaps().setMapStyle(MapStyleOptions.loadRawResourceStyle(MainActivity.getInstance(), R.raw.map_dark));
            }
        });

        satelliteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lightButton.setBackgroundResource(R.drawable.button_default);
                darkButton.setBackgroundResource(R.drawable.button_default);
                satelliteButton.setBackgroundResource(R.drawable.button_click);
                lightButton.setTextColor(getResources().getColor(R.color.black));
                darkButton.setTextColor(getResources().getColor(R.color.black));
                satelliteButton.setTextColor(getResources().getColor(R.color.white));
                imageView.setImageResource(R.drawable.satilite_map);
                updateMapStyleForFirebase("satellite");
                MainActivity.getInstance().getMaps().setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void getCurrentMapStyleForFirebase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        firebaseDatabase.getReference("MapStyle").child(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().getValue() == null) {
                    updateMapStyleForFirebase("standard");
                    currentMapStyle = "standard";
                } else {
                    currentMapStyle = task.getResult().getValue().toString();
                }
            }

            // Set the current map style
            if (currentMapStyle.equals("standard")) {
                imageView.setImageResource(R.drawable.light_map);
                lightButton.setBackgroundResource(R.drawable.button_click);
                lightButton.setTextColor(getResources().getColor(R.color.white));
            } else if (currentMapStyle.equals("dark")) {
                imageView.setImageResource(R.drawable.dark_map);
                darkButton.setBackgroundResource(R.drawable.button_click);
                darkButton.setTextColor(getResources().getColor(R.color.white));
            } else {
                imageView.setImageResource(R.drawable.satilite_map);
                satelliteButton.setBackgroundResource(R.drawable.button_click);
                satelliteButton.setTextColor(getResources().getColor(R.color.white));
            }
        });
    }

    void updateMapStyleForFirebase(String mapStyle) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        firebaseDatabase.getReference("MapStyle").child(uid).setValue(mapStyle);
    }
}
