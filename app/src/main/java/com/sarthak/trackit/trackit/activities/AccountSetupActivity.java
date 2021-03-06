package com.sarthak.trackit.trackit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.Constants;
import com.sarthak.trackit.trackit.utils.UserSharedPreferences;

public class AccountSetupActivity extends BaseActivity implements View.OnFocusChangeListener, View.OnClickListener {

    private String providerId;

    private String displayName, mUserName;
    private String account, profileImage;

    private String deviceToken;

    private EditText mDisplayNameEt, mUsernameEt;
    private Button mSignUpBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);
        setUpToolbar(this);

        initFirebase();

        setUpView();

        mUsernameEt.setOnFocusChangeListener(this);
        mSignUpBtn.setOnClickListener(this);
    }

    @Override
    protected int getToolbarID() {
        return R.id.account_setup_activity_toolbar;
    }

    // textView focus changed listener
    // code will be added later when checking unique username.
    // TODO: 1. Add textView focus changed listener for unique username
    @Override
    public void onFocusChange(View view, boolean b) {
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_account_sign_up:

                signUp();
                break;
        }
    }

    private void initFirebase() {

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }

    private void setUpView() {

        mDisplayNameEt = findViewById(R.id.et_account_display_name);
        mUsernameEt = findViewById(R.id.et_account_username);
        mSignUpBtn = findViewById(R.id.button_account_sign_up);

        setDisplayNameIfFacebookLogin();
    }

    private void setDisplayNameIfFacebookLogin() {

        providerId = mAuth.getCurrentUser().getProviders().get(0);

        if (providerId.equals("facebook.com")) {

            mDisplayNameEt.setText(mAuth.getCurrentUser().getDisplayName());
            mDisplayNameEt.setEnabled(false);
        }
    }

    private void signUp() {

        setUpUserCredentials();

        if (displayName != null && mUserName != null) {

            User user = new User(mUserName, displayName, account, deviceToken, profileImage, "thumb");

            uploadUserDetailsToFirestore(user);
        }
    }

    private void setUpUserCredentials() {

        // get device Id to allow login on one device.
        // current device Id will be matched with that in databaseand if found different, user will be signed out.
        // TODO: 2. Match current device Id with that in database.
        final String deviceToken = FirebaseInstanceId.getInstance().getToken();
        // get device Id to allow login on one device.
        // current device Id will be matched with that in database and if found different, user will be signed out.
        // TODO: 2. Match curent device Id with that in database.

        // variable account stores phone/email of the user.
        // phone in case of Phone auth.
        // email in case of FB/Google login.
        if (providerId.equals("facebook.com")) {

            account = mAuth.getCurrentUser().getEmail();
            profileImage = "https://graph.facebook.com/" + mAuth.getCurrentUser().getUid() + "/picture?height=500";
        } else {

            account = mAuth.getCurrentUser().getPhoneNumber();
            profileImage = "profile";
        }

        displayName = mDisplayNameEt.getText().toString();
        mUserName = mUsernameEt.getText().toString();
    }

    private void uploadUserDetailsToFirestore(User user) {

        mFirestore.collection(Constants.USERS_REFERENCE).document(mAuth.getCurrentUser().getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    UserSharedPreferences preferences = new UserSharedPreferences(AccountSetupActivity.this);
                    preferences.setUserStatus("true");

                    Intent mapIntent = new Intent(AccountSetupActivity.this, HomeActivity.class);
                    mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mapIntent);
                    finish();

                } else {

                    Toast.makeText(AccountSetupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
