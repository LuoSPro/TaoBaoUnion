package com.example.taobaounion.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.taobaounion.model.domain.HomePagerContent;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

public class LooperPagerAdapter extends PagerAdapter {
    private List<HomePagerContent.DataBean> mData = new ArrayList<>();
    private onLooperPagerItemClickListener mItemClickListener;

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //处理越界问题
        int realPosition = position % mData.size();
        HomePagerContent.DataBean dataBean = mData.get(realPosition);
        int measuredHeight = container.getMeasuredHeight();
        int measuredWidth = container.getMeasuredWidth();
        //LogUtils.d(this,"measuredHeight ==> " + measuredHeight);
        //LogUtils.d(this,"measuredWidth ==> " + measuredWidth);
        int ivSize = Math.max(measuredHeight, measuredWidth) / 2;
        String coverUrl = UrlUtils.getCoverPath(dataBean.getPict_url(),ivSize);
        //用代码去替换用xml布局的效果
        ImageView iv = new ImageView(container.getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        iv.setLayoutParams(layoutParams);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(container.getContext()).load(coverUrl).into(iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomePagerContent.DataBean item = mData.get(realPosition);
                mItemClickListener.onLooperItemClick(item);
            }
        });
        container.addView(iv);
        return iv;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void setData(List<HomePagerContent.DataBean> contents) {
        mData.clear();
        mData.addAll(contents);
        notifyDataSetChanged();
    }

    public int getDataSize(){
        return mData.size();
    }

    public void setOnLooperPagerItemClickListener(onLooperPagerItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public interface onLooperPagerItemClickListener{
        void onLooperItemClick(HomePagerContent.DataBean item);
    }
}
