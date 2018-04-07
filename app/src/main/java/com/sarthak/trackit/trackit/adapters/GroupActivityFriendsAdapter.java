package com.sarthak.trackit.trackit.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.CircleTransform;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupActivityFriendsAdapter extends RecyclerView.Adapter<GroupActivityFriendsAdapter.GroupFriendsViewHolder> {

    private ArrayList<String> adminStatusList = new ArrayList<>();
    private ArrayList<String> memberKeyList = new ArrayList<>();
    private ArrayList<User> groupMemberList = new ArrayList<>();

    private ItemClickListener mListener;

    public GroupActivityFriendsAdapter(ArrayList<User> groupMemberList, ArrayList<String> memberKeyList) {

        this.groupMemberList = groupMemberList;
        this.memberKeyList = memberKeyList;
        this.adminStatusList = adminStatusList;
    }

    public void setOnRecyclerViewItemClickListener(ItemClickListener listener) {
        this.mListener = listener;
    }

    public void setAdminList(ArrayList<String> adminList) {
        this.adminStatusList = adminList;
    }

    @NonNull
    @Override
    public GroupFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_friends, parent, false);
        return new GroupActivityFriendsAdapter.GroupFriendsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupFriendsViewHolder holder, int position) {
        holder.progressBar.setVisibility(View.VISIBLE);
        Picasso.with(holder.itemView.getContext())
                .load("https://www.w3schools.com/css/trolltunga.jpg")
                .transform(new CircleTransform())
                .into(holder.imgGroupFriend, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.progressBar.setVisibility(View.INVISIBLE);
                    }
                });

        holder.txtGroupFriendStatus.setText("Active");
        holder.txtGroupFriendName.setText(groupMemberList.get(position).getDisplayName());

        Log.d("oil", ""+adminStatusList.size());
        if (adminStatusList != null) {

            for (int i = 0; i < adminStatusList.size(); i++) {

                if (memberKeyList.get(holder.getAdapterPosition()).equals(adminStatusList.get(i))) {
                    holder.mAdminTv.setVisibility(View.VISIBLE);
                } else {
                    holder.mAdminTv.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return groupMemberList.size();
    }

    public class GroupFriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtGroupFriendName, txtGroupFriendStatus;
        TextView mAdminTv;
        ImageView imgGroupFriend;
        ProgressBar progressBar;

        private GroupFriendsViewHolder(View itemView) {
            super(itemView);

            txtGroupFriendName = itemView.findViewById(R.id.text_group_friend_name);
            txtGroupFriendStatus = itemView.findViewById(R.id.text_group_friend_username);
            mAdminTv = itemView.findViewById(R.id.text_admin);

            progressBar = itemView.findViewById(android.R.id.progress);

            imgGroupFriend = itemView.findViewById(R.id.image_group_friend);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.itemClicked(v, getAdapterPosition());
        }
    }

    public interface ItemClickListener {

        void itemClicked(View v, int pos);
    }
}
