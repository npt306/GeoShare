package com.example.geoshare.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.example.geoshare.FriendProfile;
import com.example.geoshare.Invite;
import com.example.geoshare.MainActivity;
import com.example.geoshare.Model.User;
import com.example.geoshare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;


public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mFriends;
    public FriendListAdapter(Context mContext, List<User> mFriends){
        this.mContext = mContext;
        this.mFriends = mFriends;
    }

    @NonNull
    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.invite_friend_item, parent, false);
        return new FriendListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListAdapter.ViewHolder holder, int position) {
        User friend = mFriends.get(position);
        holder.username.setText(friend.getUsername());
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
        holder.friend_viewProfile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext.getApplicationContext(), FriendProfile.class);
                intent.putExtra("friendID", friend.getId());
                mContext.startActivity(intent);
            }
        });
        holder.delete_friend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Delete btn pressed: " + holder.username.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        // Xác định sự kiện click cho button
//        holder.inviteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Xử lý sự kiện khi button được nhấn
////                Toast.makeText(mContext, "Invite button clicked for user: " + user.getUsername(), Toast.LENGTH_SHORT).show();
//                // Gửi yêu cầu kết bạn đến ID tương ứng
//                sendFriendRequest(user.getId());
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;
        public Button friend_viewProfile_btn, delete_friend_btn;
        public ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.invite_friend_username);
            profile_image = itemView.findViewById(R.id.friend_profile_image);
            friend_viewProfile_btn = itemView.findViewById(R.id.friend_viewProfile_button);
            delete_friend_btn = itemView.findViewById(R.id.friend_unfriend_button);
        }
    }
}
