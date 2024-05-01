package com.example.geoshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class Setting extends AppCompatActivity {
    LinearLayout selectMapSetting, selectPremiumUpgrade;
    ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((R.layout.activity_setting));
        selectMapSetting = findViewById(R.id.layout_mapsetting);
        selectPremiumUpgrade = findViewById(R.id.layout_premium);
        backButton = findViewById(R.id.back_button);

        selectMapSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapSetting.class);
                startActivity(intent);
                finish();
            }
        });
        selectPremiumUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PremiumUpgrade.class);
                startActivity(intent);
                finish();
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
