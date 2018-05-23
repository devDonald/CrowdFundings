package com.donald.crowdfunding.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.donald.crowdfunding.business.R;
import com.donald.crowdfunding.fragments.Profile;
import com.donald.crowdfunding.models.ProfileModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth.AuthStateListener authListener;
    private  FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private String uid;
    private String email;
    private CircleImageView userImage;
    private TextView userName, userEmail;
    private DatabaseReference userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userProfile = FirebaseDatabase.getInstance().getReference().child("Profiles");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser!=null) {
            uid = currentUser.getUid();
            email = currentUser.getEmail();
        }

        Log.d("uid",""+uid);
        Log.d("email",""+email);

        mAuth= FirebaseAuth.getInstance();


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LandingPage.class));
                    finish();
                }
            }
        };


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);

        userName = (TextView) hView.findViewById(R.id.nav_userName);
        userEmail = (TextView) hView.findViewById(R.id.nav_email);
        userImage = (CircleImageView) hView.findViewById(R.id.navImage);

        userEmail.setText(email);

        userProfile.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProfileModel model = dataSnapshot.getValue(ProfileModel.class);
                String user_image = dataSnapshot.child("profileImage").getValue(String.class);

                userName.setText(model.getName());
                Log.d("name",""+model.getName());
                Picasso.with(getApplicationContext()).load(user_image).into(userImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id==R.id.allProject){

        } else if (id==R.id.myProject){

        } else if(id==R.id.createProject){

        }else if(id==R.id.editProject){

        } else if (id==R.id.payments) {

        }else if (id==R.id.love){

        }else if (id==R.id.recommend){

        }else if (id==R.id.nav_profile){
            Bundle bundle = new Bundle();
            bundle.putString("userId", uid);
            bundle.putString("userEmail", email);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Profile profileActivity = new Profile();
            profileActivity.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_main, profileActivity);
            fragmentTransaction.commit();

        }
        else if (id == R.id.nav_editProfile) {


        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        // user auth state is changed - user is null
                        // launch login activity
                        startActivity(new Intent(MainActivity.this, LandingPage.class));
                        finish();
                    }
                }
            });

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
       // LayoutInflater inflater =
    }

}
