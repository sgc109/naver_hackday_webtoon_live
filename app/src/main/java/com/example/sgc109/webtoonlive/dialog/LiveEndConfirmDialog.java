package com.example.sgc109.webtoonlive.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.example.sgc109.webtoonlive.R;
import com.example.sgc109.webtoonlive.ReaderLiveActivity;

/**
 * Created by SeungKoo on 2018. 5. 17..
 */

public class LiveEndConfirmDialog extends Dialog {
    public LiveEndConfirmDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_live_end_confirm);
        final Context mContext = context;
        findViewById(R.id.button_exit_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mContext instanceof ReaderLiveActivity){
                    ((ReaderLiveActivity)mContext).finish();
                }
            }
        });
    }
}
