package com.example.geoshare;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geoshare.Adapter.ChatboxAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatBox extends AppCompatActivity {
    String chatFriendID, chatFriendUsername, senderRoom, reveiverRoom;
    TextView textViewChatFriendUsername;
    ImageButton buttonSendMessage;
    EditText editTextMessage;
    ChatboxAdapter chatboxAdapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_message);

        chatFriendID = getIntent().getStringExtra("chatFriendID");
        chatFriendUsername = getIntent().getStringExtra("chatFriendUsername");

        textViewChatFriendUsername = findViewById(R.id.message_friend_username);
        buttonSendMessage = findViewById(R.id.send_message_button);
        recyclerView = findViewById(R.id.recycles_view_chat_box);
        editTextMessage = findViewById(R.id.message_input_text);

        textViewChatFriendUsername.setText(chatFriendUsername);
        if(chatFriendID != null) {
            senderRoom = FirebaseAuth.getInstance().getCurrentUser().getUid() + "-" + chatFriendID;
            reveiverRoom = chatFriendID + "-" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        }


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference senderRoomRef = FirebaseDatabase.getInstance().getReference("Chats").child(senderRoom);
        DatabaseReference receriverRoomRef = FirebaseDatabase.getInstance().getReference("Chats").child(reveiverRoom);

        senderRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ChatMessage> chatMessageList = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                    chatMessageList.add(message);
                }
                chatboxAdapter = new ChatboxAdapter(getApplicationContext(),chatMessageList);
                recyclerView.setAdapter(chatboxAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
