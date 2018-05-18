package com.example.sgc109.webtoonlive.CustomView;

/**
 * Created by jyoung on 2018. 5. 17..
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sgc109.webtoonlive.R;

public class CommentView extends LinearLayout {

    private TextView commentText;
    private ImageView arrowImg;
    private LinearLayout commentShowLayout;


    public CommentView(Context context){
        this(context, null);
    }

    public CommentView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public CommentView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);

        initView();
        hideOrShowView();
    }
    public void hideOrShowView(){
        if(commentShowLayout.getVisibility() == GONE)
            commentShowLayout.setVisibility(VISIBLE);
        else
            commentShowLayout.setVisibility(GONE);
    }

    private void initView(){

        String layoutInfService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(layoutInfService);
        View v = li.inflate(R.layout.comment_view, this, false);

        commentText = v.findViewById(R.id.comment_show_field);
        arrowImg = v.findViewById(R.id.comment_arrow);
        commentShowLayout = v.findViewById(R.id.comment_show_layout);

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        commentText.setLayoutParams(layoutParams);

        addView(v);
    }

    public void setArrowImgPos(int x){
        LinearLayout.LayoutParams arrowParams =(LinearLayout.LayoutParams)arrowImg.getLayoutParams();
        arrowParams.setMargins(x,20,0,0);
        arrowImg.setLayoutParams(arrowParams);
    }

    public void setCommentText(String comment){
        commentText.setText(comment);
    }

    public boolean getCommentVisibility(){
        return commentShowLayout.getVisibility() == VISIBLE ? true : false;
    }


}

