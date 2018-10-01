package com.wandsworkstation.chat;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.KeyStore;

import javax.crypto.KeyGenerator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends Activity {
    /*Tag---> For Easy Access To Login Activity Name
        * Request_SignUp---> Sent To the sign up activity to track sign up success after which the user is logged in automatically
        * mAuth---> Tracks authentication state
        * */
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private String KEY_NAME;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    /*Finger Print*/
    private boolean checkFinger() {
        // Keyguard Manager
        keyguardManager = (KeyguardManager)
                getSystemService(KEYGUARD_SERVICE);

        // Fingerprint Manager
        fingerprintManager = (FingerprintManager)
                getSystemService(FINGERPRINT_SERVICE);

        try {
            // Check if the fingerprint sensor is present
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!fingerprintManager.isHardwareDetected()) {
                    // Update the UI with a message
//                    message.setText("Fingerprint authentication not supported");
                    Toast.makeText(getBaseContext(), "Fingerprint authentication not supported", Toast.LENGTH_LONG).show();
                    return false;
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!fingerprintManager.hasEnrolledFingerprints()) {
//                    message.setText("No fingerprint configured.");
                    Toast.makeText(getBaseContext(), "No fingerprint configured.", Toast.LENGTH_LONG).show();
                    return false;
                }
            }

            if (!keyguardManager.isKeyguardSecure()) {
//                message.setText("Secure lock screen not enabled.");
                Toast.makeText(getBaseContext(), "Secure lock screen not enabled.", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        catch(SecurityException se) {
            se.printStackTrace();
        }
        return true;
    }

    /*Binds the UI Components to Variables*/
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.btn_fingerprint) Button _fingerprintButton;
    @Bind(R.id.link_signup) TextView _signupLink;
    private GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        _passwordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Start Fgament*/

            }
        });

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        if (checkFinger() == false) {
            _fingerprintButton.setEnabled(false);
            checkFinger();
        }

        /*else {
            // We are ready to set up the cipher and the key
            try {
                generateKey();
                Cipher cipher = generateCipher();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
                }
            }
            catch(FingerprintException fpe) {
                // Handle exception
                _fingerprintButton.setEnabled(false);
            }
        }*/

        _fingerprintButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Fingerprint.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


        //Check If User Is Signed In And Redirect To Main Activity
        //This Ensures that user is signed in Before getting to main activity
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                    Toast.makeText(getBaseContext(), "Ooops! Not Signed In", Toast.LENGTH_LONG).show();
                }
                // ...
            }
        };

        /*GOOGLE*/

        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this *//* FragmentActivity *//*, this *//* OnConnectionFailedListener *//*)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });*/

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed(null);
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        //Alert Dialog For Error
        final AlertDialog.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder = new AlertDialog.Builder(LoginActivity.this,android.R.style.Theme_Material_Dialog_Alert);
        }else {
            builder = new AlertDialog.Builder(LoginActivity.this);
        }

        /*Check Network Connection*/
        progressDialog.setMessage("Checking Connectivity...");
        progressDialog.show();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if(!AppSatus.getInstance(getApplicationContext()).isOnline()){

                            progressDialog.setMessage("Checking Connectivity...");
                            progressDialog.show();
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            builder.setTitle("Network Error")
                                                    .setCancelable(false)
                                                    .setMessage("Ooops! No Wifi/Mobile Networks Connected")
                                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            _loginButton.setEnabled(true);
                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.stat_notify_error)
                                                    .show();
//                            Toast.makeText(getBaseContext(), "Ooops! No Wifi/Mobile Networks Connected", Toast.LENGTH_LONG).show();
                                        }
                                    }, 2000);

                        } else {
                            progressDialog.setMessage("Authenticating...");
                            progressDialog.show();

                            final String email = _emailText.getText().toString();
                            final String password = _passwordText.getText().toString();

                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            mAuth.signInWithEmailAndPassword(email, password)
                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            // If sign in fails, display a message to the user. If sign in succeeds
                                                            // the auth state listener will be notified and logic to handle the
                                                            // signed in user can be handled in the listener.
                                                            if (!task.isSuccessful()) {
                                                                progressDialog.dismiss();
                                                                onLoginFailed(task.getException().toString());
                                                            }else{
                                                                progressDialog.dismiss();
                                                                onLoginSuccess();
                                                            }

                                                            // ...
                                                        }
                                                    });

                                        }
                                    }, 3000);
                        }
                    }
                }, 2000);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        final AlertDialog.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder = new AlertDialog.Builder(LoginActivity.this,android.R.style.Theme_Material_Dialog_Alert);
        }else {
            builder = new AlertDialog.Builder(LoginActivity.this);
        }
        builder.setTitle("Login Successful")
                .setMessage("Welcome, Login Was Successful!")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        _loginButton.setEnabled(true);
                    }
                })
                .setIcon(android.R.drawable.alert_light_frame)
                .show();
    }

    public void onLoginFailed(String msg) {
        final AlertDialog.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder = new AlertDialog.Builder(LoginActivity.this,android.R.style.Theme_Material_Dialog_Alert);
        }else {
            builder = new AlertDialog.Builder(LoginActivity.this);
        }

        builder.setTitle("Login Error");
                if (msg == null)
                    builder.setMessage("Ooops! Login Was Not Successful, Check Login Details");
                else
                    builder.setMessage(msg);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        _loginButton.setEnabled(true);
                    }
                })
                .setIcon(android.R.drawable.stat_notify_error)
                .show();
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

}
