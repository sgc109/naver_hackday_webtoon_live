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
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.sgc109.webtoonlive.R;

import java.util.ArrayList;

/**
 * Created by SeungKoo on 2018. 5. 14..
 */

public class BottomEmotionBar extends LinearLayout implements View.OnTouchListener {


    private static final long TRANSITION_DURATION = 400;
    private static final String TAG = "BottomEmotionBar";

    private boolean isShowing;
    private View convertView;
    private ArrayList<Button> itemButton = new ArrayList<>();


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
        itemButton.add((Button) convertView.findViewById(R.id.itemButton0));
        itemButton.add((Button) convertView.findViewById(R.id.itemButton1));
        itemButton.add((Button) convertView.findViewById(R.id.itemButton2));
        itemButton.add((Button) convertView.findViewById(R.id.itemButton3));
        itemButton.add((Button) convertView.findViewById(R.id.itemButton4));
        isShowing = true;
        for (Button button : itemButton) {
            button.setOnTouchListener(this);
        }

    }

    public void toggle() {
        Log.d(TAG, "toggle," + isShowing);
        if (isShowing) {
            TransitionManager.beginDelayedTransition(this);
            this.setVisibility(View.GONE);
            isShowing = false;
        } else {
            TransitionManager.beginDelayedTransition(this);
            this.setVisibility(View.VISIBLE);
            isShowing = true;
        }
    }

    public void hide() {
        Log.d(TAG, "hide," + isShowing);
        if (isShowing) {
            TransitionManager.beginDelayedTransition(this);
            this.setVisibility(View.GONE);
            isShowing = false;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Rect rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                view.setScaleX(1.8f);
                view.setScaleY(1.8f);
                return true;
            case MotionEvent.ACTION_UP:
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!rect.contains(view.getLeft() + (int) motionEvent.getX(), view.getTop() + (int) motionEvent.getY())) {
                    view.setScaleX(1.0f);
                    view.setScaleY(1.0f);
                    return true;
                }
                return true;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
                return true;
        }
        return false;
    }
}
