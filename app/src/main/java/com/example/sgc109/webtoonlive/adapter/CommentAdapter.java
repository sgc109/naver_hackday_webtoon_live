package com.example.sgc109.webtoonlive.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.sgc109.webtoonlive.R;

/**
 * Created by jyoung on 2018. 5. 15..
 */

public class CommentAdapter extends RecyclerView.Adapter {
    View.OnTouchListener onTouchListener;
    int height;
    public CommentAdapter(View.OnTouchListener onTouchListener, int height) {
        this.onTouchListener = onTouchListener;
        this.height = height;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        ((CommentViewHolder)holder).commentField.setLayoutParams(layoutParams);
        ((CommentViewHolder)holder).commentField.setOnTouchListener(onTouchListener);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout commentField;

        public CommentViewHolder(View itemView) {
            super(itemView);
            commentField = itemView.findViewById(R.id.comment_field);

        }

    }


}
