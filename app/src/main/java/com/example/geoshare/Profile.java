package com.example.geoshare;

import android.app.DatePickerDialog;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
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

public class Profile extends AppCompatActivity {
    Uri image;
    ImageButton buttonSelectImage, buttonUser_name, buttonUser_dob, buttonProfileBack;
    ImageView imageViewUser;
    TextView txtId, txtUsername, txtEmail, txtDob;
    EditText editTextUser_name, editTextUser_dob;
    Button buttonUpdate, buttonCancelUpdate, buttonLogout;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    image = result.getData().getData();
                    imageViewUser = findViewById(R.id.profile_image);
                    Glide.with(getApplicationContext()).load(image).into(imageViewUser);
                    Toast.makeText(Profile.this, "null con me no roi dm", Toast.LENGTH_SHORT).show();
                }
            }
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        buttonSelectImage = findViewById(R.id.btnUploadImage);
        buttonUser_name = findViewById(R.id.btnUser_name);
        buttonUser_dob = findViewById(R.id.btnUser_dob);
        buttonProfileBack = findViewById(R.id.back_button);
        buttonUpdate = findViewById(R.id.btnUpdate);
        buttonCancelUpdate = findViewById(R.id.btnCancelUpdate);
        buttonLogout = findViewById(R.id.btnLogout);

        editTextUser_name = findViewById(R.id.editTextUser_name);
        editTextUser_dob = findViewById(R.id.editTextUser_dob);

        txtId = findViewById(R.id.user_id);
        txtEmail = findViewById(R.id.user_email);
        txtUsername = findViewById(R.id.user_name);
        txtDob = findViewById(R.id.user_dob);

        editTextUser_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        Profile.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                                editTextUser_dob.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);

                buttonUpdate.setVisibility(View.VISIBLE);
                buttonCancelUpdate.setVisibility(View.VISIBLE);
                buttonLogout.setVisibility(View.GONE);
            }
        });
        buttonUser_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextUser_name.setVisibility(View.VISIBLE);
                buttonUpdate.setVisibility(View.VISIBLE);
                buttonCancelUpdate.setVisibility(View.VISIBLE);
                buttonLogout.setVisibility(View.GONE);
            }
        });
        buttonUser_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextUser_dob.setVisibility(View.VISIBLE);
                buttonUpdate.setVisibility(View.VISIBLE);
                buttonCancelUpdate.setVisibility(View.VISIBLE);
                buttonLogout.setVisibility(View.GONE);
            }
        });
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle update profile
                if (image != null) {
                    DataOutput.updateNewImage(image);
                }
                String newUserName = String.valueOf(editTextUser_name.getText());
                if(!newUserName.isEmpty()) {
                    DataOutput.updateNewUsername(newUserName);
                }

                String newDob = String.valueOf(editTextUser_dob.getText());
                if(!newDob.isEmpty()) {
                    DataOutput.updateNewDOB(newDob);
                }

                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
                finish();
            }
        });
        buttonCancelUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                editTextUser_name.setVisibility(View.GONE);
//                editTextUser_dob.setVisibility(View.GONE);
//                buttonUpdate.setVisibility(View.GONE);
//                buttonCancelUpdate.setVisibility(View.GONE);
//                buttonLogout.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
                finish();
            }
        });
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
