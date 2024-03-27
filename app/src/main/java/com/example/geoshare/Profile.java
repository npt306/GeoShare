package com.example.geoshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {
    ImageButton buttonProfileBack;
    TextView txtId, txtUsername, txtEmail;
    Button buttonLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        buttonProfileBack = findViewById(R.id.back_button);
        buttonLogout = findViewById(R.id.btnLogout);
        txtId = findViewById(R.id.user_id);
        txtEmail = findViewById(R.id.user_email);
        txtUsername = findViewById(R.id.user_name);
        buttonProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
                finish();
            }
        });

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    if (currentUser != null) {
            String userId = currentUser.getUid();
            String email = currentUser.getEmail();
            txtId.setText(userId);
            txtEmail.setText(email);
            // Sử dụng userId và email ở đây
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    // Sử dụng username ở đây
                    txtUsername.setText(username);
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
