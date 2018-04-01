package com.sarthak.trackit.trackit.loginmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sarthak.trackit.trackit.activities.AccountSetupActivity;
import com.sarthak.trackit.trackit.activities.HomeActivity;
import com.sarthak.trackit.trackit.utils.Constants;
import com.sarthak.trackit.trackit.utils.UserSharedPreferences;

public class PhoneLoginManager {

    private Context mContext;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public PhoneLoginManager(Context context) {

        mContext = context;

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential).addOnCompleteListener(((Activity) mContext), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success");

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
                    // Sign in failed, display a message and update the UI
                    Log.w("TAG", "signInWithCredential:failure", task.getException());
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(mContext, "Invalid verification code.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
