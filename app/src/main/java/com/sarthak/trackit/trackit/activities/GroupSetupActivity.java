package com.sarthak.trackit.trackit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.adapters.GroupSetupMembersAdapter;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.Constants;

import java.util.ArrayList;

public class GroupSetupActivity extends BaseActivity implements View.OnClickListener {

    ArrayList<User> mGroupMembersList = new ArrayList<>();

    EditText mGroupNameEt;
    FloatingActionButton mFabCreateGroup;
    RecyclerView mGroupMembersRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_setup);
        setUpToolbar(this);
        mFabCreateGroup = findViewById(R.id.fab_group_complete);

        mGroupNameEt = findViewById(R.id.edit_text_group_name);
        mGroupMembersRecycler = findViewById(R.id.recycler_setup_group_members);
        mGroupMembersRecycler.setLayoutManager(new GridLayoutManager(this, 3));

        mGroupMembersList = getIntent().getParcelableArrayListExtra(Constants.GROUP_MEMBERS_LIST);
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
                if (!mGroupNameEt.getText().toString().isEmpty()) {
                    startActivity(new Intent(this,
                            GroupsActivity.class)
                            .putExtra(Constants.GROUP_NAME,
                                    mGroupNameEt.getText().toString())
                            .putParcelableArrayListExtra(Constants.GROUP_MEMBERS_LIST,
                                    mGroupMembersList));
                    finish();
                } else {
                    Toast.makeText(this, "Group name cannot be empty",
                            Toast.LENGTH_SHORT).show();
                }
        }
    }
}
