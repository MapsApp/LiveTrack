package com.sarthak.trackit.trackit.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ExpandableListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.adapters.RequestStatusAdapter;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestStatusActivity extends BaseActivity implements
        ExpandableListView.OnGroupClickListener
        , ExpandableListView.OnGroupExpandListener
        , ExpandableListView.OnGroupCollapseListener
        , ExpandableListView.OnChildClickListener
        , SwipeRefreshLayout.OnRefreshListener {

    ArrayList<User> mSentRequest = new ArrayList<>();
    ArrayList<User> mReceivedRequestList = new ArrayList<>();

    ArrayList<String> mContactHeaderList = new ArrayList<>();

    HashMap<String, ArrayList<User>> mContactKeyList = new HashMap<>();

    RequestStatusAdapter mRequestListAdapter;
    ExpandableListView expListView;
    SwipeRefreshLayout mSwipeLayout;

    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_status);
        setUpToolbar(this);

        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        expListView = findViewById(R.id.lvExp);
        mSwipeLayout=findViewById(R.id.requestStatusRefreshLayout);
        mSwipeLayout.setRefreshing(true);
        mSwipeLayout.setColorSchemeResources(R.color.md_red_400,R.color.md_green_400,R.color.md_yellow_400,R.color.md_blue_400);
        prepareListData();

        mRequestListAdapter = new RequestStatusAdapter(RequestStatusActivity.this, mContactHeaderList, mContactKeyList);
        expListView.setAdapter(mRequestListAdapter);
        mSwipeLayout.setRefreshing(false);

        // Listview Group click listener
        expListView.setOnGroupClickListener(this);

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(this);

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(this);

        // Listview on child click listener
        expListView.setOnChildClickListener(this);
        mSwipeLayout.setOnRefreshListener(this);
    }

    private void prepareListData() {

        Query sentQuery = mFirestore
                .collection(Constants.CONTACTS_REFERENCE)
                .document(mUser.getUid())
                .collection("Sent");

        sentQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            mSentRequest.clear();


                            if (!mContactHeaderList.contains("Sent")) {
                                mContactHeaderList.add("Sent");
                                mRequestListAdapter.notifyDataSetChanged();
                            }

                            for (DocumentSnapshot document : task.getResult()) {

                                if (document != null && document.exists()) {

                                    mFirestore.collection(Constants.USERS_REFERENCE).document(document.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful()) {

                                                mSentRequest.add(task.getResult().toObject(User.class));
                                                mContactKeyList.put("Sent", mSentRequest);
                                                // If timestamp is needed at some later stage, snapshot.getData will be used.
                                                mRequestListAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                });

        Query receivedQuery = mFirestore
                .collection(Constants.CONTACTS_REFERENCE)
                .document(mUser.getUid())
                .collection("Received");

        receivedQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            mReceivedRequestList.clear();

                            if (!mContactHeaderList.contains("Received")) {

                                mContactHeaderList.add("Received");
                                mRequestListAdapter.notifyDataSetChanged();
                            }

                            for (DocumentSnapshot document : task.getResult()) {

                                if (document != null && document.exists()) {

                                    mFirestore.collection(Constants.USERS_REFERENCE).document(document.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful()) {

                                                mReceivedRequestList.add(task.getResult().toObject(User.class));
                                                mContactKeyList.put("Received", mReceivedRequestList);
                                                mRequestListAdapter.notifyDataSetChanged();
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
        return R.id.activity_status_toolbar;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return false;
    }

    @Override
    public void onGroupExpand(int groupPosition) {

    }

    @Override
    public void onGroupCollapse(int groupPosition) {

    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }

    @Override
    public void onRefresh() {
        mSwipeLayout.setRefreshing(true);
        mRequestListAdapter.notifyDataSetChanged();
        mSwipeLayout.setRefreshing(false);
    }
}
