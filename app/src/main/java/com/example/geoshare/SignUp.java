package com.example.geoshare;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    EditText editTextEmail,editTextSignUpDob, editTextPassword, editTextConfirmPassword, editTextUsername;
    Button buttonSignUp, buttonBack;
    private FirebaseAuth mAuth;
    LinearLayout layout;
    DatabaseReference databaseReference;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextSignUpEmail);
        editTextSignUpDob = findViewById(R.id.editTextSignUpDob);
        editTextPassword = findViewById(R.id.editTextSignUpPassword);
        editTextConfirmPassword = findViewById(R.id.editTextSignUpConfirmPassword);
        editTextUsername = findViewById(R.id.editTextSignUpUsername);
        buttonSignUp = findViewById(R.id.btnRegister);
        layout = findViewById(R.id.signUpLayout);

        layout.setOnTouchListener((v, event) -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return false;
        });

        editTextSignUpDob.setOnClickListener(new View.OnClickListener() {
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
                        SignUp.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                                editTextSignUpDob.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password, confirmPassword, username, dob;
                email = String.valueOf(editTextEmail.getText());
                username = String.valueOf(editTextUsername.getText());
                dob = String.valueOf(editTextSignUpDob.getText());
                password = String.valueOf(editTextPassword.getText());
                confirmPassword = String.valueOf(editTextConfirmPassword.getText());
                if(TextUtils.isEmpty(email)){
                    show_notification("Enter email");
                    return;
                }
                if(TextUtils.isEmpty(username)){
                    show_notification("Enter username");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    show_notification("Enter password");
                    return;
                }
                if(TextUtils.isEmpty(confirmPassword)){
                    show_notification("Enter confirm password");
                    return;
                }
                if(!password.equals(confirmPassword)){
                    show_notification("Incorrect password");
                    editTextPassword.setText("");
                    editTextConfirmPassword.setText("");
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String id = user.getUid();

                                    DatabaseReference usersRef = database.getReference("Users").child(id);
                                    Map<String,String> userData = new HashMap<>();
                                    userData.put("id", id);
                                    userData.put("username", username);
                                    userData.put("imageURL", "default");
                                    userData.put("status","");
                                    userData.put("dob", dob);
                                    usersRef.setValue(userData);

                                    DatabaseReference friendsRef = database.getReference("Friends").child(id);
                                    Map<String,Object> userFriends = new HashMap<>();
                                    userFriends.put("friendList", Arrays.asList(new String[]{"empty"}));
                                    userFriends.put("pendingList", Arrays.asList(new String[]{"empty"}));
                                    userFriends.put("inviteSentList", Arrays.asList(new String[]{"empty"}));
                                    friendsRef.setValue(userFriends);

//                                    ArrayList<String> defaultFriendList = new ArrayList<>(Arrays.asList(new String[]{"i0dlD9bphthZkMWOuz6z5yQ28oq1"}));
//                                    ArrayList<String> defaultFriendList = new ArrayList<>(Arrays.asList(new String[]{"empty"}));
//                                    User newUser = new User(id,username,"","default", dob, defaultFriendList,"100.123","100.123");
//                                    myRef.setValue(newUser);


                                    Intent intent = new Intent(getApplicationContext(), SignIn.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                    show_notification("Account created");
                                } else {
                                    // If sign in fails, display a message to the user.
                                    show_notification("Authentication failed. Password was too short.");
                                }
                            }
                        });
            }
        });

        buttonBack = findViewById(R.id.btnBackToSignIn);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
                finish();
            }
        });
    }
    void show_notification(String message){
        Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
    }
}
