package com.donald.crowdfunding.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.donald.crowdfunding.business.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.valdesekamdem.library.mdtoast.MDToast;



public class Signup extends AppCompatActivity {
    private EditText mPassword;
    private MaterialEditText mEmail;
    private TextView alreadySigned;
    private Button btnSignUp;
    private ProgressDialog mDialog;
    private FirebaseAuth mAuth;
    private EditText mConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();

        mPassword = findViewById(R.id.etSignupPassword);
        mEmail = findViewById(R.id.etSignupEmail);
        alreadySigned=findViewById(R.id.tvSignIn);
        btnSignUp=findViewById(R.id.btnSignUp);
        mConfirmPassword = findViewById(R.id.etConfirmSignupPassword);

        mDialog= new ProgressDialog(Signup.this);
        mAuth =FirebaseAuth.getInstance();

        alreadySigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signin = new Intent(Signup.this,Signin.class);
//
                startActivity(signin);
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                    builder.setMessage("email cannot be empty")
                            .setTitle("Oops!")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;

                }if (TextUtils.isEmpty(password)|| TextUtils.isEmpty(confirmPassword)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                    builder.setMessage("password cannot be empty")
                            .setTitle("Oops!")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;

                } if (password.length()<6 || confirmPassword.length()<6){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                    builder.setMessage("password cannot be less than 6 characters")
                            .setTitle("Oops!")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                } if (!password.matches(confirmPassword)){
                    mConfirmPassword.setError("password does not match");
                    return;
                }
                else {
                    mDialog.setMessage("Registering User...");
                    mDialog.show();
                    Log.d("email",email);
                    Log.d("password",password);
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    mDialog.dismiss();
                                    if (task.isSuccessful()){
                                        MDToast.makeText(getApplication(), "Sign up successful",
                                                MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                                        Intent signin = new Intent(Signup.this,Signin.class);
//
                                        startActivity(signin);
                                        finish();
                                    } else{
                                        MDToast.makeText(getApplication(),"Sign up not successful",
                                                MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                                    }
                                }
                            });

                }
            }
        });

    }
}
