package com.example.sgc109.webtoonlive;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.sgc109.webtoonlive.data.EmotionType;
import com.example.sgc109.webtoonlive.dto.EmotionModel;
import com.example.sgc109.webtoonlive.dto.WriterComment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class ReaderLiveActivity extends LiveActivity {
    private SeekBar mSeekBar;
    private LiveInfo mLiveInfo;
    private ChildEventListener mNewScrollAddedListener;
    private ValueEventListener mLiveStateChangeListener;
    private ChildEventListener mWriterCommentAddedListener;
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
        mSeekBar = findViewById(R.id.live_seek_bar);
        mSeekBar.setVisibility(View.VISIBLE);

        // 감정표현 입력 레이아웃 초기화
        emotionBar.setLiveKey(mLiveKey);
        emotionBar.setStartedTime(mStartedTime);

        mDatabase
                .child(getString(R.string.firebase_db_live_list))
                .child(mLiveKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("DEBUG", "read LiveInfo from FirebaseDB by mLiveKey");
                        mLiveInfo = dataSnapshot.getValue(LiveInfo.class);
                        String STATE_ON_AIR = getString(R.string.live_state_on_air);

                        if (mLiveInfo != null) {
                            if (mLiveInfo.state.equals(STATE_ON_AIR)) {
                                addDataChangeListeners();
                                settingCommentListeners();
                                mSeekBar.setVisibility(View.GONE);
                            } else {
                                getRecordingDatas();
                                emotionBar.setVisibility(View.GONE);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("DEBUG", "failed to read LiveInfo from FirebaseDB by mLiveKey");
                    }
                });


        setRecyclerView();
    }


    private void test(EmotionModel emotion){
        emotionView.showEmotion(emotion);
    }

    private void getRecordingDatas() {
        mDatabase
                .child(getString(R.string.firebase_db_scroll_history))
                .child(mLiveKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            final VerticalPositionChanged scrollHistory = child.getValue(VerticalPositionChanged.class);
//                            mScrollHistories.add(scrollHistory);
                            Long passedTime = System.currentTimeMillis() - mStartedTime;
                            Long timeAfter = scrollHistory.time - passedTime;
                            if (timeAfter < 0) {
                                continue;
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    double percentage = scrollHistory.offsetProportion;

                                    int nextY = (int) (percentage * mDeviceWidth);
                                    int curY = mRecyclerView.computeVerticalScrollOffset();
                                    mRecyclerView.smoothScrollBy(0, nextY - curY);
                                }
                            }, timeAfter);
                        }
                        Long latestTime = mLiveInfo.endDate - mLiveInfo.startDate;
                        ObjectAnimator animation = ObjectAnimator.ofInt(mSeekBar, "progress", 10000);
                        animation.setDuration(latestTime);
                        animation.setInterpolator(new LinearInterpolator());
                        animation.start();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mDatabase
                .child(getString(R.string.comment_history))
                .child(mLiveKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long latestTime = 0L;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            final WriterComment commentHistory = child.getValue(WriterComment.class);
                            latestTime = Math.max(latestTime, commentHistory.getTime());
                            Long passedTime = System.currentTimeMillis() - mStartedTime;
                            Long timeAfter = commentHistory.getTime() - passedTime;
                            if (timeAfter < 0) {
                                continue;
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toasty.custom(ReaderLiveActivity.this, commentHistory.getContent(), null,
                                            Color.parseColor("#00C73C"), Toast.LENGTH_SHORT, false, true).show();
                                }
                            }, timeAfter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        // 감정표현 화면 표시 관련 코드입니다

        mDatabase
                .child(getString(R.string.firebase_db_emotion_history))
                .child(mLiveKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long latestTime = 0L;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            final EmotionModel emotionHistory = child.getValue(EmotionModel.class);
                        //    latestTime = Math.max(latestTime, emotionHistory.timeStamp);
                        //    Long passedTime = System.currentTimeMillis() - mStartedTime;
                            Long timeAfter = emotionHistory.timeStamp;
                            if (timeAfter < 0) {
                                continue;
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    EmotionType type = emotionHistory.type;
                                    test(emotionHistory);
                                }
                            }, timeAfter);
                        }
                        ObjectAnimator animation = ObjectAnimator.ofInt(mSeekBar, "progress", 10000);
                        animation.setDuration(latestTime);
                        animation.setInterpolator(new LinearInterpolator());
                        animation.start();
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


    private void settingCommentListeners() {

        mWriterCommentAddedListener =
                mDatabase.child(getString(R.string.comment_history))
                        .child(mLiveKey)
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                WriterComment writerComment = dataSnapshot.getValue(WriterComment.class);

                                Toasty.custom(ReaderLiveActivity.this, writerComment.getContent(), null,
                                        Color.parseColor("#00C73C"), Toast.LENGTH_SHORT, false, true).show();
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

    /*
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return true;
    }
    */

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
        if (mWriterCommentAddedListener != null) {
            mDatabase.child(getString(R.string.comment_history))
                    .child(mLiveKey)
                    .removeEventListener(mWriterCommentAddedListener);
        }
    }

    private void setRecyclerView(){
        //mRecyclerView.setEnabled(false);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    emotionBar.toggleShowing();
                }
                return true;
            }
        });

    }

}
