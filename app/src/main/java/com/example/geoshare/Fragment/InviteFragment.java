package com.example.geoshare.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.geoshare.Adapter.InviteAdapter;
import com.example.geoshare.DataOutput;
import com.example.geoshare.Database.Authentication.Authentication;
import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.example.geoshare.Database.Storage.Storage;
import com.example.geoshare.Invite;
import com.example.geoshare.Model.User;
import com.example.geoshare.QR;
import com.example.geoshare.R;
import com.example.geoshare.Search;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InviteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InviteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InviteFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InviteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InviteFragment newInstance(String param1, String param2) {
        InviteFragment fragment = new InviteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private RecyclerView recyclerView;
    private InviteAdapter inviteAdapter;
    private List<User> mUsers;
    private EditText editTextAddFriendUserId;
    private TextView textViewInviteUserFriend;
    private LinearLayout linearLayoutUserFound;
    private Button buttonInviteFoundFriend;
    private ImageView imageViewInviteFriendProfile;
    private ImageButton buttonFindFriend, buttonQrCode;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invite, container, false);
//        recyclerView = view.findViewById(R.id.recycles_view);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        editTextAddFriendUserId = view.findViewById(R.id.editTextAddFriendID);
        textViewInviteUserFriend = view.findViewById(R.id.invite_friend_username);
        linearLayoutUserFound = view.findViewById(R.id.layoutUserFound);
        imageViewInviteFriendProfile = view.findViewById(R.id.invite_friend_profile_image);
        buttonFindFriend = view.findViewById(R.id.btnFindFriend);
        buttonInviteFoundFriend = view.findViewById(R.id.btnInviteFoundFriend);
        buttonQrCode = view.findViewById(R.id.btnQrCode);

        buttonFindFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = String.valueOf(editTextAddFriendUserId.getText());
                if(id.length() < 28) {
                    show_dialog("Invalid UID","Please check again friend's UID.",InviteFragment.this.getContext() );
                }
                else {
                    checkUserIdExistence(id, new UserIdCheckListener() {
                        @Override
                        public void onUserIdChecked(boolean result) {
                            if(result) {
                                DatabaseReference usersRef = RealtimeDatabase.getInstance().getUsersReference();
                                String currentUserId = Authentication.getInstance().getCurrentUserId();

                                if(currentUserId.equals(id)){
                                    show_dialog("User Not Valid","Do not enter your own id.",InviteFragment.this.getContext() );
                                    return;
                                }
                                usersRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User userFound = dataSnapshot.getValue(User.class);
                                        textViewInviteUserFriend.setText(userFound.getUsername());
                                        linearLayoutUserFound.setVisibility(View.VISIBLE);

                                        if(!userFound.getImageURL().equals("default")){
                                            StorageReference storageRef = Storage.getInstance().getUsersAvatarReference();
                                            storageRef.child(userFound.getImageURL()).getDownloadUrl()
                                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            Glide.with(InviteFragment.this.getContext()).load(uri).into(imageViewInviteFriendProfile);
                                                        }
                                                    });
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Xử lý lỗi nếu có
                                        Log.d("Kiem tra ban", "Da xay ra loi: " + databaseError.getMessage());
                                    }
                                });

                            }
                        }
                    });
                }

            }
        });

        buttonInviteFoundFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inviteFriendID = String.valueOf(editTextAddFriendUserId.getText());
                DataOutput.inviteNewFriend(inviteFriendID);

                editTextAddFriendUserId.setText("");
                linearLayoutUserFound.setVisibility(View.GONE);
            }
        });

        buttonQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), QR.class);
                startActivity(intent);
            }
        });


        mUsers = new ArrayList<>();
        return view;
    }
    void show_dialog(String title, String msg, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss(); // Đóng dialog khi nhấn nút OK
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public interface UserIdCheckListener {
        void onUserIdChecked(boolean result);
    }

    private void checkUserIdExistence(String uidToCheck, UserIdCheckListener listener) {
        DatabaseReference usersRef = RealtimeDatabase.getInstance().getUsersReference();
        usersRef.child(uidToCheck).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean result = dataSnapshot.exists();
                if (listener != null) {
                    listener.onUserIdChecked(result);
                }
                else {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
                Log.e("TAG", "Lỗi: " + databaseError.getMessage());
            }
        });
    }
}