package com.donald.crowdfunding.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.donald.crowdfunding.business.R;
import com.donald.crowdfunding.models.CommentModel;
import com.donald.crowdfunding.models.CreatePostModel;
import com.donald.crowdfunding.models.ProfileModel;
import com.donald.crowdfunding.util.Util;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class SinglePost extends AppCompatActivity {

    private DatabaseReference postDetails;
    private DatabaseReference profileRef;
    private String position;
    private TextView singlePostTitle;
    private ImageView singlePostPhoto;
    private TextView singlePostCreatedBy;
    private TextView singlePostDesc;
    private TextView singlePostCategory;
    private TextView singlePostLocation;
    private TextView singlePostTarget;
    private TextView singlePostFunded;
    private TextView singlePostRemDays;
    private TextView singlePostLike;
    private TextView singlePostDislike;
    private TextView singlePostComment;
    private RecyclerView comments;
    private Button donate;
    private String projectId;
    private String userName;
    private FirebaseUser currentUser;
    private String uid;
    private DatabaseReference commentRef;
    private FirebaseRecyclerAdapter<CommentModel,AllCommentViewHolder> firebaseRecyclerAdapter;

    private Util util = new Util();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        singlePostTitle = findViewById(R.id.singlePostTitle);
        singlePostPhoto = findViewById(R.id.singlepostPhoto);
        singlePostCreatedBy = findViewById(R.id.singlePostCreatedBy);
        singlePostDesc = findViewById(R.id.singlePostdesc);
        singlePostCategory = findViewById(R.id.singlePostCategory);
        singlePostLocation = findViewById(R.id.singlePostLocation);
        singlePostTarget = findViewById(R.id.singlePostTarget);
        singlePostFunded = findViewById(R.id.singlePostFunded);
        singlePostRemDays = findViewById(R.id.singlePostRemainDays);
        singlePostLike = findViewById(R.id.singlePost_like);
        singlePostComment = findViewById(R.id.singlePost_comment);
        comments = findViewById(R.id.singlePostCommentRecycler);
        donate = findViewById(R.id.singlePostDonateBtn);

        comments.setHasFixedSize(true);
        comments.setLayoutManager(new LinearLayoutManager(SinglePost.this));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
            Log.d("uid",uid);
        }

        postDetails = FirebaseDatabase.getInstance().getReference().child("PostProjects");
        profileRef = FirebaseDatabase.getInstance().getReference().child("Profiles");
        commentRef = FirebaseDatabase.getInstance().getReference().child("Comments");

        commentRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        profileRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProfileModel model = dataSnapshot.getValue(ProfileModel.class);
                userName = model.getName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            position = extras.getString("position");
            if (position!=null){
                DatabaseReference userRef = postDetails.child(position);

                Log.d("userref",""+userRef);
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Log.d("ds",""+ds);

                        CreatePostModel details = dataSnapshot.getValue(CreatePostModel.class);
                        try {

                            singlePostTitle.setText(details.getPostTitle());
                            singlePostCategory.setText(details.getPostCategory());
                            singlePostCreatedBy.setText(details.getPostOwner());
                            singlePostLocation.setText(details.getPostLocation());
                            singlePostDesc.setText(details.getPostDescription());

                            double totalAmount1 = Double.parseDouble(details.getPostFund());
                            String totalAmount2 = ((String.valueOf(util.getFormatedAmount(totalAmount1))));

//                            singlePostTarget.setText(getString(R.string.naira_symbol).concat(totalAmount2));

                            singlePostTarget.setText(totalAmount2);
                            projectId = details.getPostId();
                            Picasso.with(SinglePost.this).load(details.getPostImage()).into(singlePostPhoto);

                        }catch (Exception e){
                            e.getMessage();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                userRef.addListenerForSingleValueEvent(valueEventListener);


            }

        }

        singlePostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent comment = new Intent(SinglePost.this,AddComment.class);
                comment.putExtra("projectid",projectId);
                comment.putExtra("commenter",userName);
                comment.putExtra("uid",uid);
                startActivity(comment);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<CommentModel, AllCommentViewHolder>(
                CommentModel.class,
                R.layout.comment_item,
                AllCommentViewHolder.class,
                commentRef
        ) {

            @Override
            protected void populateViewHolder(AllCommentViewHolder viewHolder, CommentModel model, int position) {
                viewHolder.setComment(model.getComment());
                viewHolder.setCommenter(model.getCommenter());
                viewHolder.setCommentDate(model.getDate());

            }

            @Override
            public AllCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                AllCommentViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);


                return viewHolder;
            }
        };
        comments.setAdapter(firebaseRecyclerAdapter);

    }

    public static class AllCommentViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public AllCommentViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

        }
        public void setComment(String post_comment){
            TextView postComment=(TextView)mView.findViewById(R.id.tv_comment);
            postComment.setText(post_comment);

        }
        public void setCommenter(String post_commenter){
            TextView postCommenter =(TextView)mView.findViewById(R.id.tv_Date);
            postCommenter.setText(post_commenter);
        }
        public void setCommentDate(String comment_date){
            TextView commentDate=(TextView)mView.findViewById(R.id.tv_commenter);
            commentDate.setText(comment_date);
        }

    }

}
