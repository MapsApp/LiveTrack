package com.sarthak.trackit.trackit.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.adapters.GroupFriendsAdapter;

public class GroupsActivity extends BaseActivity implements View.OnClickListener
,GroupFriendsAdapter.setOnGroupFriendClickListener{

    FloatingActionButton fabBottomSheet;
    LinearLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;
    RecyclerView rvGroupMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        setUpToolbar(this);

        rvGroupMembers=findViewById(R.id.recycler_group_members);
        fabBottomSheet = findViewById(R.id.fab_bottom_sheet);

        //addressed the container linear layout of bottom sheet
        layoutBottomSheet = findViewById(R.id.bottom_sheet_layout);

        //sets the behaviour of linear layout to a bottom sheet
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        rvGroupMembers.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rvGroupMembers.setAdapter(new GroupFriendsAdapter(this));


        //Used to change icon in floating button with states of BottomSheet
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        fabBottomSheet.setImageResource(R.drawable.ic_expand_more_white);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        fabBottomSheet.setImageResource(R.drawable.ic_expand_less_white);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        fabBottomSheet.setOnClickListener(this);
    }

    @Override
    protected int getToolbarID() {
        return R.id.groups_activity_toolbar;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_bottom_sheet:
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    fabBottomSheet.setImageResource(R.drawable.ic_expand_less_white);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    fabBottomSheet.setImageResource(R.drawable.ic_expand_more_white);
                }
                break;
        }
    }

    @Override
    public void OnGroupFriendItemClicked(View view, int position) {

    }
}
