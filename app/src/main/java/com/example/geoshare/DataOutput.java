package com.example.geoshare;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class DataOutput {
    private static StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    //Update location of current user
    public static void updateNewLoc(double locLat, double locLong) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();
        DatabaseReference myRef = database.getReference("Users").child(id);

        myRef.child("locLat").setValue(Double.toString(locLat));
        myRef.child("locLong").setValue(Double.toString(locLong));
    }

    //Update status of current user
    public static void updateNewStatus(String status) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();
        DatabaseReference myRef = database.getReference("Users").child(id);

        myRef.child("status").setValue(status);
    }
    public static void updateNewImage(Uri newImage) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();
        DatabaseReference myRef = database.getReference("Users").child(id);

        String imageUUID = UUID.randomUUID().toString();
        StorageReference reference = storageReference.child("usersAvatar/" + imageUUID);
        reference.putFile(newImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("AHUHU", "Upload success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("AHUHU", "Upload failed");
            }
        });
//                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                progress.setMax(Math.toIntExact(snapshot.getTotalByteCount()));
//                progress.setProgress(Math.toIntExact(snapshot.getBytesTransferred()));
//            }
//        });

        myRef.child("imageURL").setValue(imageUUID);
    }

    public static void updateNewUsername(String username) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();
        DatabaseReference myRef = database.getReference("Users").child(id);

        myRef.child("username").setValue(username);
    }
    public static void updateNewDOB(String DOB) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();
        DatabaseReference myRef = database.getReference("Users").child(id);

        myRef.child("dob").setValue(DOB);
    }

    public static void inviteNewFriend(String friendId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();
        DatabaseReference friendsRef = database.getReference("Friends").child(id);

        friendsRef.child("inviteSentList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<String> inviteSentArray = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(Objects.equals(snapshot.getValue(String.class), friendId))
                            return;
                        if(!Objects.equals(snapshot.getValue(String.class), "empty"))
                            inviteSentArray.add(snapshot.getValue(String.class));
                    }
                    inviteSentArray.add(friendId);
                    friendsRef.child("inviteSentList").setValue(inviteSentArray);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference friendIDRef = database.getReference("Friends").child(friendId);

        friendIDRef.child("pendingList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<String> pendingArray = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        if(Objects.equals(snapshot.getValue(String.class), id))
//                            return;
                        if(!Objects.equals(snapshot.getValue(String.class), "empty"))
                            pendingArray.add(snapshot.getValue(String.class));
                    }
                    pendingArray.add(id);
                    friendIDRef.child("pendingList").setValue(pendingArray);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void acceptNewFriend(String friendId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();

        DatabaseReference friendsRef = database.getReference("Friends").child(id);
        friendsRef.child("friendList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<String> listFriendArray = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(Objects.equals(snapshot.getValue(String.class), friendId))
                            return;
                        if(!Objects.equals(snapshot.getValue(String.class), "empty"))
                            listFriendArray.add(snapshot.getValue(String.class));
                    }
                    listFriendArray.add(friendId);
                    friendsRef.child("friendList").setValue(listFriendArray);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference friendIDRef = database.getReference("Friends").child(friendId);
        friendIDRef.child("friendList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<String> pendingArray = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(Objects.equals(snapshot.getValue(String.class), id))
                            return;
                        if(!Objects.equals(snapshot.getValue(String.class), "empty"))
                            pendingArray.add(snapshot.getValue(String.class));
                    }
                    pendingArray.add(id);
                    friendIDRef.child("friendList").setValue(pendingArray);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        friendsRef.child("pendingList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<String> pendingArray = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(!Objects.equals(snapshot.getValue(String.class), "empty") &&
                                !Objects.equals(snapshot.getValue(String.class), friendId))
                            pendingArray.add(snapshot.getValue(String.class));
                    }
                    if(pendingArray.isEmpty())
                        pendingArray.add("empty");
                    friendsRef.child("pendingList").setValue(pendingArray);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public static void sendNewMessage(ChatMessage newMessage, String senderRoom, String receiverRoom) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();

        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("Chats");
        chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(senderRoom))
                {
                    DatabaseReference senderRoomRef = chatsRef.child(senderRoom);
                    senderRoomRef.child(newMessage.getMessageID()).setValue(newMessage)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }

                            });
                    senderRoomRef.child(newMessage.getMessageID()).child("timeStamp").setValue(ServerValue.TIMESTAMP);
                }else if(snapshot.hasChild(receiverRoom)) {
                    DatabaseReference reveiverRoomRef = chatsRef.child(receiverRoom);
                    reveiverRoomRef.child(newMessage.getMessageID()).setValue(newMessage)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }

                            });
                    reveiverRoomRef.child(newMessage.getMessageID()).child("timeStamp").setValue(ServerValue.TIMESTAMP);
                }else {
                    DatabaseReference senderRoomRef = chatsRef.child(senderRoom);
                    senderRoomRef.child(newMessage.getMessageID()).setValue(newMessage)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }

                            });
                    senderRoomRef.child(newMessage.getMessageID()).child("timeStamp").setValue(ServerValue.TIMESTAMP);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        DatabaseReference receiverRoomRef = FirebaseDatabase.getInstance().getReference("Chats").child(receiverRoom);
//        receiverRoomRef.child(newMessage.getMessageID()).setValue(newMessage)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//
//                    }
//                });

    }
}