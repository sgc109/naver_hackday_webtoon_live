package com.example.sgc109.webtoonlive;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by jyoung on 2018. 5. 9..
 */

public class CommentContentDialog extends Dialog {
    TextView commentText;
    private String content;
    public CommentContentDialog(Context context, String content){
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.content = content;
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
        commentText.setText(content);

    }


}
