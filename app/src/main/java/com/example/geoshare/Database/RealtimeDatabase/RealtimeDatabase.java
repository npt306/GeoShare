package com.example.geoshare.Database.RealtimeDatabase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.geoshare.Database.Authentication.Authentication;
import com.example.geoshare.Database.FirebaseSingleton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class RealtimeDatabase {
    private static RealtimeDatabase instance = null;
    private static FirebaseDatabase mDatabase;
    private RealtimeDatabase(){
        mDatabase = FirebaseSingleton.getInstance().getFirebaseDatabase();
        Log.d("RealtimeDatabase", "Created");
    }
    public static synchronized RealtimeDatabase getInstance(){
        if(instance == null){
            instance = new RealtimeDatabase();
        }
        return instance;
    }
    public DatabaseReference getMarkLocationReference(){
        return mDatabase.getReference("markLocation");
    }

    public DatabaseReference getBatteryLevelReference(){
        return mDatabase.getReference("batteryLevel");
    }
    public DatabaseReference getReportsReference(){
        return mDatabase.getReference("Reports");
    }
    public DatabaseReference getChatsReference(){
        return mDatabase.getReference("Chats");
    }
    public DatabaseReference getFriendsReference(){
        return mDatabase.getReference("Friends");
    }
    public DatabaseReference getUsersReference(){
        return mDatabase.getReference("Users");
    }
    public DatabaseReference getBannedUsersReference(){
        return mDatabase.getReference("BannedUsers");
    }
    public DatabaseReference getStatusReference(){
        return mDatabase.getReference("status");
    }
    public DatabaseReference getCommunityReference(){
        return mDatabase.getReference("Community");
    }
    public DatabaseReference getUsersCommunityReference(){
        return mDatabase.getReference("UsersCommunity");
    }
    public DatabaseReference getCurrentBatteryLevelReference(){
        return RealtimeDatabase.getInstance()
                .getBatteryLevelReference()
                .child(Authentication.getInstance().getCurrentUserId())
                .child("currentBattery");
    }
    public DatabaseReference getCurrentUserLocationReference(){
        return mDatabase.getReference("Locations")
                .child(Authentication.getInstance().getCurrentUserId());
    }
    public void updateBatteryLevel(String battery){
        RealtimeDatabase.getInstance()
                .getCurrentBatteryLevelReference()
                .setValue(battery)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Battery update", "Dữ liệu pin đã được cập nhật thành công.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Battery update", "Lỗi khi cập nhật dữ liệu pin: " + e.getMessage());
                    }
                });
    }
    public DatabaseReference getCurrentUserStatusReference(){
        return mDatabase.getReference("status")
                .child(Authentication.getInstance().getCurrentUserId());
    }
}
