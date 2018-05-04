package com.example.sgc109.webtoonlive;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
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
import android.view.View;
import android.view.ViewGroup;
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
    private List<Integer> posHistory;
    private String mLastKey;
    private Long mLastCheckedTime;
    private int mCurPosition;

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
        posHistory = new ArrayList<>();
        mLastCheckedTime = new Date().getTime();

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

        final LinearLayoutManager layoutManager;
        if(mIsWriter) {
            layoutManager = new LinearLayoutManager(this);

            RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    mCurPosition += dy;
//                    Log.d("scroll_debug", "onScrolled Start");
//                    Log.d("scroll_debug", "last : " + mLastCheckedTime);
//                    Log.d("scroll_debug", "now  : " + new Date().getTime());
                    Log.d("scroll_debug", "curY : " + mCurPosition);
                    Long now = new Date().getTime();
                    if(now - mLastCheckedTime >= 500) {
//                        Log.d("scroll_debug", "FirebaseDB Push!!");
                        DatabaseReference ref = mDatabase.child(getString(R.string.firebase_db_pos_history));
                        ref
                                .push()
                                .setValue(new VerticalPositionChanged(mCurPosition));
                        mLastCheckedTime = now;
                    }
//                    Log.d("scroll_debug", "onScrolled Finish");
                }
            };
            mRecyclerView.addOnScrollListener(scrollListener);

        } else {
            layoutManager = new ScrollDisablingLayoutManager(this);

            DatabaseReference ref = mDatabase.child(getString(R.string.firebase_db_pos_history));
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    int pos = dataSnapshot.getValue(VerticalPositionChanged.class).position;
                    Log.d("mydebug", "pos : " + pos);
//                    mRecyclerView.smoothScrollToPosition(pos);
                    ((LinearLayoutManager)mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(0, pos);
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

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        if (!mIsWriter) {
            mRecyclerView.setNestedScrollingEnabled(false);
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

    public class ScrollDisablingLayoutManager extends LinearLayoutManager {
        private boolean isScrollEnabled = false;
//        private static final float MILLISECONDS_PER_INCH = 175f; //default is 25f (bigger = slower)
//        public ScrollDisablingLayoutManager(Context context, int orientation, boolean reverseLayout) {
//            super(context, orientation, reverseLayout);
//        }
//
//        public ScrollDisablingLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//            super(context, attrs, defStyleAttr, defStyleRes);
//        }
//
//        @Override
//        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
//
//            final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
//
//                @Override
//                public PointF computeScrollVectorForPosition(int targetPosition) {
//                    return super.computeScrollVectorForPosition(targetPosition);
//                }
//
//                @Override
//                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
//                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
//                }
//            };
//
//            linearSmoothScroller.setTargetPosition(position);
//            startSmoothScroll(linearSmoothScroller);
//        }

        public ScrollDisablingLayoutManager(Context context) {
            super(context);
        }

        public void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollVertically() {
            //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
            return isScrollEnabled && super.canScrollVertically();
        }
    }
}
