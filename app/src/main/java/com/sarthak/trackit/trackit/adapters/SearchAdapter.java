package com.sarthak.trackit.trackit.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.CircleTransform;
import com.sarthak.trackit.trackit.utils.Constants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    // If 0, no request sent/received.
    // If 1, request sent.
    // If 2, request received.
    // If 3, friends.
    int requestType = 0;
    int flag = 0;

    private ArrayList<String> userKeyList = new ArrayList<>();
    private ArrayList<User> userList = new ArrayList<>();

    private User mCurrentUser;
    private FirebaseUser mUser;


    public SearchAdapter(ArrayList<String> userKeyList, ArrayList<User> userList) {

        this.userKeyList = userKeyList;
        this.userList = userList;

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (!mUser.isAnonymous())
            getCurrentUserObject();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);

        return new SearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {

        String userKey = userKeyList.get(holder.getAdapterPosition());
        User mUser = userList.get(holder.getAdapterPosition());

        holder.bindView(userKey, mUser);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void getCurrentUserObject() {

        FirebaseFirestore.getInstance().collection(Constants.USERS_REFERENCE).document(mUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    mCurrentUser = task.getResult().toObject(User.class);
                }
            }
        });
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        String userKey;
        User user;

        private TextView mUserNameTv, mDisplayNameTv;
        private Button mRequestBtn, mCancelBtn;
        private ImageButton mSearchExpandBtn;
        private ImageView mSearchUserIv;
        private ProgressBar progressBar, progressBarRequest;
        private FrameLayout searchOptionsLayout;

        private FirebaseFirestore mFirestore;
        private FirebaseUser mUser;

        SearchViewHolder(View view) {

            super(view);

            mFirestore = FirebaseFirestore.getInstance();
            mUser = FirebaseAuth.getInstance().getCurrentUser();

            mUserNameTv = view.findViewById(R.id.text_search_username);
            mDisplayNameTv = view.findViewById(R.id.text_search_name);
            mRequestBtn = view.findViewById(R.id.button_search_add_friend);
            mCancelBtn = view.findViewById(R.id.button_cancel);
            mSearchUserIv = view.findViewById(R.id.image_search);
            mSearchExpandBtn = view.findViewById(R.id.button_search_expand);
            searchOptionsLayout = view.findViewById(R.id.search_options_layout);

            progressBar = view.findViewById(android.R.id.progress);
            progressBarRequest = view.findViewById(R.id.progress_bar_request);

            mSearchExpandBtn.setOnClickListener(this);
            mRequestBtn.setOnClickListener(this);
            mCancelBtn.setOnClickListener(this);

            mRequestBtn.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.button_search_add_friend:

                    switch (requestType) {

                        case 0:

                            sendFriendRequest();
                            break;

                        case 1:

                            cancelRequest();
                            break;

                        case 2:

                            acceptRequest();
                            break;
                    }
                    break;

                case R.id.button_cancel:

                    switch (requestType) {

                        case 2:

                            declineRequest();
                            break;
                        case 3:

                            unFriend();
                            break;
                    }
                    break;

                case R.id.button_search_expand:

                    switch (searchOptionsLayout.getVisibility()) {

                        case View.INVISIBLE:

                        case View.GONE:

                            checkForRequest();

                            searchOptionsLayout.setVisibility(View.VISIBLE);

                            mSearchExpandBtn.startAnimation(AnimationUtils.loadAnimation(itemView.getContext(), R.anim.rotate_forward));

                            mSearchExpandBtn.startAnimation(AnimationUtils.loadAnimation(itemView.getContext(), R.anim.rotate_forward));

                            break;

                        case View.VISIBLE:

                            searchOptionsLayout.setVisibility(View.GONE);

                            mSearchExpandBtn.startAnimation(AnimationUtils.loadAnimation(itemView.getContext(), R.anim.rotate_backward));

                            mSearchExpandBtn.startAnimation(AnimationUtils.loadAnimation(itemView.getContext(), R.anim.rotate_backward));

                            break;
                    }
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {

            switch (v.getId()) {

                case R.id.button_search_add_friend:

                    Toast.makeText(itemView.getContext(), "Long click.", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }

        void bindView(String userKey, User user) {

            this.userKey = userKey;
            this.user = user;

            mDisplayNameTv.setText(user.getDisplayName());
            mUserNameTv.setText(user.getUsername());

            progressBar.setVisibility(View.VISIBLE);
            Picasso.with(itemView.getContext())
                    .load("https://www.w3schools.com/css/trolltunga.jpg")
                    .transform(new CircleTransform())
                    .into(mSearchUserIv, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
        }

        private void checkForRequest() {

            Log.d("oil","Checking...");
            flag = 0;

            mFirestore
                    .collection(Constants.CONTACTS_REFERENCE)
                    .document(mUser.getUid())
                    .collection("Sent")
                    .document(userKey)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {

                                DocumentSnapshot document = task.getResult();

                                if (document != null && document.exists()) {

                                    requestType = 1;
                                    mRequestBtn.setText("Cancel Request");
                                    mRequestBtn.setEnabled(true);
                                    mCancelBtn.setEnabled(true);
                                    progressBarRequest.setVisibility(View.GONE);
                                } else {

                                    flag = flag + 1;
                                    if (flag == 6) {

                                        mRequestBtn.setEnabled(true);
                                        mCancelBtn.setEnabled(true);
                                        progressBarRequest.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    });

            mFirestore
                    .collection(Constants.CONTACTS_REFERENCE)
                    .document(mUser.getUid())
                    .collection("Received")
                    .document(userKey)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {

                                DocumentSnapshot document = task.getResult();

                                if (document != null && document.exists()) {

                                    requestType = 2;
                                    mRequestBtn.setText("Accept Request");
                                    mCancelBtn.setText("Decline Request");
                                    mRequestBtn.setEnabled(true);
                                    mCancelBtn.setEnabled(true);
                                    progressBarRequest.setVisibility(View.GONE);
                                } else {

                                    flag = flag + 2;
                                    if (flag == 6) {

                                        mRequestBtn.setEnabled(true);
                                        mCancelBtn.setEnabled(true);
                                        progressBarRequest.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    });

            mFirestore
                    .collection(Constants.CONTACTS_REFERENCE)
                    .document(mUser.getUid())
                    .collection("Friends")
                    .document(userKey)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {

                                DocumentSnapshot document = task.getResult();

                                if (document != null && document.exists()) {

                                    requestType = 3;
                                    mRequestBtn.setText("Unfriend");
                                    mRequestBtn.setEnabled(true);
                                    mCancelBtn.setEnabled(true);
                                    progressBarRequest.setVisibility(View.GONE);
                                } else {

                                    flag = flag + 3;
                                    if (flag == 6) {

                                        mRequestBtn.setEnabled(true);
                                        mCancelBtn.setEnabled(true);
                                        progressBarRequest.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    });
        }

        private void sendFriendRequest() {

            mRequestBtn.setEnabled(false);
            mCancelBtn.setEnabled(false);
            progressBarRequest.setVisibility(View.VISIBLE);

            Long time = System.currentTimeMillis() / 1000;
            final String timestamp = time.toString();

            final Map<String, Object> timeMap = new HashMap<>();
            timeMap.put("timestamp", timestamp);

            mFirestore.collection(Constants.CONTACTS_REFERENCE).document(mUser.getUid()).collection("Sent").document(userKey).set(timeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        mFirestore.collection(Constants.CONTACTS_REFERENCE).document(userKey).collection("Received").document(mUser.getUid()).set(timeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    requestType = 1;
                                    mRequestBtn.setEnabled(true);
                                    mRequestBtn.setText("Cancel Request");
                                    mCancelBtn.setEnabled(true);
                                    progressBarRequest.setVisibility(View.GONE);
                                    Toast.makeText(itemView.getContext(), "Request sent.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }

        private void acceptRequest() {

            mRequestBtn.setEnabled(false);
            mCancelBtn.setEnabled(false);
            progressBarRequest.setVisibility(View.VISIBLE);

            Long time = System.currentTimeMillis() / 1000;
            final String timestamp = time.toString();

            final Map<String, Object> timeMap = new HashMap<>();
            timeMap.put("timestamp", timestamp);

            mFirestore.collection(Constants.CONTACTS_REFERENCE).document(mUser.getUid()).collection("Friends").document(userKey).set(timeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        mFirestore.collection(Constants.CONTACTS_REFERENCE).document(userKey).collection("Friends").document(mUser.getUid()).set(timeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    mFirestore.collection(Constants.CONTACTS_REFERENCE).document(mUser.getUid()).collection("Received").document(userKey).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                mFirestore.collection(Constants.CONTACTS_REFERENCE).document(userKey).collection("Sent").document(mUser.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {

                                                            requestType = 3;
                                                            mRequestBtn.setEnabled(true);
                                                            mRequestBtn.setText("Unfriend");
                                                            mCancelBtn.setEnabled(true);
                                                            mCancelBtn.setText("Cancel");
                                                            progressBarRequest.setVisibility(View.GONE);
                                                            Toast.makeText(itemView.getContext(), "Added as friends.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }

        private void declineRequest() {

            mRequestBtn.setEnabled(false);
            mCancelBtn.setEnabled(false);
            progressBarRequest.setVisibility(View.VISIBLE);

            mFirestore.collection(Constants.CONTACTS_REFERENCE).document(mUser.getUid()).collection("Received").document(userKey).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        mFirestore.collection(Constants.CONTACTS_REFERENCE).document(userKey).collection("Sent").document(mUser.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    requestType = 0;
                                    mRequestBtn.setEnabled(true);
                                    mRequestBtn.setText("Add Friend");
                                    mCancelBtn.setEnabled(true);
                                    mCancelBtn.setText("Cancel");
                                    progressBarRequest.setVisibility(View.GONE);
                                    Toast.makeText(itemView.getContext(), "Request declined.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }

        private void cancelRequest() {

            mRequestBtn.setEnabled(false);
            mCancelBtn.setEnabled(false);
            progressBarRequest.setVisibility(View.VISIBLE);

            mFirestore.collection(Constants.CONTACTS_REFERENCE).document(mUser.getUid()).collection("Sent").document(userKey).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        mFirestore.collection(Constants.CONTACTS_REFERENCE).document(userKey).collection("Received").document(mUser.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    requestType = 0;
                                    mRequestBtn.setEnabled(true);
                                    mRequestBtn.setText("Add Friend");
                                    mCancelBtn.setEnabled(true);
                                    progressBarRequest.setVisibility(View.GONE);
                                    Toast.makeText(itemView.getContext(), "Request cancelled.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }

        private void unFriend() {

            mRequestBtn.setEnabled(false);
            mCancelBtn.setEnabled(false);
            progressBarRequest.setVisibility(View.VISIBLE);

            mFirestore.collection(Constants.CONTACTS_REFERENCE).document(mUser.getUid()).collection("Friends").document(userKey).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        mFirestore.collection(Constants.CONTACTS_REFERENCE).document(userKey).collection("Friends").document(mUser.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    // TODO: Prompt confirmation
                                    requestType = 0;
                                    mRequestBtn.setEnabled(true);
                                    mRequestBtn.setText("Add Friend");
                                    mCancelBtn.setEnabled(true);
                                    progressBarRequest.setVisibility(View.GONE);
                                    Toast.makeText(itemView.getContext(), "Unfriend.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
    }
}