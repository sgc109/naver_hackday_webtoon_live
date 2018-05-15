package com.example.sgc109.webtoonlive;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by jyoung on 2018. 5. 9..
 */

public class CommentContentDialog extends Dialog {
    private TextView commentText;
    private Button likeBtn, cancelBtn;

    private int likeCount;
    private View.OnClickListener likeClickListener;

    private String content;
    public CommentContentDialog(Context context, String content, int likeCount, View.OnClickListener likeClickListener){
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.content = content;
        this.likeCount = likeCount;
        this.likeClickListener = likeClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.1f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_comment_content);
        commentText = findViewById(R.id.comment_text);
        likeBtn = findViewById(R.id.like_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        commentText.setText(content);


        setClickListener();
    }

    private void setClickListener(){
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        likeBtn.setText(likeCount);
        likeBtn.setOnClickListener(likeClickListener);
    }


}
