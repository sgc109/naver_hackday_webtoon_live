package com.example.sgc109.webtoonlive.CustomView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import com.airbnb.lottie.LottieAnimationView;
import com.example.sgc109.webtoonlive.LiveInfo;
import com.example.sgc109.webtoonlive.R;
import com.example.sgc109.webtoonlive.data.EmotionType;
import com.example.sgc109.webtoonlive.dto.EmotionModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashSet;
import java.util.Random;

/**
 * Created by SeungKoo on 2018. 5. 14..
 */

public class EmotionView extends ConstraintLayout {
    private static final String TAG = "EmotionView";

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private LiveInfo mLiveInfo = null;

    public BottomEmotionBar inputBar;

    public HashSet<String> keySet = new HashSet<>();

    public EmotionView(Context context) {
        super(context);
        init();
    }

    public EmotionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmotionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inputBar = new BottomEmotionBar(getContext());
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomToBottom = LayoutParams.PARENT_ID;
        params.bottomMargin = (int) getResources().getDimension(R.dimen.bottom_margin);
        inputBar.setLayoutParams(params);
        addView(inputBar);
        inputBar.setVisibility(View.GONE);
    }


    public void setLiveInfo(LiveInfo liveInfo) {
        mLiveInfo = liveInfo;
    }


    public void showEmotion(EmotionModel emotionModel, boolean isMine) {
        int size = (int) getContext().getResources().getDimension(R.dimen.bottom_emotion_bar_item_size);
        final LottieAnimationView lottieAnimationView = new LottieAnimationView(getContext());
        ViewGroup.LayoutParams params = new LayoutParams(size, size);
        lottieAnimationView.setLayoutParams(params);

        float xPosition;
        if (isMine) {
            xPosition = inputBar.getShowingPosition().get(emotionModel.type.getCode());//showingPosition.get(emotionModel.type.getCode());
        } else {
            xPosition = new Random().nextInt(getWidth() - (int) getResources().getDimension(R.dimen.bottom_emotion_bar_item_size));
        }

        lottieAnimationView.setX(xPosition);
        lottieAnimationView.setY(getHeight() - (float) (getResources().getDimension(R.dimen.bottom_emotion_bar_item_size) * 1.5));
        lottieAnimationView.setAnimation(EmotionType.getResource(emotionModel.type));
        lottieAnimationView.playAnimation();

        Log.d(TAG, lottieAnimationView.getX() + ":" + lottieAnimationView.getY());

        addView(lottieAnimationView);

        moveUpAndFadeOut(lottieAnimationView, lottieAnimationView.getDuration());

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                removeView(lottieAnimationView);
            }
        }, lottieAnimationView.getDuration());
    }

    private void moveUpAndFadeOut(View view, long duration) {

        Animation translateAnimation = new TranslateAnimation(0, 0, 0, -(float) (getResources().getDimension(R.dimen.bottom_emotion_bar_item_size) * 1.5));
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(duration);
        Animation fadeAnim = new AlphaAnimation(1f, 0.2f);
        fadeAnim.setDuration(duration);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(fadeAnim);
        view.startAnimation(animationSet);
        view.setTag(view.getId(), true);
    }


    public void pushToFireBase(EmotionType emotionType) {
        if (mLiveInfo != null) {
            EmotionModel emotionModel = new EmotionModel(System.currentTimeMillis() - mLiveInfo.startDate, emotionType);
            DatabaseReference ref = mDatabase.child(getContext().getString(R.string.firebase_db_emotion_history)).child(mLiveInfo.key);
            String key = ref.push().getKey();
            ref.child(key).setValue(emotionModel);
            keySet.add(key);
            Log.d("AAA", key);
            showEmotion(emotionModel, true);
        }
    }
}
