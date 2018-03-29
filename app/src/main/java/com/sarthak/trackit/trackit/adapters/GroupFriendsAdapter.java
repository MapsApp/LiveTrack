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

import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.CircleTransform;
import com.sarthak.trackit.trackit.utils.RecyclerViewItemClickedListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupFriendsAdapter extends RecyclerView.Adapter<GroupFriendsAdapter.GroupFriendsViewHolder> {

    private ArrayList<String> adminStatusList = new ArrayList<>();
    private ArrayList<User> mGroupMembersList = new ArrayList<>();

    private ItemClickListener mListener;

    public GroupFriendsAdapter(ItemClickListener listener) {
        this.mListener = listener;
        Log.d("yayy", "");
    }

    public GroupFriendsAdapter(ArrayList<User> mGroupMembersList, ArrayList<String> adminStatusList) {

        this.mGroupMembersList = mGroupMembersList;
        this.adminStatusList = adminStatusList;
    }

    public void setOnRecyclerViewItemClickListener(ItemClickListener listener) {
        this.mListener = listener;
        Log.d("yayy", "");
    }

    @NonNull
    @Override
    public GroupFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_friends, parent, false);
        return new GroupFriendsAdapter.GroupFriendsViewHolder(itemView);
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
        holder.txtGroupFriendName.setText(mGroupMembersList.get(position).getDisplayName());

        if (adminStatusList.get(holder.getAdapterPosition()).equals("true")) {
            holder.mAdminTv.setVisibility(View.VISIBLE);
        } else {
            holder.mAdminTv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mGroupMembersList.size();
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
            mAdminTv = itemView.findViewById(R.id.admin_tv);

            progressBar = itemView.findViewById(android.R.id.progress);

            imgGroupFriend = itemView.findViewById(R.id.image_group_friend);

            itemView.setClickable(true);
            itemView.setFocusable(true);
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
