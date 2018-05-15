package com.example.sgc109.webtoonlive;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class ReaderLiveActivity extends LiveActivity {

    public static Intent newIntent(Context context, String liveKey) {
        Intent intent = new Intent(context, ReaderLiveActivity.class);
        intent.putExtra(EXTRA_LIVE_KEY, liveKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseReference ref = mDatabase.child(getString(R.string.firebase_db_scroll_history));
        mChildEventListenerHandle = ref.addChildEventListener(new ChildEventListener() {
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChildEventListenerHandle != null) {
            mDatabase.child(getString(R.string.firebase_db_scroll_history)).removeEventListener(mChildEventListenerHandle);
        }
//        if (mIsWriter) {
//            mHandler.removeCallbacks(mPeriodicScrollPosCheck);
//        }
    }
}
