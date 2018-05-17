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
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.example.sgc109.webtoonlive.CustomView.CommentPointView;
import com.example.sgc109.webtoonlive.dto.Comment;
import com.example.sgc109.webtoonlive.dto.CommentClick;
import com.example.sgc109.webtoonlive.dto.EmotionModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class ReaderLiveActivity extends LiveActivity {
    private ProgressBar mSeekBar;
    private LiveInfo mLiveInfo;
    private ChildEventListener mNewScrollAddedListener;
    private ValueEventListener mLiveStateChangeListener;
    private ChildEventListener mWriterCommentAddedListener;
    private ChildEventListener mWriterCommentShowListener;
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
        mSeekBar = findViewById(R.id.live_progress_bar);
        mSeekBar.setVisibility(View.VISIBLE);

        commentFieldScroll.setVisibility(View.VISIBLE);
        commentInfoScroll.setVisibility(View.VISIBLE);


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
                                addCommentIndicatorListener();
                                getRecordingDatas();
                                mEmotionView.inputBar.setVisibility(View.GONE);
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

    private void addCommentIndicatorListener(){
        mDatabase
                .child(getString(R.string.comment_history))
                .child(mLiveKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            final Comment comment = child.getValue(Comment.class);
                            addIndicator(comment);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addIndicator(Comment comment){
        final RelativeLayout infoView = new RelativeLayout(this);
        double rate = mRecyclerView.computeVerticalScrollRange()/comment.getScrollLength();

        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(10, 40);
        infoViewParams.setMargins( 0
                ,  (int)(comment.getPosY()*rate)-30
                ,0,0);

        infoView.setLayoutParams(infoViewParams);
        infoView.setBackgroundColor(Color.parseColor("#00C73C"));

        commentInfo.addView(infoView);
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
                        for (final DataSnapshot child : dataSnapshot.getChildren()) {
                            final Comment comment = child.getValue(Comment.class);
                            latestTime = Math.max(latestTime, comment.getTime());
                            Long passedTime = System.currentTimeMillis() - mStartedTime;
                            Long timeAfter = comment.getTime() - passedTime;
                            if (timeAfter < 0) {
                                continue;
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                   addComment(comment, child.getKey());
                                }
                            }, timeAfter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        mDatabase
                .child(getString(R.string.firebase_db_comment_click_history))
                .child(mLiveKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long latestTime = 0L;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            final CommentClick clickInfo = child.getValue(CommentClick.class);
                            latestTime = Math.max(latestTime, clickInfo.getTime());
                            Long passedTime = System.currentTimeMillis() - mStartedTime;
                            Long timeAfter = clickInfo.getTime() - passedTime;
                            if (timeAfter < 0) {
                                continue;
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showToast(clickInfo);
                                }
                            }, timeAfter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        mDatabase
                .child(getString(R.string.firebase_db_emotion_history))
                .child(mLiveKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            final EmotionModel emotionHistory = child.getValue(EmotionModel.class);
                            long timeAfter = emotionHistory.timeStamp;
                            if (timeAfter < 0) {
                                continue;
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showEmotion(emotionHistory);
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


    private void settingCommentListeners() {

        mWriterCommentAddedListener =
                mDatabase.child(getString(R.string.comment_history))
                        .child(mLiveKey)
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                final Comment comment = dataSnapshot.getValue(Comment.class);
                                addComment(comment, dataSnapshot.getKey());

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

        mWriterCommentShowListener =
                mDatabase.child(getString(R.string.firebase_db_comment_click_history))
                        .child(mLiveKey)
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                final CommentClick clickInfo = dataSnapshot.getValue(CommentClick.class);
                                showToast(clickInfo);
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

    private void showToast(CommentClick clickInfo){
       String comment = ((CommentPointView)commentField.findViewWithTag(clickInfo.getCommentId())).getComment();

        Toasty.custom(ReaderLiveActivity.this, comment, null,
                Color.parseColor("#00C73C"), Toast.LENGTH_SHORT, false, true).show();
    }

    private void addComment(final Comment comment, final String tmpKey){
        Comment tmp = new Comment();
        tmp = comment;

        final CommentPointView commentPointView = new CommentPointView(this);
        float widthRate = (float) deviceWidth / comment.getDeviceWidth();
        double rate = mRecyclerView.computeVerticalScrollRange()/comment.getScrollLength();

        commentPointView.setComment(tmp.getContent());
        commentPointView.setTag(tmpKey);

        RelativeLayout.LayoutParams commentPointParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        commentPointParams.setMargins( (int)(comment.getPosX() * widthRate)-30
                ,  (int)(comment.getPosY()*rate)-30
                ,0,0);

        commentPointView.setLayoutParams(commentPointParams);
        commentField.addView(commentPointView);

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
        if (mWriterCommentShowListener != null) {
            mDatabase.child(getString(R.string.firebase_db_comment_click_history))
                    .child(mLiveKey)
                    .removeEventListener(mWriterCommentShowListener);
        }
    }

    private void setRecyclerView() {
        //mRecyclerView.setEnabled(false);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN
                        && mLiveInfo.state.matches(getResources().getString(R.string.live_state_on_air))) {
                    mEmotionView.inputBar.toggleShowing();
                }
                return true;
            }
        });

    }

}
