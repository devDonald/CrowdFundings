package com.donald.crowdfunding.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.donald.crowdfunding.business.R;
import com.donald.crowdfunding.fragments.AllProjects;
import com.donald.crowdfunding.fragments.CreatePost;
import com.donald.crowdfunding.fragments.LikedProjects;
import com.donald.crowdfunding.fragments.MyProjects;
import com.donald.crowdfunding.fragments.Payments;
import com.donald.crowdfunding.fragments.Profile;
import com.donald.crowdfunding.models.ProfileModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AppCompatActivity activity = MainActivity.this;
    private final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private String uid;
    private String email;
    private CircleImageView userImage;
    private TextView userName, userEmail;
    private DatabaseReference userProfile;
    private DrawerLayout drawer;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_ALLPROJECTS = "allProjects";
    private static final String TAG_MYPROJECT = "myProject";
    private static final String TAG_CREATEPROJECT = "createProject";
    private static final String TAG_EDITPROJECT = "editProject";
    private static final String TAG_PAYMENT = "payment";
    private static final String TAG_RECOMMEND = "recommend";
    private static final String TAG_LOVE = "love";
    private static final String TAG_PROFILE = "profile";
    private static String CURRENT_TAG = TAG_ALLPROJECTS;
    private String[] fragmentTitles;
    // flag to load home fragment when user presses back key
    private Boolean shouldLoadHomeFragmentOnBackPress = true;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userProfile = FirebaseDatabase.getInstance().getReference().child("Profiles");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
            email = currentUser.getEmail();
        }

        Log.d("uid", "" + uid);
        Log.d("email", "" + email);

        mAuth = FirebaseAuth.getInstance();

//        // Load allProjects fragment by default
//        loadFragment(new AllProjects());

        fragmentTitles = getResources().getStringArray(R.array.nav_item_fragment_titles);

        if (savedInstanceState == null) {
            navItemIndex = 0;
            loadFragment(new AllProjects());
        }

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


        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);

        userName = hView.findViewById(R.id.nav_userName);
        userEmail = hView.findViewById(R.id.nav_email);
        userImage = hView.findViewById(R.id.navImage);

        userEmail.setText(email);

        userProfile.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProfileModel model = dataSnapshot.getValue(ProfileModel.class);
                try {
                    String user_image = dataSnapshot.child("profileImage").getValue(String.class);
                    assert model != null;
                    userName.setText(model.getName());
                    Log.d("name", "" + model.getName());
                    Picasso.with(getApplicationContext()).load(user_image).into(userImage);

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void loadFragment(Fragment fragment) {

        // selectNavMenu();

        // Set toolbar title
        setToolbarTitle();

        // if the user select the current navigation menu again, don't
        // do anything, just close the nav drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            return;
        }
        /** Sometimes when fragment has huge data, screen hangs when
         switching between navigation menus. So by using *runnable,
         the fragment is loaded with cross fade effect.
         **/

                // update the main content by replacing fragments
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();

        }


    private void setToolbarTitle() {
        getSupportActionBar().setTitle(fragmentTitles[navItemIndex]);
    }

//    private void selectNavMenu() {
//        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
//    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            MDToast.makeText(activity, "Press BACK again to exit",
                    Toast.LENGTH_SHORT, MDToast.TYPE_INFO).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        switch (menuItem.getItemId()) {

            case R.id.allProject:
                Fragment allProjects = new AllProjects();
                navItemIndex = 0;
                loadFragment(allProjects);
                break;

            case R.id.myProject:
                navItemIndex = 1;
                Fragment myProject = new MyProjects();
                loadFragment(myProject);
                break;

            case R.id.createProject:
                navItemIndex = 2;
                Fragment createProject = new CreatePost();
                loadFragment(createProject);
                break;

            case R.id.payments:
                navItemIndex = 3;
                Fragment payment = new Payments();
                loadFragment(payment);
                break;

            case R.id.love:
                navItemIndex = 4;
                Fragment likedProjects = new LikedProjects();
                loadFragment(likedProjects);
                break;

            case R.id.recommend:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = "Share crowd funding app";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Sharing Options"));
                break;

            case R.id.nav_profile:
                navItemIndex = 5;
                Fragment profile = new Profile();
                loadFragment(profile);
                Bundle bundle = new Bundle();
                bundle.putString("userId", uid);
                bundle.putString("userEmail", email);
                break;

            case R.id.nav_delete:

                if (currentUser != null) {
                    currentUser.delete().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                MDToast.makeText(getApplicationContext(), "Account deleted successfully",
                                        MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                                startActivity(new Intent(MainActivity.this, LandingPage.class));
                                finish();

                            } else {
                                MDToast.makeText(getApplicationContext(), "Sorry,account cannot be deleted",
                                        MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();

                            }
                        }
                    });
                }

            case R.id.nav_logout:
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

            default:
                navItemIndex = 0;

        }

        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
    }
}
