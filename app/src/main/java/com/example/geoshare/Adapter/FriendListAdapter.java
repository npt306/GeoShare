package com.example.geoshare.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.geoshare.Model.User;
import com.example.geoshare.R;

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
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            Glide.with(mContext).load(friend.getImageURL()).into(holder.profile_image);
        }
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
        public ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.invite_friend_username);
            profile_image = itemView.findViewById(R.id.invite_friend_profile_image);
        }
    }
}
