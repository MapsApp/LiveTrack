package com.sarthak.trackit.trackit.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

public class SearchActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {

    private ArrayList<User> searchResultArrayList = new ArrayList<>();

    private ImageButton mSearchBtn;
    private EditText mSearchEt;
    private RecyclerView mSearchRecyclerView;

    private SearchAdapter searchAdapter;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mFirestore = FirebaseFirestore.getInstance();

        initView();

        mSearchEt.addTextChangedListener(this);
        mSearchBtn.setOnClickListener(this);
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

        searchAdapter = new SearchAdapter(SearchActivity.this, searchResultArrayList);

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

                    searchResultArrayList.clear();

                    for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {

                        if (documentSnapshot != null && documentSnapshot.exists()) {

                            searchResultArrayList.add(documentSnapshot.toObject(User.class));
                        } else {

                            break;
                        }
                    }

                    searchAdapter.notifyDataSetChanged();
                } else {

                    searchResultArrayList.clear();
                    searchAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
