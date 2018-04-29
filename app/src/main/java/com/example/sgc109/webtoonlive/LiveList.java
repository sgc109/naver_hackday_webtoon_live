package com.example.sgc109.webtoonlive;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LiveList extends AppCompatActivity {
    private static final String EXTRA_IS_WRITER = "extra_is_writer";
    private boolean mIsWriter;
    public static Intent newIntent(Context context, boolean isWriter) {
        Intent intent = new Intent(context, LiveList.class);
        intent.putExtra(EXTRA_IS_WRITER, isWriter);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_list);

        if(getIntent() != null) {
            mIsWriter = getIntent().getBooleanExtra(EXTRA_IS_WRITER, false);
        }
    }
}
