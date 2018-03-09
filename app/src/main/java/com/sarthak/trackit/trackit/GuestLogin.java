package com.sarthak.trackit.trackit;

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

import static android.content.ContentValues.TAG;

/**
 * Created by karan on 3/10/2018.
 */

public class GuestLogin {

    private Context context;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;


    public GuestLogin(Context context, FirebaseAuth mAuth, ProgressBar progressBar) {
        this.context = context;
        this.mAuth = mAuth;
        this.progressBar = progressBar;
    }

    public void guestLogin() {
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInAnonymously()
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            Toast.makeText(context, "Signed In as Guest",
                                    Toast.LENGTH_SHORT).show();
                            context.startActivity(new Intent(context, HomeActivity.class));
                            progressBar.setVisibility(View.INVISIBLE);

                        } else {
                            // If sign in fails, display a message to the user.                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });


    }

}
