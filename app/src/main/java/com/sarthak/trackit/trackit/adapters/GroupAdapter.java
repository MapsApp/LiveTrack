package com.sarthak.trackit.trackit.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.model.User;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private ArrayList<User> userList = new ArrayList<>();

    public GroupAdapter(ArrayList<User> userList) {

        this.userList = userList;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_fragment_list, parent, false);

        return new GroupAdapter.GroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        holder.bindView(userList.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {

        private TextView mDisplayNameTv, mUsernameTv;
        private ImageView mGroupDpIv;

        public GroupViewHolder(View itemView) {

            super(itemView);

            mDisplayNameTv = itemView.findViewById(R.id.text_group_name);
            mUsernameTv = itemView.findViewById(R.id.text_group_username);
        }

        void bindView(User user) {

            mDisplayNameTv.setText(user.getDisplayName());
            mUsernameTv.setText(user.getUsername());
        }
    }
}
