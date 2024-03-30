package com.example.geoshare.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.geoshare.Adapter.FriendListAdapter;
import com.example.geoshare.Adapter.InviteAdapter;
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
 * Use the {@link FriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendFragment newInstance(String param1, String param2) {
        FriendFragment fragment = new FriendFragment();
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
    private FriendListAdapter friendListAdapter;
    private List<User> mFriends;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        recyclerView = view.findViewById(R.id.recycles_view_friend_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mFriends = new ArrayList<>();
        getFriendList();
        return view;
    }

    private void getFriendList(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("friends");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFriends.clear();
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    String friendId = friendSnapshot.getKey();
                    Log.d("friend key: ", friendId);
                    DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference("Users").child(friendId);
                    friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User friend = snapshot.getValue(User.class);
                            if(!friend.getId().equals((currentUser.getUid()))){
                                mFriends.add(friend);
                            }
                            if (mFriends.size() == dataSnapshot.getChildrenCount()) {
                                // Nếu đã thêm tất cả, hiển thị danh sách bạn bè
                                showFriendList();
                                friendListAdapter = new FriendListAdapter(getContext(), mFriends);
                                recyclerView.setAdapter(friendListAdapter);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }
    private void getFriendList1(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                mFriends.clear();
                for (DataSnapshot snapshot: datasnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    if(!user.getId().equals((firebaseUser.getUid()))){
                        mFriends.add(user);
                    }
                }
                friendListAdapter = new FriendListAdapter(getContext(), mFriends);
                recyclerView.setAdapter(friendListAdapter);
                showFriendList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showFriendList() {
        for (User friend : mFriends) {
            Log.d("FriendList", "UserID: " + friend.getId());
            Log.d("FriendList", "Image: " + friend.getImageURL());
            Log.d("FriendList", "Username: " + friend.getUsername());
            // Ghi thêm các thông tin khác của friend nếu có
        }
    }

}