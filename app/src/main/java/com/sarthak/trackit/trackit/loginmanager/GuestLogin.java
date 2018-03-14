package com.sarthak.trackit.trackit.loginmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sarthak.trackit.trackit.activities.HomeActivity;
import com.sarthak.trackit.trackit.utils.UserSharedPreferences;

import static android.content.ContentValues.TAG;

public class GuestLogin {

    private Context mContext;

    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;

    public GuestLogin(Context context, ProgressBar progressBar) {

        mContext = context;
        mProgressBar = progressBar;

        mAuth = FirebaseAuth.getInstance();
    }

    public void guestLogin() {

        mProgressBar.setVisibility(View.VISIBLE);

        mAuth.signInAnonymously()
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");

                            UserSharedPreferences preferences = new UserSharedPreferences(mContext);
                            preferences.setUserStatus("true");

                            mProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(mContext, "Signed In as Guest", Toast.LENGTH_SHORT).show();
                            mContext.startActivity(new Intent(mContext, HomeActivity.class));
                            ((Activity) mContext).finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }
}
