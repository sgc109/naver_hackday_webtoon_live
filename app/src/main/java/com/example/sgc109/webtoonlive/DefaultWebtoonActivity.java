package com.example.sgc109.webtoonlive;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.sgc109.webtoonlive.CustomView.CommentView;
import com.example.sgc109.webtoonlive.adapter.WebtoonAdapter;
import com.example.sgc109.webtoonlive.custom_view.CommentPointView;
import com.example.sgc109.webtoonlive.custom_view.CustomScrollView;
import com.example.sgc109.webtoonlive.dto.Comment;
import com.example.sgc109.webtoonlive.util.SharedPreferencesService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.sgc109.webtoonlive.WriterLiveActivity.convertPixelsToDp;


public class DefaultWebtoonActivity extends AppCompatActivity {
    private CustomScrollView commentFieldScroll;
    private CustomScrollView commentInfoScroll;
    private RelativeLayout commentInfo;
    private RecyclerView webtoonRcv;
    private RelativeLayout commentField;

    private int curX, curY;
    private String key, likeKey, lastPosKey = "";
    private int deviceWidth, deviceHeight;
    private DatabaseReference commentRef;

    private List<Integer> imgList;
    private CommentWriterDialog commentWriterDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webtoon_default);
        setTitle("유미의 세포들");
        init();
    }

    private void initView() {
        webtoonRcv = findViewById(R.id.webtoon_rcv);
        commentField = findViewById(R.id.comment_field);
        commentFieldScroll = findViewById(R.id.comment_field_scroll);
        commentInfoScroll = findViewById(R.id.comment_info_scroll);
        commentInfo = findViewById(R.id.comment_info);
    }

    private void init() {
        initView();
        getDeviceSize();
        setRealTimeDB();
        setRecyclerView();
        syncScroll();

        commentFieldScroll.setScrollingEnabled(false);
        commentInfoScroll.setScrollingEnabled(false);
        /*FIXME
         params height 값 메타데이터에서 얻기
         */
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(20800 / 690.0 * deviceWidth));
        commentField.setLayoutParams(layoutParams);
        commentInfo.setLayoutParams(layoutParams);


        commentField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        curX = (int) motionEvent.getX();
                        curY = (int) motionEvent.getY();

                        handler.postDelayed(longPressed, 300);

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

    private void syncScroll() {

        webtoonRcv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                commentInfoScroll.scrollTo(0, webtoonRcv.computeVerticalScrollOffset());
                commentFieldScroll.scrollTo(0, webtoonRcv.computeVerticalScrollOffset());
            }
        });

    }

    private void setRecyclerView() {
        settingList();
        webtoonRcv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        webtoonRcv.setAdapter(new WebtoonAdapter(imgList));

    }

    private void settingList() {
        imgList = new ArrayList<Integer>();
        for (int i = 0; i < getResources().getInteger(R.integer.comic2_cuts_cnt); i++) {
            imgList.add(getResources().getIdentifier("comic2_" + (i + 1), "drawable", getPackageName()));
        }
//        TypedArray typedArray = getResources().obtainTypedArray(R.array.imgArray);
//        for(int i=0; i<typedArray.length(); i++)
//            imgList.add(typedArray.getResourceId(i,-1));
//        typedArray.recycle();
    }

    private void setRealTimeDB() {
        commentRef = FirebaseDatabase.getInstance().getReference().child("comment");

        commentRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

        commentRef.addChildEventListener(new ChildEventListener() {
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

    private void addComment(final Comment comment, final String tmpKey) {
        Comment tmp = new Comment();
        tmp = comment;

        final CommentPointView commentPointView = new CommentPointView(this);
        final CommentView commentView = new CommentView(this);
        final RelativeLayout infoView = new RelativeLayout(this);

        float widthRate = (float) deviceWidth / comment.getDeviceWidth();
        double rate = webtoonRcv.computeVerticalScrollRange() / comment.getScrollLength();

        commentPointView.setComment(tmp.getContent());
        commentPointView.setTag(tmpKey);

        commentView.setCommentText(tmp.getContent());

        RelativeLayout.LayoutParams commentPointParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        commentPointParams.setMargins((int) (comment.getPosX() * widthRate) - (int) convertPixelsToDp(30, this)
                , (int) (comment.getPosY() * rate) - (int) convertPixelsToDp(30, this)
                , 0, 0);

        RelativeLayout.LayoutParams commentParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        commentParams.setMargins(0
                , (int) (comment.getPosY() * rate) - (int) convertPixelsToDp(30, this)
                , 0, 0);

        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(10, 40);
        infoViewParams.setMargins(0
                , (int) (comment.getPosY() * rate) - (int) convertPixelsToDp(30, this)
                , 0, 0);

        infoView.setLayoutParams(infoViewParams);
        infoView.setTag(tmpKey);
        infoView.setBackgroundColor(Color.parseColor("#00C73C"));

        commentPointView.setLayoutParams(commentPointParams);
        commentView.setLayoutParams(commentParams);
        commentView.setArrowImgPos((int) (comment.getPosX() * widthRate) - (int) convertPixelsToDp(50, this));
        commentView.setTag(tmpKey + "&show");

        commentField.addView(commentPointView);
        commentField.addView(commentView);
        commentInfo.addView(infoView);

        commentPointView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeKey = view.getTag().toString();

                if (likeKey == lastPosKey) {
                    if (((CommentView) commentField.findViewWithTag(likeKey + "&show")).getCommentVisibility()) {
                        ((CommentPointView) commentField.findViewWithTag(likeKey)).setSelected(false);
                        ((CommentView) commentField.findViewWithTag(likeKey + "&show")).hideOrShowView();
                    } else {
                        ((CommentPointView) commentField.findViewWithTag(likeKey)).setSelected(true);
                        ((CommentView) commentField.findViewWithTag(likeKey + "&show")).hideOrShowView();
                    }
                } else if (lastPosKey.equals("")) {
                    ((CommentPointView) commentField.findViewWithTag(likeKey)).setSelected(true);
                    ((CommentView) commentField.findViewWithTag(likeKey + "&show")).hideOrShowView();
                    lastPosKey = likeKey;
                } else {
                    ((CommentPointView) commentField.findViewWithTag(likeKey)).setSelected(true);
                    ((CommentView) commentField.findViewWithTag(likeKey + "&show")).hideOrShowView();

                    ((CommentPointView) commentField.findViewWithTag(lastPosKey)).setSelected(false);
                    if (((CommentView) commentField.findViewWithTag(lastPosKey + "&show")).getCommentVisibility())
                        ((CommentView) commentField.findViewWithTag(lastPosKey + "&show")).hideOrShowView();

                    lastPosKey = likeKey;
                }

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

    final Handler handler = new Handler();
    Runnable longPressed = new Runnable() {
        @Override
        public void run() {
            commentWriterDialog = new CommentWriterDialog(DefaultWebtoonActivity.this, onClickListener);
            commentWriterDialog.show();
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String writer = "";
            //FIXME 실서비스 사용자 고유 값
            String content = ((EditText) commentWriterDialog.findViewById(R.id.content_edit)).getText().toString();

            Map<String, Object> map = new HashMap<String, Object>();
            key = commentRef.push().getKey();

            commentRef.updateChildren(map);

            Map<String, Object> objectMap = new HashMap<String, Object>();

            objectMap.put("writer", writer);
            objectMap.put("content", content);
            objectMap.put("scrollLength", webtoonRcv.computeVerticalScrollRange());
            objectMap.put("posX", curX);
            objectMap.put("posY", curY);
            objectMap.put("deviceWidth", deviceWidth);
            objectMap.put("deviceHeight", deviceHeight);
            objectMap.put("likeCount", 0);

            commentRef.child(key).updateChildren(objectMap);
            commentWriterDialog.dismiss();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.writer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.write_comment) {
            if (commentFieldScroll.getVisibility() == View.INVISIBLE) {
                commentFieldScroll.scrollTo(0, webtoonRcv.computeVerticalScrollOffset());
                commentFieldScroll.setVisibility(View.VISIBLE);
                commentInfoScroll.setVisibility(View.GONE);
            } else {
                commentFieldScroll.setVisibility(View.INVISIBLE);
                commentInfoScroll.setVisibility(View.VISIBLE);
            }
        }


        return super.onOptionsItemSelected(item);
    }
}
