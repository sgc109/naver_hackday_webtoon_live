package com.sqoo.toutchtocreateview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by SeungKoo on 2018. 5. 8..
 */

public class TouchView extends RelativeLayout implements View.OnTouchListener {


    private GestureDetector mDetector;
    private InputControllerView controllerView;
    private View pointerView;

    private boolean isViewAdded = false;


    final int controllerSize = getResources().getDimensionPixelSize(R.dimen.controller_size);
    final int pointerSize = getResources().getDimensionPixelSize(R.dimen.pointer_size);


    // offsetX, offsetY 는 pointer 의 크기에 따른 보정을 위한 값
    float offsetX, offsetY;

    // toX, toY 는 pointer 를 이동시킬 offset
    float toX, toY;

    // cX, cY 는 view 상의 좌표
    float cX, cY;

    // centerRawX, centerRawY는 화면상의 좌표
    float centerRawX, centerRawY;

    private int eventNumber = 0;

    final int POINTER_OFFSET = (controllerSize - pointerSize) / 2;

    /**
     * 생성자들
     */

    public TouchView(Context context) {
        super(context);
        init();
    }

    public TouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    /**
     * 초기화
     */
    private void init() {
        controllerView = new InputControllerView(getContext());
        pointerView = controllerView.getPointer();
        LinearLayout.LayoutParams paramLinear = new LinearLayout.LayoutParams(controllerSize, controllerSize);
        controllerView.setLayoutParams(paramLinear);
        pointerView.setX(POINTER_OFFSET);
        pointerView.setY(POINTER_OFFSET);

        mDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent event) {
                super.onLongPress(event);
                showControllerView(event);
                centerRawX = event.getRawX();
                centerRawY = event.getRawY();
                cX = event.getX();
                cY = event.getY();
            }
        });

        this.setOnTouchListener(this);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case (MotionEvent.ACTION_MOVE):
                Log.d("TEST","ACTION_MOVE");

                toX = event.getRawX() - offsetX;
                toY = event.getRawY() - offsetY;

                if (isViewAdded) {

                    // 원 밖을 벗어나지 않도록 해줍니다.
                    float dist = getDistance(event.getX(), event.getY());
                    float radius = controllerSize / 2 - pointerSize / 2;
                    if (dist > radius) {
                        toX = ((event.getRawX() - centerRawX) * radius / dist) - offsetX + centerRawX;
                        toY = ((event.getRawY() - centerRawY) * radius / dist) - offsetX + centerRawX;
                    }
                    controllerView.movePointer(toX, toY);
                    onAngleDetect(event, dist);
                }
                return true;

                // ACTION_UP , ACTION_DOWN 은 true를 반환해야 한다.
            case (MotionEvent.ACTION_UP):
                Log.d("TEST","ACTION_UP");
                removeControllerView();
                return true;
            case MotionEvent.ACTION_DOWN:
                Log.d("TEST","ACTION_DOWN");
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }



    /**
     * @param event
     * @param distance
     * @return 0~3 이벤트 번호, -1 비정상종료 (너무 조금 드래그한 경우)
     * ToDo 이벤트 번호 하드코딩된것 static final 로 바꿔야합니다.
     */
    private int onAngleDetect(MotionEvent event, float distance) {
        float angle = getAngle(event.getX(), event.getY());
        if (distance < (controllerSize /2)) {
            eventNumber = -1;
            return eventNumber;
        }

        if (angle >= 45 && getAngle(event.getX(), event.getY()) < 135) {
            controllerView.setBackground(getResources().getDrawable(R.drawable.circle_background_left));
            eventNumber = 0;
        }
        if (angle >= 135 && getAngle(event.getX(), event.getY()) < 225) {
            controllerView.setBackground(getResources().getDrawable(R.drawable.circle_background_down));
            eventNumber = 1;
        }
        if (angle >= 225 && getAngle(event.getX(), event.getY()) < 315) {
            controllerView.setBackground(getResources().getDrawable(R.drawable.circle_background_right));
            eventNumber = 2;
        }
        if (angle >= 315 && getAngle(event.getX(), event.getY()) < 360 ||
                angle >= 360 && getAngle(event.getX(), event.getY()) < 45) {
            controllerView.setBackground(getResources().getDrawable(R.drawable.circle_background_up));
            eventNumber = 3;
        }
        return eventNumber;
    }

    /**
     * @param x
     * @param y
     * @return
     */
    private float getAngle(float x, float y) {
        float angle = (float) Math.toDegrees(Math.atan2(x - cX, y - cY));
        return angle + 180;
    }

    private float getDistance(float x, float y) {
        return (float) Math.sqrt((x - cX) * (x - cX) + (y - cY) * (y - cY));
    }

    private void showControllerView(MotionEvent event) {

        controllerView.setX(event.getX() - controllerSize / 2);
        controllerView.setY(event.getY() - controllerSize / 2);
        offsetX = event.getRawX() - pointerView.getX();
        offsetY = event.getRawY() - pointerView.getY();
        controllerView.setBackground(getResources().getDrawable(R.drawable.circle_background_normal));
        if (!isViewAdded) {
            addView(controllerView);
            isViewAdded = true;
        }

    }


    private void removeControllerView() {
        controllerView.movePointer(POINTER_OFFSET, POINTER_OFFSET);
        if (isViewAdded) {
            this.removeView(controllerView);
            isViewAdded = false;
        }
        Toast.makeText(getContext(), eventNumber + "번 이벤트 발생\n위치 " + cX + ":" + cY, Toast.LENGTH_SHORT).show();
    }


}

