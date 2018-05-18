package com.example.sgc109.webtoonlive.custom_view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.transition.ChangeBounds;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.example.sgc109.webtoonlive.R;
import com.example.sgc109.webtoonlive.data.EmotionType;

import java.util.ArrayList;

/**
 * EmotionView 의 Child 로 존재해야 합니다.
 * Created by SeungKoo on 2018. 5. 14..
 */

public class BottomEmotionBar extends ConstraintLayout implements View.OnTouchListener {

    private static final String TAG = "BottomEmotionBar";
    private static final float SCALE_VAL = 1.2f;
    private boolean isShowing = false;

    private ArrayList<Float> showingPosition = new ArrayList<>();

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
        for (int i = 0; i < itemLottie.size(); i++) {
            itemLottie.get(i).setTag(i);
            itemLottie.get(i).setOnTouchListener(this);
            showingPosition.add(0f);
        }

    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < itemLottie.size(); i++) {
            showingPosition.set(i, itemLottie.get(i).getX());
        }
//        listener.onPositionChange(showingPosition);
    }

    public ArrayList<Float> getShowingPosition() {
        return showingPosition;
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

        ChangeBounds showingTransition = new ChangeBounds();
        showingTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                Log.d(TAG, "onTransitionStart," + isShowing);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                isShowing = true;
                Log.d(TAG, "onTransitionEnd," + isShowing);
            }

            @Override
            public void onTransitionCancel(Transition transition) {
                Log.d(TAG, "onTransitionCancel," + isShowing);
            }

            @Override
            public void onTransitionPause(Transition transition) {
                Log.d(TAG, "onTransitionPause," + isShowing);
            }

            @Override
            public void onTransitionResume(Transition transition) {
                Log.d(TAG, "onTransitionResume," + isShowing);
            }
        });

        TransitionManager.go(new Scene(this), showingTransition);
        TransitionManager.beginDelayedTransition(this);
        this.setVisibility(View.VISIBLE);

    }

    /**
     * View 를 가려준다.
     */
    public void hideView() {
        isShowing = false;
        Log.d(TAG, "hide," + isShowing);
        TransitionManager.beginDelayedTransition(this);
        this.setVisibility(View.GONE);
    }


    /**
     * 감정표현 버튼 클릭시 애니메이션 및 선택된 View 확대 등의 작업을 해야 합니다.
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        boolean isLottieView = false;
        LottieAnimationView lottieAnimationView = null;
        if (view instanceof LottieAnimationView) {
            isLottieView = true;
            lottieAnimationView = (LottieAnimationView) view;
        }
        Rect rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isLottieView) {
                    playAnimation(lottieAnimationView);
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (isLottieView) {
                    stopAnimation(lottieAnimationView);
                }

                // 영역 안에서 손을 뗏을 경우
                if (isShowing &&  rect.contains(view.getLeft() + (int) motionEvent.getX(), view.getTop() + (int) motionEvent.getY())) {
                    //push top Firebase // sampling하고 보내야 한다
                    hideView();
                    pushToFirebase(EmotionType.fromCode((int) view.getTag()));
                    return true;
                }
                return true;
        }
        return false;
    }

    /**
     * lottieView 의 크기를 키워주고 애니메이션을 실행합니다.
     * xml에 default 로 자동 반복재생을 하도록 해 두었습니다.
     *
     * @param view
     */
    private void playAnimation(LottieAnimationView view) {
        android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width *= SCALE_VAL;
        params.height *= SCALE_VAL;
        view.setLayoutParams(params);
        view.playAnimation();
    }

    /**
     * lottieView 의 크기를 원상복구하고 애니메이션을 멈춥니다.
     *
     * @param view
     */
    private void stopAnimation(LottieAnimationView view) {
        android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width /= SCALE_VAL;
        params.height /= SCALE_VAL;
        view.setLayoutParams(params);
    }

    //Todo Sampling 해야함
    private void pushToFirebase(EmotionType emotionType) {
        ((EmotionView)getParent()).pushToFireBase(emotionType);
    }
}
