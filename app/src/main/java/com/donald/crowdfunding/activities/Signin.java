package com.donald.crowdfunding.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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


public class Signin extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;
    private TextView passwordReset, tvSignup;
    private EditText etPassword;
    private MaterialEditText etEmail;

    private Button btSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        getSupportActionBar().hide();

        etEmail =findViewById(R.id.etLoginEmail);
        etPassword=findViewById(R.id.etLoginPassword);
        passwordReset = findViewById(R.id.tvRestPassword);
        tvSignup = findViewById(R.id.tvsignUp);
        btSignIn = findViewById(R.id.btnLogin);

        progressDialog = new ProgressDialog(Signin.this);

        mAuth = FirebaseAuth.getInstance();
        passwordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signin.this,PasswordReset.class));
                finish();

            }
        });

        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup = new Intent(Signin.this,Signup.class);
                startActivity(signup);
                finish();
            }
        });


        if (mAuth.getCurrentUser() != null) {
            startActivity(new android.content.Intent(Signin.this, MainActivity.class));
            finish();
        }


        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Signin.this);
                    builder.setMessage("email cannot be empty")
                            .setTitle("Oops!")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;

                }if (password.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Signin.this);
                    builder.setMessage("password cannot be empty")
                            .setTitle("Oops!")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                } else {
                    progressDialog.setMessage("Signing user in...");
                    progressDialog.show();

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Signin.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (!task.isSuccessful()){
                                        MDToast.makeText(getApplication(),"incorrect email/password",
                                                MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                                    } else
                                    {
                                        MDToast.makeText(getApplication(),"SignIn Successful",
                                                MDToast.LENGTH_LONG, MDToast.TYPE_INFO).show();
                                        Intent intent = new Intent(Signin.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            });


                }


            }
        });
    }
}
