package com.sarthak.trackit.trackit.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.model.User;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {

    private ArrayList<User> searchResultArrayList = new ArrayList<>();
    private Context mContext;

    public SearchAdapter(Context context, ArrayList<User> searchResultArrayList){

        this.mContext = context;
        this.searchResultArrayList=searchResultArrayList;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_inflate_item, parent, false);

        return new SearchViewHolder(mContext, itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {

        User userSearchResult = searchResultArrayList.get(position);

        holder.mDisplayNameTv.setText(userSearchResult.getDisplayName());
        holder.mUserNameTv.setText(userSearchResult.getUsername());
    }

    @Override
    public int getItemCount() {
        return searchResultArrayList.size();
    }
}
