package com.sarthak.trackit.trackit.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.adapters.AddFriendsAdapter;
import com.sarthak.trackit.trackit.adapters.GroupsFragmentAdapter;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.Constants;
import com.sarthak.trackit.trackit.utils.RecyclerViewItemClickedListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AddFriendsActivity extends AppCompatActivity implements RecyclerViewItemClickedListener {

    private ArrayList<Integer> selectList = new ArrayList<>();

    private ArrayList<String> friendKeyList = new ArrayList<>();
    private ArrayList<User> friendList = new ArrayList<>();

    private ArrayList<String> groupMemberKeyList = new ArrayList<>();
    private ArrayList<User> groupMemberList = new ArrayList<>();

    private HashMap<String, String> memberMap = new HashMap<>();

    private RecyclerView mFriendsRv;

    private AddFriendsAdapter groupsFragmentAdapter;

    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        friendKeyList = getIntent().getStringArrayListExtra("userKeyList");

        initFirebase();

        setUpRecyclerView();

        getFriendsList();
    }

    private void initFirebase() {

        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setUpRecyclerView() {

        groupsFragmentAdapter = new AddFriendsAdapter(friendList);
        groupsFragmentAdapter.setOnItemClickListener(this);

        mFriendsRv = findViewById(R.id.friends_list);
        mFriendsRv.setLayoutManager(new LinearLayoutManager(this));
        mFriendsRv.setItemAnimator(new DefaultItemAnimator());
        mFriendsRv.setAdapter(groupsFragmentAdapter);
    }

    private void getFriendsList() {

        mFirestore.collection(Constants.CONTACTS_REFERENCE)
                .document(mUser.getUid())
                .collection("Friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            friendList.clear();

                            for (final DocumentSnapshot document : task.getResult()) {

                                if (document != null && document.exists()) {

                                    if (!friendKeyList.contains(document.getId())) {

                                        mFirestore.collection(Constants.USERS_REFERENCE)
                                                .document(document.getId())
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                if (task.isSuccessful()) {

                                                    friendList.add(task.getResult().toObject(User.class));
                                                    groupsFragmentAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onItemClicked(View view, int position) {

        if (!selectList.contains(position)) {

            groupMemberList.add(friendList.get(position));
            groupMemberKeyList.add(friendKeyList.get(position));

            memberMap.put("admin", "false");
            memberMap.put("location", "false");
            memberMap.put("displayName", friendList.get(position).getDisplayName());

            Toast.makeText(this, friendList.get(position).getDisplayName() + " added to list.", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, groupMemberList.get(position).getDisplayName() + " removed from list.", Toast.LENGTH_SHORT).show();

            groupMemberList.remove(friendList.indexOf(friendList.get(position)));
            groupMemberKeyList.remove(friendKeyList.indexOf(friendKeyList.get(position)));
        }
    }
}
