package com.example.geoshare;

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

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    EditText editTextEmail, editTextPassword, editTextConfirmPassword, editTextUsername;
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

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password, confirmPassword, username;
                email = String.valueOf(editTextEmail.getText());
                username = String.valueOf(editTextUsername.getText());
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
                                    DatabaseReference myRef = database.getReference("User").child(id);

                                    Map<String,String> userData = new HashMap<>();
                                    userData.put("username", username);
                                    userData.put("imageURL", "default");
                                    myRef.setValue(userData);


                                    Intent intent = new Intent(getApplicationContext(), SignIn.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                    show_notification("Account created");
                                } else {
                                    // If sign in fails, display a message to the user.
                                    show_notification("Authentication failed");
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
