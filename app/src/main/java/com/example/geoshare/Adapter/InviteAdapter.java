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
import com.example.geoshare.DataOutput;
import com.example.geoshare.Database.Authentication.Authentication;
import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.example.geoshare.Model.User;
import com.example.geoshare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUsers;
    public InviteAdapter(Context mContext, List<User> mUsers){
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.invite_item, parent, false);
        return new InviteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        holder.pendingFriendID.setText(user.getId());
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
//                acceptFriendRequest(user.getId());
                DataOutput.acceptNewFriend(String.valueOf(holder.pendingFriendID.getText()));

            }
        });
    }
    // Phương thức để gửi yêu cầu kết bạn
    private void acceptFriendRequest(String invitedId) {
        String currentUserId = Authentication.getInstance().getCurrentUserId();
        // Đường dẫn tới node "invites" trong Firebase Realtime Database
        DatabaseReference invitedRef = RealtimeDatabase.getInstance().getUsersReference().child(invitedId).child("requests");
        DatabaseReference currentUserRef = RealtimeDatabase.getInstance().getUsersReference().child(currentUserId).child("invites");

        invitedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exists = false;
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String id = childSnapshot.getValue(String.class);
                    if (id.equals(currentUserId)) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    // Thêm invitedId vào danh sách
                    currentUserRef.push().setValue(currentUserId);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exists = false;
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String id = childSnapshot.getValue(String.class);
                    if (id.equals(invitedId)) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    // Thêm invitedId vào danh sách
                    currentUserRef.push().setValue(invitedId);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username, pendingFriendID;
        public ImageView profile_image;
        public Button inviteButton;
        public ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.invite_friend_username);
            pendingFriendID = itemView.findViewById(R.id.invite_friend_ID);
            profile_image = itemView.findViewById(R.id.invite_profile_image);
            inviteButton = itemView.findViewById(R.id.invite_button);
        }
    }
}
