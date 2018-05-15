package com.example.sgc109.webtoonlive.data;

/**
 * Created by SeungKoo on 2018. 5. 15..
 */

public enum EmotionType {

    LIKE(0),
    LOVE(1),
    DISLIKE(2),
    FOO(3),
    BOO(4),
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
                return LIKE;
            case 1:
                return LOVE;
            case 2:
                return DISLIKE;
            case 3:
                return FOO;
            case 4:
                return BOO;
            default:
                return NONE;
        }
    }

}
