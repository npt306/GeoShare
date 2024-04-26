package com.example.geoshare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.geoshare.Adapter.ReportAdapter;
import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.example.geoshare.Database.Storage.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminReportActivity extends AppCompatActivity {
    private List<ReportListItem> itemList;
    private List<String> timeList;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_report);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                startActivity(intent);
                finish();
            }
        });

        listView = findViewById(R.id.custom_listview);
        itemList = new ArrayList<>();
        ReportAdapter adapter = new ReportAdapter(AdminReportActivity.this, itemList);
        listView.setAdapter(adapter);

        // add data from db
        DatabaseReference reference = RealtimeDatabase.getInstance().getReportsReference().orderByValue().getRef();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get list of timestamps
                for (DataSnapshot timestampSnapshot : snapshot.getChildren()) {
                    // get list of reports represented by senderId in that timestamp
                    for (DataSnapshot reportSnapshot : timestampSnapshot.getChildren()) {
                        String receiverId = reportSnapshot.child("receiver").getValue(String.class);
                        DatabaseReference receiverRef = RealtimeDatabase.getInstance()
                                .getUsersReference().child(receiverId);

                        // get receiver name from that report
                        receiverRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String userName = snapshot.child("username").getValue(String.class);
                                Uri userImg = null;
                                String imageURL = snapshot.child("imageURL").getValue(String.class);

                                if(!Objects.equals(imageURL, "default")) {
                                    StorageReference storageRef = Storage.getInstance().getUsersAvatarReference();
                                    storageRef.child(imageURL).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            itemList.add(new ReportListItem(userName, reportSnapshot.getKey(), timestampSnapshot.getKey(), uri));
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
