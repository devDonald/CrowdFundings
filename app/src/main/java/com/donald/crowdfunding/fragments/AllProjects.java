package com.donald.crowdfunding.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.donald.crowdfunding.business.R;
import com.donald.crowdfunding.models.CreatePostModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllProjects extends Fragment {
    private RecyclerView allPost_RV;
    private DatabaseReference allPostReference;
    private Context context;

    private FirebaseRecyclerAdapter<CreatePostModel,AllPostViewHolder> firebaseRecyclerAdapter;


    public AllProjects() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_projects, container, false);
        context = getContext();

        allPost_RV= view.findViewById(R.id.homeRecycler);
        allPostReference= FirebaseDatabase.getInstance().getReference().child("PostProjects");
        allPost_RV.setHasFixedSize(true);
        allPost_RV.setLayoutManager(new LinearLayoutManager(context));

        startProject();

        return view;
    }

    private void startProject() {

        firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<CreatePostModel, AllPostViewHolder>(
                CreatePostModel.class,
                R.layout.all_projects_item,
                AllPostViewHolder.class,
                allPostReference
        ) {

            @Override
            protected void populateViewHolder(AllPostViewHolder viewHolder,CreatePostModel model, int position) {
                viewHolder.setProjectName(model.getPostTitle());
                viewHolder.setDate(model.getPostDate());
                viewHolder.setCreatedBy(model.getPostOwner());

                viewHolder.setTargetFunds(model.getPostFund());
                // viewHolder.setOccupation(model.getOccupation());
                viewHolder.setImage(context,model.getPostImage());


            }

            @Override
            public AllPostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                AllPostViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                viewHolder.setOnClickListener(new AllPostViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        //Toast.makeText(getApplicationContext(), "Item clicked at " + position, Toast.LENGTH_SHORT).show();

                        Intent singlePost=new Intent(context, SinglePost.class);
                        singlePost.putExtra("position",firebaseRecyclerAdapter.getRef(position).getKey());

                        context.startActivity(singlePost);



                    }

                });

                return viewHolder;
            }
        };
        allPost_RV.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AllPostViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public AllPostViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.onItemClick(view, getAdapterPosition());

                }
            });
        }
        void setProjectName(String project_names){
            TextView projectName=(TextView)mView.findViewById(R.id.tv_project_name);
            projectName.setText(project_names);

        }
        void setDate(String dates){
            TextView date =(TextView)mView.findViewById(R.id.tv_date);
            date.setText(dates);
        }
        void setCreatedBy(String posted_by){
            TextView postedBy=(TextView)mView.findViewById(R.id.tv_createdBy);
            postedBy.setText(posted_by);
        }
        void setTargetFunds(String target_fund){
            TextView targetFunds=(TextView)mView.findViewById(R.id.tv_target_fund);
            targetFunds.setText(target_fund);
        }

        public void setProjectLike(String project_like){
            TextView projectLike=(TextView)mView.findViewById(R.id.post_like);
            projectLike.setText(project_like);
        }
        public void setProjectDisLike(String dproject_like){
            TextView dprojectLike=(TextView)mView.findViewById(R.id.post_like);
            dprojectLike.setText(dproject_like);
        }
        public void setProjectComment(String project_comment){
            TextView projectComment=(TextView)mView.findViewById(R.id.post_like);
            projectComment.setText(project_comment);
        }
        void setImage(Context ctx, String image){
            ImageView imageView =(ImageView)mView.findViewById(R.id.projectImage);
            Picasso.with(ctx).load(image).into(imageView);
        }

        private AllPostViewHolder.ClickListener mClickListener;

        public interface ClickListener{
            public void onItemClick(View view, int position);
        }

        public void setOnClickListener(AllPostViewHolder.ClickListener clickListener){
            mClickListener = clickListener;
        }


    }

}
