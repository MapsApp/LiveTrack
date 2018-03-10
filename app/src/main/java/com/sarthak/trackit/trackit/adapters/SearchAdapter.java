package com.sarthak.trackit.trackit.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.model.UserSearchResult;

import java.util.ArrayList;

/**
 * Created by hp on 3/10/2018.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.mViewHolder> {

    private ArrayList<UserSearchResult> searchResultArrayList = new ArrayList<>();

    public class mViewHolder extends RecyclerView.ViewHolder{
        public TextView userName , displayName;
        public mViewHolder(View view){
            super(view);
            userName = view.findViewById(R.id.username);
            displayName = view.findViewById(R.id.displayname);
        }

    }

    public SearchAdapter(ArrayList<UserSearchResult> searchResultArrayList){
        this.searchResultArrayList=searchResultArrayList;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_inflate_item, parent, false);

        return new mViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.mViewHolder holder, int position) {
        UserSearchResult userSearchResult = searchResultArrayList.get(position);
        holder.displayName.setText(userSearchResult.getDisplayName());
        holder.userName.setText(userSearchResult.getUserName());
    }



    @Override
    public int getItemCount() {
        return searchResultArrayList.size();
    }
}
