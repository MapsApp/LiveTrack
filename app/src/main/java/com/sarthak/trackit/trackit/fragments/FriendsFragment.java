package com.sarthak.trackit.trackit.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.adapters.FriendsAdapter;

public class FriendsFragment extends Fragment implements FriendsAdapter.setOnFriendClickListener{

    RecyclerView mFriendsList;

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

        mFriendsList = view.findViewById(R.id.recycler_friends);

        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mFriendsList.setAdapter(new FriendsAdapter(getActivity(), this));
    }

    @Override
    public void OnFriendItemClicked(View view, int position) {

    }
}
