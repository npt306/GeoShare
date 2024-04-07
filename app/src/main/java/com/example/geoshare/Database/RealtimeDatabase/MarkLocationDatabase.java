package com.example.geoshare.Database.RealtimeDatabase;

import android.util.Log;

import com.example.geoshare.Database.Authentication.Authentication;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;

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
    public void addNewMarkerLocation(LatLng latLng, String typeMarker){
        referenceToCurrentUser.child(typeMarker).push().setValue(latLng);
    }
}
