package com.example.sgc109.webtoonlive;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReaderLiveActivity extends LiveActivity {
    private LiveInfo mLiveInfo;
    private ChildEventListener mNewScrollAddedListener;
    private ValueEventListener mLiveStateChangeListener;
//    private List<VerticalPositionChanged> mScrollHistories;
    private Long mStartedTime;
    public static Intent newIntent(Context context, String liveKey) {
        Intent intent = new Intent(context, ReaderLiveActivity.class);
        intent.putExtra(EXTRA_LIVE_KEY, liveKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStartedTime = System.currentTimeMillis();
//        mScrollHistories = new ArrayList<>();
        mDatabase
                .child(getString(R.string.firebase_db_live_list))
                .child(mLiveKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("DEBUG", "read LiveInfo from FirebaseDB by mLiveKey");
                        mLiveInfo = dataSnapshot.getValue(LiveInfo.class);
                        String STATE_ON_AIR = getString(R.string.live_state_on_air);

                        if (mLiveInfo.state.equals(STATE_ON_AIR)) {
                            addDataChangeListeners();
                        } else {
                            getScrollDatas();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("DEBUG", "failed to read LiveInfo from FirebaseDB by mLiveKey");
                    }
                });


//        settingCommentListeners();
    }

    private void getScrollDatas() {
        mDatabase
                .child(getString(R.string.firebase_db_scroll_history))
                .child(mLiveKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            final VerticalPositionChanged scrollHistory = child.getValue(VerticalPositionChanged.class);
//                            mScrollHistories.add(scrollHistory);
                            Long passedTime = System.currentTimeMillis() - mStartedTime;
                            Long timeAfter = scrollHistory.time - passedTime;
                            if(timeAfter < 0) {
                                continue;
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("mydebug", "run()");
                                    double percentage = scrollHistory.offsetProportion;

                                    int nextY = (int) (percentage * mDeviceWidth);
                                    int curY = mRecyclerView.computeVerticalScrollOffset();
                                    mRecyclerView.smoothScrollBy(0, nextY - curY);
                                }
                            }, timeAfter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void addDataChangeListeners() {
        mLiveStateChangeListener =
                mDatabase
                        .child(getString(R.string.firebase_db_live_list))
                        .child(mLiveKey)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                LiveInfo liveInfo = dataSnapshot.getValue(LiveInfo.class);
                                if (liveInfo.state.equals(getString(R.string.live_state_over))) {
                                    Toast.makeText(ReaderLiveActivity.this, "방송이 종료되었습니다!", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

        mNewScrollAddedListener =
                mDatabase
                        .child(getString(R.string.firebase_db_scroll_history))
                        .child(mLiveKey)
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                VerticalPositionChanged data = dataSnapshot.getValue(VerticalPositionChanged.class);
                                double percentage = data.offsetProportion;

                                int nextY = (int) (percentage * mDeviceWidth);
                                int curY = mRecyclerView.computeVerticalScrollOffset();
                                mRecyclerView.smoothScrollBy(0, nextY - curY);
                                Log.d("scroll_debug", "nextY : " + nextY + ", curY : " + curY);
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


//    private void settingCommentListeners(){
//
//        mDatabase.child(getString(R.string.comment_history));
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNewScrollAddedListener != null) {
            mDatabase.child(getString(R.string.firebase_db_scroll_history))
                    .child(mLiveKey)
                    .removeEventListener(mNewScrollAddedListener);
        }
        if (mLiveStateChangeListener != null) {
            mDatabase.child(getString(R.string.firebase_db_live_list))
                    .child(mLiveKey)
                    .removeEventListener(mLiveStateChangeListener);
        }
//        if (mIsWriter) {
//            mHandler.removeCallbacks(mPeriodicScrollPosCheck);
//        }
    }
}
