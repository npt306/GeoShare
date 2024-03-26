package com.example.geoshare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {
    EditText editTextEmail, editTextPassword;
    Button buttonSignIn, buttonSignUp;
    FirebaseAuth mAuth;
    LinearLayout layout;
    TextView textViewForgotPassword;

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
        setContentView(R.layout.activity_signin);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextSignInEmail);
        editTextPassword = findViewById(R.id.editTextSignInPassword);
        textViewForgotPassword = findViewById(R.id.txtForgotPassword);

        layout = findViewById(R.id.signInLayout);
//        getUser();

        layout.setOnTouchListener((v, event) -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return false;
        });

        buttonSignIn = findViewById(R.id.btnSignIn);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if(TextUtils.isEmpty(email)){
                    show_notification("Enter email");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    show_notification("Enter password");
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    show_notification("Sign in successful");
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    show_notification("Authentication failed");
                                }
                            }
                        });
            }
        });

        buttonSignUp = findViewById(R.id.btnSignUp);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        SpannableString content = new SpannableString("Forgot password");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textViewForgotPassword.setText(content);

        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getUser()  {
//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String uid = "R8UXO2EQ4HMmVJhXxv5o4aC2ore2";
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("User").child(uid);
        ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                show_notification(user.getFriendList().get(0));
                Log.d("TAG", user.getFriendList().get(0));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", databaseError.getMessage()); //Don't ignore errors!
            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);
    }
    void show_notification(String message){
        Toast.makeText(SignIn.this, message, Toast.LENGTH_SHORT).show();
    }
}
