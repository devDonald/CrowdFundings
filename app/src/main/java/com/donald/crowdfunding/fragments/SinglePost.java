package com.donald.crowdfunding.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.donald.crowdfunding.activities.Payment;
import com.donald.crowdfunding.business.R;
import com.donald.crowdfunding.models.CommentModel;
import com.donald.crowdfunding.models.CreatePostModel;
import com.donald.crowdfunding.models.DisplayComment;
import com.donald.crowdfunding.models.LikesModel;
import com.donald.crowdfunding.models.ProfileModel;
import com.donald.crowdfunding.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


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
    private EditText donationAmt;
    private RecyclerView commentsRecycler;
    private Button donate;
    private String projectId;
    private String userName;
    private FirebaseUser currentUser;
    private String uid;
    private ArrayList<CommentModel> commentList;
    private DatabaseReference commentRef;

    private Util util = new Util();
    private DatabaseReference likeRef;
    private int totalComment = 0;
    private int like = 0;
    private RecyclerviewAdapter recycler;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private final String PREFS_NAME = "pref";


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
        commentsRecycler = findViewById(R.id.singlePostCommentRecycler);
        donationAmt = findViewById(R.id.donationAmount);
        donate = findViewById(R.id.singlePostDonateBtn);

//        commentList = new ArrayList<>();

        preferences = getApplicationContext().getSharedPreferences(
                PREFS_NAME, Activity.MODE_PRIVATE);

        commentsRecycler.setHasFixedSize(true);
        commentsRecycler.setLayoutManager(new LinearLayoutManager(SinglePost.this));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
            Log.d("uid", uid);
        }

        postDetails = FirebaseDatabase.getInstance().getReference().child("PostProjects");
        profileRef = FirebaseDatabase.getInstance().getReference().child("Profiles");
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        commentRef = FirebaseDatabase.getInstance().getReference().child("Comments");

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
        if (extras != null) {
            position = extras.getString("position");
            if (position != null) {
                DatabaseReference userRef = postDetails.child(position);

                Log.d("userref", "" + userRef);
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
                            // commentRef.child(projectId);


                        } catch (Exception e) {
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
                Intent comment = new Intent(SinglePost.this, AddComment.class);
                comment.putExtra("projectid", projectId);
                comment.putExtra("commenter", userName);
                comment.putExtra("uid", uid);
                startActivity(comment);
            }
        });


        singlePostLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LikesModel likes = new LikesModel(uid);
                likeRef.child(position).setValue(likes);

            }
        });

        loadComments();
        likes();

        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                String donationAmount = donationAmt.getText().toString().trim();

//                editor.putString("donationAmt", donationAmount);
//                editor.apply();

                startActivity(new Intent(SinglePost.this, Payment.class));
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadComments();
        likes();


    }

    public void loadComments() {
        try {
            commentRef.child(position).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                            CommentModel listdata = new CommentModel();

                            DisplayComment commentModel = dataSnapshot1.getValue(DisplayComment.class);

                            assert commentModel != null;
                            String comment = commentModel.getComment();
                            String commenter = commentModel.getCommenter();
                            String date = commentModel.getDate();
                            listdata.setComment(comment);
                            listdata.setCommenter(commenter);
                            listdata.setDate(date);
                            Log.d("comment", "" + comment);

                            commentList.add(listdata);
                            Log.d("listdata", "" + listdata.getComment());


                        }

                        recycler = new RecyclerviewAdapter(getApplicationContext(), commentList);

                        Log.d("list", "" + commentList.size());

                        commentsRecycler.setAdapter(recycler);

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {
            e.getMessage();

        }
    }

    public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyHolder> {

        ArrayList<CommentModel> listdata = new ArrayList<CommentModel>();
        private Context context;

        RecyclerviewAdapter(Context context, ArrayList<CommentModel> listdata) {
            this.context = context;
            this.listdata = listdata;
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);

            MyHolder myHolder = new MyHolder(view, context, listdata);
            return myHolder;
        }


        public void onBindViewHolder(MyHolder holder, int position) {
            CommentModel data = listdata.get(position);
            holder.comment.setText(data.getComment());
            holder.commenter.setText(data.getCommenter());
            holder.date.setText(data.getDate());
        }

        @Override
        public int getItemCount() {
            return listdata.size();
        }


        class MyHolder extends RecyclerView.ViewHolder {
            TextView comment, commenter, date;

            ArrayList<CommentModel> commentModel = new ArrayList<CommentModel>();
            Context context;

            public MyHolder(View itemView, Context context, ArrayList<CommentModel> listdata) {
                super(itemView);
                comment = itemView.findViewById(R.id.tv_comment);
                commenter = itemView.findViewById(R.id.tv_commenter);
                date = itemView.findViewById(R.id.tv_Date);

            }
        }


    }

    public void likes() {
        commentRef.child(position).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    totalComment = dataSnapshot.child("totalComments").getValue(Integer.class);
                    singlePostComment.setText("" + totalComment);

                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
