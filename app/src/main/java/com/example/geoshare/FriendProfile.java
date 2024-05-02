package com.example.geoshare;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.example.geoshare.Database.Authentication.Authentication;
import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.example.geoshare.Database.Storage.Storage;
import com.example.geoshare.Fragment.InviteFragment;
import com.example.geoshare.Model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Repo;
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
    Button btnReport, btnAddFriend, btnUnfriend;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        buttonProfileBack = findViewById(R.id.back_button);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        btnUnfriend = findViewById(R.id.btnUnfriend);

        txtId = findViewById(R.id.user_id);
        txtUsername = findViewById(R.id.user_name);
        txtDob = findViewById(R.id.user_dob);
        btnReport = findViewById(R.id.btnReport);
        buttonProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            Intent intent = getIntent();
            String friendID = intent.getExtras().getString("friendID");
            txtId.setText(friendID);

            DatabaseReference usersRef = RealtimeDatabase.getInstance().getUsersReference().child(friendID);
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String imageURL = dataSnapshot.child("imageURL").getValue(String.class);
                        if(!Objects.equals(imageURL, "default")) {
                            StorageReference storageRef = Storage.getInstance().getUsersAvatarReference();
                            storageRef.child(imageURL).getDownloadUrl()
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

            FirebaseUser currentUser = Authentication.getInstance().getCurrentUser();
            DatabaseReference reference = RealtimeDatabase.getInstance().getFriendsReference().child(currentUser.getUid());
            reference.child("friendList").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean checkFriend = false;
                    for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                        String friend = friendSnapshot.getValue(String.class);
                        if(!friend.equals("empty")) {
                            if(friend.equals(friendID)){
                                checkFriend = true;
                                break;
                            }
                        }
                    }
                    if(checkFriend){
                        btnAddFriend.setVisibility(View.GONE);
                    }
                    else {
                        btnUnfriend.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý lỗi nếu có
                }
            });

            btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataOutput.inviteNewFriend(friendID);
                }
            });

            btnUnfriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataOutput.deleteFriend(friendID);
                }
            });

        }
        else {
            // Người dùng chưa đăng nhập, xử lý tương ứng
        }
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReportDialog();
            }
        });
    }
    private void showReportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_report, null);
        builder.setView(dialogView);
        Report report = new Report(Authentication.getInstance().getCurrentUserId(), String.valueOf(txtId.getText()));

        final EditText editTextProblemDescription = dialogView.findViewById(R.id.editTextProblemDescription);

        CheckBox checkBoxHarassment = dialogView.findViewById(R.id.checkbox_harassment);
        checkBoxHarassment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    report.addProblems("Harrassment");
                }else {
                    report.removeProblem("Harrassment");
                }
            }

        });
        CheckBox checkBoxPretend = dialogView.findViewById(R.id.checkbox_pretend);
        checkBoxPretend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    report.addProblems("Pretending to be something");
                }else {
                    report.removeProblem("Pretending to be something");
                }
            }

        });
        CheckBox checkBoxHate = dialogView.findViewById(R.id.checkbox_hate);
        checkBoxHate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    report.addProblems("Hate speech");
                }else {
                    report.removeProblem("Hate speech");
                }
            }

        });
        CheckBox checkBoxNudity = dialogView.findViewById(R.id.checkbox_nudity);
        checkBoxNudity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    report.addProblems("Nudity or sexual content");
                }else {
                    report.removeProblem("Nudity or sexual content");
                }
            }

        });
        CheckBox checkBoxViolence = dialogView.findViewById(R.id.checkbox_violence);
        checkBoxViolence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    report.addProblems("Violence");
                }else {
                    report.removeProblem("Violence");
                }
            }

        });
        CheckBox checkBoxScam = dialogView.findViewById(R.id.checkbox_scam);
        checkBoxScam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    report.addProblems("Fraud or scam");
                }else {
                    report.removeProblem("Fraud or scam");
                }
            }

        });
        CheckBox checkBoxOther = dialogView.findViewById(R.id.checkbox_other);
        checkBoxOther.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    report.addProblems("Other");
                }else {
                    report.removeProblem("Other");
                }
            }

        });

        Button btnReport = dialogView.findViewById(R.id.btnReport);
        Button btnCancelReport = dialogView.findViewById(R.id.btnCancelReport);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String problemDescription = editTextProblemDescription.getText().toString().trim();
                if (!problemDescription.isEmpty() && !report.getReportProblems().isEmpty()) {
                    // Cập nhật username ở đây
//                    updateUsername(newUsername);
//                    if(!newUsername.isEmpty()) {
//                        txtUsername.setText(newUsername);
//                        DataOutput.updateNewUsername(newUsername);
//                    }
                    report.setReportDescription(problemDescription);
                    DataOutput.reportUser(report);
                    dialog.dismiss();
                    Toast.makeText(FriendProfile.this, "Your report will be consider, sorry for your inconvenience.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FriendProfile.this, "Problems and description cannot be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancelReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng dialog mà không cập nhật username
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
