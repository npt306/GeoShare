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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPassword extends AppCompatActivity {
    EditText editTextForgotPasswordEmail;
    Button buttonBack, buttonResetPassword;
    FirebaseAuth mAuth;
    LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editTextForgotPasswordEmail = findViewById(R.id.editTextForgotPasswordEmail);
        buttonBack = findViewById(R.id.btnForgotPasswordToSignIn);
        buttonResetPassword = findViewById(R.id.btnResetPassword);
        mAuth = FirebaseAuth.getInstance();

        layout = findViewById(R.id.forgot_password_layout);

        layout.setOnTouchListener((v, event) -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return false;
        });

        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(editTextForgotPasswordEmail.getText());
                if(!TextUtils.isEmpty(email)){
                    ResetPassword(email);
                }
                else {
                    Toast.makeText(ForgotPassword.this,"Enter email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(ForgotPassword.this, SignIn.class);
//                startActivity(intent);
                finish();
            }
        });
    }
    private void ResetPassword(String email){
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ForgotPassword.this, "Reset password link has been sent to your registered email.", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(ForgotPassword.this, SignIn.class);
//                        startActivity(intent);
                        finish();
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPassword.this, "Error : - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
