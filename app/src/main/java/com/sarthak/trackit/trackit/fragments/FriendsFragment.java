package com.sarthak.trackit.trackit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.activities.SearchActivity;
import com.sarthak.trackit.trackit.adapters.FriendsAdapter;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.Constants;

import java.util.ArrayList;

public class FriendsFragment extends Fragment implements View.OnClickListener
        , FriendsAdapter.setOnFriendClickListener
        , SwipeRefreshLayout.OnRefreshListener {

    ArrayList<User> mUserFriendList = new ArrayList<>();

    FloatingActionButton fabCreateGroup;
    RecyclerView rvFriends;
    SwipeRefreshLayout mSwipeLayout;

    FriendsAdapter mFriendsAdapter;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    RecyclerView mFriendsList;

    public static FriendsFragment newInstance() {

        Bundle args = new Bundle();

        FriendsFragment fragment = new FriendsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fabCreateGroup = view.findViewById(R.id.fab_create_group);

        mFriendsList = view.findViewById(R.id.recycler_friends);

        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mSwipeLayout = view.findViewById(R.id.swipeToRefresh);
        mSwipeLayout.setRefreshing(true);
        mSwipeLayout.setColorSchemeResources(R.color.md_red_400, R.color.md_green_400, R.color.md_yellow_400, R.color.md_blue_400);
        setRecyclerView();

        mFriendsList.setAdapter(new FriendsAdapter(this, mUserFriendList));

        fabCreateGroup.setOnClickListener(this);

        mSwipeLayout.setOnRefreshListener(this);
    }

    private void setRecyclerView() {
        Query friendQuery = mFirestore
                .collection(Constants.CONTACTS_REFERENCE)
                .document(mUser.getUid())
                .collection("Friends");

        friendQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    mUserFriendList.clear();

                    for (DocumentSnapshot document : task.getResult()) {

                        if (document != null && document.exists()) {

                            mFirestore.collection(Constants.USERS_REFERENCE).document(document.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.isSuccessful()) {

                                        mUserFriendList.add(task.getResult().toObject(User.class));
                                        mFriendsAdapter.notifyDataSetChanged();
                                        mSwipeLayout.setRefreshing(false);
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
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fab_create_group:

                launchSearch();
                break;
        }
    }

    @Override
    public void OnFriendItemClicked(View view, int position) {

    }

    private void launchSearch() {

        Intent intent = new Intent(getContext(), SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        mSwipeLayout.setRefreshing(true);
        mFriendsAdapter.notifyDataSetChanged();
        mSwipeLayout.setRefreshing(false);
    }

}
