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
import com.example.geoshare.Database.Authentication.Authentication;
import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.example.geoshare.Database.Storage.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateCommunity extends AppCompatActivity {
    Uri image;
    ImageButton buttonSelectImage, buttonCommunity_name, buttonCreateCommunityBack;
    CircleImageView imageViewUser;
    TextView txtCommunityname;
    EditText editTextCommunity_description;
    String newCommunityName = "";
    Button buttonCreate;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    image = result.getData().getData();
                    imageViewUser = findViewById(R.id.community_image);
                    Glide.with(getApplicationContext()).load(image).into(imageViewUser);
                    DataOutput.updateNewImage(image);
                }
                else {
                    Toast.makeText(CreateCommunity.this, "null con me no roi dm", Toast.LENGTH_SHORT).show();
                }
            }
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_community);

        buttonSelectImage = findViewById(R.id.btnUploadImage);
        buttonCommunity_name = findViewById(R.id.btnCommunity_name);
        buttonCreateCommunityBack = findViewById(R.id.back_button);
        buttonCreate = findViewById(R.id.btnCreate);

        txtCommunityname = findViewById(R.id.community_name);
        editTextCommunity_description = findViewById(R.id.editTextCommunityDescription);
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });
        buttonCommunity_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommunityNameDialog();
            }
        });
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle update profile
//                if (image != null) {
//                    DataOutput.updateNewImage(image);
//                }
//                String newUserName = String.valueOf(editTextUser_name.getText());
                String newGroupDescription = String.valueOf(editTextCommunity_description.getText());
                if(!newCommunityName.isEmpty() && !newGroupDescription.isEmpty() && image != null) {
                    CommunityGroup newGroup = new CommunityGroup(newCommunityName,newGroupDescription);
                    DataOutput.createNewCommunity(newGroup, image);
                }

                Intent intent = new Intent(getApplicationContext(), Community.class);
                startActivity(intent);
                finish();
            }
        });
        buttonCreateCommunityBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showCommunityNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_community_community_name, null);
        builder.setView(dialogView);

        final EditText etNewUsername = dialogView.findViewById(R.id.etCommunityName);
        Button btnUpdateCommunnityname = dialogView.findViewById(R.id.btnUpdateCommunityName);
        Button btnCancelUpdateCommunnityname = dialogView.findViewById(R.id.btnCancelCommunityName);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnUpdateCommunnityname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enterUsername = etNewUsername.getText().toString().trim();
                if (!enterUsername.isEmpty()) {
                    // Cập nhật username ở đây
//                    updateUsername(newUsername);
//                    if(!newUsername.isEmpty()) {
//                        txtUsername.setText(newUsername);
//                        DataOutput.updateNewUsername(newUsername);
//                    }
                    txtCommunityname.setText(enterUsername);
                    newCommunityName = enterUsername;
                    dialog.dismiss();
                } else {
                    etNewUsername.setError("Vui lòng nhập username");
                }
            }
        });
        btnCancelUpdateCommunnityname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng dialog mà không cập nhật username
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
