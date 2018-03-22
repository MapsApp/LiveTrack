package com.sarthak.trackit.trackit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.adapters.GroupSetupMembersAdapter;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupSetupActivity extends BaseActivity implements View.OnClickListener {

    HashMap<String, String> groupMembersMap = new HashMap<>();
    ArrayList<User> mGroupMembersList = new ArrayList<>();

    EditText mGroupNameEt;
    FloatingActionButton mFabCreateGroup;
    RecyclerView mGroupMembersRecycler;

    private FirebaseFirestore mFirestore;

    private FirebaseUser mUser;
    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_setup);
        setUpToolbar(this);

        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mFabCreateGroup = findViewById(R.id.fab_group_complete);
        mGroupNameEt = findViewById(R.id.edit_text_group_name);
        mGroupMembersRecycler = findViewById(R.id.recycler_setup_group_members);
        mGroupMembersRecycler.setLayoutManager(new GridLayoutManager(this, 3));

        getCurrentUserObject();

        mGroupMembersList = getIntent().getParcelableArrayListExtra(Constants.GROUP_MEMBERS_LIST);
        groupMembersMap = (HashMap<String, String>) getIntent().getSerializableExtra("userKey");

        mToolbar.setTitle("New Group");
        mToolbar.setSubtitle(mGroupMembersList.size() + " Participants");
        mGroupMembersRecycler.setAdapter(new GroupSetupMembersAdapter(mGroupMembersList));

        mFabCreateGroup.setOnClickListener(this);
    }

    @Override
    protected int getToolbarID() {
        return R.id.group_setup_activity_toolbar;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_group_complete:

                Long time = System.currentTimeMillis()/1000;
                String timestamp = time.toString();

                final String groupName = mGroupNameEt.getText().toString() + "+" + timestamp;

                final HashMap<String, String> groupMap = new HashMap<>();
                groupMap.put(groupName, timestamp);

                if (!mGroupNameEt.getText().toString().isEmpty()) {

                    mGroupMembersList.add(mCurrentUser);
                    groupMembersMap.put(mUser.getUid(), "admin");

                    mFirestore.collection(Constants.GROUPS_REFERENCE)
                            .document(groupName)
                            .set(groupMembersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                WriteBatch memberBatch = mFirestore.batch();

                                for (String key : groupMembersMap.keySet()) {

                                    DocumentReference memberRef = mFirestore.collection(Constants.USER_GROUPS_REFERENCE).document(key);
                                    memberBatch.set(memberRef, groupMap, SetOptions.merge());
                                }

                                memberBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            startActivity(new Intent(GroupSetupActivity.this,
                                                    GroupsActivity.class)
                                                    .putExtra(Constants.GROUP_NAME,
                                                            groupName)
                                                    .putParcelableArrayListExtra(Constants.GROUP_MEMBERS_LIST,
                                                            mGroupMembersList));
                                        }
                                    }
                                });
                            }
                        }
                    });

                } else {
                    Toast.makeText(this, "Group name cannot be empty",
                            Toast.LENGTH_SHORT).show();
                }
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
