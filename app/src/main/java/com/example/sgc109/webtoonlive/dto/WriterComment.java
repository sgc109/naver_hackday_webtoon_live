package com.example.sgc109.webtoonlive.dto;

/**
 * Created by jyoung on 2018. 5. 15..
 */

public class WriterComment {
    private String content;
    private int time;

    public WriterComment() {
    }

    public WriterComment(String content, int time) {
        this.content = content;
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
