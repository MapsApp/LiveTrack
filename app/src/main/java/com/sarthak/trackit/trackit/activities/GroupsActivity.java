package com.sarthak.trackit.trackit.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.adapters.GroupFriendsAdapter;
import com.sarthak.trackit.trackit.fragments.MapsFragment;
import com.sarthak.trackit.trackit.model.ParcelableGeoPoint;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.Constants;
import com.sarthak.trackit.trackit.utils.LocationSentListener;
import com.sarthak.trackit.trackit.utils.RecyclerViewDivider;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupsActivity extends BaseActivity implements View.OnClickListener, GroupFriendsAdapter.ItemClickListener {

    String mGroupName;
    String[] MEMBERS;
    String locationStatus;

    ArrayList<String> adminStatusList = new ArrayList<>();
    ArrayList<String> userKeyList = new ArrayList<>();
    ArrayList<User> mGroupMembersList = new ArrayList<>();

    HashMap<String, String> memberMap = new HashMap<>();
    HashMap<String, String> currentUserMap = new HashMap<>();
    HashMap<String, ParcelableGeoPoint> mParcelableGeoPointList = new HashMap<>();

    ArrayAdapter<String> adapter;

    AutoCompleteTextView mGroupMembersSearchAtv;

    TextView mGroupNameTv;

    FloatingActionButton fabBottomSheet;
    LinearLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;

    LinearLayout mLocationSharingLayout;

    RecyclerView groupMembersRecyclerView;

    GroupFriendsAdapter mGroupFriendsAdapter;

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
        mGroupName = getIntent().getStringExtra(Constants.GROUP_NAME);
        //mGroupMembersList = getIntent().getParcelableArrayListExtra(Constants.GROUP_MEMBERS_LIST);

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

            case R.id.location_sharing_layout:

                if (locationStatus.equals("false")) {
                    currentUserMap.put("location", "true");
                } else if (locationStatus.equals("true")) {
                    currentUserMap.put("location", "false");
                }
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
        mGroupNameTv.setText(mGroupName.substring(0, mGroupName.indexOf("+")));

        mLocationSharingLayout = findViewById(R.id.location_sharing_layout);
        mLocationSharingLayout.setOnClickListener(this);

        setUpRecyclerView();

        setUpBottomSheet();
    }

    private void setUpRecyclerView() {

        groupMembersRecyclerView = findViewById(R.id.recycler_group_members);
        groupMembersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        groupMembersRecyclerView.addItemDecoration(new RecyclerViewDivider(this,
                ContextCompat.getColor(this, R.color.md_blue_grey_200), 0.5f));

        mGroupFriendsAdapter = new GroupFriendsAdapter(mGroupMembersList, adminStatusList);
        mGroupFriendsAdapter.setOnRecyclerViewItemClickListener(GroupsActivity.this);
        groupMembersRecyclerView.setAdapter(mGroupFriendsAdapter);
    }

    private void setUpBottomSheet() {

        fabBottomSheet = findViewById(R.id.fab_bottom_sheet);
        //addressed the container linear layout of bottom sheet
        layoutBottomSheet = findViewById(R.id.bottom_sheet_layout);
        //sets the behaviour of linear layout to a bottom sheet
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
    }

    private void populateSearch() {

        MEMBERS = new String[mGroupMembersList.size()];

        for (int i = 0; i < MEMBERS.length; i++) {
            MEMBERS[i] = mGroupMembersList.get(i).getDisplayName();
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
                .document(mGroupName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            final DocumentSnapshot document = task.getResult();

                            if (document != null && document.exists()) {

                                for (final String member : document.getData().keySet()) {

                                    mFirestore.collection(Constants.LOCATION_REFERENCE)
                                            .document(member)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                    if (task.isSuccessful()) {

                                                        DocumentSnapshot snapshot = task.getResult();

                                                        if (snapshot != null && snapshot.exists()) {

                                                            mParcelableGeoPointList.put(member, snapshot.toObject(ParcelableGeoPoint.class));

                                                            if (mLocationSentListener != null) {
                                                                mLocationSentListener.passLocationToFragment(mParcelableGeoPointList);
                                                            }
                                                        }
                                                    }
                                                }
                                            });

                                    mFirestore.collection(Constants.USERS_REFERENCE)
                                            .document(member)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                    if (task.isSuccessful()) {

                                                        DocumentSnapshot snapshot = task.getResult();

                                                        if (snapshot != null && snapshot.exists()) {

                                                            memberMap = (HashMap<String, String>) document.getData().get(member);
                                                            if (memberMap.get("admin").equals("true")) {

                                                                adminStatusList.add("true");
                                                            } else {

                                                                adminStatusList.add("false");
                                                            }

                                                            setLocationLayoutVisibility(member, memberMap);
                                                            userKeyList.add(member);
                                                            mGroupMembersList.add(snapshot.toObject(User.class));
                                                            mGroupFriendsAdapter.notifyDataSetChanged();
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

    private void setLocationLayoutVisibility(String member, HashMap<String, String> memberMap) {

        if (member.equals(mUser.getUid())) {

            currentUserMap = memberMap;
            if (memberMap.get("location").equals("false")) {

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
