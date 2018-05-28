package com.donald.crowdfunding.models;

public class LikesModel {
    String uid;

    public LikesModel(){

    }

    public LikesModel(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
