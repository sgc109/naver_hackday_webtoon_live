package com.example.sgc109.webtoonlive;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.sgc109.webtoonlive.custom_view.CustomScrollView;
import com.example.sgc109.webtoonlive.custom_view.EmotionView;
import com.example.sgc109.webtoonlive.custom_view.FixedSizeImageView;
import com.example.sgc109.webtoonlive.dto.EmotionModel;
import com.example.sgc109.webtoonlive.util.SharedPreferencesService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class LiveActivity extends AppCompatActivity {
    private static final String TAG = "LiveActivity";
    protected static final String EXTRA_LIVE_KEY = "extra_live_key";

    private LiveInfo liveInfo;

    protected RecyclerView mRecyclerView;
    protected DatabaseReference mDatabase;
    private LinearLayoutManager mLayoutManager;
    protected String mLiveKey;

    protected CustomScrollView commentFieldScroll;
    protected CustomScrollView commentInfoScroll;
    protected RelativeLayout commentInfo;
    protected RelativeLayout commentField;

    protected int mDeviceWidth;
    protected EmotionView mEmotionView;


    protected int deviceWidth, deviceHeight;
    protected int curX, curY;
    private View bottomMenu;
    private Animation menuSlideDownAnim;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        setTitle("마음의 소리");
        mLiveKey = getIntent().getStringExtra(EXTRA_LIVE_KEY);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecyclerView = findViewById(R.id.activity_live_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mDeviceWidth = displayMetrics.widthPixels;


        mEmotionView = findViewById(R.id.emotionView);
        getLiveInfo();

        commentField = findViewById(R.id.comment_field);
        commentFieldScroll = findViewById(R.id.comment_field_scroll);
        commentInfoScroll = findViewById(R.id.comment_info_scroll);
        commentInfo = findViewById(R.id.comment_info);

        bottomMenu = findViewById(R.id.bottom_menu);
        menuSlideDownAnim = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
        bottomMenu.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                bottomMenu.startAnimation(menuSlideDownAnim);
                bottomMenu.setVisibility(View.GONE);
            }
        }, 3500);


        setToasty();
        getDeviceSize();
        commentFieldSetting();
        syncScroll();

        RecyclerView.Adapter<SceneImageViewHolder> adapter = new RecyclerView.Adapter<SceneImageViewHolder>() {
            final int VIEW_TYPE_NOT_LAST = 0;
            final int VIEW_TYPE_LAST = 1;

            @NonNull
            @Override
            public SceneImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_scene, parent, false);
                if (viewType == VIEW_TYPE_LAST) {
                    return new SceneImageViewHolder(view, true);
                }
                return new SceneImageViewHolder(view, false);
            }


            @Override
            public int getItemViewType(int position) {
                if (position == getItemCount() - 1) {
                    Log.d("mydebug", "last_position!");
                    return VIEW_TYPE_LAST;
                }
                return VIEW_TYPE_NOT_LAST;
            }

            @Override
            public void onBindViewHolder(@NonNull SceneImageViewHolder holder, int position) {
                holder.bindImage(position, getItemCount() - 1);
            }

            @Override
            public int getItemCount() {
                return getResources().getInteger(R.integer.comic1_cuts_cnt);
            }
        };

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);

        setLiveEmotionListen();
    }


    private void syncScroll() {

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                commentInfoScroll.scrollTo(0, mRecyclerView.computeVerticalScrollOffset());
                commentFieldScroll.scrollTo(0, mRecyclerView.computeVerticalScrollOffset());
            }
        });

    }

    private void getDeviceSize() {
        SharedPreferencesService.getInstance().load(this);

        if (SharedPreferencesService.getInstance().getPrefIntegerData("deviceWidth") == 0) {
            DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
            int width = dm.widthPixels;
            int height = dm.heightPixels;

            SharedPreferencesService.getInstance().setPrefData("deviceWidth", width);
            SharedPreferencesService.getInstance().setPrefData("deviceHeight", height);
        }
        deviceWidth = SharedPreferencesService.getInstance().getPrefIntegerData("deviceWidth");
        deviceHeight = SharedPreferencesService.getInstance().getPrefIntegerData("deviceHeight");
    }

    private void setToasty() {
        Toasty.Config.getInstance()
                .setTextColor(Color.WHITE)
                .apply();
    }

    private void commentFieldSetting() {
        commentFieldScroll.setScrollingEnabled(false);
        commentInfoScroll.setScrollingEnabled(false);
        /*FIXME
         params height 값 메타데이터에서 얻기
         */
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (40000 / 600.0 * mDeviceWidth));
        commentField.setLayoutParams(layoutParams);
        commentInfo.setLayoutParams(layoutParams);

    }

    class SceneImageViewHolder extends RecyclerView.ViewHolder {
        FixedSizeImageView mImageView;


        public SceneImageViewHolder(View itemView, boolean mIsLast) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.list_item_scene_image_view);
//            mImageView.setLastPosition(mIsLast);
            mImageView.setCutSize(600, 1600);
        }

        public void bindImage(int position, int lastPosition) {
            Glide.with(LiveActivity.this)
                    .load(getResources().getIdentifier("comic1_" + (position + 1), "drawable", getPackageName()))
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(false))
                    .into(mImageView);
        }
    }

    protected void showEmotion(EmotionModel emotion) {
        mEmotionView.showEmotion(emotion, false);
    }

    private void setLiveEmotionListen() {
        // 감정표현 관련 코드입니다.
        mDatabase.child(getString(R.string.firebase_db_emotion_history))
                .child(mLiveKey).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                EmotionModel emotion = dataSnapshot.getValue(EmotionModel.class);
                long pastTime = System.currentTimeMillis() - liveInfo.startDate;
                long diff = pastTime - emotion.timeStamp;
                long oneSec = 5000;
                Log.d("AA", "diff: " + diff);
                if (!mEmotionView.keySet.contains(dataSnapshot.getKey()) && diff < oneSec) {
                    showEmotion(emotion);
                }
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

    private void getLiveInfo() {
        mDatabase
                .child(getString(R.string.firebase_db_live_list))
                .child(mLiveKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        liveInfo = dataSnapshot.getValue(LiveInfo.class);
                        // 감정표현 입력 레이아웃 초기화
                        mEmotionView.setLiveInfo(liveInfo);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }


}
