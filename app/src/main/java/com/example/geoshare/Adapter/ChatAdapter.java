package com.example.geoshare.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.geoshare.ChatBox;
import com.example.geoshare.Model.User;
import com.example.geoshare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        User friend = friendList.get(position);
        holder.username.setText(friend.getUsername());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext.getApplicationContext(), ChatBox.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("chatFriendUsername", friend.getUsername());
                intent.putExtra("chatFriendID", friend.getId());
                intent.putExtra("chatFriendImageURL",friend.getImageURL());
                mContext.getApplicationContext().startActivity(intent);
                holder.itemView.setBackgroundColor(0xFF00FF00);
            }
        });
        if(friend.getImageURL().equals("default")){
//            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            storageRef.child("usersAvatar/" + friend.getImageURL()).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(mContext.getApplicationContext()).load(uri).into(holder.profile_image);
                        }
                    });
        }
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
