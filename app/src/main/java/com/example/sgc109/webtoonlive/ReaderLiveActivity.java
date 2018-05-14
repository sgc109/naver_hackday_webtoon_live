package com.example.sgc109.webtoonlive;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class ReaderLiveActivity extends LiveActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_live);

        DatabaseReference ref = mDatabase.child(getString(R.string.firebase_db_scroll_history));
        mChildEventListenerHandle = ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                VerticalPositionChanged data = dataSnapshot.getValue(VerticalPositionChanged.class);
                double percentage = data.offsetProportion;

                int curY = (int) (percentage * mDeviceWidth);
                int dy = curY - mCurY;
//                    Log.d("scroll_debug", "prvY : " + mCurY + ", curY : " + curY + ", dy : " + dy);
                Log.d("scroll_debug", "mCurY : " + mCurY);
                mRecyclerView.smoothScrollBy(0, dy);
                mCurY = curY;
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
}
