package com.example.sgc109.webtoonlive;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class WriterLiveActivity extends LiveActivity {

    private Long mStartedTime;
    private CommentWriterDialog commentWriterDialog;

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

    }

    private void askEndLiveOrNot() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        mDatabase.child(getString(R.string.firebase_db_live_list))
                                .child(mLiveKey)
                                .child(getString(R.string.live_info_state))
                                .setValue(getString(R.string.live_state_over))
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

    @Override
    public void onBackPressed() {
        askEndLiveOrNot();
    }

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.writer_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if(id == R.id.write_comment){
//           commentWriterDialog = new CommentWriterDialog(WriterLiveActivity.this, onClickListener);
//
//
//        }
//
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    View.OnClickListener onClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            String content = ((EditText)commentWriterDialog.findViewById(R.id.content_edit)).getText().toString();
//            String key;
//
//            Map<String, Object> map = new HashMap<String, Object>();
//            key = mDatabase.child(getString(R.string.comment_history)).push().getKey();
//
//            mDatabase.child(getString(R.string.comment_history)).updateChildren(map);
//
//            Map<String, Object> objectMap = new HashMap<String, Object>();
//            objectMap.put("content", content);
//            objectMap.put("time", System.currentTimeMillis() - mStartedTime);
//
//            mDatabase.child(getString(R.string.comment_history)).child(key).updateChildren(objectMap);
//            commentWriterDialog.dismiss();
//        }
//    };

}
