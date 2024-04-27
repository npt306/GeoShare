package com.example.geoshare.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.geoshare.ChatBox;
import com.example.geoshare.CommunityGroup;
import com.example.geoshare.DataOutput;
import com.example.geoshare.Database.Authentication.Authentication;
import com.example.geoshare.Database.FirebaseSingleton;
import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.example.geoshare.Database.Storage.Storage;
import com.example.geoshare.FriendProfile;
import com.example.geoshare.Model.User;
import com.example.geoshare.R;
import com.example.geoshare.Report;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder>{
    private Context mContext;
    private Context dialogContext;
    private List<CommunityGroup> groupList;
    public CommunityAdapter(Context mContext, Context dialogContext){
        this.mContext = mContext;
        this.dialogContext = dialogContext;
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
                showCommunityDialog(group);
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

    private void showCommunityDialog(CommunityGroup group) {
        AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
        View dialogView = LayoutInflater.from(dialogContext).inflate(R.layout.dialog_community_profile, null);
        builder.setView(dialogView);

        ImageView community_image = dialogView.findViewById(R.id.community_image);
        if(group.getGroupImageURL().equals("default")){
//            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            StorageReference storageRef = Storage.getInstance().getCommunityAvatarReference();
            storageRef.child(group.getGroupImageURL()).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(mContext.getApplicationContext()).load(uri).into(community_image);
                        }
                    });
        }

        TextView textViewCommunityName = dialogView.findViewById(R.id.textViewCommunityName);
        TextView textViewCommunityDescription = dialogView.findViewById(R.id.textViewCommunityDescription);
        textViewCommunityName.setText(group.getGroupName());
        textViewCommunityDescription.setText(group.getGroupDescription());

        Button btnJoin = dialogView.findViewById(R.id.btnJoinCommunity);
        Button btnReport = dialogView.findViewById(R.id.btnReportCommunity);
        Button btnLeave = dialogView.findViewById(R.id.btnLeaveCommunity);

        DatabaseReference usersCommunityRef = RealtimeDatabase.getInstance().getUsersCommunityReference();
        usersCommunityRef.child(Authentication.getInstance().getCurrentUserId()).child("CommunityList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isInComm = false;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(Objects.equals(dataSnapshot.getValue(String.class), group.getGroupID())) {
                        isInComm = true;
                        break;
                    }
                }

                if(isInComm) {
                    btnJoin.setVisibility(View.GONE);
                    btnLeave.setVisibility(View.VISIBLE);
                }else {
                    btnJoin.setVisibility(View.VISIBLE);
                    btnLeave.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataOutput.joinCommunity(group.getGroupID());
            }
        });
        btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataOutput.leaveCommunity(group.getGroupID());
            }
        });
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
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
