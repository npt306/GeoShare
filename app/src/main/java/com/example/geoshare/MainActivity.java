package com.example.geoshare;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button buttonLogout, buttonSend;
    TextView textView, textViewResult;
    EditText editTextSend;
    FirebaseUser firebaseUser;
    ImageButton imageButtonProfile, imageButtonInvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser == null){
            Intent intent = new Intent(getApplicationContext(), SignIn.class);
            startActivity(intent);
            finish();
        }

        imageButtonInvite = findViewById(R.id.btnInvite);
        imageButtonProfile =findViewById(R.id.btnProfile);
        imageButtonInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Invite.class);
                startActivity(intent);
                finish();
            }
        });

        imageButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
                finish();
            }
        });
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        mAuth = FirebaseAuth.getInstance();
//        buttonLogout = findViewById(R.id.logout);
//        textView = findViewById(R.id.user_details);
//        buttonSend = findViewById(R.id.send_data);
//        textViewResult = findViewById(R.id.result);
//        editTextSend = findViewById(R.id.data_test);
//
//        firebaseUser = mAuth.getCurrentUser();
//
//        if(firebaseUser == null){
//            Intent intent = new Intent(getApplicationContext(), SignIn.class);
//            startActivity(intent);
//            finish();
//        }
//        else {
//            textView.setText(firebaseUser.getEmail());
//        }
//        buttonLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getApplicationContext(), SignIn.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//        buttonSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String send_data = String.valueOf(editTextSend.getText());
//                int batteryLevel = getCurrentBatteryLevel();
//                // Write a message to the database
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference myRef = database.getReference("User");
//                myRef.child(firebaseUser.getUid()).child("batteryLevel").setValue(batteryLevel)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                // Gửi dữ liệu thành công
//                                Log.d("Firebase", "Data sent successfully!");
//                                textViewResult.setText("successfully");
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                // Gửi dữ liệu thất bại
//                                Log.e("Firebase", "Failed to send data: " + e.getMessage());
//                                // Xử lý nguyên nhân thất bại tại đây
//                                textViewResult.setText(e.getMessage());
//                            }
//                        });
//                myRef.child(firebaseUser.getUid()).child("status").setValue(send_data)
//
////                myRef.setValue(send_data)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                // Gửi dữ liệu thành công
//                                Log.d("Firebase", "Data sent successfully!");
//                                textViewResult.setText("successfully");
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                // Gửi dữ liệu thất bại
//                                Log.e("Firebase", "Failed to send data: " + e.getMessage());
//                                // Xử lý nguyên nhân thất bại tại đây
//                                textViewResult.setText(e.getMessage());
//                            }
//                        });
//            }
//        });
//
//    }

    private int getCurrentBatteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;

        return (int)(batteryPct*100);
    }
}