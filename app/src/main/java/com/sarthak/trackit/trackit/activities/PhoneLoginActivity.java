package com.sarthak.trackit.trackit.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.sarthak.trackit.trackit.loginmanager.PhoneLoginManager;
import com.sarthak.trackit.trackit.R;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends BaseActivity implements View.OnClickListener {

    private String phone, verificationId;

    int btnType = 0;

    private EditText mPhoneEt, mCodeEt;
    private Button mLoginBtn;

    private PhoneAuthProvider.ForceResendingToken resendToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpToolbar(this);

        // initialise phone auth callback
        initPhoneAuthCallback();

        setUpView();
    }

    @Override
    protected int getToolbarID() {
        return R.id.login_activity_toolbar;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_login_login:

                if (btnType == 0) {

                    mPhoneEt.setEnabled(false);
                    mLoginBtn.setEnabled(false);

                    phone = mPhoneEt.getText().toString();

                    verifyPhoneNumber(phone);
                } else {

                    mLoginBtn.setEnabled(false);

                    String verificationCode = mCodeEt.getText().toString();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verificationCode);
                    PhoneLoginManager phoneLoginManager = new PhoneLoginManager(PhoneLoginActivity.this);
                    phoneLoginManager.signInWithPhoneAuthCredential(credential);
                }
                break;
        }
    }

    private void setUpView() {

        mPhoneEt = findViewById(R.id.et_login_phone);
        mCodeEt = findViewById(R.id.et_login_verification);
        mLoginBtn = findViewById(R.id.btn_login_login);

        mLoginBtn.setOnClickListener(this);
    }

    private void verifyPhoneNumber(String phone) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );
    }

    private void initPhoneAuthCallback() {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                PhoneLoginManager phoneLoginManager = new PhoneLoginManager(PhoneLoginActivity.this);
                phoneLoginManager.signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Toast.makeText(PhoneLoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String id, PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                verificationId = id;
                mCodeEt.setText(verificationId);
                resendToken = forceResendingToken;

                btnType = 1;

                mLoginBtn.setText("Verify code");
                mLoginBtn.setEnabled(true);
            }
        };
    }
}
