package com.example.geoshare.Database.Storage;

import android.util.Log;

import com.example.geoshare.Database.FirebaseSingleton;
import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Storage {
    private static Storage instance = null;
    private static FirebaseStorage mStorage;
    private Storage(){
        mStorage = FirebaseSingleton.getInstance().getFirebaseStorage();
        Log.d("FirebaseStorage", "Created");
    }
    public static synchronized Storage getInstance(){
        if(instance == null){
            instance = new Storage();
        }
        return instance;
    }
    public StorageReference getUsersAvatarReference(){
        return mStorage.getReference("usersAvatar");
    }
}
