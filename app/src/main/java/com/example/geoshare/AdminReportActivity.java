package com.example.geoshare;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.core.OrderBy;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminReportActivity extends AppCompatActivity {
    private List<ReportListItem> itemList;
    private ListView listView;
    private static long itemCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_report);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listView = findViewById(R.id.custom_listview);
        itemList = new ArrayList<>();
        ReportAdapter adapter = new ReportAdapter(AdminReportActivity.this, itemList);
        listView.setAdapter(adapter);


        // add data from db
        DatabaseReference reference = RealtimeDatabase.getInstance().getReportsReference().orderByKey().getRef();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get list of timestamps
                long itemSize = snapshot.getChildrenCount();
                if (itemSize == 0) Toast.makeText(AdminReportActivity.this, "The list is empty!", Toast.LENGTH_SHORT).show();

                for (DataSnapshot timestampSnapshot : snapshot.getChildren()) {
                    // get list of reports represented by senderId in that timestamp
                    for (DataSnapshot reportSnapshot : timestampSnapshot.getChildren()) {
                        String receiverId = reportSnapshot.child("receiverId").getValue(String.class);
                        DatabaseReference receiverRef = RealtimeDatabase.getInstance()
                                .getUsersReference().child(receiverId);

                        // get receiver name from that report
                        receiverRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String userName = snapshot.child("username").getValue(String.class);
                                String imageURL = snapshot.child("imageURL").getValue(String.class);

                                if(!Objects.equals(imageURL, "default")) {
                                    StorageReference storageRef = Storage.getInstance().getUsersAvatarReference();
                                    storageRef.child(imageURL).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            itemList.add(new ReportListItem(userName, reportSnapshot.getKey(), timestampSnapshot.getKey(), uri));
                                            adapter.notifyDataSetChanged();
                                            itemCount++;
                                            if (itemCount == itemSize) Toast.makeText(AdminReportActivity.this, "All data has been loaded!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    itemList.add(new ReportListItem(userName, reportSnapshot.getKey(), timestampSnapshot.getKey(), null));
                                    adapter.notifyDataSetChanged();
                                    itemCount++;
                                    if (itemCount == itemSize) Toast.makeText(AdminReportActivity.this, "All data has been loaded!", Toast.LENGTH_SHORT).show();
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
