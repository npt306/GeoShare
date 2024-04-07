package com.example.geoshare.Database.Authentication;

import com.example.geoshare.Database.FirebaseSingleton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Authentication {
    private static FirebaseAuth firebaseAuth;
    private static FirebaseUser currentUser;
    private static String currentUserId;
    private static Authentication instance = null;
    private Authentication(){
        firebaseAuth = FirebaseSingleton.getInstance().getFirebaseAuth();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
    }
    public static synchronized  Authentication getInstance(){
        if(instance == null){
            instance = new Authentication();
        }
        return instance;
    }
    public FirebaseAuth getFirebaseAuth(){
        return firebaseAuth;
    }
    public FirebaseUser getCurrentUser(){
        return currentUser;
    }
    public String getCurrentUserId(){
        return currentUserId;
    }

}
