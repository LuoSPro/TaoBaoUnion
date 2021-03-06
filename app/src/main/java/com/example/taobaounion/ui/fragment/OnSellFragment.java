package com.example.taobaounion.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.domain.IBaseInfo;
import com.example.taobaounion.model.domain.OnSellContent;
import com.example.taobaounion.presenter.IOnSellPagePresenter;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.ui.activity.TicketActivity;
import com.example.taobaounion.ui.adapter.OnSellContentAdapter;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.utils.SizeUtils;
import com.example.taobaounion.utils.TicketUtil;
import com.example.taobaounion.utils.ToastUtil;
import com.example.taobaounion.view.IOnSellPageCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import butterknife.BindView;

public class OnSellFragment extends BaseFragment implements IOnSellPageCallback, OnSellContentAdapter.OnSellPageItemClickListener {

    private IOnSellPagePresenter mOnSellPagePresenter;

    private static final int DEFAULT_SPAN_COUNT = 2;

    @BindView(R.id.on_sell_content_list)
    public RecyclerView mContentRv;

    @BindView(R.id.on_sell_refresh_layout)
    public TwinklingRefreshLayout mTwinklingRefreshLayout;

    private OnSellContentAdapter mOnSellContentAdapter;

    @Override
    protected void initPresenter() {
        super.initPresenter();
        mOnSellPagePresenter = PresenterManager.getInstance().getOnSellPagePresenter();
        mOnSellPagePresenter.registerViewCallback(this);
        mOnSellPagePresenter.getOnSellContent();
    }

    @Override
    protected void release() {
        super.release();
        if (mOnSellPagePresenter != null) {
            mOnSellPagePresenter.unregisterViewCallback(this);
        }
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_on_sell;
    }

    @Override
    protected void initView(View rootView) {
        setUpStates(State.SUCCESS);
        mOnSellContentAdapter = new OnSellContentAdapter();
        //?????????????????????
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), DEFAULT_SPAN_COUNT);
        mContentRv.setLayoutManager(gridLayoutManager);
        mContentRv.setAdapter(mOnSellContentAdapter);
        mContentRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(), 2.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(), 2.5f);
                outRect.left = SizeUtils.dip2px(getContext(), 2.5f);
                outRect.right = SizeUtils.dip2px(getContext(), 2.5f);
            }
        });
        //????????????
        mTwinklingRefreshLayout.setEnableLoadmore(true);
        //????????????
        mTwinklingRefreshLayout.setEnableRefresh(false);
        //????????????????????????
        mTwinklingRefreshLayout.setEnableOverScroll(true);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mTwinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //????????????????????????
                if (mOnSellPagePresenter != null) {
                    mOnSellPagePresenter.loadMore();
                }
            }
        });
        mOnSellContentAdapter.setOnSellPageItemClickListener(this);
    }

    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_bar_layout,container,false);
    }

    @Override
    public void onContentLoadedSuccess(OnSellContent result) {
        setUpStates(State.SUCCESS);
        //????????????
        //??????UI
        mOnSellContentAdapter.setData(result);
    }

    @Override
    public void onMoreLoaded(OnSellContent moreResult) {
        //????????????
        mTwinklingRefreshLayout.finishLoadmore();
        int size = moreResult.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size();
        ToastUtil.showToast("?????????" + size + "?????????");
        //??????????????????????????????
        mOnSellContentAdapter.onMoreLoaded(moreResult);
    }

    @Override
    public void onMoreLoadedError() {
        mTwinklingRefreshLayout.finishLoadmore();
        ToastUtil.showToast("????????????,???????????????");
    }

    @Override
    public void onMoreLoadedEmpty() {
        mTwinklingRefreshLayout.finishLoadmore();
        ToastUtil.showToast("??????????????????...");
    }

    @Override
    public void onError() {
        setUpStates(State.ERROR);
    }

    @Override
    public void onLoading() {
        setUpStates(State.LOADING);
    }

    @Override
    public void onEmpty() {
        setUpStates(State.EMPTY);
    }

    @Override
    public void onSellItemClick(IBaseInfo data) {
        //???????????????????????????
        TicketUtil.toTicketPage(getContext(), data);
    }
}
