package com.sarthak.trackit.trackit.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.adapters.FriendsStatusAdapter;
import com.sarthak.trackit.trackit.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendsStatusActivity extends BaseActivity implements ExpandableListView.OnGroupClickListener
        , ExpandableListView.OnGroupExpandListener
        , ExpandableListView.OnGroupCollapseListener
        , ExpandableListView.OnChildClickListener {

    private static final String TAG = FriendsStatusActivity.class.getName();
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;
    FriendsStatusAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    //List<Query> queryList;
    HashMap<String, List<String>> listDataChild;
    HashMap<String, List<Query>> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_status);
        setUpToolbar(this);

        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        expListView = findViewById(R.id.lvExp);

        prepareListData();

        listAdapter = new FriendsStatusAdapter(this, listDataHeader, listDataChild);
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
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        listData=new HashMap<String,List<Query>>();

        Query mQuery1=mFirestore.collection(Constants.CONTACTS_REFERENCE)
                .document(mUser.getUid())
                .collection("Sent");

                /*.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });*/

        Query mQuery2=mFirestore.collection(Constants.CONTACTS_REFERENCE)
                .document(mUser.getUid())
                .collection("Received");
                /*.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });*/

        // Adding child data
        /*queryList.add(mQuery1);
        queryList.add(mQuery2);*/

        listDataHeader.add("Pending Request");
        listDataHeader.add("Sent Request");

        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");

        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
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
