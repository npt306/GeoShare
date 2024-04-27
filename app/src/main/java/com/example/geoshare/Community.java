package com.example.geoshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geoshare.Adapter.ChatAdapter;
import com.example.geoshare.Adapter.CommunityAdapter;
import com.example.geoshare.Database.Authentication.Authentication;
import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.example.geoshare.Model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Community extends AppCompatActivity {
    ImageButton buttonBack, buttonCreate;
    RecyclerView recyclerViewCommunityList;
    CommunityAdapter communityAdapter;
    public static int numberOfColumns = 2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        recyclerViewCommunityList = findViewById(R.id.recycles_view_Community_list);
        buttonBack = findViewById(R.id.btnCommunityBack);
        buttonCreate = findViewById(R.id.btnNewCommunity);

        communityAdapter = new CommunityAdapter(getApplicationContext(), this);
        recyclerViewCommunityList.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateCommunity.class);
                startActivity(intent);
            }
        });

        DatabaseReference communityRef = RealtimeDatabase.getInstance().getCommunityReference();
        communityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                communityAdapter.clearGroupList();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CommunityGroup group = dataSnapshot.getValue(CommunityGroup.class);
                    if(group != null) {
                        communityAdapter.addGroupToList(group);
                        communityAdapter.notifyDataSetChanged();
                    }
                }
                recyclerViewCommunityList.setAdapter(communityAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
