package com.donald.crowdfunding.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.donald.crowdfunding.business.R;


public class LandingPage extends AppCompatActivity {
    private Button mLogin;
    private Button mSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        getSupportActionBar().hide();


        mLogin=(Button)findViewById(R.id.signin);
        mSignup=(Button)findViewById(R.id.signup);

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup = new Intent(LandingPage.this,Signup.class);
                startActivity(signup);
                finish();
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signin = new Intent(LandingPage.this,Signin.class);
                startActivity(signin);
                finish();
            }
        });

    }
}
