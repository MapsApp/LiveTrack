package com.sarthak.trackit.trackit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sarthak.trackit.trackit.activities.AccountSetupActivity;
import com.sarthak.trackit.trackit.activities.HomeActivity;
import com.sarthak.trackit.trackit.utils.Constants;
import com.sarthak.trackit.trackit.utils.UserSharedPreferences;

public class FacebookLoginManager {

    private Context mContext;

    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public FacebookLoginManager(Context context, ProgressBar progressBar) {

        this.mContext = context;
        this.mProgressBar = progressBar;
        // create an instance auth and get current user
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }

    /**
     * Register user to firebase authentication from facebook token obtained by logging in to facebook.
     *
     * @param token is the facebook login token
     */
    public void handleFacebookAccessToken(final AccessToken token) {

        mProgressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential).addOnCompleteListener(((Activity) mContext), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    mProgressBar.setVisibility(View.INVISIBLE);

                    final UserSharedPreferences preferences = new UserSharedPreferences(mContext);

                    FirebaseUser user = task.getResult().getUser();

                    DocumentReference userReference = mFirestore.collection(Constants.USERS_REFERENCE).document(user.getUid());
                    userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {

                                DocumentSnapshot snapshot = task.getResult();

                                if (snapshot != null && snapshot.exists()) {

                                    preferences.setUserStatus("true");

                                    Intent homeIntent = new Intent(mContext, HomeActivity.class);
                                    mContext.startActivity(homeIntent);
                                    ((Activity) mContext).finish();
                                } else {

                                    preferences.setUserStatus("false");

                                    Intent accountSetupIntent = new Intent(mContext, AccountSetupActivity.class);
                                    mContext.startActivity(accountSetupIntent);
                                    ((Activity) mContext).finish();
                                }
                            }
                        }
                    });

                } else {
                    // If sign in fails, display a message to the user.
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(mContext, "Facebook login failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
