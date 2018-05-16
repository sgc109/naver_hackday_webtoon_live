package com.example.sgc109.webtoonlive.CustomView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by SeungKoo on 2018. 5. 14..
 */
public class FixedSizeImageView extends android.support.v7.widget.AppCompatImageView {
    private static final String TAG = "FixedSizeImageView";
    private static final float IMAGE_WIDTH = 690;
    private static final float IMAGE_HEIGHT = 1600;
    private static final float IMAGE_HEIGHT_LAST = 1576;

    private boolean isLastPosition = false;

    public FixedSizeImageView(Context context) {
        super(context);
    }

    public FixedSizeImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedSizeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     *  계산된 화면의 가로 크기를 가져온 후 이미지의 비율대로 FixedSizeImageView의 크기를 고정해 준다.
     *  마지막 이미지의 경우 비율이 맞지 않아 따로 처리해주지 않고 super.onMeasure 만 호출되도록 한다.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure");
        int width = getMeasuredWidth();
        if (!isLastPosition) {
            Log.d("mydebug", "not last!!");
            setMeasuredDimension(width, ((int) (width * (IMAGE_HEIGHT / IMAGE_WIDTH))));
        } else {
            Log.d("mydebug", "last!!");
            setMeasuredDimension(width, ((int) (width * (IMAGE_HEIGHT_LAST / IMAGE_WIDTH))));
        }
    }

    /**
     * 마지막 포지션의 이미지인지 여부 확인
     * @param isLastPosition
     */
    public void setLastPosition(boolean isLastPosition) {
        Log.d(TAG, "setLastPosition");
        this.isLastPosition = isLastPosition;
    }
}
