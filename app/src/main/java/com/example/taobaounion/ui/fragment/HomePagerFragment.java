package com.example.taobaounion.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.domain.Categories;
import com.example.taobaounion.model.domain.HomePagerContent;
import com.example.taobaounion.model.domain.IBaseInfo;
import com.example.taobaounion.presenter.ICategoryPagerPresenter;
import com.example.taobaounion.ui.activity.MainActivity;
import com.example.taobaounion.ui.adapter.LinearItemContentAdapter;
import com.example.taobaounion.ui.adapter.LooperPagerAdapter;
import com.example.taobaounion.ui.custom.AutoLoopViewPager;
import com.example.taobaounion.utils.Constants;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.utils.SizeUtils;
import com.example.taobaounion.utils.TicketUtil;
import com.example.taobaounion.utils.ToastUtil;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.ICategoryPagerCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.views.TbNestedScrollView;
import com.ls.bannerview.banner.BannerAdapter;
import com.ls.bannerview.banner.BannerView;
import com.ls.bannerview.banner.BannerViewPager;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;

public class HomePagerFragment extends BaseFragment implements ICategoryPagerCallback, LinearItemContentAdapter.OnListenItemClickListener {

    private ICategoryPagerPresenter mCategoryPagerPresenter;
    private int mMaterialId;
    private LinearItemContentAdapter mContentAdapter;
    private List<HomePagerContent.DataBean> mContents;

    public static HomePagerFragment newInstance(Categories.DataBean category) {
        HomePagerFragment homePagerFragment = new HomePagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_HOME_PAGER_TITLE, category.getTitle());
        bundle.putInt(Constants.KEY_HOME_PAGER_ID, category.getId());
        homePagerFragment.setArguments(bundle);
        return homePagerFragment;
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_home_pager;
    }

    @Override
    protected void initListener() {
        //给recyclerView内容item设置监听
        mContentAdapter.setOnListenItemClickListener(this);
        //由于RecyclerView是放在NestedScrollView中的，并且是match_parent，所以每次数据都是获取得一次完整请求得数据量
        //而不是一个屏幕对应得数据量，所以，这里处理之后，能把每次获取得数据量控制在一个屏幕范围内
        homePagerParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (homePagerContainer == null) {
                    return;
                }
                //解决滑动冲突
                homePagerNestedView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int headerHeight = homePagerContainer.getMeasuredHeight();
                        //LogUtils.d(HomePagerFragment.this,"headerHeight ==> " + headerHeight);
                        homePagerNestedView.setHeaderHeight(headerHeight);
                    }
                },100);
                int measuredHeight = homePagerParent.getMeasuredHeight();
                //LogUtils.d(HomePagerFragment.this,"measuredHeight ==> " +measuredHeight );
                ViewGroup.LayoutParams layoutParams = mContentList.getLayoutParams();
                layoutParams.height = measuredHeight;
                mContentList.setLayoutParams(layoutParams);
                if (measuredHeight != 0){
                    //这样就不会频繁去调用这个监听事件（移除）
                    homePagerParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        currentCategoryTitleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int measuredHeight = mContentList.getMeasuredHeight();
                LogUtils.d(HomePagerFragment.this,"measuredHeight ==> " +measuredHeight);
            }
        });

        mTwinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                LogUtils.d(HomePagerFragment.this, "触发Loader more...");
                //模拟加载
////                mTwinklingRefreshLayout.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        //模拟加载数据
////                        //结束刷新
////                        mTwinklingRefreshLayout.finishLoadmore();
////                        LogUtils.d(HomePagerFragment.this,"结束刷新");
////                        Toast.makeText(getContext(),"加载100条数据",Toast.LENGTH_SHORT).show();
////                    }
////                },3000);
                //加载更多的内容
                if (mCategoryPagerPresenter != null) {
                    mCategoryPagerPresenter.loaderMore(mMaterialId);
                }
            }
        });
    }

    @Override
    protected void initView(View rootView) {
        //设置布局管理器
        mContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        mContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(),1.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(),1.5f);
            }
        });
        //创建适配器
        mContentAdapter = new LinearItemContentAdapter();
        //设置设配器
        mContentList.setAdapter(mContentAdapter);
        //设置适配器
//        looperPager.setAdapter(mLooperPagerAdapter);
        //设置Refresh相关属性
        //不允许下拉刷新
        mTwinklingRefreshLayout.setEnableRefresh(false);
        //允许上拉加载更多
        mTwinklingRefreshLayout.setEnableLoadmore(true);
//        mTwinklingRefreshLayout.setBottomView();
    }

    @Override
    protected void initPresenter() {
        mCategoryPagerPresenter = PresenterManager.getInstance().getCategoryPagerPresenter();
        mCategoryPagerPresenter.registerViewCallback(this);
    }

    @Override
    protected void loadData() {
        Bundle arguments = getArguments();
        String title = arguments.getString(Constants.KEY_HOME_PAGER_TITLE);
        mMaterialId = arguments.getInt(Constants.KEY_HOME_PAGER_ID);
        LogUtils.d(this, "title ==> " + title + " id ==> " + mMaterialId);
        //加载数据
        if (mCategoryPagerPresenter != null) {
            mCategoryPagerPresenter.getContentByCategoryId(mMaterialId);
        }
        if (currentCategoryTitleTv != null) {
            currentCategoryTitleTv.setText(title);
        }
    }

    @BindView(R.id.home_pager_content_list)
    public RecyclerView mContentList;

    @BindView(R.id.banner_view)
    public BannerView mBannerView;

    @BindView(R.id.home_pager_title)
    public TextView currentCategoryTitleTv;


    @BindView(R.id.home_pager_nested_scroller)
    public TbNestedScrollView homePagerNestedView;

    @BindView(R.id.home_pager_header_container)
    public LinearLayout homePagerContainer;

    @BindView(R.id.home_pager_refresh)
    public TwinklingRefreshLayout mTwinklingRefreshLayout;

    @BindView(R.id.home_pager_parent)
    public LinearLayout homePagerParent;

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d(this,"onResume...");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.d(this,"onPause...");
    }

    @Override
    public void onContentLoaded(List<HomePagerContent.DataBean> contents) {
        //数据列表加载到了
        mContentAdapter.setData(contents);
        setUpStates(State.SUCCESS);
    }

    @Override
    public int getCategoryId() {
        return mMaterialId;
    }

    @Override
    public void onError() {
        //网络错误
        setUpStates(State.ERROR);
    }

    @Override
    public void onLoading() {
        setUpStates(State.LOADING);
    }

    public void onNetworkError() {
        //网络错误
        setUpStates(State.ERROR);
    }

    @Override
    public void onEmpty() {

    }

    @Override
    public void onLoaderMoreError() {
        ToastUtil.showToast("网络异常，请稍后重试");
        if (mTwinklingRefreshLayout != null) {
            //注：这里不能使用finishRefreshing，否者之后再刷新的时候，刷新事件不再受监听，所以正确的是用finishLoadmore()
//            mTwinklingRefreshLayout.finishRefreshing();
            mTwinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoaderMoreEmpty() {
        ToastUtil.showToast("没有更多商品");
        if (mTwinklingRefreshLayout != null) {
            mTwinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoaderMoreLoaded(List<HomePagerContent.DataBean> contents) {
        //添加到适配器数据的底部
        mContentAdapter.addDate(contents);
        if (mTwinklingRefreshLayout != null) {
            mTwinklingRefreshLayout.finishLoadmore();
        }
        ToastUtil.showToast("加载了" + contents.size() + "条数据");
    }

    private void showListData() {
        mBannerView.setAdapter(new BannerAdapter() {
            @Override
            public View getView(int position, View convertView) {//支持用户自定义轮播的View
                ImageView imageView = null;
                //covertView：缓存的View
                if (convertView == null){
                    imageView = new ImageView(getContext());
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }else{
                    imageView = (ImageView) convertView;
                }
//                LogUtils.d(getContext(),"图片url --> " + mContents.get(position).getPict_url());
//                LogUtils.d(getContext(),"图片 position --> " + position);
                int measuredHeight = mBannerView.getMeasuredHeight();
                int measuredWidth = mBannerView.getMeasuredWidth();
                //LogUtils.d(this,"measuredHeight ==> " + measuredHeight);
                //LogUtils.d(this,"measuredWidth ==> " + measuredWidth);
                int ivSize = Math.max(measuredHeight, measuredWidth) / 2;
                String convertUrl = UrlUtils.getCoverPath(mContents.get(position).getPict_url(),ivSize);
                Glide.with(Objects.requireNonNull(getContext())).load(convertUrl).into(imageView);
                return imageView;
            }

            //支持用户自定义指示器的圆点个数
            @Override
            public int getCount() {
                return 5;
            }

            //支持用户自定义指示器每个ItemView的描述
            @Override
            public String getBannerDesc(int position) {
//                LogUtils.d(getContext(),"图片 position --> " + position);
                return mContents.get(position).getTitle();
            }
        });
        //开启自动滚动
        mBannerView.startRoll();
        //设置监听
        mBannerView.setBannerItemClickListener(new BannerViewPager.BannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                handleItemClick(mContents.get(position));
            }
        });
    }

    @Override
    public void onLooperListLoaded(List<HomePagerContent.DataBean> contents) {
        mContents = contents;
        LogUtils.d(this, "looper size ==> " + contents.size());
        mBannerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                showListData();
            }
        },50);
    }

    @Override
    protected void release() {
        if (mCategoryPagerPresenter != null) {
            mCategoryPagerPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onItemClick(IBaseInfo item) {
        //列表内容被点击了
        LogUtils.d(this,"item click ==> " + item.getTitle());
        handleItemClick(item);
    }

    private void handleItemClick(IBaseInfo item) {
        TicketUtil.toTicketPage(getContext(),item);
    }
}
