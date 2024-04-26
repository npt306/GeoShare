package com.example.geoshare.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.geoshare.ChatBox;
import com.example.geoshare.CommunityGroup;
import com.example.geoshare.Database.Storage.Storage;
import com.example.geoshare.Model.User;
import com.example.geoshare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder>{
    private Context mContext;
    private List<CommunityGroup> groupList;
    public CommunityAdapter(Context mContext){
        this.mContext = mContext;
        this.groupList = new ArrayList<>();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.community_item, parent, false);
        return new CommunityAdapter.ViewHolder(view);
    }

    public void setGroupList(List<CommunityGroup> groupList) {
        this.groupList = new ArrayList<>(groupList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommunityGroup group = groupList.get(position);
        holder.communityName.setText(group.getGroupName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext.getApplicationContext(), ChatBox.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("chatFriendUsername", friend.getUsername());
//                intent.putExtra("chatFriendID", friend.getId());
//                intent.putExtra("chatFriendImageURL",friend.getImageURL());
//                mContext.getApplicationContext().startActivity(intent);
//                holder.itemView.setBackgroundColor(0xFF00FF00);
                Toast.makeText(mContext, group.getGroupID(), Toast.LENGTH_SHORT).show();
            }
        });

        if(group.getGroupImageURL().equals("default")){
//            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            StorageReference storageRef = Storage.getInstance().getCommunityAvatarReference();
            storageRef.child(group.getGroupImageURL()).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(mContext.getApplicationContext()).load(uri).into(holder.community_image);
                        }
                    });
        }
    }

    public void addGroupToList(CommunityGroup group) {
        this.groupList = new ArrayList<>(this.groupList);
        this.groupList.add(group);
        this.notifyDataSetChanged();
    }
    public void clearGroupList() {
        this.groupList.clear();;
        this.groupList = new ArrayList<>();
        this.notifyDataSetChanged();
    }
    public List<CommunityGroup> getGroupList() {
        return this.groupList;
    }
    @Override
    public int getItemCount() {
        return this.groupList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView communityName;
        public TextView communityMembers;
        public ImageView community_image;
        public ViewHolder(View itemView){
            super(itemView);

            communityName = itemView.findViewById(R.id.community_name);
            communityMembers = itemView.findViewById(R.id.community_member);
            community_image = itemView.findViewById(R.id.community_image);
        }
    }
}
