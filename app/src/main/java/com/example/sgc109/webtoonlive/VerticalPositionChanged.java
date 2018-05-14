package com.example.sgc109.webtoonlive;

import java.util.Date;

public class VerticalPositionChanged {
    public double offsetProportion;
    public Date curDate;
    public VerticalPositionChanged(){}
    public VerticalPositionChanged(double offsetProportion, Date curDate){
        this.offsetProportion = offsetProportion;
        this.curDate = curDate;
    }
}
