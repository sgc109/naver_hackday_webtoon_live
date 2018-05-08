package com.sqoo.toutchtocreateview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by SeungKoo on 2018. 5. 8..
 */

public class InputControllerView extends ConstraintLayout {

    private static final String TAG = "EmotionView";
    private View convertView;


    /**
     * 생성자들
     */
    public InputControllerView(Context context) {
        super(context);
        init();
    }

    public InputControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InputControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 초기화
     */
    private void init() {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_input_controller, null);
        this.addView(convertView);
    }


    public View getPointer() {
        return convertView.findViewById(R.id.pointer);
    }

    public void movePointer(float toX , float toY){
        getPointer().animate()
                .x(toX)
                .y(toY)
                .setDuration(0)
                .start();
    }

    @Override
    public void setBackground(Drawable drawable){
        convertView.findViewById(R.id.backgroundView).setBackground(drawable);
    }


}
