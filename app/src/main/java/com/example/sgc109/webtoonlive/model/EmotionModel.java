package com.example.sgc109.webtoonlive.model;

import com.example.sgc109.webtoonlive.data.EmotionType;

/**
 * Created by SeungKoo on 2018. 5. 15..
 */

public class EmotionModel {
    private long date;
    private int type;

    public EmotionModel(long date, int type) {
        this.date = date;
        this.type = type;
    }

    public EmotionModel(EmotionType emotionType){
        date = System.currentTimeMillis();
        this.type = emotionType.getCode();
    }

    public long getDate() {
        return date;
    }

    public int getType() {
        return type;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setType(int type) {
        this.type = type;
    }
}
