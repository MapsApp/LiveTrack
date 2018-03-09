package com.sarthak.trackit.trackit.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.utils.CircleTransform;
import com.squareup.picasso.Picasso;

/**
 * Created by karan on 3/9/2018.
 */

public class GroupFriendsAdapter extends RecyclerView.Adapter<GroupFriendsAdapter.GroupFriendsViewHolder> {

    private setOnGroupFriendClickListener mListener;

    public GroupFriendsAdapter(setOnGroupFriendClickListener mListener) {
        this.mListener = mListener;
    }

    public interface setOnGroupFriendClickListener{
        void OnGroupFriendItemClicked(View view, int position);
    }

    @Override
    public GroupFriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_friends,parent,false);
        return new GroupFriendsAdapter.GroupFriendsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GroupFriendsViewHolder holder, int position) {
        String[] names=holder.itemView.getContext().getResources().getStringArray(R.array.names);

        Picasso.with(holder.itemView.getContext())
                .load("https://www.w3schools.com/css/trolltunga.jpg")
                .transform(new CircleTransform())
                .into(holder.imgGroupFriend);

        holder.txtGroupFriendStatus.setText("Active");

        for(int i=0;i<=position;i++){
            holder.txtGroupFriendName.setText(names[i]);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class GroupFriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txtGroupFriendName,txtGroupFriendStatus;
        ImageView imgGroupFriend;

        private GroupFriendsViewHolder(View itemView) {
            super(itemView);

            txtGroupFriendName=itemView.findViewById(R.id.text_group_friend_name);
            txtGroupFriendStatus=itemView.findViewById(R.id.text_group_friend_username);

            imgGroupFriend=itemView.findViewById(R.id.image_group_friend);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.OnGroupFriendItemClicked(v,getAdapterPosition());
        }
    }
}
