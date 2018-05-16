package com.example.sgc109.webtoonlive.dto;

/**
 * Created by jyoung on 2018. 5. 16..
 */

public class CommentClick {
    private String commentId;
    private int time;

    public CommentClick(String commentId, int time) {
        this.commentId = commentId;
        this.time = time;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
