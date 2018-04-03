package com.sarthak.trackit.trackit.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupCreateMembersAdapter extends RecyclerView.Adapter<GroupCreateMembersAdapter.GroupMembersHolder> {

    private ArrayList<User> mGroupList = new ArrayList<>();
    private setOnGroupMemberClicked mSetGroupMemberClicked;

    public GroupCreateMembersAdapter(ArrayList<User> mGroupList, setOnGroupMemberClicked mSetGroupMemberClicked) {
        this.mGroupList = mGroupList;
        this.mSetGroupMemberClicked = mSetGroupMemberClicked;
    }


    public interface setOnGroupMemberClicked {
        void onGroupMemberClicked(View v, int position);
    }

    @NonNull
    @Override
    public GroupMembersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_group_member, parent, false);

        return new GroupCreateMembersAdapter.GroupMembersHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMembersHolder holder, int position) {

        Picasso.with(holder.itemView.getContext())
                .load("https://www.w3schools.com/css/trolltunga.jpg")
                .transform(new CircleTransform())
                .into(holder.mGroupMemberImage);

        holder.mGroupMemberName.setText(mGroupList.get(position).getDisplayName());
    }

    @Override
    public int getItemCount() {
        return mGroupList.size();
    }

    public class GroupMembersHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mGroupMemberName;
        ImageView mGroupMemberImage;
        ImageButton mDeleteMemberBtn;

        public GroupMembersHolder(View itemView) {
            super(itemView);

            mGroupMemberName = itemView.findViewById(R.id.text_group_member_name);
            mGroupMemberImage = itemView.findViewById(R.id.image_group_member);
            mDeleteMemberBtn = itemView.findViewById(R.id.button_delete_member);

            mDeleteMemberBtn.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            mSetGroupMemberClicked.onGroupMemberClicked(v, getAdapterPosition());
        }
    }
}
