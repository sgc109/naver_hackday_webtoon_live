package com.example.sgc109.webtoonlive.CustomView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.sgc109.webtoonlive.R;
import com.example.sgc109.webtoonlive.data.EmotionType;
import com.example.sgc109.webtoonlive.dto.EmotionModel;

/**
 * Created by SeungKoo on 2018. 5. 14..
 */

public class EmotionView extends FrameLayout {

    private static final String TAG = "EmotionView";
    public EmotionView(Context context) {
        super(context);
    }

    public EmotionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmotionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void showEmotion(EmotionModel emotionModel){
        int size = (int)getContext().getResources().getDimension(R.dimen.bottom_emotion_bar_item_size);
        final LottieAnimationView lottieAnimationView = new LottieAnimationView(getContext());
        ViewGroup.LayoutParams params = new LayoutParams(size, size);
        lottieAnimationView.setLayoutParams(params);
        Log.d(TAG, getWidth() + ":"+ getWidth());
        lottieAnimationView.setX(getWidth() / ( emotionModel.type.getCode() + 1));
        lottieAnimationView.setY(getHeight() - (float)(getResources().getDimension(R.dimen.bottom_emotion_bar_item_size)*1.5));
        lottieAnimationView.setAnimation(EmotionType.getResource(emotionModel.type));
        lottieAnimationView.playAnimation();

        //lottieAnimationView.getDuration()
        addView(lottieAnimationView);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                removeView(lottieAnimationView);
            }
        }, lottieAnimationView.getDuration());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
