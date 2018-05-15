package com.example.sgc109.webtoonlive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.sgc109.webtoonlive.CustomView.BottomEmotionBar;
import com.example.sgc109.webtoonlive.CustomView.FixedSizeImageView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class LiveActivity extends AppCompatActivity {
    private static final String TAG = "LiveActivity";
    protected static final String EXTRA_LIVE_KEY = "extra_live_key";

    protected RecyclerView mRecyclerView;
    protected DatabaseReference mDatabase;
    private LinearLayoutManager mLayoutManager;
    protected String mLiveKey;

    protected int mDeviceWidth;
    protected BottomEmotionBar emotionBar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        mLiveKey = getIntent().getStringExtra(EXTRA_LIVE_KEY);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecyclerView = findViewById(R.id.activity_live_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mDeviceWidth = displayMetrics.widthPixels;
        emotionBar = findViewById(R.id.emotionBar);

        RecyclerView.Adapter<SceneImageViewHolder> adapter = new RecyclerView.Adapter<SceneImageViewHolder>() {
            @NonNull
            @Override
            public SceneImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_scene, parent, false);
                return new SceneImageViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull SceneImageViewHolder holder, int position) {
                holder.bindImage(position, getItemCount() -1);
            }

            @Override
            public int getItemCount() {
                return 35;
            }
        };

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        emotionBar.toggleShowing();
                        break;
                }
                return false;
            }
        });

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
    }

    class SceneImageViewHolder extends RecyclerView.ViewHolder {
        FixedSizeImageView mImageView;



        public SceneImageViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.list_item_scene_image_view);
            mImageView.setLastPosition(false);
        }

        public void bindImage(int position, int lastPosition) {
//            Log.d("debug","cut" + (position + 1));
//            mImageView.setImageResource(getResources().getIdentifier("cut" + (position + 1), "drawable", getPackageName()));
            Glide.with(LiveActivity.this)
                    .load(getResources().getIdentifier("cut" + (position + 1), "drawable", getPackageName()))
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(false))
                    .into(mImageView);
            if(position == lastPosition) {
                mImageView.setLastPosition(true);
            }
        }

    }

}
