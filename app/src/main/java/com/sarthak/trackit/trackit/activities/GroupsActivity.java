package com.sarthak.trackit.trackit.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.sarthak.trackit.trackit.adapters.GroupFriendsAdapter;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.Constants;
import com.sarthak.trackit.trackit.utils.RecyclerViewDivider;

import java.util.ArrayList;

public class GroupsActivity extends BaseActivity implements View.OnClickListener
        , GroupFriendsAdapter.setOnGroupFriendClickListener {

    String mGroupName;
    String[] MEMBERS;
    ArrayList<User> mGroupMembersList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    GroupFriendsAdapter mGroupFriendsAdapter;

    AutoCompleteTextView mGroupMembersSearchAtv;
    TextView mGroupNameTv;
    FloatingActionButton fabBottomSheet;
    LinearLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;
    RecyclerView rvGroupMembers;

    FirebaseFirestore mFirestore;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        setUpToolbar(this);
        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mGroupName = getIntent().getStringExtra(Constants.GROUP);

        mGroupMembersSearchAtv = mToolbar.findViewById(R.id.autocomplete_search);

        mGroupNameTv = findViewById(R.id.text_group_name);
        rvGroupMembers = findViewById(R.id.recycler_group_members);
        fabBottomSheet = findViewById(R.id.fab_bottom_sheet);

        getFriends();
        //mGroupMembersList = getIntent().getParcelableArrayListExtra(Constants.GROUP_MEMBERS_LIST);
        populateSearch();
        mGroupNameTv.setText(getIntent().getStringExtra(Constants.GROUP_NAME));

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, MEMBERS);
        mGroupMembersSearchAtv.setAdapter(adapter);
        //addressed the container linear layout of bottom sheet
        layoutBottomSheet = findViewById(R.id.bottom_sheet_layout);
        //sets the behaviour of linear layout to a bottom sheet
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        mGroupFriendsAdapter = new GroupFriendsAdapter(mGroupMembersList, this);
        rvGroupMembers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvGroupMembers.setAdapter(mGroupFriendsAdapter);
        rvGroupMembers.addItemDecoration(new RecyclerViewDivider(this,
                ContextCompat.getColor(this, R.color.md_blue_grey_200), 0.5f));

        //Used to change icon in floating button with states of BottomSheet
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        fabBottomSheet.setImageResource(R.drawable.ic_expand_more_white);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        fabBottomSheet.setImageResource(R.drawable.ic_expand_less_white);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        //fabBottomSheet.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward));
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        //fabBottomSheet.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward));
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        fabBottomSheet.setOnClickListener(this);
    }

    @Override
    protected int getToolbarID() {
        return R.id.groups_activity_toolbar;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fab_bottom_sheet:

                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {

                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    fabBottomSheet.setImageResource(R.drawable.ic_expand_less_white);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    fabBottomSheet.setImageResource(R.drawable.ic_expand_more_white);
                }
                break;
        }
    }


    @Override
    public void OnGroupFriendItemClicked(View view, int position) {

    }

    private void getFriends() {

        mFirestore.
                collection(Constants.GROUP)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            mGroupMembersList.clear();

                            for (DocumentSnapshot document : task.getResult()) {

                                if (document != null && document.exists()) {

                                    mFirestore.collection(Constants.GROUP)
                                            .document(mGroupName)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful()) {

                                                mGroupMembersList.add(task.getResult()
                                                        .toObject(User.class));
                                                mGroupFriendsAdapter.notifyDataSetChanged();
                                            } else {

                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
    }


    private void populateSearch() {

        MEMBERS = new String[mGroupMembersList.size()];

        for (int i = 0; i < MEMBERS.length; i++) {
            MEMBERS[i] = mGroupMembersList.get(i).getDisplayName();
        }
    }
}
