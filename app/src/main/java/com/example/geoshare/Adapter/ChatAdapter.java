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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context mContext;
    private List<User> friendList;
    public ChatAdapter(Context mContext, List<User> mUsers){
        this.mContext = mContext;
        this.friendList = mUsers;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.invite_item, parent, false);
        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = friendList.get(position);
        holder.username.setText(user.getUsername());
        if(user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }
        // Xác định sự kiện click cho button
        holder.inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện khi button được nhấn
//                Toast.makeText(mContext, "Invite button clicked for user: " + user.getUsername(), Toast.LENGTH_SHORT).show();
                // Gửi yêu cầu kết bạn đến ID tương ứng
//                sendFriendRequest(user.getId());
            }
        });
    }
    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;
        public Button inviteButton;
        public ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.invite_username);
            profile_image = itemView.findViewById(R.id.invite_profile_image);
            inviteButton = itemView.findViewById(R.id.invite_button);
        }
    }
}
