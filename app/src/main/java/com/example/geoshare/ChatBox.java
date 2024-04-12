package com.example.geoshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.UUID;

public class ChatBox extends AppCompatActivity {
    String chatFriendID, chatFriendUsername, senderRoom, receiverRoom;
    TextView textViewChatFriendUsername;
    ImageButton btnMessageBack, buttonSendMessage;
    EditText editTextMessage;
    ChatboxAdapter chatboxAdapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_message);

        chatFriendID = getIntent().getStringExtra("chatFriendID");
        chatFriendUsername = getIntent().getStringExtra("chatFriendUsername");
        if(chatFriendID != null) {
            senderRoom = FirebaseAuth.getInstance().getCurrentUser().getUid() + "-" + chatFriendID;
            receiverRoom = chatFriendID + "-" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        btnMessageBack = findViewById(R.id.btnMessageBack);
        textViewChatFriendUsername = findViewById(R.id.message_friend_username);
        buttonSendMessage = findViewById(R.id.send_message_button);
        recyclerView = findViewById(R.id.recycles_view_chat_box);
        editTextMessage = findViewById(R.id.message_input_text);

        btnMessageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Chat.class);
                startActivity(intent);
            }
        });

        textViewChatFriendUsername.setText(chatFriendUsername);

        chatboxAdapter = new ChatboxAdapter(getApplicationContext());
        recyclerView.setAdapter(chatboxAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference senderRoomRef = FirebaseDatabase.getInstance().getReference("Chats").child(senderRoom);

        senderRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ChatMessage> chatMessageList = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                    chatMessageList.add(message);
                }
                chatboxAdapter.clearMessageList();
                for(ChatMessage chatMessage : chatMessageList) {
                    chatboxAdapter.addNewMessage(chatMessage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = String.valueOf(editTextMessage.getText());
                if(message.trim().length() > 0) {
                    String messageID = UUID.randomUUID().toString();
                    ChatMessage sendMessage = new ChatMessage(messageID,message,
                            FirebaseAuth.getInstance().getCurrentUser().getUid(), chatFriendID);
                    chatboxAdapter.addNewMessage(sendMessage);
                    DataOutput.sendNewMessage(sendMessage, senderRoom, receiverRoom);
                    editTextMessage.setText("");
                }else {
                    Toast.makeText(ChatBox.this, "Message empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.scrollToPosition(chatboxAdapter.getItemCount() - 1);
        editTextMessage.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu_invite_nav, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
