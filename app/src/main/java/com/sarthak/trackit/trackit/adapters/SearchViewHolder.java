package com.sarthak.trackit.trackit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sarthak.trackit.trackit.R;

public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView mUserNameTv, mDisplayNameTv;
    private Button mRequestBtn;

    private Context mContext;

    SearchViewHolder(Context context, View view) {

        super(view);

        this.mContext = context;

        mUserNameTv = view.findViewById(R.id.username);
        mDisplayNameTv = view.findViewById(R.id.displayname);
        mRequestBtn = view.findViewById(R.id.request_btn);

        mRequestBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.request_btn:

                Toast.makeText(mContext, "Request sent.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
