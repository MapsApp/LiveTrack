package com.sarthak.trackit.trackit.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sarthak.trackit.trackit.utils.CircleTransform;
import com.sarthak.trackit.trackit.R;
import com.squareup.picasso.Picasso;

/**
 * Created by karan on 3/9/2018.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {

    setOnFriendClickListener mListener;

    public FriendsAdapter(setOnFriendClickListener mListener) {
        this.mListener = mListener;
    }

    public interface setOnFriendClickListener{
        void OnFriendItemClicked(View view,int position);
    }

    @Override
    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends,parent,false);
        return new FriendsAdapter.FriendsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FriendsViewHolder holder, int position) {

        //Dummy Data
        String[] names=holder.itemView.getContext().getResources().getStringArray(R.array.names);
        String[] user_names=holder.itemView.getContext().getResources().getStringArray(R.array.user_names);

        Picasso.with(holder.itemView.getContext())
                .load("https://www.w3schools.com/css/trolltunga.jpg")
                .transform(new CircleTransform())
                .into(holder.imgFriend);

        for(int i=0;i<=position;i++){
            holder.txtFriendName.setText(names[i]);
            holder.txtFriendUserName.setText(user_names[i]);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txtFriendName,txtFriendUserName;
        ImageView imgFriend;

        private FriendsViewHolder(View itemView) {
            super(itemView);

            txtFriendName=itemView.findViewById(R.id.text_friend_name);
            txtFriendUserName=itemView.findViewById(R.id.text_friend_username);

            imgFriend=itemView.findViewById(R.id.image_friend);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.OnFriendItemClicked(v,getAdapterPosition());
        }
    }
}
