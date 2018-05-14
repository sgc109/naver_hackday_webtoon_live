package com.example.sgc109.webtoonlive;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class ReaderLiveActivity extends LiveActivity {

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ReaderLiveActivity.class);
        return intent;
    }

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
}
