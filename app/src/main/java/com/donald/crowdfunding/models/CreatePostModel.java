package com.donald.crowdfunding.models;

public class CreatePostModel {
    private String uId;
    private String postId;
    private String postTitle;
    private String postCategory;
    private String postDescription;
    private String postLocation;
    private String postTargetDay;
    private String postImage;

    public CreatePostModel() {

    }

    public CreatePostModel(String uId, String postId, String postTitle, String postCategory,
                           String postDescription, String postLocation, String postTargetDay,
                           String postFund, String postImage) {
        this.uId = uId;
        this.postId = postId;
        this.postTitle = postTitle;
        this.postCategory = postCategory;
        this.postDescription = postDescription;
        this.postLocation = postLocation;
        this.postTargetDay = postTargetDay;
        this.postFund = postFund;
        this.postImage = postImage;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostCategory() {
        return postCategory;
    }

    public void setPostCategory(String postCategory) {
        this.postCategory = postCategory;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostLocation() {
        return postLocation;
    }

    public void setPostLocation(String postLocation) {
        this.postLocation = postLocation;
    }

    public String getPostTargetDay() {
        return postTargetDay;
    }

    public void setPostTargetDay(String postTargetDay) {
        this.postTargetDay = postTargetDay;
    }

    public String getPostFund() {
        return postFund;
    }

    public void setPostFund(String postFund) {
        this.postFund = postFund;
    }

    private String postFund;

}
