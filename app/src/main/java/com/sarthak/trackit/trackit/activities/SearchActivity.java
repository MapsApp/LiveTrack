package com.sarthak.trackit.trackit.activities;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.adapters.SearchAdapter;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.Constants;

import java.util.ArrayList;

public class SearchActivity extends BaseActivity implements TextWatcher, View.OnClickListener {

    private ArrayList<String> userKeyList = new ArrayList<>();
    private ArrayList<User> userList = new ArrayList<>();

    private ImageButton mSearchBtn;
    private EditText mSearchEt;
    private RecyclerView mSearchRecyclerView;

    private SearchAdapter searchAdapter;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setUpToolbar(this);

        mFirestore = FirebaseFirestore.getInstance();

        initView();

        mSearchEt.addTextChangedListener(this);
        mSearchBtn.setOnClickListener(this);
    }

    @Override
    protected int getToolbarID() {
        return R.id.search_activity_toolbar;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.search_btn:

                String searchText = mSearchEt.getText().toString();
                firestoreUserSearch(searchText);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_search, menu);

        //Sets the search icon in the Toolbar
        //Also, its behaviour is defined
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.isSubmitButtonEnabled();
        searchView.animate();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_search:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        if (charSequence.length() != 0) {

            firestoreUserSearch(charSequence.toString());
        } else {

            firestoreUserSearch(" ");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private void initView () {

        mSearchBtn = findViewById(R.id.search_btn);
        mSearchRecyclerView = findViewById(R.id.result_list);
        mSearchEt = findViewById(R.id.search_field);

        initRecyclerView();
    }

    private void initRecyclerView (){

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
