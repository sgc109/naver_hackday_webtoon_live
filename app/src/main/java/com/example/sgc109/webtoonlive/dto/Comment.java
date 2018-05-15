package com.example.sgc109.webtoonlive.dto;

/**
 * Created by jyoung on 2018. 5. 9..
 */

public class Comment {

    private String writer;
    private String content;
    private int likeCount;
    private int posX;
    private int posY;
    private int deviceWidth;
    private int deviceHeight;
    private double scrollLength;

    public Comment() {
    }

    public Comment(String writer, String content, int likeCount, int posX, int posY, int deviceWidth, int deviceHeight, double scrollLength) {
        this.writer = writer;
        this.content = content;
        this.likeCount = likeCount;
        this.posX = posX;
        this.posY = posY;
        this.deviceWidth = deviceWidth;
        this.deviceHeight = deviceHeight;
        this.scrollLength = scrollLength;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getDeviceWidth() {
        return deviceWidth;
    }

    public void setDeviceWidth(int deviceWidth) {
        this.deviceWidth = deviceWidth;
    }

    public int getDeviceHeight() {
        return deviceHeight;
    }

    public void setDeviceHeight(int deviceHeight) {
        this.deviceHeight = deviceHeight;
    }

    public double getScrollLength() {
        return scrollLength;
    }

    public void setScrollLength(double scrollLength) {
        this.scrollLength = scrollLength;
    }
}
