package com.example.sgc109.webtoonlive.CustomView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
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
    public EmotionView(Context context) {
        super(context);
    }

    public EmotionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmotionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(){

    }

    public void showEmotion(EmotionModel emotionModel){

        int size = (int)getContext().getResources().getDimension(R.dimen.bottom_emotion_bar_item_size);
        LottieAnimationView lottieAnimationView = new LottieAnimationView(getContext());
        ViewGroup.LayoutParams params = new LayoutParams(size, size);
        lottieAnimationView.setLayoutParams(params);
        lottieAnimationView.setX(100);
        lottieAnimationView.setY(100);
        lottieAnimationView.setAnimation(EmotionType.getResource(emotionModel.getType()));
        lottieAnimationView.playAnimation();
        addView(lottieAnimationView);
    }
}
