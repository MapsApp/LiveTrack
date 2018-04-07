package com.sarthak.trackit.trackit.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.utils.CircleTransform;
import com.sarthak.trackit.trackit.utils.RecyclerViewItemClickedListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserGroupFragmentAdapter extends RecyclerView.Adapter<UserGroupFragmentAdapter.UserGroupViewHolder> {

    private ArrayList<String> userList = new ArrayList<>();

    private RecyclerViewItemClickedListener mListener;

    public UserGroupFragmentAdapter(ArrayList<String> userList) {

        this.userList = userList;
    }

    public void setOnItemClickListener(RecyclerViewItemClickedListener recyclerViewItemClickedListener) {

        this.mListener = recyclerViewItemClickedListener;
    }

    @NonNull
    @Override
    public UserGroupFragmentAdapter.UserGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_fragment_list, parent, false);

        return new UserGroupFragmentAdapter.UserGroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserGroupFragmentAdapter.UserGroupViewHolder holder, int position) {

        holder.bindView(userList.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserGroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mDisplayNameTv, mUsernameTv;
        private ImageView mGroupDpIv;

        public UserGroupViewHolder(View itemView) {

            super(itemView);

            mDisplayNameTv = itemView.findViewById(R.id.text_group_name);
            mUsernameTv = itemView.findViewById(R.id.text_group_members);
            mGroupDpIv=itemView.findViewById(R.id.image_group_dp);
            itemView.setOnClickListener(this);
        }

        void bindView(String name) {

            mDisplayNameTv.setText(name);
            //mUsernameTv.setText(user.getUsername());
            Picasso.with(itemView.getContext())
                    .load("https://www.w3schools.com/css/trolltunga.jpg")
                    .transform(new CircleTransform())
                    .into(mGroupDpIv);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClicked(v, getAdapterPosition());
        }
    }
}
