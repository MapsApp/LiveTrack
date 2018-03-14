package com.sarthak.trackit.trackit.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.CircleTransform;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestStatusAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<String> listDataHeader;
    private HashMap<String, ArrayList<User>> userKeyList;

    public RequestStatusAdapter(Context mContext, ArrayList<String> listDataHeader, HashMap<String, ArrayList<User>> userKeyList) {

        this.mContext = mContext;
        this.listDataHeader = listDataHeader;
        this.userKeyList = userKeyList;
    }


    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return userKeyList.get(listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        User user = (User) getChild(groupPosition, childPosition);

        final String childText = user.getDisplayName();
        final String childText1 = user.getUsername();

        if (convertView == null) {

            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_friend_status, null);
        }

        TextView txtListChild = convertView.findViewById(R.id.text_person_name);
        TextView txtListChild1 = convertView.findViewById(R.id.text_person_username);
        final ProgressBar progressBar = convertView.findViewById(android.R.id.progress);

        ImageView imageFriendStatus = convertView.findViewById(R.id.image_person);

        progressBar.setVisibility(View.VISIBLE);
        Picasso.with(convertView.getContext())
                .load("https://www.w3schools.com/css/trolltunga.jpg")
                .transform(new CircleTransform())
                .into(imageFriendStatus, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

        txtListChild.setText(childText);
        txtListChild1.setText(childText1);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        if (userKeyList != null) {

            if (userKeyList.get(listDataHeader.get(groupPosition)) != null) {
                return userKeyList.get(listDataHeader.get(groupPosition)).size();
            } else {
                return 0;
            }
        } else {

            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.parent_item, null);
        }

        TextView mPersonNameTv = convertView.findViewById(R.id.text_parent);
        mPersonNameTv.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
