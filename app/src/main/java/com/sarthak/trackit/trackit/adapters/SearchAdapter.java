package com.sarthak.trackit.trackit.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private ArrayList<String> userKeyList = new ArrayList<>();
    private ArrayList<User> userList = new ArrayList<>();

    private Context mContext;

    public SearchAdapter(Context context, ArrayList<String> userKeyList, ArrayList<User> userList) {

        this.mContext = context;
        this.userKeyList = userKeyList;
        this.userList = userList;
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

    public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        String userKey;

        private TextView mUserNameTv, mDisplayNameTv;
        private Button mRequestBtn, mCancelBtn;
        private ImageButton mSearchExpandBtn;
        private ImageView mSearchUserIv;
        private ProgressBar progressBar;
        private LinearLayout searchOptionsLayout;

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

            progressBar=view.findViewById(android.R.id.progress);

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

                            checkForRequest(searchOptionsLayout);

                            searchOptionsLayout.setVisibility(View.VISIBLE);

                            mSearchExpandBtn.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotate_forward));

                            mSearchExpandBtn.startAnimation(AnimationUtils.loadAnimation(itemView.getContext(), R.anim.rotate_forward));

                            break;

                        case View.VISIBLE:

                            searchOptionsLayout.setVisibility(View.GONE);

                            mSearchExpandBtn.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotate_backward));

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

                    Toast.makeText(itemView.getContext(), "Request sent.", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }

        void bindView(String userKey, User user) {

            this.userKey = userKey;

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

        private void checkForRequest(final LinearLayout layout) {

            mFirestore.collection(Constants.CONTACTS_REFERENCE).document(mUser.getUid()).collection("Sent").document(userKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {

                        DocumentSnapshot document = task.getResult();

                        if (document != null && document.exists()) {

                            requestType = 1;
                            layout.setBackgroundColor(mContext.getResources().getColor(R.color.md_green_100));
                        }
                    }
                }
            });

            mFirestore.collection(Constants.CONTACTS_REFERENCE).document(mUser.getUid()).collection("Received").document(userKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {

                        DocumentSnapshot document = task.getResult();

                        if (document != null && document.exists()) {

                            requestType = 2;
                            layout.setBackgroundColor(mContext.getResources().getColor(R.color.md_blue_100));
                        }
                    }
                }
            });

            mFirestore.collection(Constants.CONTACTS_REFERENCE).document(mUser.getUid()).collection("Friends").document(userKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {

                        DocumentSnapshot document = task.getResult();

                        if (document != null && document.exists()) {

                            requestType = 3;
                            layout.setBackgroundColor(mContext.getResources().getColor(R.color.md_deep_purple_100));
                        }
                    }
                }
            });
        }

        private void sendFriendRequest() {

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
                                    Toast.makeText(mContext, "Request sent.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }

        private void acceptRequest() {

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
                                                            Toast.makeText(mContext, "Added as friends.", Toast.LENGTH_SHORT).show();
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

            mFirestore.collection(Constants.CONTACTS_REFERENCE).document(mUser.getUid()).collection("Received").document(userKey).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        mFirestore.collection(Constants.CONTACTS_REFERENCE).document(userKey).collection("Sent").document(mUser.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    requestType = 0;
                                    Toast.makeText(mContext, "Request declined.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }

        private void cancelRequest() {

            mFirestore.collection(Constants.CONTACTS_REFERENCE).document(mUser.getUid()).collection("Sent").document(userKey).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        mFirestore.collection(Constants.CONTACTS_REFERENCE).document(userKey).collection("Received").document(mUser.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    requestType = 0;
                                    Toast.makeText(mContext, "Request cancelled.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }

        private void unFriend() {

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
                                    Toast.makeText(mContext, "Unfriend.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
    }
}