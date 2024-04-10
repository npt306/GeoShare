package com.example.geoshare;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfile extends AppCompatActivity {
    Uri image;
    ImageButton buttonProfileBack;
    CircleImageView imageViewUser;
    TextView txtId, txtUsername, txtDob;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        buttonProfileBack = findViewById(R.id.back_button);

        txtId = findViewById(R.id.user_id);
        txtUsername = findViewById(R.id.user_name);
        txtDob = findViewById(R.id.user_dob);
        buttonProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Invite.class);
                startActivity(intent);
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            Intent intent = getIntent();
            String friendID = intent.getExtras().getString("friendID");
            txtId.setText(friendID);

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(friendID);
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String imageURL = dataSnapshot.child("imageURL").getValue(String.class);
                        if(!Objects.equals(imageURL, "default")) {
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                            storageRef.child("usersAvatar/" + imageURL).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            imageViewUser = findViewById(R.id.profile_image);
                                            Glide.with(getApplicationContext()).load(uri).into(imageViewUser);
                                        }
                                    });
                        }
                        String username = dataSnapshot.child("username").getValue(String.class);
                        txtUsername.setText(username);
                        String dob = dataSnapshot.child("dob").getValue(String.class);
                        txtDob.setText(dob);
                    } else {
                        // Không tìm thấy thông tin người dùng, xử lý tương ứng
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý lỗi nếu cần
                }
            });
        }
        else {
            // Người dùng chưa đăng nhập, xử lý tương ứng
        }
    }
}
