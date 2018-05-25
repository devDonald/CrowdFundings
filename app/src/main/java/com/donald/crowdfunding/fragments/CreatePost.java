package com.donald.crowdfunding.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.donald.crowdfunding.business.R;
import com.donald.crowdfunding.utils.Utils;
import com.valdesekamdem.library.mdtoast.MDToast;

public class CreatePost extends Fragment {

    private final String TAG = CreatePost.class.getSimpleName();
    private EditText postTitle, postDescription, postLocation, postCategory, postFund, postRemainingDay;
    private Button postPicture, submitButton;
    private Utils utils;
    private String title, description, location, category, fund, remainingDay;


    public CreatePost() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_create_post, container, false);

        postTitle = view.findViewById(R.id.post_title);
        postDescription = view.findViewById(R.id.post_description);
        postLocation = view.findViewById(R.id.post_location);
        postCategory = view.findViewById(R.id.post_category);
        postFund = view.findViewById(R.id.post_fund);
        postRemainingDay = view.findViewById(R.id.post_remaining_day);
        postPicture = view.findViewById(R.id.post_profile_pix);
        submitButton = view.findViewById(R.id.submit_btn);

        utils = new Utils();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = postTitle.getText().toString().trim();
                description = postDescription.getText().toString().trim();
                location = postLocation.getText().toString().trim();
                category = postCategory.getText().toString().trim();
                fund = postFund.getText().toString().trim();
                remainingDay = postRemainingDay.getText().toString().trim();

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
                if (TextUtils.isEmpty(remainingDay)) {
                    postRemainingDay.setError("Please enter validity period for this crowd funding");
                    return;
                }
                if (!utils.isNetworkAvailable(getActivity())) {
                    MDToast.makeText(getActivity(), "Network unavailable",
                            MDToast.TYPE_WARNING, Toast.LENGTH_SHORT).show();
                } else {
                    //start activity
                    MDToast.makeText(getActivity(), "Post upload is successful",
                            MDToast.TYPE_SUCCESS, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

}
