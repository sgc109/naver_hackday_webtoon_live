package com.example.sgc109.webtoonlive;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button mWriterTypeButton;
    Button mWatcherTypeButton;
    Button mDefaultTypeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWriterTypeButton = findViewById(R.id.main_activity_user_type_button_writer);
        mWatcherTypeButton = findViewById(R.id.main_activity_user_type_button_reader);
        mDefaultTypeButton = findViewById(R.id.main_activity_user_type_button_default);

        mWriterTypeButton.setOnClickListener(this);
        mWatcherTypeButton.setOnClickListener(this);
        mDefaultTypeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.main_activity_user_type_button_writer:
                intent = LiveListActivity.newIntent(this, true);
                startActivity(intent);
                break;
            case R.id.main_activity_user_type_button_reader:
                intent = LiveListActivity.newIntent(this, false);
                startActivity(intent);
                break;
            case R.id.main_activity_user_type_button_default:
                intent = new Intent(this, DefaultWebtoonActivity.class);
                startActivity(intent);
        }
    }
}
