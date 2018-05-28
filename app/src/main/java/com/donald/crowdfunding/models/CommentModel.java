package com.donald.crowdfunding.models;

public class CommentModel {
    private String comment,commenter,date;

    public CommentModel() {
    }

    public CommentModel(String comment, String commenter, String date) {
        this.comment = comment;
        this.commenter = commenter;
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
