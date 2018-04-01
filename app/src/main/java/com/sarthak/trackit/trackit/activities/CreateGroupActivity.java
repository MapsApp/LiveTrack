package com.sarthak.trackit.trackit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.adapters.FriendsAdapter;
import com.sarthak.trackit.trackit.adapters.GroupMembersAdapter;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateGroupActivity extends BaseActivity implements FriendsAdapter.setOnFriendClickListener
        , GroupMembersAdapter.setOnGroupMemberClicked {

    ArrayList<User> friendList = new ArrayList<>();
    ArrayList<String> friendKeyList = new ArrayList<>();

    ArrayList<User> groupMemberList = new ArrayList<>();
    ArrayList<String> groupMemberKeyList = new ArrayList<>();

    HashMap<String, HashMap<String, String>> groupMemberMap = new HashMap<>();
    HashMap<String, String> memberMap = new HashMap<>();

    TextView mGroupCountTv;
    RecyclerView mFriendsRecyclerView, mGroupRecyclerView;

    GroupMembersAdapter mGroupFriendsAdapter = null;
    FriendsAdapter mFriendsAdapter;

    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        setUpToolbar(this);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        
        setUpView();

        populateFriendsRecyclerView();
    }

    @Override
    protected int getToolbarID() {
        return R.id.create_group_toolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_group_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                onBackPressed();
                break;

            case R.id.action_done:

                if (!groupMemberList.isEmpty()) {

                    Log.d("oil", groupMemberMap.size() + " " + groupMemberList.size() + " " + groupMemberKeyList.size());
                    Intent groupSetupIntent = new Intent(this, GroupSetupActivity.class);
                    groupSetupIntent.putExtra("userKey", groupMemberMap);
                    groupSetupIntent.putParcelableArrayListExtra(Constants.GROUP_MEMBERS_LIST, groupMemberList);
                    startActivity(groupSetupIntent);
                } else {
                    Toast.makeText(this, "Add at least one member", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnFriendItemClicked(View view, int position) {

        if (!groupMemberList.contains(friendList.get(position))) {

            groupMemberList.add(friendList.get(position));
            groupMemberKeyList.add(friendKeyList.get(position));

            memberMap.put("admin", "false");
            memberMap.put("location", "false");
            memberMap.put("displayName", friendList.get(position).getDisplayName());

            groupMemberMap.put(friendKeyList.get(position), memberMap);
            mGroupFriendsAdapter.notifyDataSetChanged();

            if (groupMemberList.size() == 0) {
                mGroupCountTv.setText("No Members");
            } else if (groupMemberList.size() == 1) {
                mGroupCountTv.setText(String.valueOf(groupMemberList.size()) + " Member");
            } else {
                mGroupCountTv.setText(String.valueOf(groupMemberList.size()) + " Members");
            }
        }
    }

    @Override
    public void onGroupMemberClicked(View v, int position) {

        if (!groupMemberList.isEmpty()) {

            groupMemberList.remove(groupMemberList.get(position));
            groupMemberMap.remove(groupMemberKeyList.get(position));
            groupMemberKeyList.remove(groupMemberKeyList.get(position));
            mGroupFriendsAdapter.notifyDataSetChanged();

            if (groupMemberList.size() == 0) {
                mGroupCountTv.setText("No Members");
            } else if (groupMemberList.size() == 1) {
                mGroupCountTv.setText(String.valueOf(groupMemberList.size()) + " Member");
            } else {
                mGroupCountTv.setText(String.valueOf(groupMemberList.size()) + " Members");
            }
        }
    }

    private void setUpView() {

        mGroupCountTv = findViewById(R.id.text_member_count);

        mFriendsRecyclerView = findViewById(R.id.recycler_friends_new_group);
        mFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFriendsAdapter = new FriendsAdapter(this, friendList);
        mFriendsRecyclerView.setAdapter(mFriendsAdapter);

        mGroupRecyclerView = findViewById(R.id.recycler_group_members);
        mGroupRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mGroupFriendsAdapter = new GroupMembersAdapter(groupMemberList, this);
        mGroupRecyclerView.setAdapter(mGroupFriendsAdapter);
    }

    private void populateFriendsRecyclerView() {
        
        Query friendQuery = FirebaseFirestore
                .getInstance()
                .collection(Constants.CONTACTS_REFERENCE)
                .document(mUser.getUid())
                .collection("Friends");

        friendQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    friendList.clear();

                    for (DocumentSnapshot document : task.getResult()) {

                        if (document != null && document.exists()) {

                            FirebaseFirestore
                                    .getInstance()
                                    .collection(Constants.USERS_REFERENCE)
                                    .document(document.getId())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful()) {

                                                friendList.add(task.getResult().toObject(User.class));
                                                friendKeyList.add(task.getResult().getId());
                                                mFriendsAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                        }
                    }
                }
            }
        });

    }
}
