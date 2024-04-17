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

import com.example.geoshare.Adapter.InviteAdapter;
import com.example.geoshare.Adapter.RequestAdapter;
import com.example.geoshare.Database.Authentication.Authentication;
import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
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
 * Use the {@link RequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestFragment newInstance(String param1, String param2) {
        RequestFragment fragment = new RequestFragment();
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
    private RequestAdapter pendingListAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request, container, false);
        recyclerView = view.findViewById(R.id.recycles_view_pending_list);

        pendingListAdapter = new RequestAdapter(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getPendingList();

        return view;
    }
    private void getPendingList(){
        FirebaseUser currentUser = Authentication.getInstance().getCurrentUser();
        DatabaseReference friendsRef = RealtimeDatabase.getInstance().getFriendsReference().child(currentUser.getUid());
        friendsRef.child("pendingList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pendingListAdapter.clearRequestList();
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    String friendId = friendSnapshot.getValue(String.class);
                    Log.d("friend key: ", friendId);
                    if(friendId.equals("empty")) {
                       return;
                    }
                    DatabaseReference pendingUserRef = RealtimeDatabase.getInstance().getUsersReference().child(friendId);
                    Log.d("findFriend", friendId);
                    pendingUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User pendingUserFound = snapshot.getValue(User.class);
                            if(pendingUserFound != null) {
                                pendingListAdapter.addFriendToList(pendingUserFound);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                recyclerView.setAdapter(pendingListAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }
}