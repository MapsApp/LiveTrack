package com.sarthak.trackit.trackit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.activities.AllContactsActivity;
import com.sarthak.trackit.trackit.activities.CreateGroupActivity;
import com.sarthak.trackit.trackit.activities.GroupsActivity;
import com.sarthak.trackit.trackit.adapters.GroupAdapter;
import com.sarthak.trackit.trackit.model.User;

import java.util.ArrayList;

public class GroupsFragment extends Fragment implements View.OnClickListener {

    private ArrayList<User> userList = new ArrayList<>();
    Button mBtn;
    FloatingActionButton mCreateGroupFab;
    RecyclerView mFriendsList;

    FirebaseFirestore mFirestore;
    FirebaseUser mUser;

    public static GroupsFragment newInstance() {

        Bundle args = new Bundle();

        GroupsFragment fragment = new GroupsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();

        mBtn=view.findViewById(android.R.id.button1);
        mCreateGroupFab=view.findViewById(R.id.fab_create);
        mFriendsList = view.findViewById(R.id.recycler_groups);

        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mFriendsList.setAdapter(new GroupAdapter(userList));

        mCreateGroupFab.setOnClickListener(this);
        mBtn.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onClick(View v) {
        if(v==mCreateGroupFab){
            //startActivity(new Intent(getContext(), CreateGroupActivity.class));
        }
        if (v==mBtn){
            startActivity(new Intent(getContext(), GroupsActivity.class));
        }
    }
}
