package com.sarthak.trackit.trackit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sarthak.trackit.trackit.loginmanager.FacebookLoginManager;
import com.sarthak.trackit.trackit.loginmanager.GuestLoginManager;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.utils.UserSharedPreferences;

import java.util.Arrays;

public class NewUserActivity extends BaseActivity implements View.OnClickListener, FacebookCallback<LoginResult> {

    private Button btnPhoneLogin,btnFbLogin,btnGuestLogin;

    private ProgressBar progressNewUser;

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        setUpToolbar(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        setUpView();

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, NewUserActivity.this);

        btnPhoneLogin.setOnClickListener(this);
        btnFbLogin.setOnClickListener(this);
        btnGuestLogin.setOnClickListener(this);
    }

    @Override
    protected int getToolbarID() {
        return R.id.new_user_activity_toolbar;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            // check userStatus in shared prefs
            // if true, launch HomeActivity. else AccountSetupActivity.
            String userStatus = new UserSharedPreferences(NewUserActivity.this).getUserStatus();

            if (userStatus != null) {

                if (userStatus.equals("true")) {

                    Intent homeIntent = new Intent(NewUserActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    finish();
                } else {

                    Intent accountSetupIntent = new Intent(NewUserActivity.this, AccountSetupActivity.class);
                    startActivity(accountSetupIntent);
                    finish();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_guest_sign_in:

                GuestLoginManager guestLoginManager = new GuestLoginManager(this, progressNewUser);
                guestLoginManager.guestLogin();
                break;

            case R.id.button_phone_login:

                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                break;

            case R.id.button_fb_login:
                // create an instance of facebook loginManager to login via facebook
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_friends"));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //----------------------------------------------------------------------------------------------
    // facebook login callbacks
    //----------------------------------------------------------------------------------------------
    @Override
    public void onSuccess(LoginResult loginResult) {

        new FacebookLoginManager(NewUserActivity.this, progressNewUser)
                .handleFacebookAccessToken(loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {

        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
    }

    private void setUpView() {

        progressNewUser = findViewById(android.R.id.progress);
        btnPhoneLogin = findViewById(R.id.button_phone_login);
        btnFbLogin = findViewById(R.id.button_fb_login);
        btnGuestLogin = findViewById(R.id.button_guest_sign_in);
    }
}
