package com.example.geoshare.Database.FirestoreDatabase;

import com.example.geoshare.Database.Authentication.Authentication;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreDatabase {
    private static FirestoreDatabase instance = null;
    FirebaseFirestore firestore;
    private FirestoreDatabase(){
        firestore = FirebaseFirestore.getInstance();
    }
    public static synchronized FirestoreDatabase getInstance(){
        if(instance == null){
            instance = new FirestoreDatabase();
        }
        return instance;
    }

}
