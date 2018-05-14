package com.example.sgc109.webtoonlive.CustomView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by SeungKoo on 2018. 5. 14..
 */
public class FixedSizeImageView extends android.support.v7.widget.AppCompatImageView {
    private static final float IMAGE_WIDTH = 690;
    private static final float IMAGE_HEIGHT = 1600;

    public FixedSizeImageView(Context context) {
        super(context);
    }

    public FixedSizeImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedSizeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, ((int) (width * (IMAGE_HEIGHT / IMAGE_WIDTH))));

    }
}
