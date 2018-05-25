package com.donald.crowdfunding.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.donald.crowdfunding.activities.UpdateProfile;
import com.donald.crowdfunding.business.R;
import com.donald.crowdfunding.models.ProfileModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {
    private Button update_profile;
    private Context context;
    private DatabaseReference profileReference;

    private TextView profileName,profileBio,profileEmail,profileDonate,profileAddress;
    private TextView profileOccupation, profilePhone;
    private ImageView profileImage;
    private String uid,bio;
    private int totalDonate,totalFriend;
    private FirebaseUser currentUser;


    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context=getContext();
        update_profile = view.findViewById(R.id.update_profile);
        profileName = view.findViewById(R.id.profileUserName);
        profileBio = view.findViewById(R.id.profileUserBio);
        profileEmail = view.findViewById(R.id.profileUserEmail);
        profileImage = view.findViewById(R.id.profileUserPhoto);
        profileAddress = view.findViewById(R.id.profileUserAddress);
        profileOccupation = view.findViewById(R.id.profileUserOccupation);
        profilePhone = view.findViewById(R.id.profileUserPhone);

        profileReference= FirebaseDatabase.getInstance().getReference().child("Profiles");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser!=null) {
            uid = currentUser.getUid();
        }

        profileReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProfileModel profileModel = dataSnapshot.getValue(ProfileModel.class);
                try {
                    String image = dataSnapshot.child("profileImage").getValue(String.class);
                    profileName.setText(profileModel.getName());
                    profileAddress.setText(profileModel.getAddress());
                    profileBio.setText(profileModel.getBiography());
                    profileEmail.setText(profileModel.getEmail());
                    profileOccupation.setText(profileModel.getOccupation());
                    profilePhone.setText(profileModel.getPhone());

                    Picasso.with(context).load(image).into(profileImage);


                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, UpdateProfile.class));
            }
        });

        return view;
    }

}
