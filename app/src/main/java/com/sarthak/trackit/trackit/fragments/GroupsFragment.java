package com.sarthak.trackit.trackit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.activities.CreateGroupActivity;
import com.sarthak.trackit.trackit.activities.GroupsActivity;
import com.sarthak.trackit.trackit.adapters.UserGroupFragmentAdapter;
import com.sarthak.trackit.trackit.model.Group;
import com.sarthak.trackit.trackit.utils.Constants;
import com.sarthak.trackit.trackit.utils.RecyclerViewItemClickedListener;

import java.util.ArrayList;

public class GroupsFragment extends Fragment implements View.OnClickListener, RecyclerViewItemClickedListener {

    private ArrayList<String> groupList = new ArrayList<>();
    private ArrayList<String> groupKeyList = new ArrayList<>();

    FloatingActionButton mCreateGroupFab;

    RecyclerView mFriendsListRv;
    UserGroupFragmentAdapter adapter;

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

        mCreateGroupFab = view.findViewById(R.id.fab_create_group);
        mFriendsListRv = view.findViewById(R.id.recycler_groups);

        adapter = new UserGroupFragmentAdapter(groupList);
        adapter.setOnItemClickListener(this);

        mFriendsListRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mFriendsListRv.setAdapter(adapter);

        mCreateGroupFab.setOnClickListener(this);

        getUserGroups();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onClick(View v) {
        if (v == mCreateGroupFab) {
            startActivity(new Intent(getContext(), CreateGroupActivity.class));
        }
    }

    @Override
    public void onItemClicked(View view, int position) {

        String groupName = groupList.get(position);
        Toast.makeText(getActivity(), groupList.get(position), Toast.LENGTH_SHORT).show();

        startActivity(new Intent(getContext(), GroupsActivity.class)
                .putExtra(Constants.GROUP_NAME, groupName)
                .putExtra(Constants.GROUP_KEY, groupKeyList.get(position)));
    }

    private void getUserGroups() {

        mFirestore.collection(Constants.USER_GROUPS_REFERENCE)
                .document(mUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            groupList.clear();

                            DocumentSnapshot document = task.getResult();

                            if (document != null && document.exists()) {

                                for (final String group : document.getData().keySet()) {

                                    mFirestore.collection(Constants.GROUPS_REFERENCE)
                                            .document(group)
                                            .collection("GroupDetails")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                    for (DocumentSnapshot groupSnapshot : task.getResult()) {

                                                        Group groupDetails = groupSnapshot.toObject(Group.class);
                                                        groupList.add(groupDetails.getName());
                                                        groupKeyList.add(group);
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
    }
}
