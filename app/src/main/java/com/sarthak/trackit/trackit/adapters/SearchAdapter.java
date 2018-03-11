package com.sarthak.trackit.trackit.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private ArrayList<User> searchResultArrayList = new ArrayList<>();

    public SearchAdapter(ArrayList<User> searchResultArrayList) {

        this.searchResultArrayList = searchResultArrayList;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);

        return new SearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {

        User userSearchResult = searchResultArrayList.get(position);

        holder.mDisplayNameTv.setText(userSearchResult.getDisplayName());
        holder.mUserNameTv.setText(userSearchResult.getUsername());

        Picasso.with(holder.itemView.getContext())
                .load("https://www.w3schools.com/css/trolltunga.jpg")
                .transform(new CircleTransform())
                .into(holder.mSearchUserIv);
    }

    @Override
    public int getItemCount() {
        return searchResultArrayList.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
            , View.OnLongClickListener {

        TextView mUserNameTv, mDisplayNameTv;
        private ImageButton mRequestBtn, mSearchExpandBtn;
        private ImageView mSearchUserIv;
        LinearLayout searchOptionsLayout;

        SearchViewHolder(View view) {

            super(view);

            mUserNameTv = view.findViewById(R.id.text_search_username);
            mDisplayNameTv = view.findViewById(R.id.text_search_name);
            mRequestBtn = view.findViewById(R.id.button_search_add_friend);
            mSearchUserIv = view.findViewById(R.id.image_search);
            mSearchExpandBtn = view.findViewById(R.id.button_search_expand);
            searchOptionsLayout = view.findViewById(R.id.search_options_layout);

            mSearchExpandBtn.setOnClickListener(this);
            mRequestBtn.setOnClickListener(this);

            mRequestBtn.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.button_search_add_friend:

                    Toast.makeText(itemView.getContext(), "Request sent.", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.button_search_expand:
                    switch (searchOptionsLayout.getVisibility()) {
                        case View.INVISIBLE:
                        case View.GONE:
                            searchOptionsLayout.setVisibility(View.VISIBLE);
                            mSearchExpandBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward));
                            break;
                        case View.VISIBLE:
                            searchOptionsLayout.setVisibility(View.GONE);
                            mSearchExpandBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward));
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
    }
}