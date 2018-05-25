package com.donald.crowdfunding.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.donald.crowdfunding.activities.MainActivity;
import com.donald.crowdfunding.business.R;
import com.donald.crowdfunding.models.CreatePostModel;
import com.donald.crowdfunding.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import static android.app.Activity.RESULT_OK;

public class CreatePost extends Fragment {

    private final String TAG = CreatePost.class.getSimpleName();
    private EditText postTitle, postDescription, postLocation, postCategory, postFund, postTargetDay;
    private Button submitButton;
    private LinearLayout postPicture;
    private Utils utils;
    private String title, description, location, category, fund, targetDay;
    private DatabaseReference postReference;
    private StorageReference postImageReference;
    private String uid, postId;
    private FirebaseUser firebaseUser;
    private static final int GALLERY_REQUEST =78;
    private static final int CAMERA_REQUEST_CODE = 1;
    private Uri imageUri =null;
    private Context context;
    private ImageView displayImage;
    private ProgressDialog mProgress;


    public CreatePost() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_create_post, container, false);

        context = getContext();

        postTitle = view.findViewById(R.id.post_title);
        postDescription = view.findViewById(R.id.post_description);
        postLocation = view.findViewById(R.id.post_location);
        postCategory = view.findViewById(R.id.post_category);
        postFund = view.findViewById(R.id.post_fund);
        postTargetDay = view.findViewById(R.id.post_target_day);
        postPicture = view.findViewById(R.id.post_profile_pix);
        submitButton = view.findViewById(R.id.submit_btn);
        displayImage = view.findViewById(R.id.upload_post_image);


        utils = new Utils();

        mProgress=new ProgressDialog(context);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        postReference = FirebaseDatabase.getInstance().getReference().child("PostProjects");
        postImageReference = FirebaseStorage.getInstance().getReference().child("PostImages");

        //getting the current user id from firebase
        uid = firebaseUser.getUid();

        //picking pictures from the gallary
        postPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(galIntent, "Choose picture"), GALLERY_REQUEST);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = postTitle.getText().toString().trim();
                description = postDescription.getText().toString().trim();
                location = postLocation.getText().toString().trim();
                category = postCategory.getText().toString().trim();
                fund = postFund.getText().toString().trim();
                targetDay = postTargetDay.getText().toString().trim();

                if (TextUtils.isEmpty(title)) {
                    postTitle.setError("Please enter post title");
                    return;
                }
                if (TextUtils.isEmpty(description)) {
                    postDescription.setError("Please enter post description");
                    return;
                }
                if (TextUtils.isEmpty(location)) {
                    postLocation.setError("Please enter post location");
                    return;
                }
                if (TextUtils.isEmpty(category)) {
                    postCategory.setError("Please enter post category");
                    return;
                }
                if (TextUtils.isEmpty(fund)) {
                    postFund.setError("Please enter required fund");
                    return;
                }
                if (TextUtils.isEmpty(targetDay)) {
                    postTargetDay.setError("Please enter validity period for this crowd funding");
                    return;
                }
                if (!utils.isNetworkAvailable(getActivity())) {
                    MDToast.makeText(getActivity(), "Network unavailable",
                            MDToast.TYPE_WARNING, Toast.LENGTH_SHORT).show();
                } else {
                    //start activity
                    mProgress.setMessage("Updating user...");
                    mProgress.show();
                    StorageReference filePath = postImageReference.child(imageUri.getLastPathSegment());
                    filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            postId = postReference.push().getKey();
                            CreatePostModel model = new CreatePostModel(uid,postId,title,category,description,
                                    location,targetDay,fund,downloadUri.toString());
                            postReference.child(postId).setValue(model);

                            MDToast mdToast = MDToast.makeText(context,
                                    "Project created successfully!",
                                    MDToast.LENGTH_LONG,MDToast.TYPE_SUCCESS);
                            mdToast.show();
                            mProgress.dismiss();
                            Intent allPost = new Intent(context, MainActivity.class);
                            allPost.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(allPost);
                        }
                    });

                }
            }
        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK)
        {
           imageUri=data.getData();
            Picasso.with(context).load(imageUri).noPlaceholder().centerCrop().fit()
                    .into(displayImage);

        }

    }

}
