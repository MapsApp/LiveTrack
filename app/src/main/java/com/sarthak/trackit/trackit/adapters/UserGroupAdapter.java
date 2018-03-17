package com.sarthak.trackit.trackit.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.model.User;

import java.util.ArrayList;

public class UserGroupAdapter extends RecyclerView.Adapter<UserGroupAdapter.UserGroupViewHolder> {

    private ArrayList<String> userList = new ArrayList<>();

    public UserGroupAdapter(ArrayList<String> userList) {

        this.userList = userList;
    }

    @NonNull
    @Override
    public UserGroupAdapter.UserGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_fragment_list, parent, false);

        return new UserGroupAdapter.UserGroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserGroupAdapter.UserGroupViewHolder holder, int position) {

        holder.bindView(userList.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserGroupViewHolder extends RecyclerView.ViewHolder {

        private TextView mDisplayNameTv, mUsernameTv;

        public UserGroupViewHolder(View itemView) {

            super(itemView);

            mDisplayNameTv = itemView.findViewById(R.id.text_group_display_name);
            mUsernameTv = itemView.findViewById(R.id.text_group_username);
        }

        void bindView(String name) {

            mDisplayNameTv.setText(name);
            //mUsernameTv.setText(user.getUsername());
        }
    }
}
