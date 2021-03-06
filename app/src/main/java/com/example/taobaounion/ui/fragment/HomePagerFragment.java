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
        //???recyclerView??????item????????????
        mContentAdapter.setOnListenItemClickListener(this);
        //??????RecyclerView?????????NestedScrollView??????????????????match_parent??????????????????????????????????????????????????????????????????
        //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        homePagerParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (homePagerContainer == null) {
                    return;
                }
                //??????????????????
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
                    //????????????????????????????????????????????????????????????
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
                LogUtils.d(HomePagerFragment.this, "??????Loader more...");
                //????????????
////                mTwinklingRefreshLayout.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        //??????????????????
////                        //????????????
////                        mTwinklingRefreshLayout.finishLoadmore();
////                        LogUtils.d(HomePagerFragment.this,"????????????");
////                        Toast.makeText(getContext(),"??????100?????????",Toast.LENGTH_SHORT).show();
////                    }
////                },3000);
                //?????????????????????
                if (mCategoryPagerPresenter != null) {
                    mCategoryPagerPresenter.loaderMore(mMaterialId);
                }
            }
        });
    }

    @Override
    protected void initView(View rootView) {
        //?????????????????????
        mContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        mContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(),1.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(),1.5f);
            }
        });
        //???????????????
        mContentAdapter = new LinearItemContentAdapter();
        //???????????????
        mContentList.setAdapter(mContentAdapter);
        //???????????????
//        looperPager.setAdapter(mLooperPagerAdapter);
        //??????Refresh????????????
        //?????????????????????
        mTwinklingRefreshLayout.setEnableRefresh(false);
        //????????????????????????
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
        //????????????
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
        //????????????????????????
        mContentAdapter.setData(contents);
        setUpStates(State.SUCCESS);
    }

    @Override
    public int getCategoryId() {
        return mMaterialId;
    }

    @Override
    public void onError() {
        //????????????
        setUpStates(State.ERROR);
    }

    @Override
    public void onLoading() {
        setUpStates(State.LOADING);
    }

    public void onNetworkError() {
        //????????????
        setUpStates(State.ERROR);
    }

    @Override
    public void onEmpty() {

    }

    @Override
    public void onLoaderMoreError() {
        ToastUtil.showToast("??????????????????????????????");
        if (mTwinklingRefreshLayout != null) {
            //????????????????????????finishRefreshing???????????????????????????????????????????????????????????????????????????????????????finishLoadmore()
//            mTwinklingRefreshLayout.finishRefreshing();
            mTwinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoaderMoreEmpty() {
        ToastUtil.showToast("??????????????????");
        if (mTwinklingRefreshLayout != null) {
            mTwinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoaderMoreLoaded(List<HomePagerContent.DataBean> contents) {
        //?????????????????????????????????
        mContentAdapter.addDate(contents);
        if (mTwinklingRefreshLayout != null) {
            mTwinklingRefreshLayout.finishLoadmore();
        }
        ToastUtil.showToast("?????????" + contents.size() + "?????????");
    }

    private void showListData() {
        mBannerView.setAdapter(new BannerAdapter() {
            @Override
            public View getView(int position, View convertView) {//??????????????????????????????View
                ImageView imageView = null;
                //covertView????????????View
                if (convertView == null){
                    imageView = new ImageView(getContext());
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }else{
                    imageView = (ImageView) convertView;
                }
//                LogUtils.d(getContext(),"??????url --> " + mContents.get(position).getPict_url());
//                LogUtils.d(getContext(),"?????? position --> " + position);
                int measuredHeight = mBannerView.getMeasuredHeight();
                int measuredWidth = mBannerView.getMeasuredWidth();
                //LogUtils.d(this,"measuredHeight ==> " + measuredHeight);
                //LogUtils.d(this,"measuredWidth ==> " + measuredWidth);
                int ivSize = Math.max(measuredHeight, measuredWidth) / 2;
                String convertUrl = UrlUtils.getCoverPath(mContents.get(position).getPict_url(),ivSize);
                Glide.with(Objects.requireNonNull(getContext())).load(convertUrl).into(imageView);
                return imageView;
            }

            //?????????????????????????????????????????????
            @Override
            public int getCount() {
                return 5;
            }

            //????????????????????????????????????ItemView?????????
            @Override
            public String getBannerDesc(int position) {
//                LogUtils.d(getContext(),"?????? position --> " + position);
                return mContents.get(position).getTitle();
            }
        });
        //??????????????????
        mBannerView.startRoll();
        //????????????
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
        //????????????????????????
        LogUtils.d(this,"item click ==> " + item.getTitle());
        handleItemClick(item);
    }

    private void handleItemClick(IBaseInfo item) {
        TicketUtil.toTicketPage(getContext(),item);
    }
}
