package com.example.geoshare.Database;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseSingleton {
    private static FirebaseSingleton instance = null;
    private static FirebaseAuth mAuth;
    private static FirebaseFirestore mFirestore;
    private static FirebaseDatabase mDatabase;
    private static FirebaseStorage mStorage;

    private FirebaseSingleton() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        Log.d("FirebaseSingleton", "Created");
    }

    public static synchronized FirebaseSingleton getInstance() {
        if (instance == null) {
            instance = new FirebaseSingleton();
        }
        return instance;
    }

    public FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    public FirebaseFirestore getFirebaseFirestore() {
        return mFirestore;
    }
    public FirebaseDatabase getFirebaseDatabase(){
        return mDatabase;
    }
    public static FirebaseStorage getFirebaseStorage() {return mStorage;}
}