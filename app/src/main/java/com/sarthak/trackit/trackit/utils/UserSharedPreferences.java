package com.sarthak.trackit.trackit.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.Context.MODE_PRIVATE;

public class UserSharedPreferences {

    private Context mContext;
    private FirebaseUser mUser;

    private SharedPreferences preferences;

    public UserSharedPreferences(Context context) {

        mContext = context;
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        preferences = mContext.getSharedPreferences(Constants.USERS_SHARED_PREFERENCE, MODE_PRIVATE);
    }

    public void setUserStatus(String status) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(mUser.getUid(), status);
        editor.apply();
    }

    public String getUserStatus() {

        return preferences.getString(mUser.getUid(), null);
    }

    public void deleteStatus() {

        SharedPreferences.Editor deleteEditor = preferences.edit();
        deleteEditor.remove(mUser.getUid());
        deleteEditor.apply();
    }
}
