package com.example.geoshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geoshare.Adapter.ChatAdapter;
import com.example.geoshare.Adapter.FriendListAdapter;
import com.example.geoshare.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Chat extends AppCompatActivity {
    ImageButton buttonBack;
    RecyclerView recyclerViewChatList;
    ChatAdapter chatAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        recyclerViewChatList = findViewById(R.id.recycles_view_chat_list);
        buttonBack = findViewById(R.id.btnChatBack);

        chatAdapter = new ChatAdapter(getApplicationContext());
        recyclerViewChatList.setLayoutManager(new LinearLayoutManager(this));

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("Friends").child(currentUser.getUid());
        friendsRef.child("friendList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatAdapter.clearFriendList();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String friendId = dataSnapshot.getValue(String.class);
                    if(Objects.equals(friendId, "empty"))
                    {
                        return;
                    }
                    else {
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users").child(friendId);
                        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User friend = snapshot.getValue(User.class);
                                chatAdapter.addFriendToList(friend);
                                chatAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                recyclerViewChatList.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
