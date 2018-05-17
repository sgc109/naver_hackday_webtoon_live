package com.example.sgc109.webtoonlive;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by sgc109 on 2018-05-17.
 */

public class MyLayoutManager extends LinearLayoutManager {
    private int mImageHeight;
    public MyLayoutManager(Context context) {
        super(context);
        mImageHeight = context.getResources().getInteger(R.integer.entire_image_height);
    }

    @Override
    public int computeVerticalScrollRange(RecyclerView.State state) {
        return mImageHeight;
    }
}
