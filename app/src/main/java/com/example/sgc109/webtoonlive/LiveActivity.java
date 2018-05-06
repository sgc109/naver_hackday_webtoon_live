package com.example.sgc109.webtoonlive;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LiveActivity extends AppCompatActivity {
    private static final String EXTRA_IS_WRITER = "extra_is_writer";
    private boolean mIsWriter;
    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabase;
    private String mLastKey;
    private Long mLastCheckedTime;
    private LinearLayoutManager mLayoutManager;
    private Runnable  mPeriodicScrollPosCheck;
    private int mHeight;
    private int mCurY;
    private ChildEventListener mChildEventListenerHandle;
    private Handler mHandler;

    public static Intent newIntent(Context context, boolean isWriter) {
        Intent intent = new Intent(context, LiveActivity.class);
        intent.putExtra(EXTRA_IS_WRITER, isWriter);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        if (getIntent() != null) {
            mIsWriter = getIntent().getBooleanExtra(EXTRA_IS_WRITER, false);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecyclerView = findViewById(R.id.activity_live_recycler_view);
        mLastCheckedTime = new Date().getTime();
        mLayoutManager = new LinearLayoutManager(this);

        RecyclerView.Adapter<SceneImageViewHolder> adapter = new RecyclerView.Adapter<SceneImageViewHolder>() {
            @NonNull
            @Override
            public SceneImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_scene, parent, false);
                return new SceneImageViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull SceneImageViewHolder holder, int position) {
                holder.bindImage(position);
            }

            @Override
            public int getItemCount() {
                return 30;
            }
        };

        if(mIsWriter) {
            RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    pushScrollPosToDB();
                }
            };

            RecyclerView.OnFlingListener flingListener = new RecyclerView.OnFlingListener() {
                @Override
                public boolean onFling(int velocityX, int velocityY) {
                    Log.d("scroll_debug", "onFling()!");
                    return false;
                }
            };
            mRecyclerView.addOnScrollListener(scrollListener);
            mRecyclerView.setOnFlingListener(flingListener);
        } else {
            DatabaseReference ref = mDatabase.child(getString(R.string.firebase_db_pos_history));
            mChildEventListenerHandle = ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    VerticalPositionChanged data = dataSnapshot.getValue(VerticalPositionChanged.class);
                    double percentage = data.posPercent;
                    int totalScrollLength = mRecyclerView.computeVerticalScrollRange() - mRecyclerView.computeVerticalScrollExtent();

                    int curY = (int)(percentage * totalScrollLength);
                    int dy = curY - mCurY;
                    Log.d("scroll_debug", "prvY : " + mCurY + ", curY : " + curY + ", dy : " + dy);
//                    mRecyclerView.smoothScrollBy(0, curY - mRecyclerView.computeVerticalScrollOffset());
//                    mRecyclerView.smoothScrollToPosition(curY);
                    mRecyclerView.smoothScrollBy(0, dy);
                    mCurY = curY;
//                    ((LinearLayoutManager)mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(0, pos);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
        if (!mIsWriter) {
            mRecyclerView.setNestedScrollingEnabled(false);
        }

        mHandler = new Handler();
        if(mIsWriter) {
            mPeriodicScrollPosCheck = new Runnable() {
                @Override
                public void run() {
                    Log.d("runnable_debug", "run()");
                    pushScrollPosToDB();
                    mHandler.postDelayed(this, 500);
                }
            };
            mHandler.post(mPeriodicScrollPosCheck);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(!mIsWriter) return true;
        return super.dispatchTouchEvent(ev);
    }

    private void pushScrollPosToDB() {
        Long now = new Date().getTime();
        if(now - mLastCheckedTime >= 500) {
            int totalScrollLength = mRecyclerView.computeVerticalScrollRange() - mRecyclerView.computeVerticalScrollExtent();
            int offset = mRecyclerView.computeVerticalScrollOffset();
            double posPercent = (double)offset / totalScrollLength;

            Log.d("scroll_debug", "curY : " + posPercent);
            DatabaseReference ref = mDatabase.child(getString(R.string.firebase_db_pos_history));
            ref
                    .push()
                    .setValue(new VerticalPositionChanged(posPercent));
            mLastCheckedTime = now;
        }
    }

    class SceneImageViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public SceneImageViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.list_item_scene_text_view);
        }

        public void bindImage(int position) {
            mTextView.setText(Integer.toString(position));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mChildEventListenerHandle != null) {
            mDatabase.child(getString(R.string.firebase_db_pos_history)).removeEventListener(mChildEventListenerHandle);
        }
        if(mIsWriter) {
            mHandler.removeCallbacks(mPeriodicScrollPosCheck);
        }
    }
}
