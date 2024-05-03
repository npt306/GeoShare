package com.example.geoshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class PremiumUpgrade extends AppCompatActivity {
    ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upgrade_premium);

        backButton = findViewById(R.id.back_button);
        LinearLayout premiumLayout1 = findViewById(R.id.price_layout1);
        LinearLayout premiumLayout2 = findViewById(R.id.price_layout2);
        LinearLayout premiumLayout3 = findViewById(R.id.price_layout3);

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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        premiumLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productId = "prod_Q2ZgygFEMIEQQq";
                String priceId = "price_1PCUXHL1bECNnFcvaOWt26Vj";

                Intent intent = new Intent(PremiumUpgrade.this, CheckoutActivity.class);
                intent.putExtra("productId", productId);
                intent.putExtra("priceId", priceId);
                startActivity(intent);
            }
        });

        premiumLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productId = "prod_Q2ZjppsU2YISdN";
                String priceId = "price_1PCUaFL1bECNnFcvLh848hRr";

                Intent intent = new Intent(PremiumUpgrade.this, CheckoutActivity.class);
                intent.putExtra("productId", productId);
                intent.putExtra("priceId", priceId);
                startActivity(intent);
            }
        });

        premiumLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productId = "prod_Q2ZkPUOhLvTpLE";
                String priceId = "price_1PCUabL1bECNnFcvvmKt9edx";

                Intent intent = new Intent(PremiumUpgrade.this, CheckoutActivity.class);
                intent.putExtra("productId", productId);
                intent.putExtra("priceId", priceId);
                startActivity(intent);
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
