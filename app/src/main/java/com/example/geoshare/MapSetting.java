package com.example.geoshare;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MapSetting extends AppCompatActivity{
    ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_setting_layout);
        // Initialize views
        Button lightButton = findViewById(R.id.lightButton);
        Button darkButton = findViewById(R.id.darkButton);
        Button satelliteButton = findViewById(R.id.satiliteButton);
        ImageView imageView = findViewById(R.id.mapImage);
        backButton = findViewById(R.id.back_button);
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
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
