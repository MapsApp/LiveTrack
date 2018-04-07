package com.sarthak.trackit.trackit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.adapters.GroupActivityFriendsAdapter;
import com.sarthak.trackit.trackit.fragments.MapsFragment;
import com.sarthak.trackit.trackit.model.Group;
import com.sarthak.trackit.trackit.model.GroupMember;
import com.sarthak.trackit.trackit.model.ParcelableGeoPoint;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.Constants;
import com.sarthak.trackit.trackit.utils.LocationSentListener;
import com.sarthak.trackit.trackit.utils.RecyclerViewDivider;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupsActivity extends BaseActivity implements
        View.OnClickListener
        , GroupActivityFriendsAdapter.ItemClickListener {

    String groupName;
    String groupKey;

    ArrayList<String> adminKeyList = new ArrayList<>();

    String[] MEMBERS;
    String locationStatus;

    ArrayList<String> userKeyList = new ArrayList<>();
    ArrayList<User> groupMembersList = new ArrayList<>();

    HashMap<String, ParcelableGeoPoint> mParcelableGeoPointList = new HashMap<>();

    ArrayAdapter<String> adapter;

    GroupMember groupMember = new GroupMember();

    AutoCompleteTextView mGroupMembersSearchAtv;

    TextView mGroupNameTv;

    FloatingActionButton fabBottomSheet;
    LinearLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;

    LinearLayout mLocationSharingLayout;
    LinearLayout mAddMemberLayout;

    RecyclerView groupMembersRecyclerView;

    GroupActivityFriendsAdapter groupActivityFriendsAdapter;

    FirebaseFirestore mFirestore;
    FirebaseUser mUser;

    public LocationSentListener mLocationSentListener;
    public LocationReceivedListener mLocationReceivedListener;

    public interface LocationReceivedListener {

        void onLocationReceived(String key);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        setUpToolbar(this);
        groupName = getIntent().getStringExtra(Constants.GROUP_NAME);
        groupKey = getIntent().getStringExtra(Constants.GROUP_KEY);
        //groupMembersList = getIntent().getParcelableArrayListExtra(Constants.GROUP_MEMBERS_LIST);

        initFirebase();

        populateSearch();

        setUpView();

        fragmentInflate(MapsFragment.newInstance());

        getFriends();

        //Used to change icon in floating button with states of BottomSheet
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {

                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        fabBottomSheet.setImageResource(R.drawable.ic_expand_more_white);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        fabBottomSheet.setImageResource(R.drawable.ic_expand_less_white);
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

            case R.id.text_group_name:

                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {

                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    fabBottomSheet.setImageResource(R.drawable.ic_expand_less_white);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    fabBottomSheet.setImageResource(R.drawable.ic_expand_more_white);
                }
                break;

            case R.id.location_sharing_layout:

                if (locationStatus.equals("false")) {
                    groupMember.setLocation("true");
                } else if (locationStatus.equals("true")) {
                    groupMember.setLocation("false");
                }
                break;

            case R.id.add_member_layout:

                startActivity(new Intent(GroupsActivity.this, AddFriendsActivity.class)
                        .putStringArrayListExtra("userKeyList", userKeyList));
                break;
        }
    }

    @Override
    public void itemClicked(View v, int pos) {

        if (userKeyList != null) {
            mLocationReceivedListener.onLocationReceived(userKeyList.get(pos));
        }
    }

    private void initFirebase() {

        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setUpView() {

        mGroupMembersSearchAtv = mToolbar.findViewById(R.id.autocomplete_search);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, MEMBERS);
        mGroupMembersSearchAtv.setAdapter(adapter);

        mGroupNameTv = findViewById(R.id.text_group_name);
        mGroupNameTv.setText(groupName);
        mGroupNameTv.setOnClickListener(this);

        mLocationSharingLayout = findViewById(R.id.location_sharing_layout);
        mLocationSharingLayout.setOnClickListener(this);

        mAddMemberLayout = findViewById(R.id.add_member_layout);
        mAddMemberLayout.setOnClickListener(this);

        setUpRecyclerView();

        setUpBottomSheet();
    }

    private void setUpRecyclerView() {

        groupMembersRecyclerView = findViewById(R.id.recycler_group_members);
        groupMembersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        groupMembersRecyclerView.addItemDecoration(new RecyclerViewDivider(this,
                ContextCompat.getColor(this, R.color.md_blue_grey_200), 0.5f));

        groupActivityFriendsAdapter = new GroupActivityFriendsAdapter(groupMembersList, userKeyList);
        groupActivityFriendsAdapter.setOnRecyclerViewItemClickListener(GroupsActivity.this);
        groupMembersRecyclerView.setAdapter(groupActivityFriendsAdapter);
    }

    private void setUpBottomSheet() {

        fabBottomSheet = findViewById(R.id.fab_bottom_sheet);
        //addressed the container linear layout of bottom sheet
        layoutBottomSheet = findViewById(R.id.bottom_sheet_layout);
        //sets the behaviour of linear layout to a bottom sheet
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
    }

    private void populateSearch() {

        MEMBERS = new String[groupMembersList.size()];

        for (int i = 0; i < MEMBERS.length; i++) {
            MEMBERS[i] = groupMembersList.get(i).getDisplayName();
        }
    }

    /*Used to pass fragment on item selected*/
    public void fragmentInflate(Fragment fragment) {

        Bundle bundle = new Bundle();
        bundle.putInt("activityType", 2);
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.map_container, fragment);
        fragmentTransaction.commit();
    }

    private void getFriends() {

        mFirestore.collection(Constants.GROUPS_REFERENCE)
                .document(groupKey)
                .collection("GroupDetails")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (DocumentSnapshot groupSnapshot : task.getResult()) {

                            Group group = groupSnapshot.toObject(Group.class);
                            adminKeyList = group.getAdmin();
                            Log.d("TAG", group.getAdmin().size()+"");
                            groupActivityFriendsAdapter.setAdminList(adminKeyList);
                         }
                    }
                });

        mFirestore.collection(Constants.GROUPS_REFERENCE)
                .document(groupKey)
                .collection("GroupMembers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (final DocumentSnapshot document : task.getResult()) {

                                if (document != null && document.exists()) {

                                    mFirestore.collection(Constants.LOCATION_REFERENCE)
                                            .document(document.getId())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                    if (task.isSuccessful()) {

                                                        DocumentSnapshot snapshot = task.getResult();

                                                        if (snapshot != null && snapshot.exists()) {

                                                            mParcelableGeoPointList.put(document.getId(), snapshot.toObject(ParcelableGeoPoint.class));

                                                            if (mLocationSentListener != null) {
                                                                mLocationSentListener.passLocationToFragment(mParcelableGeoPointList);
                                                            }
                                                        }
                                                    }
                                                }
                                            });

                                    mFirestore.collection(Constants.USERS_REFERENCE)
                                            .document(document.getId())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                    if (task.isSuccessful()) {

                                                        final DocumentSnapshot snapshot = task.getResult();

                                                        if (snapshot != null && snapshot.exists()) {

                                                            if (adminKeyList.contains(snapshot.getId())) {
                                                                mAddMemberLayout.setVisibility(View.VISIBLE);
                                                            } else {
                                                                mAddMemberLayout.setVisibility(View.GONE);
                                                            }

                                                            setLocationLayoutVisibility(document.getId(), document.toObject(GroupMember.class));
                                                            userKeyList.add(document.getId());
                                                            groupMembersList.add(snapshot.toObject(User.class));
                                                            groupActivityFriendsAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
    }

    private void setLocationLayoutVisibility(String member, GroupMember groupMember) {

        if (member.equals(mUser.getUid())) {

            if (groupMember.getLocation().equals("false")) {

                locationStatus = "false";
                mLocationSharingLayout.setVisibility(View.VISIBLE);
            } else {

                locationStatus = "true";
                mLocationSharingLayout.setVisibility(View.GONE);
            }
        }
    }

    public void sendLocation(LocationSentListener listener) {
        this.mLocationSentListener = listener;
    }

    public void receiveLocation(LocationReceivedListener listener) {
        this.mLocationReceivedListener = listener;
    }
}