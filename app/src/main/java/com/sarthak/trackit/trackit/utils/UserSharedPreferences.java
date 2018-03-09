package com.sarthak.trackit.trackit.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sarthak.trackit.trackit.utils.Constants;

import static android.content.Context.MODE_PRIVATE;

public class UserSharedPreferences {

    private Context mContext;
    private FirebaseUser mUser;

    public UserSharedPreferences(Context context) {

        mContext = context;
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void setUserStatus(String status) {

        SharedPreferences.Editor editor = mContext.getSharedPreferences(Constants.USERS_SHARED_PREFERENCE, MODE_PRIVATE).edit();
        editor.putString(mUser.getUid(), status);
        editor.apply();
    }

    public String getUserStatus() {

        SharedPreferences preferences = mContext.getSharedPreferences(Constants.USERS_SHARED_PREFERENCE, MODE_PRIVATE);
        return preferences.getString(mUser.getUid(), null);
    }
}
