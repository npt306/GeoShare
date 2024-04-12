package com.example.geoshare.Database.RealtimeDatabase;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.geoshare.Database.Authentication.Authentication;
import com.example.geoshare.MainActivity;
import com.example.geoshare.MarkLocation.MarkLocationList;
import com.example.geoshare.MarkLocation.MarkerLocationModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class MarkLocationDatabase {
    private static  MarkLocationDatabase instance = null;
    private static DatabaseReference reference;
    private static DatabaseReference referenceToCurrentUser;
    public MarkLocationDatabase(){
        reference = RealtimeDatabase.getInstance().getMarkLocationReference();
        referenceToCurrentUser = reference.child(Authentication.getInstance().getCurrentUserId());
        Log.d("MarkLocationDatabase", "Created");
    }
    public static synchronized MarkLocationDatabase getInstance(){
        if(instance == null){
            instance = new MarkLocationDatabase();
        }
        return instance;
    }
    public DatabaseReference getReference(){
        return reference;
    }
    public DatabaseReference getReferenceToCurrentUser(){
        return referenceToCurrentUser;
    }
    public void addNewMarkerLocation(MarkerLocationModel marker){
        referenceToCurrentUser.push().setValue(marker);
    }
    public void deleteMarkerLocation(MarkerLocationModel marker, Context context) {
        referenceToCurrentUser.child(marker.getKey()).removeValue((error, ref) -> {
            if (error == null) {
                // Xóa thành công
                Log.d("DeleteMarker", "Marker deleted successfully");
                Toast.makeText(context, "Marker deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Xảy ra lỗi khi xóa
                Log.e("DeleteMarker", "Failed to delete marker: " + error.getMessage());
                Toast.makeText(context, "Failed to delete marker", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
