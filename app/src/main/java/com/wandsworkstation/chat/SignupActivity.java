package com.wandsworkstation.chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignupActivity extends Activity {
    private static final String TAG = "SignupActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    @Bind(R.id.input_name) EditText _nameText;
//    @Bind(R.id.input_address) EditText _addressText;
    @Bind(R.id.input_email) EditText _emailText;
//    @Bind(R.id.input_mobile) EditText _mobileText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed(null);
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        //Alert Dialog
        final AlertDialog.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder = new AlertDialog.Builder(SignupActivity.this,android.R.style.Theme_Material_Dialog_Alert);
        }else {
            builder = new AlertDialog.Builder(SignupActivity.this);
        }

        progressDialog.setMessage("Checking Connectivity...");
        progressDialog.show();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        //check to know if internert is availabe
                        if(AppSatus.getInstance(getApplicationContext()).isOnline()){
                            progressDialog.setMessage("Creating Account...");
                            progressDialog.show();

                            final String name = _nameText.getText().toString();
                    //        String address = _addressText.getText().toString();
                            final String email = _emailText.getText().toString();
                    //        String mobile = _mobileText.getText().toString();
                            final String password = _passwordText.getText().toString();
                            String reEnterPassword = _reEnterPasswordText.getText().toString();



                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {

                                            mAuth.createUserWithEmailAndPassword(email,password)
                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                /*progressDialog.dismiss();
                                                                onSignupSuccess();*/
                                                                final FirebaseUser user = mAuth.getCurrentUser();
                                                                if (user != null) {
                                                                    // User is signed in
                                                                    progressDialog.setMessage("Sending Verification Email...");
                                                                    progressDialog.show();
                                                                    // Then send the verification email
                                                                    new android.os.Handler().postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            user.sendEmailVerification()
                                                                                    .addOnCompleteListener(SignupActivity.this, new OnCompleteListener() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull final Task task){
                                                                                            if (task.isSuccessful()) {
                                                                                                String user_id = user.getUid();
                                                                                                DatabaseReference current_user = mDatabase.child(user_id);

                                                                                                current_user.child("name").setValue(name);
                                                                                                progressDialog.dismiss();
                                                                                                onSignupSuccess();
                                                                                            } else {
                                                                                                progressDialog.dismiss();
                                                                                                onSignupFailed(task.getException().toString());
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    },2000);
                                                                }
                                                            }else{
                                                                progressDialog.dismiss();
                                                                onSignupFailed(task.getException().toString());
                                                            }
                                                        }
                                                    });
                                        }
                                    }, 3000);
                        }else {
                              progressDialog.dismiss();
                              builder.setTitle("Network Error")
                                      .setMessage("Ooops! No Wifi/Mobile Networks Connected")
                                      .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialogInterface, int i) {

                                  }
                              })
                           .setIcon(android.R.drawable.stat_notify_error)
                           .show();
                           _signupButton.setEnabled(true);
                       }
//                            Toast.makeText(getBaseContext(), "Ooops! No Wifi/Mobile Networks Connected", Toast.LENGTH_LONG).show();
                    }
                }, 2000);
    }


    public void onSignupSuccess() {
        final AlertDialog.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder = new AlertDialog.Builder(SignupActivity.this,android.R.style.Theme_Material_Dialog_Alert);
        }else {
            builder = new AlertDialog.Builder(SignupActivity.this);
        }
        builder.setTitle("Sign Up Successful")
                .setMessage("Welcome "+mAuth.getCurrentUser().getEmail()+", Sign Up Was Successful!")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        _signupButton.setEnabled(true);
                        setResult(RESULT_OK, null);
                        finish();
                    }
                })
                .setIcon(android.R.drawable.alert_light_frame)
                .show();
//        _signupButton.setEnabled(true);
//        setResult(RESULT_OK, null);
//        Toast.makeText(getBaseContext(), "Sign Up Succes", Toast.LENGTH_LONG).show();
//        finish();
    }

    public void onSignupFailed(String msg) {
        final AlertDialog.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder = new AlertDialog.Builder(SignupActivity.this,android.R.style.Theme_Material_Dialog_Alert);
        }else {
            builder = new AlertDialog.Builder(SignupActivity.this);
        }

        /*If method call is as a result of failed validation msg is equals to Null, then print String
        * Else Failure is as a result of Firebase Exception thrown to msg Variable, display Exception(fireBase)
        * */
        builder.setTitle("Signup Error");
        if (msg == null)
            builder.setMessage("Ooops! Sign Up Was Not Successful, Check Sign Up Details");
        else
            builder.setMessage(msg);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                _signupButton.setEnabled(true);
            }
        })
                .setIcon(android.R.drawable.stat_notify_error)
                .show();

        Toast.makeText(getBaseContext(), "Sign Up failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    /*public void sendEmailVerification(){
        final FirebaseUser user;
        user = mAuth.getCurrentUser();

        final AlertDialog.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder = new AlertDialog.Builder(SignupActivity.this,android.R.style.Theme_Material_Dialog_Alert);
        }else {
            builder = new AlertDialog.Builder(SignupActivity.this);
        }

        builder.setTitle("Verification Email");
        user.sendEmailVerification()
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull final Task task){
                        if (task.isSuccessful()) {
                            builder.setMessage("Thank You For Signing Up, An Email Has Been Sent To Your Inbox Please Verify Your Email");
                            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onSignupSuccess();
                                }
                            });
                            builder.setIcon(android.R.drawable.btn_default)
                                    .show();
                        } else {
                            builder.setMessage("Error Sending Verification To Email");
                            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onSignupFailed(task.getException().toString());
                                }
                            });
                            builder.setIcon(android.R.drawable.stat_notify_error)
                                    .show();
                        }
                    }
                });
    }*/

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
//        String address = _addressText.getText().toString();
        String email = _emailText.getText().toString();
//        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        /*if (address.isEmpty()) {
            _addressText.setError("Enter Valid Address");
            valid = false;
        } else {
            _addressText.setError(null);
        }*/


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        /*if (mobile.isEmpty() || mobile.length()!=10) {
            _mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }*/

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }
}