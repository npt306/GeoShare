package com.example.geoshare.Database.RealtimeDatabase;

import android.util.Log;

import com.example.geoshare.Database.FirebaseSingleton;
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
}
