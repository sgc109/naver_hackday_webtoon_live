package com.example.sgc109.webtoonlive.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.sgc109.webtoonlive.R;


/**
 * Created by jyoung on 2018. 5. 14..
 */

public class CommentPointView extends LinearLayout {

    private RelativeLayout point;
    private String comment;

    public CommentPointView(Context context){
        this(context, null);
    }

    public CommentPointView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public CommentPointView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView(){
        String layoutInfService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(layoutInfService);
        View v = li.inflate(R.layout.comment_point_view, this, false);

        addView(v);

        point = (RelativeLayout)findViewById(R.id.comment_point);

    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public String getComment(){
        return comment;
    }



}
