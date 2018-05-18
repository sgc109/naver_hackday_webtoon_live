package com.example.sgc109.webtoonlive;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by jyoung on 2018. 5. 9..
 */

public class CommentWriterDialog extends Dialog {
    Button yesBtn, noBtn;

    private View.OnClickListener yesClickListener;

    public CommentWriterDialog(Context context,
                               View.OnClickListener yesClickListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.yesClickListener = yesClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_comment_write);
        yesBtn = findViewById(R.id.yes_btn);
        noBtn = findViewById(R.id.no_btn);

        yesBtn.setOnClickListener(yesClickListener);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        /*FIXME
            다이얼로그 xml 수정 필요.
         */
    }


}
