package com.example.sgc109.webtoonlive;

import java.util.Date;

public class VerticalPositionChanged {
    public double offsetProportion;
    public Long curDate;
    public VerticalPositionChanged(){}
    public VerticalPositionChanged(double offsetProportion){
        this.offsetProportion = offsetProportion;
        curDate = System.currentTimeMillis();
    }
}
