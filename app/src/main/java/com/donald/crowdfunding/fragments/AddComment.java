package com.donald.crowdfunding.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.donald.crowdfunding.business.R;
import com.donald.crowdfunding.models.CommentModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.DateFormat;
import java.util.Date;


public class AddComment extends AppCompatActivity {

    private String postId;
    private String commenter;
    private EditText mComment;
    private Button submitComment;
    private DatabaseReference commentRef;
    private String uid;
    private int commentCount = 0;

    private ProgressDialog dialog;

    private String commentDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        mComment = findViewById(R.id.commentDesc);
        submitComment = findViewById(R.id.commentSubmit);

        commentRef = FirebaseDatabase.getInstance().getReference().child("Comments");
        dialog = new ProgressDialog(AddComment.this);

        commentDate = DateFormat.getDateTimeInstance().format(new Date());



        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            postId=extras.getString("projectid");
            commenter = extras.getString("commenter");
            uid = extras.getString("uid");
        }

        commentRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    commentCount = dataSnapshot.child("totalComments").getValue(Integer.class);

                }catch (Exception e){

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        submitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = mComment.getText().toString().trim();
                if (TextUtils.isEmpty(comment)){
                    MDToast.makeText(AddComment.this,"Comment cannot be empty",
                   MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show();
                } else {

                    dialog.setMessage("Commenting...");
                    dialog.show();
                    try {
                        String id = commentRef.push().getKey();
                        CommentModel commentModel = new CommentModel(comment,commenter,commentDate);
                        commentRef.child(postId).child(id).setValue(commentModel);
                        commentRef.child(postId).child(id).child("uid").setValue(uid);
                        commentCount=commentCount+1;
                        commentRef.child(postId).child("totalComments").setValue(commentCount);


                        dialog.dismiss();
                        Intent back = new Intent(AddComment.this,SinglePost.class);
                        back.putExtra("position",postId);

                        finish();

                    }catch (Exception e){
                        e.fillInStackTrace();
                    }

                }
            }
        });



    }
}
