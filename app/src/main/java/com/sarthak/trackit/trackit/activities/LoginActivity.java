package com.sarthak.trackit.trackit.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sarthak.trackit.trackit.utils.Constants;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.utils.UserSharedPreferences;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private String phone, verificationId;

    int btnType = 0;

    private EditText mPhoneEt, mCodeEt;
    private Button mLoginBtn;

    private PhoneAuthProvider.ForceResendingToken resendToken;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpToolbar(this);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // initialise phone auth callback
        initPhoneAuthCallback();

        mPhoneEt = findViewById(R.id.et_login_phone);
        mCodeEt = findViewById(R.id.et_login_verification);
        mLoginBtn = findViewById(R.id.btn_login_login);

        mLoginBtn.setOnClickListener(this);
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

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phone,
                            60,
                            TimeUnit.SECONDS,
                            this,
                            mCallbacks
                    );
                } else {

                    mLoginBtn.setEnabled(false);

                    String verificationCode = mCodeEt.getText().toString();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
                break;
        }
    }

    private void initPhoneAuthCallback() {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            final UserSharedPreferences preferences = new UserSharedPreferences(LoginActivity.this);

                            FirebaseUser user = task.getResult().getUser();

                            DocumentReference userReference = mFirestore.collection(Constants.USERS_REFERENCE).document(user.getUid());
                            userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.isSuccessful()) {

                                        DocumentSnapshot snapshot = task.getResult();

                                        if (snapshot != null && snapshot.exists()) {

                                            preferences.setUserStatus("true");

                                            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                            startActivity(homeIntent);
                                            finish();
                                        } else {

                                            preferences.setUserStatus("false");

                                            Intent accountSetupIntent = new Intent(LoginActivity.this, AccountSetupActivity.class);
                                            startActivity(accountSetupIntent);
                                            finish();
                                        }
                                    }
                                }
                            });

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}
