package com.example.geoshare;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminBanDetail extends AppCompatActivity{
    private BanListItem chosenItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_ban_detail);

        chosenItem = (BanListItem) getIntent().getSerializableExtra("chosenBan");

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button unbanBtn = (Button) findViewById(R.id.unban_Btn);
        unbanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeCurrentBan();
            }
        });

        // get UI elements for uploading
        TextView userName = findViewById(R.id.userName);
        TextView banDate = findViewById(R.id.ban_date);
        TextView unbanDate = findViewById(R.id.unban_date);
        TextView banReasons = findViewById(R.id.ban_reasons);
        TextView reportDescription = findViewById(R.id.report_description);
        CircleImageView userImage = findViewById(R.id.user_Avatar);


        // upload UI elements
        userName.setText(chosenItem.getUserName());
        banDate.setText(chosenItem.getBanDate());
        unbanDate.setText(chosenItem.getUnbanDate());
        reportDescription.setText(chosenItem.getReportDescription());

        if (chosenItem.getImage() != null){
            Glide.with(getApplicationContext()).load(chosenItem.getImage()).into(userImage);
        }

        // get ban reasons
        StringBuilder builder = new StringBuilder();

        for (String reason : chosenItem.getBanReasons()) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(reason);
        }
        banReasons.setText(builder.toString());

    }

    private void removeCurrentBan(){
        RealtimeDatabase.getInstance()
                .getBannedUsersReference()
                .child(chosenItem.getUserId()).removeValue();
        System.exit(0);
    }
}

