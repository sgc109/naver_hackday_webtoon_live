package com.example.sgc109.webtoonlive.CustomView;

/**
 * Created by jyoung on 2018. 5. 17..
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sgc109.webtoonlive.R;

public class CommentView extends LinearLayout {

    private TextView commentText;
    private ImageView arrowImg;


    public CommentView(Context context){
        this(context, null);
        Log.d("init?", "1");
    }

    public CommentView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
        Log.d("init?", "2");

    }

    public CommentView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        Log.d("init?", "3");

        initView();
//        hideOrShowView();
    }
    private void hideOrShowView(){
        if(commentText.getVisibility() == GONE){
            commentText.setVisibility(VISIBLE);
            arrowImg.setVisibility(VISIBLE);
        }
        else{
            commentText.setVisibility(GONE);
            arrowImg.setVisibility(GONE);
        }
    }

    private void initView(){

        Log.d("init?", "들어와랑");
        String layoutInfService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(layoutInfService);
        View v = li.inflate(R.layout.comment_view, this, false);

        commentText = v.findViewById(R.id.comment_show_field);
        arrowImg = v.findViewById(R.id.comment_arrow);
        addView(v);
    }

    public void setArrowImgPos(int x){
        LinearLayout.LayoutParams arrowParams =(LinearLayout.LayoutParams)arrowImg.getLayoutParams();
        arrowParams.setMargins(x,40,0,0);
        arrowImg.setLayoutParams(arrowParams);
    }

    public void setCommentText(String comment){
        commentText.setText(comment);
    }



}

