package com.sarthak.trackit.trackit.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.adapters.SearchAdapter;
import com.sarthak.trackit.trackit.adapters.SearchCursorAdapter;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.Constants;

import java.util.ArrayList;

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    private ArrayList<String> userKeyList = new ArrayList<>();
    private ArrayList<User> userList = new ArrayList<>();
    Menu menu;
    SearchView searchView;
    private EditText mSearchEt;
    private RecyclerView mSearchRecyclerView;
    private SearchCursorAdapter searchCursorAdapter;
    private SearchAdapter searchAdapter;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setUpToolbar(this);

        mFirestore = FirebaseFirestore.getInstance();
        //searchCursorAdapter = new SearchCursorAdapter(this, , true);

        mToolbar.inflateMenu(R.menu.home);
        menu=mToolbar.getMenu();
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchEt = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        mSearchEt.setHint("Search..");
        mSearchEt.setHintTextColor(Color.DKGRAY);
        mSearchEt.setTextColor(getResources().getColor(R.color.colorPrimary));
        //searchView.setSuggestionsAdapter();
        searchView.animate();
        mSearchRecyclerView = findViewById(R.id.result_list);

        initRecyclerView();
    }

    @Override
    protected int getToolbarID() {
        return R.id.search_activity_toolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.activity_search, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.isSubmitButtonEnabled();
        ImageView closeButton = searchView.findViewById(R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.ic_close);

        searchView.setOnQueryTextListener(this);

        //Sets the search icon in the Toolbar
        //Also, its behaviour is defined
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:

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

            firestoreUserSearch(searchText);
        } else {

            firestoreUserSearch(" ");
        }

        return false;
    }

    private void initRecyclerView() {

        searchAdapter = new SearchAdapter(SearchActivity.this, userKeyList, userList);

        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSearchRecyclerView.setAdapter(searchAdapter);
    }

    private void firestoreUserSearch(final String userSearch) {

        Query searchQuery = mFirestore.collection(Constants.USERS_REFERENCE).orderBy("username").startAt(userSearch).endAt(userSearch + "\uf8ff");

        searchQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshot, FirebaseFirestoreException e) {

                if (snapshot != null) {

                    userKeyList.clear();
                    userList.clear();

                    for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {

                        if (documentSnapshot != null && documentSnapshot.exists()) {

                            userKeyList.add(documentSnapshot.getId());
                            userList.add(documentSnapshot.toObject(User.class));
                        } else {

                            break;
                        }
                    }

                    searchAdapter.notifyDataSetChanged();

                } else {

                    userKeyList.clear();
                    userList.clear();
                    searchAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
