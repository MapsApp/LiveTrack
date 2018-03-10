package com.sarthak.trackit.trackit.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sarthak.trackit.trackit.R;

/**
 * Created by karan on 3/10/2018.
 */

public class SearchCursorAdapter extends CursorAdapter {

    public interface setOnSuggestionClickListener {
        void onSuggestionClickListener(View view, String type, String name);
    }

    public SearchCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_suggestion, parent);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView txtSuggestionName = view.findViewById(R.id.text_suggestion_name);
        ImageView ImgSuggestion = view.findViewById(R.id.image_suggestion);
        TextView txtSuggestionType = view.findViewById(R.id.text_suggestion_username);
    }
}
