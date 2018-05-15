package com.example.sgc109.webtoonlive;

import android.content.res.Resources;

import java.util.Date;

/**
 * Created by sgc109 on 2018-05-15.
 */

public class LiveInfo {
    public Long key;
    public String title;
    public String state;
    public Date date;
    public Long runningTime;

    LiveInfo() {
    }

    LiveInfo(Long key, String title, String state) {
        this.key = key;
        this.title = title;
        this.state = state;
        this.date = new Date();
    }

    public int compareTo(LiveInfo rhs) {
        String STATE_OVER = Resources.getSystem().getString(R.string.live_state_over);
        String STATE_ON_AIR = Resources.getSystem().getString(R.string.live_state_on_air);

        if (state == STATE_OVER && rhs.state == STATE_ON_AIR) {
            return 1;
        }
        if (state == STATE_ON_AIR && rhs.state == STATE_OVER) {
            return -1;
        }
        if(key < rhs.key) {
            return 1;
        }
        return -1;
    }
}
