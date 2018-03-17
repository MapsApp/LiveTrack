package com.sarthak.trackit.trackit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class CreateGroupActivity extends BaseActivity implements
        FriendsAdapter.setOnFriendClickListener
        , GroupMembersAdapter.setOnGroupMemberClicked
        , View.OnClickListener {

    ArrayList<User> mFriendsList = new ArrayList<>();
    ArrayList<User> mFriendsGroupList = new ArrayList<>();

    ArrayList<String> friendKeyList = new ArrayList<>();
    HashMap<String, String> groupMemberMap = new HashMap<>();

    GroupMembersAdapter mGroupFriendsAdapter = null;
    FriendsAdapter mFriendsAdapter;

    TextView mGroupCountTv;
    LinearLayout mGroupFriendsLayout;
    RecyclerView mFriendsRecycler, mGroupRecycler;

    FirebaseUser mUser;
    User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        setUpToolbar(this);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mGroupCountTv = findViewById(R.id.text_member_count);

        mGroupFriendsLayout = findViewById(R.id.groupFriendsLayout);
        mGroupFriendsLayout.setVisibility(View.VISIBLE);

        mFriendsRecycler = findViewById(R.id.recycler_friends_new_group);
        mGroupRecycler = findViewById(R.id.recycler_group_members);

        mFriendsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mGroupRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mFriendsAdapter = new FriendsAdapter(this, mFriendsList);
        mFriendsRecycler.setAdapter(mFriendsAdapter);

        mGroupFriendsAdapter = new GroupMembersAdapter(mFriendsGroupList, this);
        mGroupRecycler.setAdapter(mGroupFriendsAdapter);

        setFriendsRecyclerView();
        getCurrentUserObject();

        mGroupFriendsLayout.setOnClickListener(this);
    }

    private void setFriendsRecyclerView() {
        Query friendQuery = FirebaseFirestore
                .getInstance()
                .collection(Constants.CONTACTS_REFERENCE)
                .document(mUser.getUid())
                .collection("Friends");

        friendQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    mFriendsList.clear();

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

                                                mFriendsList.add(task.getResult().toObject(User.class));
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

    @Override
    protected int getToolbarID() {
        return R.id.create_group_toolbar;
    }

    @Override
    public void OnFriendItemClicked(View view, int position) {

        if (!mFriendsGroupList.contains(mFriendsList.get(position))) {

            mFriendsGroupList.add(mFriendsList.get(position));
            groupMemberMap.put(friendKeyList.get(position), "false");
            mGroupFriendsAdapter.notifyDataSetChanged();
            mGroupCountTv.setText(String.valueOf(mFriendsGroupList.size()) + " Members");
        } else {
        }

        mGroupFriendsLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGroupMemberClicked(View v, int position) {

        if (!mFriendsGroupList.isEmpty()) {

            mFriendsGroupList.remove(mFriendsGroupList.get(position));
            mGroupFriendsAdapter.notifyDataSetChanged();
            mGroupCountTv.setText(String.valueOf(mFriendsGroupList.size()) + " Members");
        }
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
                mFriendsGroupList.clear();
                break;
            case R.id.action_done:
                mFriendsGroupList.add(mCurrentUser);
                groupMemberMap.put(mUser.getUid(), "true");
                Intent groupSetupIntent = new Intent(this, GroupSetupActivity.class);
                groupSetupIntent.putExtra("userKey", groupMemberMap);
                groupSetupIntent.putParcelableArrayListExtra(Constants.GROUP_MEMBERS_LIST, mFriendsGroupList);
                startActivity(groupSetupIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == mGroupFriendsLayout) {
        }
    }

    private void getCurrentUserObject() {

        FirebaseFirestore.getInstance().collection(Constants.USERS_REFERENCE).document(mUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    mCurrentUser = task.getResult().toObject(User.class);
                }
            }
        });
    }
}
