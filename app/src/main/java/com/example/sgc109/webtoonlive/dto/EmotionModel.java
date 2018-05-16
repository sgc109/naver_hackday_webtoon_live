package com.example.sgc109.webtoonlive.dto;

import com.example.sgc109.webtoonlive.data.EmotionType;

/**
 * Created by SeungKoo on 2018. 5. 15..
 */

public class EmotionModel {
    public long timeStamp;
    public EmotionType type;

    public EmotionModel() {
    }

    public EmotionModel(long date, EmotionType type) {
        this.timeStamp = date;
        this.type = type;
    }

}
