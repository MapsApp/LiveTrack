package com.sarthak.trackit.trackit.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sarthak.trackit.trackit.R;

/**
 * Created by karan on 3/11/2018.
 */

public class SearchCursorAdapter extends CursorAdapter implements View.OnClickListener {

    public SearchCursorAdapter(Context context, Cursor c, boolean autoRequery, String query) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView mUserNameTv, mDisplayNameTv;
        ImageButton mRequestBtn, mSearchExpandBtn;
        ImageView mSearchUserIv;
        LinearLayout searchOptionsLayout;

        mUserNameTv = view.findViewById(R.id.text_search_username);
        mDisplayNameTv = view.findViewById(R.id.text_search_name);
        mRequestBtn = view.findViewById(R.id.button_search_add_friend);
        mSearchUserIv = view.findViewById(R.id.image_search);
        mSearchExpandBtn = view.findViewById(R.id.button_search_expand);
        searchOptionsLayout = view.findViewById(R.id.search_options_layout);

        mSearchExpandBtn.setOnClickListener(this);
        mRequestBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
