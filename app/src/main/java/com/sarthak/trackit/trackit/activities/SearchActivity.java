package com.sarthak.trackit.trackit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.adapters.SearchAdapter;
import com.sarthak.trackit.trackit.model.UserSearchResult;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private ImageButton searchButton;
    private EditText searchText;
    private RecyclerView searchRecyclerView;
    private FirebaseFirestore db;
    private String searchRawData;
    private ArrayList<UserSearchResult> searchResultArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
    }

    private void init() {
        searchButton = findViewById(R.id.SearchButton);
        searchRecyclerView = findViewById(R.id.SearchResultList);
        searchText = findViewById(R.id.searchText);
        db = FirebaseFirestore.getInstance();
    }

    private void getSearchRawData() {
        searchRawData = searchText.getText().toString();
    }

    public void StartSearch(View v) {
        getSearchRawData();
        readDatabase();
        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        searchRecyclerView.setLayoutManager(manager);
        searchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        searchRecyclerView.setAdapter(new SearchAdapter(searchResultArrayList));
    }

    private void readDatabase() {
        db.collection("Users")
                .whereGreaterThan("username", searchRawData)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        searchResultArrayList.clear();
                        for (DocumentSnapshot snapshot : documentSnapshots) {
                            String userName = snapshot.getString("username");
                            String displayName = snapshot.getString("displayName");
                            Log.d("db", userName + "  ,  " + displayName);
                            UserSearchResult userSearchResult = new UserSearchResult(userName, displayName);
                            searchResultArrayList.add(userSearchResult);
                        }
                    }
                });
    }
}
