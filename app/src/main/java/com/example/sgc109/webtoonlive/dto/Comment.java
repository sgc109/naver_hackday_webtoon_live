package com.example.sgc109.webtoonlive.dto;

/**
 * Created by jyoung on 2018. 5. 9..
 */

public class Comment {

    private String writer;
    private String content;
    private int posX;
    private int posY;
    private int deviceWidth;
    private int deviceHeight;
    private int time;
    private double scrollLength;

    public Comment() {
    }

    public Comment(String writer, String content, int posX, int posY, int deviceWidth, int deviceHeight, int time, double scrollLength) {
        this.writer = writer;
        this.content = content;
        this.posX = posX;
        this.posY = posY;
        this.deviceWidth = deviceWidth;
        this.deviceHeight = deviceHeight;
        this.time = time;
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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getScrollLength() {
        return scrollLength;
    }

    public void setScrollLength(double scrollLength) {
        this.scrollLength = scrollLength;
    }
}
