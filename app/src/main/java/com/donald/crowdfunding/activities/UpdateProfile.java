package com.donald.crowdfunding.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.donald.crowdfunding.business.R;
import com.donald.crowdfunding.models.ProfileModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.valdesekamdem.library.mdtoast.MDToast;

public class UpdateProfile extends AppCompatActivity {
    private EditText uname;
    private Spinner ugender;
    private EditText uaddress;
    private EditText uphone;
    private EditText uoccupation;
    private EditText uBiography;
    private EditText uEmail;
    private Button updateButton;

    private ImageView member_image;
    private DatabaseReference profileReference;
    private StorageReference profileStorage;
    private static final int GALLERY_REQUEST =78;
    private static final int CAMERA_REQUEST_CODE = 1;
    private Uri imageUri =null;
    private ProgressDialog mProgress;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private String uid,id,email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        uname = findViewById(R.id.et_person_name);
        ugender = findViewById(R.id.gender);
        uaddress = findViewById(R.id.et_person_address);
        uphone =findViewById(R.id.et_person_phone);
        uoccupation = findViewById(R.id.et_person_occupation);
        uBiography = findViewById(R.id.et_person_biography);
        member_image = findViewById(R.id.ib_person_image);
        uEmail = findViewById(R.id.et_person_email);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser!=null) {
            uid = currentUser.getUid();
            email = currentUser.getEmail();
        }

        member_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(galIntent, "Choose picture"), GALLERY_REQUEST);
            }
        });
        profileReference=FirebaseDatabase.getInstance().getReference().child("Profiles");
        mProgress = new ProgressDialog(this);
        profileStorage = FirebaseStorage.getInstance().getReference().child("ProfileImages");
        profileReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        updateButton = findViewById(R.id.bt_update_profile);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = uname.getText().toString().trim();
                final String gender=ugender.getItemAtPosition(
                        ugender.getSelectedItemPosition()).toString().trim();
                final String address=uaddress.getText().toString().trim();
                final String phone =uphone.getText().toString().trim();
                final String occupation = uoccupation.getText().toString().trim();
                final String biography = uBiography.getText().toString().trim();

                if (TextUtils.isEmpty(name)){
                    MDToast.makeText(getApplicationContext(),"Names cannot be Empty",
                            MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show();

                }else if (TextUtils.isEmpty(address)){
                    MDToast.makeText(getApplicationContext(),"Address cannot be Empty",
                            MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show();

                }else if (TextUtils.isEmpty(phone)){
                    MDToast.makeText(getApplicationContext(),"Phone Number cannot be Empty",
                            MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show();

                } else if (TextUtils.isEmpty(occupation)){
                    MDToast.makeText(getApplicationContext(),"Occupation cannot be Empty",
                            MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show();

                } else if (TextUtils.isEmpty(biography)){
                    MDToast.makeText(UpdateProfile.this,"biography cannot be Empty",
                            MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show();

                }else if (gender.equalsIgnoreCase("Select Gender")){
                    MDToast.makeText(getApplicationContext()," Pls Select a valid gender",
                            MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show();

                } else {
                    mProgress.setMessage("Updating user...");
                    mProgress.show();
                    StorageReference filePath = profileStorage.child(imageUri.getLastPathSegment());
                    filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            id = profileReference.push().getKey();
                             ProfileModel model = new ProfileModel(uid,name,gender,address, email, phone,
                                     occupation,biography);
                            profileReference.child(uid).setValue(model);
                            profileReference.child(uid).child("profileImage").setValue(downloadUri.toString());

                            MDToast mdToast = MDToast.makeText(getApplicationContext(),
                                    "Profile Updated successfully!",
                                    MDToast.LENGTH_LONG,MDToast.TYPE_SUCCESS);
                            mdToast.show();
                            mProgress.dismiss();
                            Intent payIntent = new Intent(UpdateProfile.this, MainActivity.class);
                            payIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(payIntent);
                            finish();

                        }
                    });

                }



            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK)
        {
            imageUri = data.getData();

            try {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                // Setting up bitmap selected image into ImageView.
                member_image.setImageBitmap(bitmap);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }
}
