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

import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.activities.SearchActivity;
import com.sarthak.trackit.trackit.adapters.FriendsAdapter;

/**
 * Created by karan on 3/9/2018.
 */

public class FriendsFragment extends Fragment implements View.OnClickListener
,FriendsAdapter.setOnFriendClickListener{

    RecyclerView rvFriends;
    FloatingActionButton fabCreateGroup;

    public static FriendsFragment newInstance() {

        Bundle args = new Bundle();

        FriendsFragment fragment = new FriendsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFriends=view.findViewById(R.id.recycler_friends);
        fabCreateGroup=view.findViewById(R.id.fab_create_group);

        rvFriends.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        rvFriends.setAdapter(new FriendsAdapter(this));

        fabCreateGroup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_create_group:
                launchSearch();
                break;
        }

    }

    @Override
    public void OnFriendItemClicked(View view, int position) {

    }

    private void launchSearch(){
        Intent intent = new Intent(getContext(), SearchActivity.class);
        startActivity(intent);

    }
}
