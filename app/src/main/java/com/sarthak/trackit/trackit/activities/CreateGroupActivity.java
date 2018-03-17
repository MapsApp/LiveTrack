package com.sarthak.trackit.trackit.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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

public class CreateGroupActivity extends BaseActivity implements
        FriendsAdapter.setOnFriendClickListener {

    ArrayList<User> mFriendsList = new ArrayList<>();
    ArrayList<User> mFriendsGroupList = new ArrayList<>();

    GroupMembersAdapter mGroupFriendsAdapter;
    FriendsAdapter mFriendsAdapter;

    LinearLayout mGroupFriendsLayout;
    RecyclerView mFriendsRecycler, mGroupRecycler;

    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        setUpToolbar(this);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mGroupFriendsLayout = findViewById(R.id.groupFriendsLayout);
        mGroupFriendsLayout.setVisibility(View.GONE);

        mFriendsRecycler = findViewById(R.id.recycler_friends_new_group);
        mGroupRecycler = findViewById(R.id.recycler_group_members);

        mFriendsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mGroupRecycler.setLayoutManager(new GridLayoutManager(this, 2));

        mFriendsAdapter = new FriendsAdapter(this, mFriendsList);
        mFriendsRecycler.setAdapter(mFriendsAdapter);

        mGroupFriendsAdapter = new GroupMembersAdapter(mFriendsGroupList);
        mGroupRecycler.setAdapter(mGroupFriendsAdapter);

        setFriendsRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_create_group, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                mFriendsGroupList.clear();
                break;

            case R.id.done:

                Toast.makeText(this, "OK.", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
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
            mGroupFriendsAdapter.notifyDataSetChanged();
            Toast.makeText(this,  mFriendsList.get(position).getDisplayName() + "  Added "+mFriendsGroupList.size(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, mFriendsList.get(position).getDisplayName() + " Already Present "+mFriendsGroupList.size(), Toast.LENGTH_SHORT).show();
        }

        mGroupFriendsLayout.setVisibility(View.VISIBLE);
    }
}
