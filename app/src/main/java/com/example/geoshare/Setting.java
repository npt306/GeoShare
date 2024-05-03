package com.example.geoshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

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

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        DatabaseReference reference = firebaseDatabase.getReference("Premium").child(userId);
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().getValue() == null) {
                    AdView mAdView = findViewById(R.id.adView);
                    AdManager.loadBannerAd(mAdView);
                }
            }
        });

        selectMapSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapSetting.class);
                startActivity(intent);
            }
        });
        selectPremiumUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PremiumUpgrade.class);
                startActivity(intent);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        DatabaseReference reference = firebaseDatabase.getReference("Premium").child(userId);
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().getValue() != null) {
                    AdView mAdView = findViewById(R.id.adView);
                    mAdView.setVisibility(View.GONE);
                }
            }
        });
    }
}
