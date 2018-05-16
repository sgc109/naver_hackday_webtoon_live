package com.example.sgc109.webtoonlive.CustomView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.sgc109.webtoonlive.R;
import com.example.sgc109.webtoonlive.data.EmotionType;
import com.example.sgc109.webtoonlive.model.EmotionModel;

import java.util.ArrayList;

/**
 * Created by SeungKoo on 2018. 5. 14..
 */

public class BottomEmotionBar extends LinearLayout implements View.OnTouchListener {


    private static final String TAG = "BottomEmotionBar";
    private static final float SCLAE_VAL = 1.2f;
    private boolean isShowing = false;
    private View convertView;
    private ArrayList<LottieAnimationView> itemLottie = new ArrayList<>();


    /**
     * Constructor
     */
    public BottomEmotionBar(Context context) {
        super(context);
        init();
    }

    public BottomEmotionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomEmotionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * initialize
     */
    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        convertView = li.inflate(R.layout.bottom_emotion_bar, this, false);
        addView(convertView);
        itemLottie.add((LottieAnimationView) convertView.findViewById(R.id.lottie_00));
        itemLottie.add((LottieAnimationView) convertView.findViewById(R.id.lottie_01));
        itemLottie.add((LottieAnimationView) convertView.findViewById(R.id.lottie_02));
        itemLottie.add((LottieAnimationView) convertView.findViewById(R.id.lottie_03));
        itemLottie.add((LottieAnimationView) convertView.findViewById(R.id.lottie_04));
        //itemButton.add((Button) convertView.findViewById(R.id.itemButton3));
        //itemButton.add((Button) convertView.findViewById(R.id.itemButton4));
        for (LottieAnimationView lottieAnimationView : itemLottie) {
            lottieAnimationView.setOnTouchListener(this);
        }


    }

    /**
     * View가 보이는 상태에서는 가려주고, 안보이는 상태에선 보여준다.
     */
    public void toggleShowing() {
        Log.d(TAG, "toggleShowing," + isShowing);
        if (isShowing) {
            hideView();
        } else {
            showView();
        }
    }

    /**
     * View 를 보여준다.
     */
    public void showView() {
        Log.d(TAG, "show," + isShowing);
        TransitionManager.beginDelayedTransition(this);
        this.setVisibility(View.VISIBLE);
        isShowing = true;
    }

    /**
     * View 를 가려준다.
     */
    public void hideView() {
        Log.d(TAG, "hide," + isShowing);
        TransitionManager.beginDelayedTransition(this);
        this.setVisibility(View.GONE);
        isShowing = false;
    }

    /**
     *  ToDo 미구현.
     *  감정표현 버튼 클릭시 애니메이션 및 선택된 View 확대 등의 작업을 해야 합니다.
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        boolean isLottieView = false;
        LottieAnimationView lottieAnimationView = null;
        if(view instanceof LottieAnimationView){
            isLottieView = true;
            lottieAnimationView = (LottieAnimationView)view;
        }
        Rect rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isLottieView){
                    playAnimation(lottieAnimationView);
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (isLottieView){
                    stopAnimation(lottieAnimationView);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!rect.contains(view.getLeft() + (int) motionEvent.getX(), view.getTop() + (int) motionEvent.getY())) {
                    //view.setScaleX(1.0f);
                    //view.setScaleY(1.0f);
                    return true;
                }
                return true;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
                if (isLottieView){
                    stopAnimation(lottieAnimationView);
                }
                return true;
        }
        return false;
    }

    /**
     * lottieView 의 크기를 키워주고 애니메이션을 실행합니다.
     * xml에 default 로 자동 반복재생을 하도록 해 두었습니다.
     * @param view
     */
    private void playAnimation(LottieAnimationView view){
        android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width *= SCLAE_VAL;
        params.height *= SCLAE_VAL;
        view.setLayoutParams(params);
        //view.setScaleX(1.5f);
        //view.setScaleY(1.5f);
        view.playAnimation();
    }
    /**
     * lottieView 의 크기를 원상복구하고 애니메이션을 멈춥니다.
     * @param view
     */
    private void stopAnimation(LottieAnimationView view){
        android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width /= SCLAE_VAL;
        params.height /= SCLAE_VAL;
        view.setLayoutParams(params);
        view.cancelAnimation();
        view.setFrame(0);
    }

    private void sampling(int emotionType){
        EmotionType.fromCode(emotionType);
    }

    public void pushToFirebase(EmotionType emotionType) {
        new EmotionModel(emotionType);

/*
        int offset = mRecyclerView.computeVerticalScrollOffset();
        Log.d("scroll_debug", "offset : " + offset);
        double posPercent = (double) offset / mDeviceWidth;

        DatabaseReference ref = mDatabase.child(getString(R.string.firebase_db_scroll_history));
        ref.push()
                .setValue(new VerticalPositionChanged(posPercent, new Date()));
                */
    }
}
