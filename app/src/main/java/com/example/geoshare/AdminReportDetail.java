package com.example.geoshare;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.example.geoshare.Database.Storage.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

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
                removeCurrentReport();
            }
        });

        Button banBtn = (Button) findViewById(R.id.ban_Btn);
        banBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference usersReference = RealtimeDatabase.getInstance().getUsersReference().child(chosenItem.getReceiverId());
//                String banTime = "30d";
                Date today = new Date();
                Date unbanDate = new Date(today.getTime() + (long)(30L * 3600 * 1000 * 24)); // 30d in millis

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateTime = dateFormat.format(unbanDate);
                usersReference.child("unbanDate").setValue(currentDateTime);

                removeCurrentReport();
            }
        });

        TextView userName = findViewById(R.id.userName);
        TextView reportTime = findViewById(R.id.reportTime);
        TextView reportDetail = findViewById(R.id.user_reportDetail);
        TextView reportDescription = findViewById(R.id.reportDescription);
        CircleImageView userImage = findViewById(R.id.user_Avatar);

        DatabaseReference receiverRef = RealtimeDatabase.getInstance()
                .getUsersReference().child(chosenItem.getReceiverId());
        receiverRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get name
                String str_userName = snapshot.child("username").getValue(String.class);

                // get problems
                StringBuilder builder = new StringBuilder();

                for (String problem : chosenItem.getReportProblems()) {
                    if (builder.length() != 0) {
                        builder.append(", ");
                    }
                    builder.append(problem);
                }

                // get image
                String imageURL = snapshot.child("imageURL").getValue(String.class);

                if (imageURL != null && !Objects.equals(imageURL, "default")) {
                    StorageReference storageRef = Storage.getInstance().getUsersAvatarReference();
                    storageRef.child(imageURL).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getApplicationContext()).load(uri).into(userImage);
                        }
                    });
                }

                // upload UI elements
                userName.setText(str_userName);
                reportTime.setText(chosenItem.getTimestamp());
                reportDetail.setText(builder.toString());
                reportDescription.setText(chosenItem.getReportDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void removeCurrentReport(){
        RealtimeDatabase.getInstance()
                .getReportsReference()
                .child(chosenItem.getTimestamp())
                .child(chosenItem.getSenderId()).removeValue();
        System.exit(0);
    }
}

