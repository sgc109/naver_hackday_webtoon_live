package com.example.sgc109.webtoonlive;

import android.content.res.Resources;

import java.util.Date;

/**
 * Created by sgc109 on 2018-05-15.
 */

public class LiveInfo {
    public String key;
    public String title;
    public String state;
    public Long startDate;
    public Long endDate;

    LiveInfo() {
    }

    LiveInfo(String key, String title, String state) {
        this.key = key;
        this.title = title;
        this.state = state;
        this.startDate = System.currentTimeMillis();
        this.endDate = 0L;
    }
}
