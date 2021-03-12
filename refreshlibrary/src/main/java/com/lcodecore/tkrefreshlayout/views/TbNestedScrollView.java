package com.lcodecore.tkrefreshlayout.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
public class TbNestedScrollView extends NestedScrollView {
    private static final String TAG = "TbNestedScrollView";
    private int mHeaderHeight = 0;//onScrollChanged方法中，滑动距离得最大值为466
    private int originScroll = 0;
    private RecyclerView mRecyclerView;

    public TbNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public TbNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TbNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setHeaderHeight(int headerHeight){
        this.mHeaderHeight = headerHeight;
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        //LogUtils.d(this,"dy ==> " + dy);
        if (target instanceof RecyclerView){
            this.mRecyclerView = (RecyclerView)target;
        }
        if (originScroll < mHeaderHeight){
            scrollBy(dx,dy);
            consumed[0] = dx;
            consumed[1] = dy;
        }
        super.onNestedPreScroll(target, dx, dy, consumed, type);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        this.originScroll = t;
        //LogUtils.d(this,"vertical ==> " + t);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    /**
     * 判断子类是否滑动到了底部
     * @return
     */
    public boolean isInBottom() {
        if (mRecyclerView != null) {
            //这里通过使用RecyclerView的判断是否到达底部的方法，来通知refreshView当前是否已经到达底部
            //进而动态的控制nestedView的滑动
            //在滑动过程中mRecyclerView.canScrollVertically(1)返回true，滑倒底部返回false
            //我们希望滑动过程中不加载更多，所以!true=false，滑到底部后，!false=true，可以加载更多
            boolean isBottom = !mRecyclerView.canScrollVertically(1);
            //Log.d(TAG, "canScroll ==> " + isBottom);
            return isBottom;
        }
        return false;
    }
}
