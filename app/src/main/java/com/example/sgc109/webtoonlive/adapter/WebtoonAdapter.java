package com.example.sgc109.webtoonlive.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.sgc109.webtoonlive.custom_view.FixedSizeImageView;
import com.example.sgc109.webtoonlive.R;

import java.util.List;


/**
 * Created by jyoung on 2018. 5. 9..
 */

public class WebtoonAdapter extends RecyclerView.Adapter {
    private List<Integer> imgList;

    public WebtoonAdapter(List<Integer> imgList) {
        this.imgList = imgList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_scene, parent, false);
        Log.d("oom_check", "??  ");

        return new WebtoonHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((WebtoonHolder)holder).bindImage(imgList.get(position));
    }

    @Override
    public int getItemCount() {
        return imgList !=null ? imgList.size() : 0;
    }


    public class WebtoonHolder extends RecyclerView.ViewHolder {
        FixedSizeImageView webtoonImg;

        public WebtoonHolder(View itemView) {
            super(itemView);
            webtoonImg = itemView.findViewById(R.id.list_item_scene_image_view);
            webtoonImg.setCutSize(690, 1600);
        }

        public void bindImage(Integer img) {
            Glide.with(webtoonImg.getContext())
                    .load(img)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(false))
                    .into(webtoonImg);

        }
    }
}
