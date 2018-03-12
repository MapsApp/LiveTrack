package com.sarthak.trackit.trackit.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.Query;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendsStatusAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<String> listDataHeader;
    private HashMap<String, ArrayList<User>> userKeyList;

    public FriendsStatusAdapter(Context mContext, ArrayList<String> listDataHeader, HashMap<String, ArrayList<User>> userKeyList) {

        this.mContext = mContext;
        this.listDataHeader = listDataHeader;
        this.userKeyList = userKeyList;
    }


    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return userKeyList.get(listDataHeader.get(groupPosition)).get(childPosititon).getDisplayName();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {

            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.parent_list_item, null);
        }

        TextView txtListChild = convertView.findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
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
            convertView = infalInflater.inflate(R.layout.item_friend_status, null);
        }

        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

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
