package com.example.geoshare.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geoshare.ChatBox;
import com.example.geoshare.Model.User;
import com.example.geoshare.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context mContext;
    private List<User> friendList;
    public ChatAdapter(Context mContext){
        this.mContext = mContext;
        this.friendList = new ArrayList<>();
    }
    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item, parent, false);
        return new ChatAdapter.ViewHolder(view);
    }

    public void setFriendList(List<User> friendList) {
        this.friendList = new ArrayList<>(friendList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = friendList.get(position);
        holder.username.setText(user.getUsername());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext.getApplicationContext(), ChatBox.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("chatFriendUsername", user.getUsername());
                intent.putExtra("chatFriendID", user.getId());
                mContext.getApplicationContext().startActivity(intent);
                holder.itemView.setBackgroundColor(0xFF00FF00);
            }
        });
//        if(user.getImageURL().equals("default")){
//            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
//        }
//        else {
//            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
//        }
    }
    public void addFriendToList(User friend) {
        this.friendList = new ArrayList<>(this.friendList);
        this.friendList.add(friend);
        this.notifyDataSetChanged();
    }
    public void clearFriendList() {
        this.friendList.clear();;
        this.friendList = new ArrayList<>();
        this.notifyDataSetChanged();
    }
    public void clear() {
        if(!this.friendList.isEmpty() && this.friendList != null)
            this.friendList.clear();
    }
    public List<User> getFriendList() {
        return this.friendList;
    }
    @Override
    public int getItemCount() {
        return this.friendList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;
        public ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.chat_username);
            profile_image = itemView.findViewById(R.id.chat_profile_image);
        }
    }
}
