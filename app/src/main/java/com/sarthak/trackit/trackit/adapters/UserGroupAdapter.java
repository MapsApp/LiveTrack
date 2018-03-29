package com.sarthak.trackit.trackit.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.utils.RecyclerViewItemClickedListener;

import java.util.ArrayList;

public class UserGroupAdapter extends RecyclerView.Adapter<UserGroupAdapter.UserGroupViewHolder> {

    private ArrayList<String> userList = new ArrayList<>();

    private RecyclerViewItemClickedListener mListener;

    public UserGroupAdapter(ArrayList<String> userList) {

        this.userList = userList;
    }

    public void setOnItemClickListener(RecyclerViewItemClickedListener recyclerViewItemClickedListener) {

        this.mListener = recyclerViewItemClickedListener;
    }

    @NonNull
    @Override
    public UserGroupAdapter.UserGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_fragment_list, parent, false);

        return new UserGroupAdapter.UserGroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserGroupAdapter.UserGroupViewHolder holder, int position) {

        holder.bindView(userList.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserGroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mDisplayNameTv, mUsernameTv;

        public UserGroupViewHolder(View itemView) {

            super(itemView);

            mDisplayNameTv = itemView.findViewById(R.id.text_group_display_name);
            mUsernameTv = itemView.findViewById(R.id.text_group_username);

            itemView.setOnClickListener(this);
        }

        void bindView(String name) {

            mDisplayNameTv.setText(name.substring(0, name.indexOf("+")));
            //mUsernameTv.setText(user.getUsername());
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClicked(v, getAdapterPosition());
        }
    }
}
