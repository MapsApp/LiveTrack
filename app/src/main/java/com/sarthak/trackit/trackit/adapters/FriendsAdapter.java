package com.sarthak.trackit.trackit.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.utils.CircleTransform;
import com.squareup.picasso.Picasso;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {

    private setOnFriendClickListener mListener;

    private Context mContext;

    public FriendsAdapter(Context context, setOnFriendClickListener mListener) {

        this.mContext = context;
        this.mListener = mListener;
    }

    public interface setOnFriendClickListener {

        void OnFriendItemClicked(View view, int position);
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends, parent, false);

        return new FriendsAdapter.FriendsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position) {

        //Dummy Data
        String[] names = holder.itemView.getContext().getResources().getStringArray(R.array.names);
        String[] user_names = holder.itemView.getContext().getResources().getStringArray(R.array.user_names);

        Picasso.with(holder.itemView.getContext())
                .load("https://www.w3schools.com/css/trolltunga.jpg")
                .transform(new CircleTransform())
                .into(holder.imgFriend);

        for (int i = 0; i <= position; i++) {
            holder.txtFriendName.setText(names[i]);
            holder.txtFriendUserName.setText(user_names[i]);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txtFriendName, txtFriendUserName;
        ImageView imgFriend;
        LinearLayout optionsLayout;
        ImageButton btnRemoveFriend, btnCreateGroup, btnExpand, btnDummy;
        RelativeLayout parentLayout;

        private FriendsViewHolder(View itemView) {

            super(itemView);

            parentLayout = itemView.findViewById(R.id.parent_layout_friends);
            txtFriendName = itemView.findViewById(R.id.text_friend_name);
            txtFriendUserName = itemView.findViewById(R.id.text_friend_username);
            optionsLayout = itemView.findViewById(R.id.options_layout);

            btnRemoveFriend = itemView.findViewById(R.id.button_remove_friend);
            btnCreateGroup = itemView.findViewById(R.id.button_create_group);
            btnDummy = itemView.findViewById(R.id.button_dummy);

            btnExpand = itemView.findViewById(R.id.button_expand);

            imgFriend = itemView.findViewById(R.id.image_friend);

            btnRemoveFriend.setOnLongClickListener(this);
            btnCreateGroup.setOnLongClickListener(this);
            btnDummy.setOnLongClickListener(this);

            btnExpand.setOnClickListener(this);
            parentLayout.setOnClickListener(this);

            btnRemoveFriend.setOnClickListener(this);
            btnCreateGroup.setOnClickListener(this);
            btnDummy.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {


            switch (v.getId()) {
                case R.id.parent_layout_friends:
                    mListener.OnFriendItemClicked(v, getAdapterPosition());
                    break;

                case R.id.button_expand:

                    switch (optionsLayout.getVisibility()) {

                        case View.INVISIBLE:

                        case View.GONE:

                            optionsLayout.setVisibility(View.VISIBLE);

                            btnExpand.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotate_forward));

                            btnExpand.startAnimation(AnimationUtils.loadAnimation(itemView.getContext(),R.anim.rotate_forward));

                            break;

                        case View.VISIBLE:

                            optionsLayout.setVisibility(View.GONE);

                            btnExpand.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotate_backward));

                            btnExpand.startAnimation(AnimationUtils.loadAnimation(itemView.getContext(),R.anim.rotate_backward));

                            break;
                    }
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {

            switch (v.getId()) {

                case R.id.button_remove_friend:

                    Toast.makeText(itemView.getContext(), "Remove Friend Clicked", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.button_create_group:

                    Toast.makeText(itemView.getContext(), "Create Group Clicked", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.button_dummy:

                    Toast.makeText(itemView.getContext(), "Dummy Clicked", Toast.LENGTH_SHORT).show();
                    break;
            }

            return false;
        }
    }
}
