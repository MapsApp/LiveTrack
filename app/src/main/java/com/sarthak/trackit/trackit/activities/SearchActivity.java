package com.sarthak.trackit.trackit.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.adapters.GroupsFragmentAdapter;
import com.sarthak.trackit.trackit.adapters.SearchAdapter;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.Constants;

import java.util.ArrayList;

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    private ArrayList<String> userKeyList = new ArrayList<>();
    private ArrayList<User> userList = new ArrayList<>();

    private Menu menu;
    private SearchView searchView;
    private TextView mErrorText;

    private RecyclerView mSearchRecyclerView;

    private SearchAdapter searchAdapter;
    private GroupsFragmentAdapter groupsFragmentAdapter;

    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setUpToolbar(this);

        initFirebase();

        setUpView();

        getFriendsList();
    }

    @Override
    protected int getToolbarID() {
        return R.id.search_activity_toolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.activity_search, menu);

        ImageView closeButton = searchView.findViewById(R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.ic_close);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.isSubmitButtonEnabled();
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {

                userList.clear();
                userKeyList.clear();
                mSearchRecyclerView.setAdapter(groupsFragmentAdapter);

                getFriendsList();
                return true;
            }
        });

        //Sets the search icon in the Toolbar
        //Also, its behaviour is defined
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_search:

                userList.clear();
                mSearchRecyclerView.setAdapter(searchAdapter);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchText) {

        if (searchText.length() != 0) {

            fireStoreUserSearch(searchText);
        } else {

            fireStoreUserSearch(" ");
        }

        return false;
    }


    private void initFirebase() {

        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setUpView() {

        mToolbar.inflateMenu(R.menu.home);
        menu = mToolbar.getMenu();

        setUpSearchToolbar();

        setUpRecyclerView();

        mErrorText = findViewById(R.id.text_no_results);
        mErrorText.setVisibility(View.VISIBLE);
    }

    private void setUpSearchToolbar() {

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        //searchView.setSuggestionsAdapter();
        searchView.animate();

        EditText mSearchEt = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        mSearchEt.setHint("Search...");
        mSearchEt.setHintTextColor(Color.DKGRAY);
        mSearchEt.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void setUpRecyclerView() {

        searchAdapter = new SearchAdapter(userKeyList, userList);
        groupsFragmentAdapter = new GroupsFragmentAdapter(userList);

        mSearchRecyclerView = findViewById(R.id.result_list);
        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSearchRecyclerView.setAdapter(groupsFragmentAdapter);
    }

    private void getFriendsList() {

        mFirestore.collection(Constants.CONTACTS_REFERENCE)
                .document(mUser.getUid())
                .collection("Friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            userList.clear();

                            for (final DocumentSnapshot document : task.getResult()) {

                                if (document != null && document.exists()) {

                                    mFirestore.collection(Constants.USERS_REFERENCE)
                                            .document(document.getId())
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful()) {

                                                userList.add(task.getResult().toObject(User.class));
                                                groupsFragmentAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
    }

    private void fireStoreUserSearch(final String userSearch) {

        Query userNameQuery = mFirestore
                .collection(Constants.USERS_REFERENCE)
                .orderBy("username")
                .startAt(userSearch)
                .endAt(userSearch + "\uf8ff");

        userNameQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshot, FirebaseFirestoreException e) {

                if (snapshot != null) {

                    userKeyList.clear();
                    userList.clear();

                    for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {

                        if (documentSnapshot != null && documentSnapshot.exists()) {

                            userKeyList.add(documentSnapshot.getId());
                            userList.add(documentSnapshot.toObject(User.class));
                            mErrorText.setVisibility(View.GONE);
                        } else {

                            break;
                        }
                    }

                    searchAdapter.notifyDataSetChanged();

                } else {

                    userKeyList.clear();
                    userList.clear();
                    searchAdapter.notifyDataSetChanged();
                    mErrorText.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
