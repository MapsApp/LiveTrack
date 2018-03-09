package com.sarthak.trackit.trackit.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sarthak.trackit.trackit.utils.Constants;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.utils.UserSharedPreferences;

public class AccountSetupActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnClickListener {

    private String displayName, username;

    private EditText mDisplayNameEt, mUsernameEt;
    private Button mSignUpBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mDisplayNameEt = findViewById(R.id.account_display_name_et);
        mUsernameEt = findViewById(R.id.account_username_et);
        mSignUpBtn = findViewById(R.id.account_sign_up_btn);

        mUsernameEt.setOnFocusChangeListener(this);
        mSignUpBtn.setOnClickListener(this);
    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.account_sign_up_btn:

                final String deviceToken = FirebaseInstanceId.getInstance().getToken();

                final String account = mAuth.getCurrentUser().getPhoneNumber();

                displayName = mDisplayNameEt.getText().toString();
                username = mUsernameEt.getText().toString();

                if (displayName != null && username != null) {

                    User user = new User(username, displayName, account, deviceToken, "profile", "thumb");

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
    }
}
