package com.example.geoshare.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.geoshare.DataOutput;
import com.example.geoshare.Model.User;
import com.example.geoshare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mRequest;
    public RequestAdapter(Context mContext){
        this.mContext = mContext;
        this.mRequest = new ArrayList<>();
    }

    @NonNull
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.request_item, parent, false);
        return new RequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, int position) {
        User friend = mRequest.get(position);
        holder.username.setText(friend.getUsername());
        holder.pendingFriendID.setText(friend.getId());
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
        // Xác định sự kiện click cho button
//        holder.inviteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Xử lý sự kiện khi button được nhấn
////                Toast.makeText(mContext, "Invite button clicked for user: " + user.getUsername(), Toast.LENGTH_SHORT).show();
//                // Gửi yêu cầu kết bạn đến ID tương ứng
////                acceptFriendRequest(user.getId());
//                DataOutput.acceptNewFriend(String.valueOf(holder.pendingFriendID.getText()));
//
//            }
//        });
    }
    // Phương thức để gửi yêu cầu kết bạn
    private void acceptFriendRequest(String invitedId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Đường dẫn tới node "invites" trong Firebase Realtime Database
        DatabaseReference invitedRef = FirebaseDatabase.getInstance().getReference().child("Users").child(invitedId).child("requests");
        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("invites");

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
    public void addFriendToList(User friend) {
        this.mRequest = new ArrayList<>(this.mRequest);
        this.mRequest.add(friend);
        this.notifyDataSetChanged();
    }
    public void clearRequestList() {
        this.mRequest.clear();;
        this.mRequest = new ArrayList<>();
        this.notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return mRequest.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username, pendingFriendID;
        public ImageView profile_image;
        public ImageView buttonAccept, buttonReject;
        public ViewHolder(View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.request_friend_username);
            pendingFriendID = itemView.findViewById(R.id.request_friend_ID);
            profile_image = itemView.findViewById(R.id.request_profile_image);
            buttonAccept = itemView.findViewById(R.id.button_request_accept);
            buttonReject = itemView.findViewById(R.id.button_request_reject);

        }
    }
}
