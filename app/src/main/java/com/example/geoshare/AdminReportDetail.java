package com.example.geoshare;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

public class AdminReportDetail extends AppCompatActivity{
    private Report chosenItem;

    // Implement to get context from other Intent
    private static AdminReportDetail instance;

    public AdminReportDetail() {
        instance = this;
    }

    public static AdminReportDetail getInstance() {
        if (instance == null){
            instance = new AdminReportDetail();
        }
        return instance;
    }

    public void setChosenItem(Report item){
        this.chosenItem = item;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_report_detail);

        chosenItem = (Report) getIntent().getSerializableExtra("chosenReport");

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button skipBtn = (Button) findViewById(R.id.skip_Btn);
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button banBtn = (Button) findViewById(R.id.ban_Btn);
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView userName = findViewById(R.id.userName);
        TextView reportTime = findViewById(R.id.reportTime);
        TextView reportDetail = findViewById(R.id.user_reportDetail);
        TextView reportDescription = findViewById(R.id.reportDescription);

        DatabaseReference receiverRef = RealtimeDatabase.getInstance()
                .getUsersReference().child(chosenItem.getReceiverId()).child("username");
        receiverRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String str_userName = snapshot.getValue(String.class);
                userName.setText(str_userName);

                StringBuilder builder = new StringBuilder();

                for (String problem : chosenItem.getReportProblems()) {
                    if (builder.length() != 0){
                        builder.append(", ");
                    }
                    builder.append(problem);
                }

                reportTime.setText(chosenItem.getTimestamp());
                reportDetail.setText(builder.toString());
                reportDescription.setText(chosenItem.getReportDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}

