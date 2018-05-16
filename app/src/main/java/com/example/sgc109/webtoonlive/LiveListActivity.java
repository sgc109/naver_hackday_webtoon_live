package com.example.sgc109.webtoonlive;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class LiveListActivity extends AppCompatActivity {
    public static final String EXTRA_IS_WRITER = "extra.is.writer";
    private TextView mEmptyMsgTextView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private DatabaseReference mDatabase;
    private List<LiveInfo> mLiveInfoList;
    private boolean mIsWriter;
    private boolean mExistOnAirLive;
    private ValueEventListener mPrevEventListener;
    private ProgressBar mProgressBar;

    public static Intent newIntent(Context context, boolean isWriter) {
        Intent intent = new Intent(context, LiveListActivity.class);
        intent.putExtra(EXTRA_IS_WRITER, isWriter);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_list);

        mIsWriter = getIntent().getBooleanExtra(EXTRA_IS_WRITER, false);
        mLiveInfoList = new ArrayList<>();
        mProgressBar = findViewById(R.id.live_list_progress_bar);
        mRecyclerView = findViewById(R.id.live_list_recycler_view);
        mEmptyMsgTextView = findViewById(R.id.live_list_empty_message_text_view);
        mAdapter = new RecyclerView.Adapter<LiveInfoViewHolder>() {
            @NonNull
            @Override
            public LiveInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.list_item_live, parent, false);
                LiveInfoViewHolder holder = new LiveInfoViewHolder(view);
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull LiveInfoViewHolder holder, int position) {
                holder.bindLiveInfo(mLiveInfoList.get(position));
            }

            @Override
            public int getItemCount() {
                return mLiveInfoList.size();
            }
        };
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    class LiveInfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextView;
        private TextView mTextViewLive;
        private TextView mDateTextView;
        private LiveInfo mLiveInfo;
        private int mPrvColor;

        public LiveInfoViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.list_item_live_text_view);
            mTextViewLive = itemView.findViewById(R.id.list_item_live_text_view_live);
            mDateTextView = itemView.findViewById(R.id.list_item_live_date_text_view);
            itemView.setOnClickListener(this);
        }

        public void bindLiveInfo(LiveInfo liveInfo) {
            mLiveInfo = liveInfo;
            mTextView.setText(liveInfo.title);
//            Calendar calendar = convertLongToCalendar(liveInfo.date);
            mDateTextView.setText(DateDisplayer.dateToStringFormat(new Date(liveInfo.date)));
            if (liveInfo.state.equals(getString(R.string.live_state_on_air))) {
                mPrvColor = mTextView.getCurrentTextColor();
                mTextView.setTextColor(Color.RED);
                mTextViewLive.setVisibility(View.VISIBLE);
                mDateTextView.setVisibility(View.GONE);
            } else {
                if (mPrvColor != 0) {
                    mTextView.setTextColor(mPrvColor);
                }
                mTextViewLive.setVisibility(View.GONE);
                mDateTextView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View view) {
            Intent intent = ReaderLiveActivity.newIntent(LiveListActivity.this, mLiveInfo.key);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!mIsWriter) {
            return super.onOptionsItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.menu_item_create_live:
                Intent intent = CreateLiveActivity.newIntent(this);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mIsWriter) {
            return super.onCreateOptionsMenu(menu);
        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.live_list_activity, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        mProgressBar.setVisibility(View.VISIBLE);
        mEmptyMsgTextView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        if (mPrevEventListener != null) {
            mDatabase
                    .child(getString(R.string.firebase_db_live_list))
                    .removeEventListener(mPrevEventListener);
        }
        mPrevEventListener =
                mDatabase
                        .child(getString(R.string.firebase_db_live_list))
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d("mydebug", "onDataChange()!");
                                mProgressBar.setVisibility(View.GONE);
                                mLiveInfoList = new ArrayList<>();
                                mExistOnAirLive = false;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    LiveInfo liveInfo = snapshot.getValue(LiveInfo.class);
                                    mLiveInfoList.add(liveInfo);
                                    if (liveInfo.state.equals(getString(R.string.live_state_on_air))) {
                                        mExistOnAirLive = true;
                                    }
                                }
                                if (mLiveInfoList.size() > 0) {
                                    mEmptyMsgTextView.setVisibility(View.GONE);
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                } else {
                                    mEmptyMsgTextView.setVisibility(View.VISIBLE);
                                }
                                Collections.sort(mLiveInfoList, new Comparator<LiveInfo>() {
                                    @Override
                                    public int compare(LiveInfo o1, LiveInfo o2) {
                                        String STATE_OVER = getString(R.string.live_state_over);
                                        String STATE_ON_AIR = getString(R.string.live_state_on_air);

                                        if (o1.state.equals(STATE_OVER) && o2.state.equals(STATE_ON_AIR)) {
                                            return 1;
                                        }
                                        if (o1.state.equals(STATE_ON_AIR) && o2.state.equals(STATE_OVER)) {
                                            return -1;
                                        }
                                        if (o1.date < o2.date) {
                                            return 1;
                                        }
                                        return -1;
                                    }
                                });
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
    }
//    public Calendar convertLongToCalendar(Long date){
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(date);
//        return calendar;
//    }
}
