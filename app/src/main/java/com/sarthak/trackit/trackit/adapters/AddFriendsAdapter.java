package com.sarthak.trackit.trackit.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.RecyclerViewItemClickedListener;

import java.util.ArrayList;

public class AddFriendsAdapter extends RecyclerView.Adapter<AddFriendsAdapter.AddFriendsViewHolder> {

    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<Integer> selectList = new ArrayList<>();

    private RecyclerViewItemClickedListener mListener;

    public AddFriendsAdapter(ArrayList<User> userList) {

        this.userList = userList;
    }

    public void setOnItemClickListener(RecyclerViewItemClickedListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public AddFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_fragment_list, parent, false);

        return new AddFriendsAdapter.AddFriendsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AddFriendsViewHolder holder, final int position) {
        holder.bindView(userList.get(holder.getAdapterPosition()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClicked(v, holder.getAdapterPosition());

                if (!selectList.contains(position)) {
                    selectList.add(position);
                    holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.md_blue_grey_50));
                } else {
                    selectList.remove(selectList.indexOf(position));
                    holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.md_grey_50));
                }
                Log.e("oila", selectList.size()+"");
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class AddFriendsViewHolder extends RecyclerView.ViewHolder {

        private TextView mDisplayNameTv, mUsernameTv;
        private ImageView mGroupDpIv;

        public AddFriendsViewHolder(View itemView) {

            super(itemView);

            mDisplayNameTv = itemView.findViewById(R.id.text_group_name);
            mUsernameTv = itemView.findViewById(R.id.text_group_members);
        }

        void bindView(User user) {

            mDisplayNameTv.setText(user.getDisplayName());
            mUsernameTv.setText(user.getUsername());
        }
    }
}
