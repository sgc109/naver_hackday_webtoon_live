package com.example.sgc109.webtoonlive;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sgc109.webtoonlive.custom_view.CommentPointView;
import com.example.sgc109.webtoonlive.dto.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class WriterLiveActivity extends LiveActivity {

    private Long mStartedTime;
    private CommentWriterDialog commentWriterDialog;
    private String key, likeKey;

    public static Intent newIntent(Context context, String liveKey) {
        Intent intent = new Intent(context, WriterLiveActivity.class);
        intent.putExtra(EXTRA_LIVE_KEY, liveKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DEBUG", "WriterLiveActivity");

        mStartedTime = System.currentTimeMillis();
        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    pushScrollPosToDB();
                }
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);

        commentFieldEventSetting();
        setRealTimeDB();
    }

    private void commentFieldEventSetting(){
        commentField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        curX = (int)motionEvent.getX();
                        curY = (int)motionEvent.getY();

                        handler.postDelayed(longPressed, 1000);

                        return true;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacks(longPressed);

                        return false;
                    case MotionEvent.ACTION_CANCEL:
                        handler.removeCallbacks(longPressed);
                        return false;
                }

                return false;
            }
        });
    }

    private void setRealTimeDB(){

        mDatabase
                .child(getString(R.string.comment_history))
                .child(mLiveKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()) {
                    DataSnapshot tmp = i.next();
                    addComment(tmp.getValue(Comment.class), tmp.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase
                .child(getString(R.string.comment_history))
                .child(mLiveKey)
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addComment(dataSnapshot.getValue(Comment.class), dataSnapshot.getKey());
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

    private void addComment(final Comment comment, final String tmpKey){
        Comment tmp = new Comment();
        tmp = comment;

        final CommentPointView commentPointView = new CommentPointView(this);
        final RelativeLayout infoView = new RelativeLayout(this);
        float widthRate = (float) deviceWidth / comment.getDeviceWidth();
        double rate = mRecyclerView.computeVerticalScrollRange()/comment.getScrollLength();

        commentPointView.setComment(tmp.getContent());
        commentPointView.setTag(tmpKey);

        RelativeLayout.LayoutParams commentPointParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        commentPointParams.setMargins( (int)(comment.getPosX() * widthRate)-30
                ,  (int)(comment.getPosY()*rate)-30
                ,0,0);

        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(10, 40);
        infoViewParams.setMargins( 0
                ,  (int)(comment.getPosY()*rate)-30
                ,0,0);

        infoView.setLayoutParams(infoViewParams);
        infoView.setBackgroundColor(Color.parseColor("#00C73C"));


        commentPointView.setLayoutParams(commentPointParams);
        commentField.addView(commentPointView);
        commentInfo.addView(infoView);

        commentPointView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeKey = view.getTag().toString();

                CommentPointView tmp = ((CommentPointView)commentField
                        .findViewWithTag(likeKey));

                Toasty.custom(WriterLiveActivity.this, tmp.getComment(), null,
                        Color.parseColor("#00C73C"), Toast.LENGTH_SHORT, false, true).show();


                Map<String, Object> map = new HashMap<String, Object>();
                key = mDatabase
                        .child(getString(R.string.firebase_db_comment_click_history))
                        .child(mLiveKey)
                        .push().getKey();

                mDatabase.child(getString(R.string.firebase_db_comment_click_history))
                        .child(mLiveKey)
                        .updateChildren(map);


                Map<String, Object> objectMap = new HashMap<String, Object>();

                objectMap.put("commentId", likeKey);
                objectMap.put("time", System.currentTimeMillis() - mStartedTime);

                mDatabase.child(getString(R.string.firebase_db_comment_click_history))
                        .child(mLiveKey)
                        .child(key).updateChildren(objectMap);

            }
        });
    }

    private void askEndLiveOrNot() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Map<String, Object> objectMap = new HashMap<String, Object>();
                        objectMap.put(getString(R.string.firebase_db_live_info_state), getString(R.string.live_state_over));
                        objectMap.put(getString(R.string.firebase_db_live_info_end_date), System.currentTimeMillis());

                        mDatabase.child(getString(R.string.firebase_db_live_list))
                                .child(mLiveKey)
                                .updateChildren(objectMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        WriterLiveActivity.this.finish();
                                    }
                                });
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.end_live_warning_message)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener).show();
    }

    final Handler handler = new Handler();
    Runnable longPressed = new Runnable() {
        @Override
        public void run() {
            commentWriterDialog = new CommentWriterDialog(WriterLiveActivity.this, onClickListener);
            commentWriterDialog.show();
        }
    };



    public void pushScrollPosToDB() {
        int offset = mRecyclerView.computeVerticalScrollOffset();
        Log.d("scroll_debug", "offset : " + offset);
        double posPercent = (double) offset / mDeviceWidth;

        DatabaseReference ref = mDatabase
                .child(getString(R.string.firebase_db_scroll_history))
                .child(mLiveKey);
        ref.push()
                .setValue(new VerticalPositionChanged(posPercent, System.currentTimeMillis() - mStartedTime));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setTitle("　");
        getMenuInflater().inflate(R.menu.writer_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        askEndLiveOrNot();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

            if(id == R.id.write_comment){
                if(commentFieldScroll.getVisibility() == View.GONE) {
                    commentFieldScroll.scrollTo(0, mRecyclerView.computeVerticalScrollOffset());
                    commentFieldScroll.setVisibility(View.VISIBLE);
                    commentInfoScroll.setVisibility(View.GONE);
                }
                else {
                    commentFieldScroll.setVisibility(View.GONE);
                    commentInfoScroll.setVisibility(View.VISIBLE);
                }
            }


        return super.onOptionsItemSelected(item);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String writer = ((EditText)commentWriterDialog.findViewById(R.id.writer_edit)).getText().toString();
            String content = ((EditText)commentWriterDialog.findViewById(R.id.content_edit)).getText().toString();

            Map<String, Object> map = new HashMap<String, Object>();
            key = mDatabase
                    .child(getString(R.string.comment_history))
                    .child(mLiveKey)
                    .push().getKey();

            mDatabase.child(getString(R.string.comment_history))
                    .child(mLiveKey)
                    .updateChildren(map);

            Map<String, Object> objectMap = new HashMap<String, Object>();

            objectMap.put("writer", writer);
            objectMap.put("content", content);
            objectMap.put("scrollLength", mRecyclerView.computeVerticalScrollRange());
            objectMap.put("posX", curX);
            objectMap.put("posY", curY);
            objectMap.put("deviceWidth", deviceWidth);
            objectMap.put("deviceHeight", deviceHeight);
            objectMap.put("time", System.currentTimeMillis() - mStartedTime);

            mDatabase.child(getString(R.string.comment_history))
                    .child(mLiveKey)
                    .child(key).updateChildren(objectMap);

            commentWriterDialog.dismiss();
        }
    };

}
