package com.donald.crowdfunding.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.donald.crowdfunding.business.R;
import com.donald.crowdfunding.models.CreatePostModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProjects extends Fragment {

    private RecyclerView myPost_RV;
    private DatabaseReference myPostReference;
    private Context context;

    FirebaseRecyclerAdapter<CreatePostModel, MyPostViewHolder> firebaseRecyclerAdapter;

    public MyProjects() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_projects, container, false);
    }

    private static class MyPostViewHolder extends RecyclerView.ViewHolder{
        View view;
        public MyPostViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.onItemClick(view, getAdapterPosition());
                }
            });
        }

        public void setProjectName(String project_names){
            TextView projectName= view.findViewById(R.id.tv_project_name);
            projectName.setText(project_names);

        }
        public void setDate(String dates){
            TextView date = view.findViewById(R.id.tv_date);
            date.setText(dates);
        }
        public void setCreatedBy(String posted_by){
            TextView postedBy= view.findViewById(R.id.tv_createdBy);
            postedBy.setText(posted_by);
        }
        public void setTargetFunds(String target_fund){
            TextView targetFunds= view.findViewById(R.id.tv_target_fund);
            targetFunds.setText(target_fund);
        }

        public void setProjectLike(String project_like){
            TextView projectLike= view.findViewById(R.id.post_like);
            projectLike.setText(project_like);
        }
        public void setProjectDisLike(String dproject_like){
            TextView dprojectLike= view.findViewById(R.id.post_like);
            dprojectLike.setText(dproject_like);
        }
        public void setProjectComment(String project_comment){
            TextView projectComment= view.findViewById(R.id.post_like);
            projectComment.setText(project_comment);
        }
        public void setImage(Context ctx, String image){
            ImageView imageView = view.findViewById(R.id.projectImage);
            Picasso.with(ctx).load(image).into(imageView);
        }

        private MyPostViewHolder.ClickListener mClickListener;

        public interface ClickListener{
            public void onItemClick(View view, int position);
        }

        public void setOnClickListener(MyPostViewHolder.ClickListener clickListener){
            mClickListener = clickListener;
        }
    }
}
