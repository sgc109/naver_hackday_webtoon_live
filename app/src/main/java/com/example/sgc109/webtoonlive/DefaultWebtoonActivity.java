package com.example.sgc109.webtoonlive;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.TypedArray;
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

import com.example.sgc109.webtoonlive.CustomView.CommentPointView;
import com.example.sgc109.webtoonlive.CustomView.CustomScrollView;
import com.example.sgc109.webtoonlive.adapter.WebtoonAdapter;
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


public class DefaultWebtoonActivity extends AppCompatActivity {
    private CustomScrollView commentFieldScroll;
    private CustomScrollView commentInfoScroll;
    private RelativeLayout commentInfo;
    private RecyclerView webtoonRcv;
    private RelativeLayout commentField;

    private int curX, curY;
    private String key;
    private int deviceWidth, deviceHeight;
    private DatabaseReference commentRef;

    private List<Integer> imgList;
    private CommentContentDialog commentContentDialog;
    private CommentWriterDialog commentWriterDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webtoon_default);
        init();
    }

    private void initView(){
        webtoonRcv = findViewById(R.id.webtoon_rcv);
        commentField = findViewById(R.id.comment_field);
        commentFieldScroll = findViewById(R.id.comment_field_scroll);
        commentInfoScroll = findViewById(R.id.comment_info_scroll);
        commentInfo = findViewById(R.id.comment_info);
    }

    private void init(){
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
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 116865);
        commentField.setLayoutParams(layoutParams);
        commentInfo.setLayoutParams(layoutParams);

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

    private void syncScroll(){

        webtoonRcv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                commentInfoScroll.scrollTo(0, webtoonRcv.computeVerticalScrollOffset());
                commentFieldScroll.scrollTo(0, webtoonRcv.computeVerticalScrollOffset());
            }
        });


    }

    private void setRecyclerView(){
        settingList();
        webtoonRcv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        webtoonRcv.setAdapter(new WebtoonAdapter(imgList));

    }

    private void settingList(){
        imgList = new ArrayList<Integer>();
        TypedArray typedArray = getResources().obtainTypedArray(R.array.imgArray);
        for(int i=0; i<typedArray.length(); i++)
            imgList.add(typedArray.getResourceId(i,-1));
        typedArray.recycle();
    }

    private void setRealTimeDB(){
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
    @SuppressLint("RestrictedApi")
    private void addComment(final Comment comment, String tmpKey){
        Comment tmp = new Comment();
        tmp = comment;

        final CommentPointView commentPointView = new CommentPointView(this);
        final LinearLayout infoView = new CommentPointView(this);

        commentPointView.setComment(tmp.getContent());
        commentPointView.setLikeCount(tmp.getLikeCount());
        commentPointView.setTag(comment);

        float widthRate = (float) deviceWidth / comment.getDeviceWidth();

        double rate = webtoonRcv.computeVerticalScrollRange()/comment.getScrollLength();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins( (int)(comment.getPosX() * widthRate)-30
                ,  (int)(comment.getPosY()*rate)-30
                ,0,0);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(10, 40);
        params2.setMargins( 0
                ,  (int)(comment.getPosY()*rate)-30
                ,0,0);

        infoView.setLayoutParams(params2);
        infoView.setBackgroundColor(Color.parseColor("#00C73C"));
        commentPointView.setLayoutParams(params);
        commentField.addView(commentPointView);

        commentInfo.addView(infoView);

        commentPointView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Comment tmpComment = (Comment) view.getTag();

                commentContentDialog = new CommentContentDialog(DefaultWebtoonActivity.this, tmpComment.getContent(), tmpComment.getLikeCount(), likeClickListener);
                commentContentDialog.show();
                commentPointView.setSelected(true);
                commentContentDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        commentPointView.setSelected(false);
                    }
                });
            }
        });
    }


    private void getDeviceSize(){
        SharedPreferencesService.getInstance().load(this);

        if(SharedPreferencesService.getInstance().getPrefIntegerData("deviceWidth") == 0) {
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

    @SuppressLint("RestrictedApi")
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String writer = ((EditText)commentWriterDialog.findViewById(R.id.writer_edit)).getText().toString();
            String content = ((EditText)commentWriterDialog.findViewById(R.id.content_edit)).getText().toString();

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

            commentRef.child(key).updateChildren(objectMap);
            commentWriterDialog.dismiss();
        }
    };

    View.OnClickListener likeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

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

        if(id == R.id.write_comment){
            if(commentFieldScroll.getVisibility() == View.GONE) {
                commentFieldScroll.scrollTo(0,webtoonRcv.computeVerticalScrollOffset());
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
}
