package com.sarthak.trackit.trackit.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ExpandableListView;
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
import com.sarthak.trackit.trackit.adapters.FriendsStatusAdapter;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsStatusActivity extends BaseActivity implements
        ExpandableListView.OnGroupClickListener
        , ExpandableListView.OnGroupExpandListener
        , ExpandableListView.OnGroupCollapseListener
        , ExpandableListView.OnChildClickListener {

    ArrayList<User> sentUserList = new ArrayList<>();
    ArrayList<User> receivedUserList = new ArrayList<>();
    ArrayList<User> friendList = new ArrayList<>();

    ArrayList<String> contactHeaderList = new ArrayList<>();

    HashMap<String, ArrayList<User>> contactKeyList = new HashMap<>();

    FriendsStatusAdapter listAdapter;
    ExpandableListView expListView;

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

        prepareListData();

        listAdapter = new FriendsStatusAdapter(FriendsStatusActivity.this, contactHeaderList, contactKeyList);
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(this);

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(this);

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(this);

        // Listview on child click listener
        expListView.setOnChildClickListener(this);
    }

    private void prepareListData() {

        Query sentQuery = mFirestore.collection(Constants.CONTACTS_REFERENCE).document(mUser.getUid()).collection("Sent");

        sentQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    sentUserList.clear();

                    if (!contactHeaderList.contains("Sent")) {

                        contactHeaderList.add("Sent");
                        listAdapter.notifyDataSetChanged();
                    }

                    for (DocumentSnapshot document : task.getResult()) {

                        if (document != null && document.exists()) {

                            mFirestore.collection(Constants.USERS_REFERENCE).document(document.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.isSuccessful()) {

                                        sentUserList.add(task.getResult().toObject(User.class));
                                        contactKeyList.put("Sent", sentUserList);
                                        // If timestamp is needed at some later stage, snapshot.getData will be used.
                                        listAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

        Query receivedQuery = mFirestore.collection(Constants.CONTACTS_REFERENCE).document(mUser.getUid()).collection("Received");

        receivedQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    receivedUserList.clear();

                    if (!contactHeaderList.contains("Received")) {

                        contactHeaderList.add("Received");
                        listAdapter.notifyDataSetChanged();
                    }

                    for (DocumentSnapshot document : task.getResult()) {

                        if (document != null && document.exists()) {

                            mFirestore.collection(Constants.USERS_REFERENCE).document(document.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.isSuccessful()) {

                                        receivedUserList.add(task.getResult().toObject(User.class));
                                        contactKeyList.put("Received", receivedUserList);
                                        listAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

        Query friendQuery = mFirestore.collection(Constants.CONTACTS_REFERENCE).document(mUser.getUid()).collection("Friends");

        friendQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    friendList.clear();

                    if (!contactHeaderList.contains("Friends")) {

                        contactHeaderList.add("Friends");
                        listAdapter.notifyDataSetChanged();
                    }

                    for (DocumentSnapshot document : task.getResult()) {

                        if (document != null && document.exists()) {

                            mFirestore.collection(Constants.USERS_REFERENCE).document(document.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.isSuccessful()) {

                                        friendList.add(task.getResult().toObject(User.class));
                                        contactKeyList.put("Friends", friendList);
                                        listAdapter.notifyDataSetChanged();
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
}
