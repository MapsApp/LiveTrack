package com.sarthak.trackit.trackit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sarthak.trackit.trackit.GuestLogin;
import com.sarthak.trackit.trackit.R;

public class NewUserActivity extends BaseActivity implements View.OnClickListener {

    Button btnGuestLogin,btnPhoneLogin;
    private ProgressBar progressNewUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        setUpToolbar(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        progressNewUser=findViewById(android.R.id.progress);
        btnGuestLogin = findViewById(R.id.button_guest_sign_in);
        btnPhoneLogin=findViewById(R.id.button_phone_login);

        mAuth = FirebaseAuth.getInstance();
        btnGuestLogin.setOnClickListener(this);
        btnPhoneLogin.setOnClickListener(this);
    }

    @Override
    protected int getToolbarID() {
        return R.id.new_user_activity_toolbar;
    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_guest_sign_in:
                GuestLogin guestLogin=new GuestLogin(this,mAuth,progressNewUser);
                guestLogin.guestLogin();
                break;
            case R.id.button_phone_login:
                startActivity(new Intent(this,LoginActivity.class));
                break;
        }
    }

}
