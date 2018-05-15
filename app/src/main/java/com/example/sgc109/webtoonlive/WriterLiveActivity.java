package com.example.sgc109.webtoonlive;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.Writer;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class WriterLiveActivity extends LiveActivity {

    public static Intent newIntent(Context context, String liveKey) {
        Intent intent = new Intent(context, WriterLiveActivity.class);
        intent.putExtra(EXTRA_LIVE_KEY, liveKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("DEBUG", "WriterLiveActivity");
        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    pushScrollPosToDB();
                }

                emotionBar.hideView();
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);
    }

    private void askEndLiveOrNot() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        mDatabase.child(getString(R.string.firebase_db_live_list))
                                .child(mLiveKey)
                                .child(getString(R.string.live_info_state))
                                .setValue(getString(R.string.live_state_over))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        WriterLiveActivity.this.finish();
                                    }
                                });
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.end_live_warning_message)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener).show();
    }

    @Override
    public void onBackPressed() {
        askEndLiveOrNot();
    }
}
