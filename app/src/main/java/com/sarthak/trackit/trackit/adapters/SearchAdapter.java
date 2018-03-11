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

import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.model.UserSearchResult;
import com.sarthak.trackit.trackit.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by hp on 3/10/2018.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.mViewHolder> {

    private ArrayList<UserSearchResult> searchResultArrayList = new ArrayList<>();

    public class mViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener
            , View.OnClickListener {

        ImageView imgSuggestionImage;
        TextView txtSuggestionUserName, txtSuggestionDisplayName;
        LinearLayout optionsLayout;
        ImageButton btnRemoveFriend, btnCreateGroup, btnExpand, btnDummy;

        public mViewHolder(View view) {
            super(view);
            txtSuggestionUserName = view.findViewById(R.id.text_suggestion_username);
            txtSuggestionDisplayName = view.findViewById(R.id.text_suggestion_display_name);
            imgSuggestionImage=view.findViewById(R.id.image_suggestion);

            optionsLayout = itemView.findViewById(R.id.suggestions_options_layout);

            btnRemoveFriend = itemView.findViewById(R.id.button_remove_friend);
            btnCreateGroup = itemView.findViewById(R.id.button_create_group);
            btnDummy = itemView.findViewById(R.id.button_dummy);

            btnExpand = itemView.findViewById(R.id.button_suggestion_expand);

            btnRemoveFriend.setOnLongClickListener(this);
            btnCreateGroup.setOnLongClickListener(this);
            btnDummy.setOnLongClickListener(this);

            btnExpand.setOnClickListener(this);

            btnRemoveFriend.setOnClickListener(this);
            btnCreateGroup.setOnClickListener(this);
            btnDummy.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.parent_layout_friends:
                    break;

                case R.id.button_suggestion_expand:

                    switch (optionsLayout.getVisibility()) {
                        case View.INVISIBLE:
                        case View.GONE:
                            optionsLayout.setVisibility(View.VISIBLE);
                            btnExpand.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward));
                            break;
                        case View.VISIBLE:
                            optionsLayout.setVisibility(View.GONE);
                            btnExpand.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward));
                            break;
                    }
                    break;
            }
        }
    }

    public SearchAdapter(ArrayList<UserSearchResult> searchResultArrayList) {
        this.searchResultArrayList = searchResultArrayList;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search, parent, false);

        return new mViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.mViewHolder holder, int position) {
        UserSearchResult userSearchResult = searchResultArrayList.get(position);
        holder.txtSuggestionDisplayName.setText(userSearchResult.getDisplayName());
        holder.txtSuggestionUserName.setText(userSearchResult.getUserName());
        Picasso.with(holder.itemView.getContext())
                .load("https://www.w3schools.com/css/trolltunga.jpg")
                .transform(new CircleTransform())
                .into(holder.imgSuggestionImage);
    }


    @Override
    public int getItemCount() {
        return searchResultArrayList.size();
    }
}
