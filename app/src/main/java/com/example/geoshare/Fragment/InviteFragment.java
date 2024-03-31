package com.example.geoshare.Fragment;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.example.geoshare.Adapter.InviteAdapter;
import com.example.geoshare.Invite;
import com.example.geoshare.Model.User;
import com.example.geoshare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private Button buttonAddFriend, buttonDeleteFriend;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invite, container, false);
//        recyclerView = view.findViewById(R.id.recycles_view);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        editTextAddFriendUserId = view.findViewById(R.id.editTextAddFriendID);
        buttonAddFriend = view.findViewById(R.id.btnAddFriend);
        buttonDeleteFriend = view.findViewById(R.id.btnDeleteFriend);

        DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends");

        buttonAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = String.valueOf(editTextAddFriendUserId.getText());
                checkUserIdExistence(id, new UserIdCheckListener() {
                    @Override
                    public void onUserIdChecked(boolean result) {
                        if (result) {
                            // UID tồn tại trong thư mục "Users"
                            Log.d("TAG", "UID tồn tại trong thư mục Users");
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
                            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            if(currentUserId.equals(id)){
                                show_dialog("User Not Valid","Do not enter your own id.",InviteFragment.this.getContext() );
                                return;
                            }
                            usersRef.child(currentUserId).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(id)) {
                                        // Người này đã là bạn của bạn, không thêm lại vào danh sách bạn bè
                                        Log.d("Kiem tra ban", "Da la ban be");
                                        show_dialog("Add friend","Users are already friends.",InviteFragment.this.getContext() );
                                    } else {
                                        // Người này chưa là bạn của bạn, thêm vào danh sách bạn bè
                                        usersRef.child(currentUserId).child("friends").child(id).setValue(true);
                                        usersRef.child(id).child("friends").child(currentUserId).setValue(true);
                                        show_dialog("Add friend","Added friends successfully.",InviteFragment.this.getContext() );
                                        Log.d("Kiem tra ban", "Chua la ban be them ban be");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Xử lý lỗi nếu có
                                    Log.d("Kiem tra ban", "Da xay ra loi: " + databaseError.getMessage());
                                }
                            });

                        } else {
                            // UID không tồn tại trong thư mục "Users"
                            Log.d("TAG", "UID không tồn tại trong thư mục Users");
//                            Toast.makeText(mContext, "User not found", Toast.LENGTH_SHORT).show();
                            show_dialog("User Not Found","The user you are looking for was not found.",InviteFragment.this.getContext() );

                        }
                    }
                });
            }
        });



        mUsers = new ArrayList<>();

        readUser();

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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Users");
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
    private void readUser(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot: datasnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    if(!user.getId().equals((firebaseUser.getUid()))){
                        mUsers.add(user);
                    }
                }
                inviteAdapter = new InviteAdapter(getContext(), mUsers);
//                recyclerView.setAdapter(inviteAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}