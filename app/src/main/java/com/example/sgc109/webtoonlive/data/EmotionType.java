package com.example.sgc109.webtoonlive.data;

/**
 * Created by SeungKoo on 2018. 5. 15..
 */

public enum EmotionType {

    LOVE(0),
    TONGUE(1),
    WINK(2),
    SHOCK(3),
    STAR(4),
    NONE(100);


    private int id;

    EmotionType(int id) {
        this.id = id;
    }

    public int getCode() {
        return id;
    }

    public static EmotionType fromCode(int code){
        switch (code){
            case 0:
                return LOVE;
            case 1:
                return TONGUE;
            case 2:
                return WINK;
            case 3:
                return SHOCK;
            case 4:
                return STAR;
            default:
                return NONE;
        }
    }

}
