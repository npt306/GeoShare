package com.example.geoshare;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geoshare.Adapter.ChatboxAdapter;

public class ChatBox extends AppCompatActivity {
    ImageButton buttonSendMessage;
    EditText editTextMessage;
    ChatboxAdapter messageAdapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_message);


    }
}
