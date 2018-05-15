package com.example.sgc109.webtoonlive;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
}
