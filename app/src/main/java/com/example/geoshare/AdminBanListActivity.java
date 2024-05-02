package com.example.geoshare;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.geoshare.Adapter.BanListAdapter;
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

public class AdminBanListActivity extends AppCompatActivity {
    private List<BanListItem> itemList;
    private static long itemCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_banned_list);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ListView listView = findViewById(R.id.custom_listview);
        itemList = new ArrayList<>();
        BanListAdapter adapter = new BanListAdapter(AdminBanListActivity.this, itemList);
        listView.setAdapter(adapter);

        // add data from db
        DatabaseReference reference = RealtimeDatabase.getInstance().getBannedUsersReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get list of banned users
                long itemSize = snapshot.getChildrenCount();

                if (itemSize == 0) Toast.makeText(AdminBanListActivity.this, "The list is empty!", Toast.LENGTH_SHORT).show();

                for (DataSnapshot bannedUserSnapshot : snapshot.getChildren()) {
                    String bannedUserId = bannedUserSnapshot.getKey();
                    DatabaseReference userRef = RealtimeDatabase.getInstance()
                            .getUsersReference().child(bannedUserId);

                    // ban detail
                    String banDate = bannedUserSnapshot.child("banDate").getValue(String.class);
                    String unbanDate = bannedUserSnapshot.child("unbanDate").getValue(String.class);
                    String reportDescription = bannedUserSnapshot.child("reportDescription").getValue(String.class);
                    ArrayList<String> banReasons = (ArrayList<String>) bannedUserSnapshot.child("banReasons").getValue();

                    // get banned user's name and image
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String userName = snapshot.child("username").getValue(String.class);
                            String imageURL = snapshot.child("imageURL").getValue(String.class);

                            if(!Objects.equals(imageURL, "default")) {
                                StorageReference storageRef = Storage.getInstance().getUsersAvatarReference();
                                storageRef.child(imageURL).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        itemList.add(new BanListItem(bannedUserId, userName, banDate, unbanDate, reportDescription, banReasons, uri));
                                        adapter.notifyDataSetChanged();
                                        itemCount++;
                                        if (itemCount == itemSize) Toast.makeText(AdminBanListActivity.this, "All data has been loaded!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else{
                                itemList.add(new BanListItem(bannedUserId, userName, banDate, unbanDate, reportDescription, banReasons, null));
                                adapter.notifyDataSetChanged();
                                itemCount++;
                                if (itemCount == itemSize) Toast.makeText(AdminBanListActivity.this, "All data has been loaded!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
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
