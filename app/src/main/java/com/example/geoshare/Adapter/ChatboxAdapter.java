package com.example.geoshare.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geoshare.ChatMessage;
import com.example.geoshare.Message;
import com.example.geoshare.Model.User;
import com.example.geoshare.R;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatboxAdapter extends RecyclerView.Adapter<ChatboxAdapter.ViewHolder> {
    private static final int VIEW_TYPE_SEND = 1;
    private static final int VIEW_TYPE_RECEIVE = 2;
    private Context mContext;
    private List<ChatMessage> messageList;
    public ChatboxAdapter(Context mContext, List<ChatMessage> messageList){
        this.mContext = mContext;
        this.messageList = messageList;
    }
    @NonNull
    @Override
    public ChatboxAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        if(viewType == VIEW_TYPE_SEND) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item, parent, false);
            return new ChatboxAdapter.ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item, parent, false);
            return new ChatboxAdapter.ViewHolder(view);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        if(Objects.equals(message.getSender(), FirebaseAuth.getInstance().getCurrentUser().getUid()))
        {
            holder.textViewSendMessage.setText(message.getMessage());
        }else {
            holder.textViewReceiveMessage.setText(message.getMessage());
        }
    }
    @Override
    public int getItemViewType(int position) {
        if(messageList.get(position).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_SEND;
        }else {
            return VIEW_TYPE_RECEIVE;
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewSendMessage, textViewReceiveMessage;
        public ImageView profile_image;
        public ViewHolder(View itemView){
            super(itemView);

            textViewSendMessage = itemView.findViewById(R.id.textViewSendMessage);
            textViewReceiveMessage = itemView.findViewById(R.id.textViewReceiveMessage);
        }
    }
}
