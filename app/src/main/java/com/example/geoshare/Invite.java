package com.example.geoshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.geoshare.Fragment.FriendFragment;
import com.example.geoshare.Fragment.InviteFragment;
import com.example.geoshare.Fragment.RequestFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Invite extends AppCompatActivity {

    TextView textViewSelect;
    EditText editTextInviteID;
    BottomNavigationView navigationView;
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ImageButton buttonBack;
    Button btnFindID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        firebaseAuth = FirebaseAuth.getInstance();

        navigationView = findViewById(R.id.navigation);
        editTextInviteID = findViewById(R.id.editTextInviteID);
        textViewSelect = findViewById(R.id.txtInviteSelect);
        buttonBack = findViewById(R.id.btnInviteBack);
        btnFindID = findViewById(R.id.btnFindID);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        // When we open the application first
        // time the fragment should be shown to the user
        // in this case it is invite fragment
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnFindID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inviteID = String.valueOf(editTextInviteID.getText());
                DataOutput.addNewFriend(inviteID);
                Toast.makeText(Invite.this, "Adding new friend", Toast.LENGTH_SHORT).show();
            }
        });

        InviteFragment fragment = new InviteFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int itemId = menuItem.getItemId();
            if(itemId == R.id.nav_invite){
                textViewSelect.setText("Invite");
                InviteFragment fragment = new InviteFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content, fragment, "");
                fragmentTransaction.commit();
                return true;
            } else if (itemId == R.id.nav_friend) {
                textViewSelect.setText("Friend list");
                FriendFragment fragment1 = new FriendFragment();
                FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction1.replace(R.id.content, fragment1);
                fragmentTransaction1.commit();
                return true;
            } else if (itemId == R.id.nav_request) {
                textViewSelect.setText("Request");
                RequestFragment fragment2 = new RequestFragment();
                FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction2.replace(R.id.content, fragment2, "");
                fragmentTransaction2.commit();
                return true;
            }
            else {
            }
            return false;
        }
    };
}
